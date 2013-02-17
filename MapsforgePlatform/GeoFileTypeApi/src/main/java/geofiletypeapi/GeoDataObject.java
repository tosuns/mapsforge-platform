/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package geofiletypeapi;

import java.awt.geom.Rectangle2D;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;

/**
 *
 * @author Serdar
 */
public abstract class GeoDataObject extends MultiDataObject {

    public GeoDataObject(FileObject fo, MultiFileLoader loader) throws DataObjectExistsException {
        super(fo, loader);
    }

    public abstract Rectangle2D getBoundingBox();
}
