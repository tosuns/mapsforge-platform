/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.converter;

import de.fub.gpxmodule.xml.gpx.Gpx;
import java.util.List;
import org.openide.filesystems.FileObject;

/**
 *
 * @author Serdar
 */
public interface DataConverter {

    /**
     * Indicated whether this converter is able to convert the specified
     * fileObject to a GPSTrack instance. The method should check at first the
     * mime type of the fileObject and as a fall back the file extention.
     *
     * @param fileObject
     * @return true if this DataConverter is able to convert the specified
     * fileObject to a GPX object, otherwise false.
     */
    public boolean isFileTypeSupported(FileObject fileObject);

    /**
     * Converts the specified fileObject to a List<GPSTrack> instance.
     *
     * @param fileObject - that will be read and converted to anGPSTrack
     * instance.
     * @return List<GPSTrack> instance
     * @throws
     * de.fub.mapsforge.project.detector.model.converter.DataConverter.DataConverterException
     */
    public List<Gpx> convert(FileObject fileObject) throws DataConverterException;

    public static class DataConverterException extends Exception {

        private static final long serialVersionUID = 1L;

        public DataConverterException() {
        }

        public DataConverterException(String message) {
            super(message);
        }

        public DataConverterException(String message, Throwable cause) {
            super(message, cause);
        }

        public DataConverterException(Throwable cause) {
            super(cause);
        }

        public DataConverterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
