/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fub.maps.project.aggregator.pipeline.processes;

import de.fub.agg2graph.osm.OsmExporter;
import de.fub.agg2graph.roadgen.RoadNetwork;
import de.fub.maps.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.maps.project.aggregator.xml.ProcessDescriptor;
import de.fub.maps.project.api.process.ProcessPipeline;
import de.fub.maps.project.api.statistics.StatisticProvider;
import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 * Process unit implementation of the osm export funktion of the generated
 * RoadNetwork of an Aggregator instance.
 *
 * @author Serdar
 */
@ServiceProvider(service = AbstractAggregationProcess.class)
@NbBundle.Messages({
    "CLT_OSM_Export_Title=OSM File Export",
    "CLT_OSM_Export_Description=Exports the graph to an osm xml file"
})
public class OSMExportProcess extends AbstractAggregationProcess<RoadNetwork, FileObject> implements StatisticProvider {

    @StaticResource
    private static final String ICON_PATH = "de/fub/maps/project/aggregator/pipeline/processes/datasourceProcessIcon.png";
    private static final Image IMAGE = ImageUtilities.loadImage(ICON_PATH);
    private RoadNetwork roadNetwork;
    private FileObject fileObject;

    public OSMExportProcess() {
    }

    @Override
    public void setInput(RoadNetwork input) {
        this.roadNetwork = input;
    }

    @Override
    public FileObject getResult() {
        synchronized (RUN_MUTEX) {
            return fileObject;
        }
    }

    @Override
    protected void start() {
        if (roadNetwork != null) {

            FileChooserBuilder builder = new FileChooserBuilder(OSMExportProcess.class);
            builder.setTitle(Bundle.CLT_OSM_Export_Title()).addFileFilter(new OsmXmlFileFilter());
            JFileChooser fileChooser = builder.createFileChooser();
            int showSaveDialog = fileChooser.showSaveDialog(null);

            if (JFileChooser.APPROVE_OPTION == showSaveDialog) {
                File selectedFile = fileChooser.getSelectedFile();
                if (selectedFile != null) {
                    handleExport(selectedFile);
                }
            }
            fireProcessProgressEvent(new ProcessPipeline.ProcessEvent<OSMExportProcess>(this, "Creating OSM File...", 100));
        }
    }

    private void handleExport(File selectedFile) {
        OutputStream outputStream = null;
        try {
            if (!selectedFile.exists()) {
                selectedFile.createNewFile();
            }
            OsmExporter osmExport = new OsmExporter();
            outputStream = new FileOutputStream(selectedFile);
            osmExport.export(roadNetwork, outputStream);
            fileObject = FileUtil.toFileObject(selectedFile);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    @Override
    public String getName() {
        if (getProcessDescriptor() != null) {
            return getProcessDescriptor().getDisplayName();
        }
        return Bundle.CLT_OSM_Export_Title();
    }

    @Override
    public String getDescription() {
        if (getProcessDescriptor() != null) {
            return getProcessDescriptor().getDescription();
        }
        return Bundle.CLT_OSM_Export_Description();
    }

    @Override
    public Image getIcon() {
        return IMAGE;
    }

    @Override
    public JComponent getSettingsView() {
        return null;
    }

    @Override
    public List<StatisticSection> getStatisticData() throws StatisticNotAvailableException {
        List<StatisticSection> statisticSections = new ArrayList<StatisticSection>();
        statisticSections.add(getPerformanceData());
        return statisticSections;
    }

    @Override
    public boolean cancel() {
        canceled.set(true);
        return canceled.get();
    }

    @Override
    public Component getVisualRepresentation() {
        return null;
    }

    @Override
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor descriptor = new ProcessDescriptor();
        descriptor.setJavaType(OSMExportProcess.class.getName());
        descriptor.setDisplayName(Bundle.CLT_OSM_Export_Title());
        descriptor.setDescription(Bundle.CLT_OSM_Export_Description());
        return descriptor;
    }

    private static class OsmXmlFileFilter extends FileFilter {

        public OsmXmlFileFilter() {
        }

        @Override
        public boolean accept(File f) {
            return f.getName().endsWith("xml");
        }

        @Override
        public String getDescription() {
            return "Osm Xml File";
        }
    }
}
