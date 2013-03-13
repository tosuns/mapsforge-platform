/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.converter;

import de.fub.agg2graph.input.GPXReader;
import de.fub.agg2graph.structs.GPSTrack;
import java.util.ArrayList;
import java.util.List;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@ServiceProvider(service = DataConverter.class)
public class IdentityConverter implements DataConverter {

    @Override
    public boolean isFileTypeSupported(FileObject fileObject) {
        boolean result = false;

        if (fileObject != null) {
            try {
                DataObject dataObject = DataObject.find(fileObject);
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }


            if ("text/gpx+xml".equals(fileObject.getMIMEType())) {
                result = true;
            } else {
                result = "gpx".equalsIgnoreCase(fileObject.getExt());
            }
        }
        return result;
    }

    @Override
    public synchronized List<GPSTrack> convert(FileObject fileObject) throws DataConverterException {
        List<GPSTrack> gpxtrack = new ArrayList<GPSTrack>();

        if (fileObject != null) {
            gpxtrack = GPXReader.getTracks(FileUtil.toFile(fileObject));
            if (gpxtrack == null) {
                throw new DataConverterException("Failed to convert specified file: " + fileObject.getPath());
            }
        } else {
            throw new DataConverterException("Couldn't convert file because file == null!");
        }
        return gpxtrack;
    }
}
