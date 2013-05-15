/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.tasks.map;

import de.fub.gpxmodule.xml.Gpx;
import de.fub.mapforgeproject.api.process.ProcessState;
import de.fub.mapsforge.project.aggregator.pipeline.AggregatorProcessPipeline;
import de.fub.mapsforge.project.aggregator.xml.Source;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.inference.InferenceModelResultDataSet;
import de.fub.mapsforge.project.detector.model.pipeline.postprocessors.tasks.Task;
import de.fub.mapsforge.project.models.Aggregator;
import de.fub.mapsforge.project.utils.AggregatorUtils;
import de.fub.utilsmodule.Collections.ObservableArrayList;
import de.fub.utilsmodule.Collections.ObservableList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map.Entry;
import javax.swing.JComponent;
import javax.xml.bind.JAXBException;
import org.openide.filesystems.FileObject;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_MapRenderer_Name=Map Renderer",
    "CLT_MapRenderer_Description=Renders Gps data with the help of an Aggregator"
})
@ServiceProvider(service = Task.class)
public class MapRenderer extends Task {

    static final String PROP_NAME_AGGREGATOR_FILE_PATH = "maprenderer.aggregator.file.path";
    private final ObservableList<Aggregator> aggregatorList = new ObservableArrayList<Aggregator>();
    private final MapRendererSupport mapRendererSupport = new MapRendererSupport(MapRenderer.this);

    public MapRenderer() {
        validate();
    }

    @Override
    protected void setDetector(Detector detector) {
        super.setDetector(detector);
        validate();
    }

    public ObservableList<Aggregator> getAggregatorList() {
        return aggregatorList;
    }

    private void validate() {
        setProcessState(ProcessState.INACTIVE);
        try {
            Aggregator aggregatorTemplate = mapRendererSupport.createAggregator();
            if (aggregatorTemplate == null) {
                throw new FileNotFoundException();
            } else {
                aggregatorTemplate.getDataObject().getPrimaryFile().delete();
            }
        } catch (FileNotFoundException ex) {
            setProcessState(ProcessState.SETTING_ERROR);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void clear() {
        if (!aggregatorList.isEmpty()) {
            for (Aggregator aggregator : aggregatorList) {
                try {
                    aggregator.getDataObject().getPrimaryFile().delete();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            aggregatorList.clear();
        }
        // this is hack and should have a better work around.
        fireProcessFinishedEvent();
    }

    @Override
    protected void start() {
        clear();
        try {
            Aggregator aggregator = mapRendererSupport.createAggregator();
            if (aggregator != null && aggregator.getAggregatorDescriptor() != null) {

                InferenceModelResultDataSet resultDataSet = getResultDataSet();
                for (Entry<String, List<Gpx>> entry : resultDataSet.entrySet()) {
                    try {
                        if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                            // specify the name of the aggregator
                            aggregator.getAggregatorDescriptor().setName(
                                    MessageFormat.format("{0} [{1}]",
                                    entry.getKey(),
                                    getDetector().getDetectorDescriptor().getName()));

                            // to be sure there is no datasource in the template
                            // we clear the source list of the aggregator
                            List<Source> sourceList = aggregator.getSourceList();
                            sourceList.clear();

                            FileObject tmpFolder = mapRendererSupport.createTempFolder(
                                    URLEncoder.encode(
                                    MessageFormat.format("MapRendererTransportation: {0}",
                                    aggregator.getAggregatorDescriptor().getName()),
                                    "UTF-8"));

                            for (Gpx gpx : entry.getValue()) {
                                File tmpFile = mapRendererSupport.createTmpfile(tmpFolder);
                                try {
                                    AggregatorUtils.saveGpxToFile(tmpFile, gpx);
                                    sourceList.add(new Source(tmpFile.getAbsolutePath()));
                                } catch (JAXBException ex) {
                                    Exceptions.printStackTrace(ex);
                                }
                            }
                            aggregator.updateSource();
                            aggregator.start();
                            aggregatorList.add(aggregator);
                        }
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        aggregator = mapRendererSupport.createAggregator();
                    }
                }
            } else {
                setProcessState(ProcessState.SETTING_ERROR);
            }
        } catch (FileNotFoundException ex) {
            throw new AggregatorProcessPipeline.PipelineException(ex.getMessage(), ex);
        }
    }

    public Detector getProcessParentDetector() {
        return getDetector();
    }

    @Override
    public JComponent getSettingsView() {
        return null;
    }

    @Override
    public String getName() {
        if (getProcessDescriptor() != null && getProcessDescriptor().getName() != null) {
            return getProcessDescriptor().getName();
        }
        return Bundle.CLT_MapRenderer_Name();
    }

    @Override
    public String getDescription() {
        if (getProcessDescriptor() != null && getProcessDescriptor().getDescription() != null) {
            return getProcessDescriptor().getDescription();
        }
        return Bundle.CLT_MapRenderer_Description();
    }

    @Override
    protected Node createNodeDelegate() {
        return new MapRendererNode(MapRenderer.this);
    }

    /**
     * The propose of this class is only for access reasons. The protector
     * access to the TaskProcessNode will be extended to package access.
     */
    static abstract class MapRendererProcessNode extends Task.TaskProcessNode {

        public MapRendererProcessNode(Task taskProcess) {
            super(taskProcess);
        }

        public MapRendererProcessNode(Children children, Task taskProcess) {
            super(children, taskProcess);
        }
    }
}
