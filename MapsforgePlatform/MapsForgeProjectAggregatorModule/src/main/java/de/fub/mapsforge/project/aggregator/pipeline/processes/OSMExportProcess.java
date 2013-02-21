/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.pipeline.processes;

import de.fub.agg2graph.osm.OsmExporter;
import de.fub.agg2graph.roadgen.RoadNetwork;
import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptor;
import de.fub.mapsforge.project.models.Aggregator;
import de.fub.mapsforge.project.utils.AggregateUtils;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
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
 *
 * @author Serdar
 */
@ServiceProvider(service = AbstractAggregationProcess.class)
@NbBundle.Messages({"CLT_OSM_Export_Title=OSM File Export",
    "CLT_OSM_Export_Description=OSM Exporter process"})
public class OSMExportProcess extends AbstractAggregationProcess<RoadNetwork, FileObject> {

    @StaticResource
    private static final String ICON_PATH = "de/fub/mapsforge/project/aggregator/pipeline/processes/datasourceProcessIcon.png";
    private static final Image IMAGE = ImageUtilities.loadImage(ICON_PATH);
    private RoadNetwork roadNetwork;
    private FileObject fileObject;
    private final Object MUTEX = new Object();

    public OSMExportProcess() {
        super(null);
    }

    public OSMExportProcess(Aggregator container) {
        super(container);
    }

    @Override
    public void setInput(RoadNetwork input) {
        this.roadNetwork = input;
    }

    @Override
    public FileObject getResult() {
        synchronized (MUTEX) {
            return fileObject;
        }
    }

    @Override
    protected void start() {
        synchronized (MUTEX) {
            if (roadNetwork != null) {

                FileChooserBuilder builder = new FileChooserBuilder(OSMExportProcess.class);
                builder.setTitle(Bundle.CLT_OSM_Export_Title()).addFileFilter(new OsmXmlFileFilter());
                JFileChooser fileChooser = builder.createFileChooser();
                int showSaveDialog = fileChooser.showSaveDialog(null);

                if (JFileChooser.APPROVE_OPTION == showSaveDialog) {
                    File selectedFile = fileChooser.getSelectedFile();
                    if (selectedFile != null) {
                        try {
                            if (!selectedFile.exists()) {
                                selectedFile.createNewFile();
                            }
                            OsmExporter osmExport = new OsmExporter();
                            osmExport.setTargetFile(selectedFile);
                            osmExport.export(roadNetwork);

                            fileObject = FileUtil.toFileObject(selectedFile);
                        } catch (IOException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            }
        }
    }

    @Override
    public String getName() {
        return Bundle.CLT_OSM_Export_Title();
    }

    @Override
    public String getDescription() {
        return Bundle.CLT_OSM_Export_Description();
    }

    @Override
    public void setDescriptor(ProcessDescriptor descriptor) {
        this.descriptor = descriptor;
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
    protected ProcessDescriptor createProcessDescriptor() {
        ProcessDescriptor desc = null;
        try {
            desc = AggregateUtils.getProcessDescriptor(getClass());
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }

        return desc;
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
