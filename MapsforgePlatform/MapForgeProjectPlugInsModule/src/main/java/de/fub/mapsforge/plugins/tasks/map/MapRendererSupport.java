/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.tasks.map;

import static de.fub.mapsforge.plugins.tasks.map.MapRenderer.PROP_NAME_AGGREGATOR_FILE_PATH;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.Property;
import de.fub.mapsforge.project.models.Aggregator;
import de.fub.mapsforge.project.utils.AggregatorUtils;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;

/**
 * A helper class that is responsible for the creation of Aggregator template
 * objects and temp files.
 *
 * @author Serdar
 */
final class MapRendererSupport {

    private final MapRenderer mapRenderer;
    private FileObject aggregatorFileObject;

    public MapRendererSupport(MapRenderer mapRenderer) {
        this.mapRenderer = mapRenderer;
    }

    Aggregator createAggregator() throws FileNotFoundException {
        ProcessDescriptor processDescriptor = mapRenderer.getProcessDescriptor();
        if (processDescriptor != null) {
            for (Property propery : processDescriptor.getProperties().getPropertyList()) {
                if (PROP_NAME_AGGREGATOR_FILE_PATH.equalsIgnoreCase(propery.getId())) {
                    String pathString = propery.getValue();
                    if (pathString != null) {
                        FileObject aggregatorFile = FileUtil.getConfigFile(pathString);
                        if (aggregatorFile.isValid()) {
                            File createAggregatorCopy = createAggregatorCopy(aggregatorFile);
                            if (createAggregatorCopy != null) {
                                aggregatorFileObject = FileUtil.toFileObject(createAggregatorCopy);

                                if (aggregatorFileObject != null) {
                                    return AggregatorUtils.createAggregator(aggregatorFileObject);
                                }
                            } else {
                                propery.setValue(null);
                                throw new FileNotFoundException(MessageFormat.format("aggregator {0} does not exist!", aggregatorFile.getPath()));
                            }
                        } else {
                            propery.setValue(null);
                            throw new FileNotFoundException(MessageFormat.format("aggregator {0} does not exist!", aggregatorFile.getPath()));
                        }
                    }
                }
            }
        }
        return null;
    }

    private File createAggregatorCopy(FileObject fileObject) {
        File copyFileObject = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            copyFileObject = File.createTempFile("tmp", ".agg");
            inputStream = fileObject.getInputStream();
            outputStream = new FileOutputStream(copyFileObject);
            FileUtil.copy(inputStream, outputStream);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return copyFileObject;
    }

    File createTmpfile(FileObject parentFolder) throws IOException {
        File tmpFile = File.createTempFile("tmp", ".gpx", FileUtil.toFile(parentFolder));
        tmpFile.deleteOnExit();
        return tmpFile;
    }

    FileObject createTempFolder(String name) throws IOException {
        String tmpdir = System.getProperty("java.io.tmpdir");
        if (tmpdir != null) {
            File tmpdirFile = new File(tmpdir);
            if (tmpdirFile.exists()) {
                FileObject fileObject = FileUtil.toFileObject(tmpdirFile);
                if (fileObject.getFileObject(hashCode() + name) == null) {
                    return fileObject.createFolder(hashCode() + name);
                } else {
                    return fileObject.getFileObject(hashCode() + name);
                }
            }
        }
        throw new FileNotFoundException("Couldn't find temp dir!"); // NO18N
    }
}
