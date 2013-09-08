/*
 * Copyright (C) 2013 Serdar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fub.maps.project.plugins.tasks.map;

import de.fub.gpxmodule.xml.Gpx;
import de.fub.maps.project.aggregator.pipeline.AggregatorProcessPipeline;
import de.fub.maps.project.aggregator.xml.Source;
import de.fub.maps.project.api.process.ProcessState;
import de.fub.maps.project.detector.model.Detector;
import de.fub.maps.project.detector.model.gpx.TrackSegment;
import de.fub.maps.project.detector.model.inference.InferenceModelResultDataSet;
import de.fub.maps.project.detector.model.pipeline.postprocessors.tasks.Task;
import de.fub.maps.project.detector.model.xmls.ProcessDescriptor;
import de.fub.maps.project.detector.model.xmls.Property;
import de.fub.maps.project.detector.utils.GPSUtils;
import de.fub.maps.project.models.Aggregator;
import de.fub.maps.project.utils.AggregatorUtils;
import de.fub.utilsmodule.Collections.ObservableArrayList;
import de.fub.utilsmodule.Collections.ObservableList;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.HashSet;
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
 * Implementation of an Detector task, which handels the creation of an
 * specified Aggregator for each transport mode defined in the result of the
 * classification of the inferenceModel of the Detector.
 *
 * Each Aggregator creates a navigation graph.
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_MapRenderer_Name=Map Renderer",
    "CLT_MapRenderer_Description=Renders Gps data with the help of an Aggregator",
    "CLT_MapRenderer_Property_AggregationFilePath_Name=Aggregator File Path",
    "CLT_MapRenderer_Property_AggregationFilePath_Description=The file path to the aggregator file.",
    "CLT_MapRenderer_Property_OpenAfterFinished_Name=Open after inference is finished",
    "CLT_MapRenderer_Property_OpenAfterFinished_Description=Opens after aggregation is finished all aggregator panels."
})
@ServiceProvider(service = Task.class)
public class MapRenderer extends Task {

    static final String PROP_NAME_AGGREGATOR_FILE_PATH = "maprenderer.aggregator.file.path";
    private static final String PROP_NAME_OPEN_AFTER_FINISHED = "maprenderer.open.after.finished";
    private final ObservableList<Aggregator> aggregatorList = new ObservableArrayList<Aggregator>();
    private final MapRendererSupport mapRendererSupport = new MapRendererSupport(MapRenderer.this);

    public MapRenderer() {
        validate();
    }

    @Override
    protected void setDetector(Detector detector) {
        super.setDetector(detector);
    }

    @Override
    protected void setProcessDescriptor(ProcessDescriptor processDescriptor) {
        super.setProcessDescriptor(processDescriptor);
        validate();
    }

    public ObservableList<Aggregator> getAggregatorList() {
        return aggregatorList;
    }

    /**
     * Checks whether a valid aggregator template is provider for this
     * MapRenderer instance.
     */
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

    /**
     * deletes all created Aggregator instances with there descriptor files.
     */
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
                for (Entry<String, HashSet<TrackSegment>> entry : resultDataSet.entrySet()) {
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
                            Gpx gpx = GPSUtils.convert(entry.getValue());
                            File tmpFile = mapRendererSupport.createTmpfile(tmpFolder);
                            try {
                                AggregatorUtils.saveGpxToFile(tmpFile, gpx);
                                sourceList.add(new Source(tmpFile.getAbsolutePath()));
                            } catch (JAXBException ex) {
                                Exceptions.printStackTrace(ex);
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

                // TODO: display aggregators if open.after.finished property
                // is true
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

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(MapRenderer.class.getName());
        descriptor.setName(Bundle.CLT_MapRenderer_Name());
        descriptor.setDescription(Bundle.CLT_MapRenderer_Description());

        Property property = new Property();
        property.setId(PROP_NAME_AGGREGATOR_FILE_PATH);
        property.setJavaType(String.class.getName());
        property.setName(Bundle.CLT_MapRenderer_Property_AggregationFilePath_Name());
        property.setDescription(Bundle.CLT_MapRenderer_Property_AggregationFilePath_Description());
        descriptor.getProperties().getPropertyList().add(property);

        property = new Property();
        property.setId(PROP_NAME_OPEN_AFTER_FINISHED);
        property.setJavaType(Boolean.class.getName());
        property.setValue(Boolean.TRUE.toString());
        property.setName(Bundle.CLT_MapRenderer_Property_OpenAfterFinished_Name());
        property.setDescription(Bundle.CLT_MapRenderer_Property_OpenAfterFinished_Description());
        descriptor.getProperties().getPropertyList().add(property);

        return descriptor;
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
