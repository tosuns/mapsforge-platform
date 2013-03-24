/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.tasks;

import de.fub.gpxmodule.xml.gpx.Gpx;
import de.fub.mapsforge.project.aggregator.xml.Source;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.inference.InferenceModelResultDataSet;
import de.fub.mapsforge.project.detector.model.pipeline.postprocessors.Task;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.Property;
import de.fub.mapsforge.project.models.Aggregator;
import de.fub.mapsforge.project.utils.AggregatorUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map.Entry;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import javax.xml.bind.JAXBException;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
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

    private static final String PROP_NAME_AGGREGATOR_FILE_PATH = "maprenderer.aggregator.file.path";
    private static final String PROP_NAME_OPEN_AFTER_FINISHED = "maprenderer.open.after.finished";
    private FileObject aggregatorFileObject = null;

    public MapRenderer() {
        this(null);
    }

    public MapRenderer(Detector detector) {
        super(detector);
    }

    private boolean isOpenAfterFinished() {
        ProcessDescriptor processDescriptor = getProcessDescriptor();
        if (processDescriptor != null) {
            for (Property property : processDescriptor.getProperties().getPropertyList()) {
                if (PROP_NAME_OPEN_AFTER_FINISHED.equals(property.getId())) {
                    return Boolean.parseBoolean(property.getValue());
                }
            }
        }
        return false;
    }

    private Aggregator createAggregator() {
        ProcessDescriptor processDescriptor = getProcessDescriptor();
        if (processDescriptor != null) {
            for (Property propery : processDescriptor.getProperties().getPropertyList()) {
                if (PROP_NAME_AGGREGATOR_FILE_PATH.equalsIgnoreCase(propery.getId())) {
                    String pathString = propery.getValue();
                    if (pathString != null) {
                        File aggregatorFile = new File(pathString);
                        if (aggregatorFile.exists()) {
                            aggregatorFileObject = FileUtil.toFileObject(aggregatorFile);
                            if (aggregatorFileObject != null) {
                                return AggregatorUtils.createAggregator(aggregatorFileObject);
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    protected void start() {
        Aggregator aggregator = createAggregator();
        if (aggregator != null) {
            InferenceModelResultDataSet resultDataSet = getResultDataSet();
            for (Entry<String, List<Gpx>> entry : resultDataSet.entrySet()) {
                try {
                    aggregator.getDescriptor().setName(entry.getKey());

                    List<Source> sourceList = aggregator.getSourceList();
                    sourceList.clear();

                    FileObject createTempFolder = createTempFolder(URLEncoder.encode(MessageFormat.format("Transportation: {0}", entry.getKey()), "UTF-8"));

                    for (Gpx gpx : entry.getValue()) {
                        File tmpFile = createTmpfile(createTempFolder);
                        try {
                            AggregatorUtils.saveGpxToFile(tmpFile, gpx);
                            sourceList.add(new Source(tmpFile.getAbsolutePath()));
                        } catch (JAXBException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }

                    new OpenEditorAction(aggregator).run();

                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    aggregator = createAggregator();
                }
            }
        }
    }

    private File createTmpfile(FileObject parentFolder) throws IOException {
        File tmpFile = File.createTempFile("tmp", ".gpx", FileUtil.toFile(parentFolder));
        tmpFile.deleteOnExit();
        return tmpFile;
    }

    private FileObject createTempFolder(String name) throws IOException {
        String tmpdir = System.getProperty("java.io.tmpdir");
        if (tmpdir != null) {
            File tmpdirFile = new File(tmpdir);
            if (tmpdirFile.exists()) {
                FileObject fileObject = FileUtil.toFileObject(tmpdirFile);
                if (fileObject.getFileObject(name) == null) {
                    return fileObject.createFolder(name);
                } else {
                    return fileObject.getFileObject(name);
                }
            }
        }
        throw new FileNotFoundException("Couldn't find temp dir!"); // NO18N
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

    private class OpenEditorAction implements Runnable {

        private final Aggregator aggregator;

        public OpenEditorAction(Aggregator aggregator) {
            this.aggregator = aggregator;
        }

        @Override
        public void run() {
            if (isOpenAfterFinished()) {
                try {
                    aggregator.notifyModified();
                    aggregator.start();
                    DataObject dataObject = DataObject.find(aggregatorFileObject);
                    dataObject.getNodeDelegate();
                    OpenCookie openCookie = dataObject.getLookup().lookup(OpenCookie.class);
                    if (openCookie != null) {
                        openCookie.open();

                    }
                } catch (DataObjectNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }
}
