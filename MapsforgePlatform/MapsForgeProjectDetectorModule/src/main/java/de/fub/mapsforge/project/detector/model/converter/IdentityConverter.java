/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.converter;

import de.fub.gpxmodule.GPXDataObject;
import de.fub.gpxmodule.xml.gpx.Gpx;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openide.filesystems.FileObject;
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
    public synchronized List<Gpx> convert(FileObject fileObject) throws DataConverterException {
        List<Gpx> gpxList = new ArrayList<Gpx>();
        Gpx gpx = null;
        if (fileObject != null) {
            try {
                DataObject dataObject = DataObject.find(fileObject);
                if (dataObject instanceof GPXDataObject) {
                    gpx = ((GPXDataObject) dataObject).getGpx();

                }
                if (gpx == null) {
                    throw new DataConverterException("Failed to convert specified file: " + fileObject.getPath());
                } else {
                    gpxList = Arrays.asList(gpx);
                }
            } catch (DataObjectNotFoundException ex) {
                throw new DataConverterException("Couldn't convert file to gpx because !");
            }
        } else {
            throw new DataConverterException("Couldn't convert file gpx because file == null!");
        }
        return gpxList;
    }
}
