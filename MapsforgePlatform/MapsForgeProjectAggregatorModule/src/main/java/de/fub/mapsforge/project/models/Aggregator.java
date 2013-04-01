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
import de.fub.mapsforge.project.utils.AggregatorUtils;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
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
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 * A high level implementation of an gps navigation graph map renderer.
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
    private PipelineListener pipelineListener = null;

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
                initAggregator();
                dataObjectModelSynchonizerClient.modelChangedFromSource();
            }
        });
        AggregatorDescriptor aggregatorDescriptor = getAggregatorDescriptor();
        if (aggregatorDescriptor != null) {
            initAggregator();
            if (pipelineListener == null) {
                pipelineListener = new PipelineListenerImpl();
                getPipeline().addPipelineListener(pipelineListener);
            }
        } else {
            setAggregatorState(AggregatorState.ERROR_NOT_EXECUTABLE);
        }
    }

    private void initAggregator() {
        setAggregatorState(AggregatorState.INACTIVE);
        initAggregatorContainer();
        initPipeline();
    }

    private void initAggregatorContainer() {
        AggregatorDescriptor descriptor = getAggregatorDescriptor();
        File sourceFolder = null;
        if (descriptor.getCacheFolderPath() == null) {
            sourceFolder = createDefaultCacheFolder();
        } else {
            sourceFolder = new File(descriptor.getCacheFolderPath());
            if (!sourceFolder.exists()) {
                try {
                    sourceFolder.createNewFile();
                } catch (IOException ex) {
                    // failed to create the cache folder. Fall back
                    // to create the cache folder in the same folder where
                    // the .agg file lies.
                    LOG.log(Level.WARNING, MessageFormat.format("{0}.\\n sourceFolder: {1}! Creating default sourceFolder in the same folder where the agg file lies !", ex.getMessage(), sourceFolder.getAbsolutePath()), ex); //NO18N
                    sourceFolder = createDefaultCacheFolder();
                }
            }
        }

        IAggregationStrategy aggregateStrategy = AggregatorUtils.createInstance(IAggregationStrategy.class, descriptor.getAggregationStrategy());
        ICachingStrategy cachingStrategy = AggregatorUtils.createInstance(ICachingStrategy.class, descriptor.getTileCachingStrategy());

        if (aggContainer != null) {
            aggContainer.setAggregationStrategy(aggregateStrategy);
            aggContainer.setCachingStrategy(cachingStrategy);
            aggContainer.setDataSource(sourceFolder);
        } else {
            aggContainer = AggContainer.createContainer(sourceFolder, aggregateStrategy, cachingStrategy);
        }
    }

    private void initPipeline() {
        AggregatorDescriptor descriptor = getAggregatorDescriptor();
        pipeline.clear();
        try {
            AbstractAggregationProcess process = null;
            for (ProcessDescriptor processDescriptor : descriptor.getPipeline().getList()) {
                process = createProcess(processDescriptor);
                pipeline.add(process);
            }
        } catch (AggregatorProcessPipeline.PipelineException ex) {
            setAggregatorState(AggregatorState.ERROR_NOT_EXECUTABLE);
            Exceptions.printStackTrace(ex);
        }
    }

    private File createDefaultCacheFolder() {
        AggregatorDescriptor descriptor = getAggregatorDescriptor();
        File sourceFolder = null;
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
        return sourceFolder;
    }

    /**
     * Starts and processes the specified process units in the list. The process
     * units in the list can't be in out of order
     *
     * @param processes
     */
    @NbBundle.Messages({
        "# {0} - processName",
        "# {1} - aggregatorName",
        "CLT_Proceeding_Process={1}: Running {0}..."})
    @SuppressWarnings("unchecked")
    public void start(final List<AbstractAggregationProcess<?, ?>> processes) {
        getPipeline().start(processes);
    }

    /**
     * Convenience method to run all process units that are in the pipeline from
     * the first process unit to the last one.
     */
    public void start() {
        start(new ArrayList<AbstractAggregationProcess<?, ?>>(getPipeline().getProcesses()));
    }

    /**
     * Returns the underlying Aggregator container.
     *
     * @return Always an AggContainer instance.
     */
    public AggContainer getAggContainer() {
        return aggContainer;
    }

    /**
     * Returns the process pipeline of this Aggregator object.
     *
     * @return Always an AggregatorProcessPipeline instance.
     */
    public AggregatorProcessPipeline getPipeline() {
        return pipeline;
    }

    /**
     * A convenience method to delegate the call to the respective method of the
     * Aggregator descriptor.
     *
     * @return in all cases a List instance.
     */
    public List<Source> getSourceList() {
        return getAggregatorDescriptor().getDatasources();
    }

    /**
     * Returns the current aggregator state.
     *
     * @return Always an AggregatorState instance.
     */
    public synchronized AggregatorState getAggregatorState() {
        return aggregatorState;
    }

    /**
     * Sets the current aggregator state and fires a property change event.
     *
     * @param aggregatorState an AggregatorState, null not permitted.
     */
    public synchronized void setAggregatorState(AggregatorState aggregatorState) {
        assert aggregatorState != null : "null permitted as aggregator state.";
        Object oldValue = this.aggregatorState;
        this.aggregatorState = aggregatorState;
        pcs.firePropertyChange(PROP_NAME_AGGREGATOR_STATE, oldValue, this.aggregatorState);

    }

    /**
     * Returns the underlying DataObject.
     *
     * @return Always an AggregatorDataObject instance.
     */
    public AggregatorDataObject getDataObject() {
        return dataObject;
    }

    /**
     * Convenience method to get the Aggregator descriptor. This method
     * delegates the call to the underlying AggregatorDataObject.
     *
     * @return A valid AggregatorDescriptor instance if possible, otherwise
     * null.
     */
    public AggregatorDescriptor getAggregatorDescriptor() {
        try {
            return dataObject.getAggregatorDescriptor();
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (JAXBException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
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

                        if (!constructor.isAccessible()) {
                            constructor.setAccessible(true);
                            aggregateProcess = constructor.newInstance(Aggregator.this);
                            constructor.setAccessible(false);
                        } else {
                            aggregateProcess = constructor.newInstance(Aggregator.this);
                        }
                        aggregateProcess.setProcessDescriptor(processDescriptor);

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

        ERROR("Error"),
        RUNNING("Running"),
        INACTIVE("Inactive"),
        ERROR_NOT_EXECUTABLE("Errot not executable"),
        WARNING("Warning");
        private String displayName;

        private AggregatorState(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
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
