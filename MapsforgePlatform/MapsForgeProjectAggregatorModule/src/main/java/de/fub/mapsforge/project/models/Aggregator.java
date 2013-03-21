/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.models;

import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.agg.IAggregationStrategy;
import de.fub.agg2graph.agg.tiling.ICachingStrategy;
import de.fub.mapforgeproject.api.process.Process;
import de.fub.mapforgeproject.api.process.ProcessPipeline.PipelineListener;
import de.fub.mapforgeproject.api.statistics.StatisticProvider;
import de.fub.mapsforge.project.aggregator.filetype.AggregatorDataObject;
import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.mapsforge.project.aggregator.pipeline.AggregatorProcessPipeline;
import de.fub.mapsforge.project.aggregator.xml.AggregatorDescriptor;
import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptor;
import de.fub.mapsforge.project.aggregator.xml.Source;
import de.fub.mapsforge.project.utils.AggregateUtils;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBException;
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
public class Aggregator extends ModelSynchronizer {

    private static final Logger LOG = Logger.getLogger(Aggregator.class.getName());
    public static final String PROP_NAME_AGGREGATOR_STATE = "aggregator.state";
    private final AggregatorDataObject dataObject;
    private AggregatorState aggregatorState = AggregatorState.INACTIVE;
    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private final AggregatorProcessPipeline pipeline;
    private AggContainer aggContainer;
    private ModelSynchronizerClient dataObjectModelSynchonizerClient;
    private final Object MUTEX_PROCESS_CREATOR = new Object();

    public Aggregator(AggregatorDataObject dataObject) {
        assert dataObject != null;
        this.dataObject = dataObject;
        pipeline = new AggregatorProcessPipeline(this);
        init();
    }

    private void init() {
        dataObjectModelSynchonizerClient = create(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                // do nothing
            }
        });

        this.dataObject.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                setUpPipeline();
                dataObjectModelSynchonizerClient.modelChangedFromSource();
            }
        });
        setUpPipeline();
        getPipeline().addPipelineListener(new PipelineListenerImpl());
    }

    private void setUpPipeline() {
        AggregatorDescriptor descriptor = getDescriptor();
        if (descriptor != null) {
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
                        LOG.log(Level.SEVERE, MessageFormat.format("{0}.\\n sourceFikder: {1}", ex.getMessage(), sourceFolder.getAbsolutePath()), ex);
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
    public void start(final List<AbstractAggregationProcess<?, ?>> processes) {
        getPipeline().start(processes);
    }

    public AggContainer getAggContainer() {
        return aggContainer;
    }

    public AggregatorProcessPipeline getPipeline() {
        return pipeline;
    }

    public List<Source> getSourceList() {
        return getDescriptor().getDatasources();
    }

    public synchronized AggregatorState getAggregatorState() {
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

    public void notifyModified() {
        dataObject.modifySourceEditor();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        pcs.removePropertyChangeListener(listener);
    }

    public AbstractAggregationProcess createProcess(ProcessDescriptor processDescriptor) {
        synchronized (MUTEX_PROCESS_CREATOR) {
            assert processDescriptor != null;
            AbstractAggregationProcess<?, ?> aggregateProcess = null;
            Set<Class<? extends AbstractAggregationProcess>> allClasses = Lookup.getDefault().lookupResult(AbstractAggregationProcess.class).allClasses();

            for (Class<? extends AbstractAggregationProcess> clazz : allClasses) {
                if (clazz != null && clazz.getName().equals(processDescriptor.getJavaType())) {
                    try {
                        Constructor<? extends AbstractAggregationProcess> constructor = clazz.getDeclaredConstructor(Aggregator.class);
                        aggregateProcess = constructor.newInstance(Aggregator.this);
                        aggregateProcess.setDescriptor(processDescriptor);
                    } catch (SecurityException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (IllegalArgumentException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (ReflectiveOperationException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                    break;
                }
            }
            return aggregateProcess;
        }
    }

    public List<StatisticProvider> getStatistics() {
        List<StatisticProvider> statisticProviders = new ArrayList<StatisticProvider>();
        for (AbstractAggregationProcess<?, ?> process : getPipeline().getProcesses()) {
            if (process instanceof StatisticProvider) {
                statisticProviders.add(((StatisticProvider) process));
            }
        }
        return statisticProviders;
    }

    @Override
    public void updateSource() {
        if (dataObject != null) {
            dataObject.modifySourceEditor();
        }
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

    private class PipelineListenerImpl implements PipelineListener {

        public PipelineListenerImpl() {
        }

        @Override
        public void willStart(List<Process> propcesses) {
            setAggregatorState(Aggregator.AggregatorState.INACTIVE);
        }

        @Override
        public void started() {
            setAggregatorState(Aggregator.AggregatorState.RUNNING);
        }

        @Override
        public void stoped(PipelineListener.Result result) {
            switch (result) {
                case CANCELED:
                    setAggregatorState(Aggregator.AggregatorState.INACTIVE);
                    break;
                case FINISHED:
                    setAggregatorState(Aggregator.AggregatorState.INACTIVE);
                    break;
                case ERROR:
                    setAggregatorState(Aggregator.AggregatorState.ERROR);
                    break;
            }
        }

        @Override
        public void pipelineChanged() {
            // something to do?
        }
    }
}
