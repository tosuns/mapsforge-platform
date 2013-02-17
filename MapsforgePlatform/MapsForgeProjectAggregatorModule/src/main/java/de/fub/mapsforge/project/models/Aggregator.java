/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.models;

import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.agg.IAggregationStrategy;
import de.fub.agg2graph.agg.tiling.ICachingStrategy;
import de.fub.mapsforge.project.aggregator.filetype.AggregatorDataObject;
import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.mapsforge.project.aggregator.pipeline.AggregateProcessPipeline;
import de.fub.mapsforge.project.aggregator.pipeline.ProcessPipeline;
import de.fub.mapsforge.project.aggregator.xml.AggregatorDescriptor;
import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptor;
import de.fub.mapsforge.project.aggregator.xml.Source;
import de.fub.mapsforge.project.utils.AggregateUtils;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBException;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
public class Aggregator implements ChangeListener, PropertyChangeListener {

    private static final Logger LOG = Logger.getLogger(Aggregator.class.getName());
    public static final String PROP_NAME_AGGREGATOR_STATE = "aggregator.state";
    public static final String PROP_NAME_DATAOBJECT = "aggregator.dataobject";
    private final AggregatorDataObject dataObject;
    private AggregatorState aggregatorState = AggregatorState.INACTIVE;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private AggregateProcessPipeline pipeline = new AggregateProcessPipeline(this);
    private AggContainer aggContainer;

    public Aggregator(AggregatorDataObject dataObject) {
        assert dataObject != null;
        this.dataObject = dataObject;
        this.dataObject.addChangeListener(Aggregator.this);
        init();
    }

    private void init() {
        AggregatorDescriptor descriptor = getDescriptor();

        if (descriptor != null) {
            descriptor.addPropertyChangeListener(Aggregator.this);
            File sourceFolder = null;
            if (descriptor.getCacheFolderPath() == null) {
                FileObject parent = this.dataObject.getPrimaryFile().getParent();
                if (parent != null) {
                    try {
                        if (parent.getFileObject(descriptor.getName()) != null) {
                            sourceFolder = FileUtil.toFile(parent.getFileObject(descriptor.getName()));
                        } else {
                            FileObject cacheFolder = parent.createFolder(descriptor.getName());
                            descriptor.setCacheFolderPath(cacheFolder.getPath());
                            sourceFolder = FileUtil.toFile(cacheFolder);
                        }
                        descriptor.setCacheFolderPath(sourceFolder.getAbsolutePath());
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                        LOG.info(ex.getMessage());
                    }
                }
                dataObject.save();
            } else {
                sourceFolder = new File(descriptor.getCacheFolderPath());
                if (!sourceFolder.exists()) {
                    try {
                        sourceFolder.createNewFile();
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
            IAggregationStrategy aggregateStrategy = AggregateUtils.createInstance(IAggregationStrategy.class, descriptor.getAggregationStrategy());
            ICachingStrategy cachingStrategy = AggregateUtils.createInstance(ICachingStrategy.class, descriptor.getTileCachingStrategy());

            if (aggContainer != null) {
                aggContainer.setAggregationStrategy(aggregateStrategy);
                aggContainer.setCachingStrategy(cachingStrategy);
                aggContainer.setDataSource(sourceFolder);
            } else {
                aggContainer = AggContainer.createContainer(sourceFolder, aggregateStrategy, cachingStrategy);
            }

            pipeline.clear();
            AbstractAggregationProcess process = null;
            for (ProcessDescriptor processDescriptor : descriptor.getPipeline().getList()) {
                process = createProcess(processDescriptor);
                pipeline.add(process);
            }
        }
    }

    @NbBundle.Messages({"# {0} - processName", "# {1} - aggregatorName", "CLT_Proceeding_Process={1}: Running {0}..."})
    @SuppressWarnings("unchecked")
    public synchronized void start(final List<AbstractAggregationProcess<?, ?>> processes) {
        final ProgressHandle handler = ProgressHandleFactory.createHandle(Bundle.CLT_Proceeding_Process("", getDescriptor().getName()));
        try {
            setAggregatorState(Aggregator.AggregatorState.INACTIVE);

            handler.start(100);
            int i = 0;
            AbstractAggregationProcess lastProcess = null;
            setAggregatorState(Aggregator.AggregatorState.RUNNING);

            long lastTime = System.currentTimeMillis();

            for (int j = 0; j < processes.size(); j++) {
                final int unit = j;
                AbstractAggregationProcess process = processes.get(j);
                handler.setDisplayName(Bundle.CLT_Proceeding_Process(process.getName(), getDescriptor().getName()));
                if (j == 0) {
                    int indexOf = getPipeline().indexOf(process);
                    if (indexOf > 0) {
                        lastProcess = getPipeline().get(indexOf - 1);
                    }
                }
                if (lastProcess != null) {
                    Object result = lastProcess.getResult();
                    process.setInput(result);
                }
                ProcessPipeline.ProcessListener processListener = new ProcessPipeline.ProcessListener() {
                    @Override
                    public void changed(ProcessPipeline.ProcessEvent event) {
                        double progress = (100d / processes.size());
                        progress = (progress / 100 * event.getPercentfinished()) + (100 / processes.size() * unit);
                        LOG.log(Level.INFO, "progress {0}", progress);
                        handler.progress(event.getProcessMessage(), (int) progress);

                    }
                };
                process.addProcessListener(processListener);
                lastTime = System.currentTimeMillis();
                process.run();
                long now = System.currentTimeMillis();
                process.removeProcessListener(processListener);
                LOG.log(Level.INFO, "Process {0} took {1} time.", new Object[]{process.getName(), new Long(now - lastTime)});
                lastProcess = process;
                lastTime = now;
            }
            setAggregatorState(Aggregator.AggregatorState.INACTIVE);
        } catch (Throwable ex) {
            Exceptions.printStackTrace(ex);
            setAggregatorState(Aggregator.AggregatorState.ERROR);
        } finally {
            handler.finish();
        }
    }

    public AggContainer getAggContainer() {
        return aggContainer;
    }

    public AggregateProcessPipeline getPipeline() {
        return pipeline;
    }

    public List<Source> getSourceList() {
        return getDescriptor().getDatasources();
    }

    public AggregatorState getAggregatorState() {
        return aggregatorState;
    }

    public synchronized void setAggregatorState(AggregatorState aggregatorState) {
        Object oldValue = this.aggregatorState;
        this.aggregatorState = aggregatorState;
        pcs.firePropertyChange(PROP_NAME_AGGREGATOR_STATE, oldValue, this.aggregatorState);
    }

    public AggregatorDataObject getDataObject() {
        return dataObject;
    }

    public AggregatorDescriptor getDescriptor() {
        try {
            return dataObject.getAggregator();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (JAXBException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
        if (getDescriptor() != null) {
            getDescriptor().addPropertyChangeListener(listener);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
        if (getDescriptor() != null) {
            getDescriptor().removePropertyChangeListener(listener);
        }
    }

    public synchronized AbstractAggregationProcess createProcess(ProcessDescriptor processDescriptor) {
        assert processDescriptor != null;
        AbstractAggregationProcess<?, ?> aggregateProcess = null;
        Set<Class<? extends AbstractAggregationProcess>> allClasses = Lookup.getDefault().lookupResult(AbstractAggregationProcess.class).allClasses();

        for (Class<? extends AbstractAggregationProcess> clazz : allClasses) {
            if (clazz != null && clazz.getName().equals(processDescriptor.getJavatype())) {
                try {
                    Constructor<? extends AbstractAggregationProcess> constructor = clazz.getDeclaredConstructor(Aggregator.class);
                    aggregateProcess = constructor.newInstance(Aggregator.this);
                    aggregateProcess.setDescriptor(processDescriptor);
                } catch (Exception ex) {
                    Exceptions.printStackTrace(ex);
                }
                break;
            }
        }
        return aggregateProcess;
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (getDescriptor() != null) {
            getDescriptor().removePropertyChangeListener(this);
        }
        init();
        pcs.firePropertyChange(PROP_NAME_DATAOBJECT, null, getDataObject());
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        getDataObject().save();
    }

    public enum AggregatorState {

        ERROR("Error", ImageUtilities.loadImage(AggregateUtils.ICON_PATH_ERROR)),
        RUNNING("Running", ImageUtilities.loadImage(AggregateUtils.ICON_PATH_BUSY)),
        INACTIVE("Inactive", ImageUtilities.loadImage(AggregateUtils.ICON_PATH_NORMAL));
        private String displayName;
        private Image image;

        private AggregatorState(String displayName, Image image) {
            this.displayName = displayName;
            this.image = image;
        }

        public String getDisplayName() {
            return displayName;
        }

        public Image getImage() {
            return image;
        }
    }
}
