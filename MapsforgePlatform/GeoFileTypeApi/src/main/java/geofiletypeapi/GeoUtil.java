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
