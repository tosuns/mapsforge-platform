/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geofiletypeapi;

import java.awt.geom.Rectangle2D;
import java.io.File;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.util.Exceptions;

/**
 *
 * @author Serdar
 */
public class GeoUtil {

    /**
     * A Helper method that returns the bounding box of file that contains geo
     * data.
     *
     * @param geoDataFile file that has an associated DataObject of type
     * GeoDataObject
     * @return The bounding box for the geo data file. If the file does not have
     * an associated DataObject of type GeoDataObject then null will be
     * returned.
     */
    public static Rectangle2D getBoundingBox(File geoDataFile) {
        Rectangle2D boundingBox = null;
        if (geoDataFile != null && geoDataFile.exists()) {
            FileObject fileObject = FileUtil.toFileObject(geoDataFile);
            if (fileObject != null) {
                try {
                    DataObject dataObject = DataObject.find(fileObject);
                    if (dataObject instanceof GeoDataObject) {
                        GeoDataObject geoDataObject = (GeoDataObject) dataObject;
                        boundingBox = geoDataObject.getBoundingBox();
                    }
                } catch (DataObjectNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return boundingBox;
    }
}
