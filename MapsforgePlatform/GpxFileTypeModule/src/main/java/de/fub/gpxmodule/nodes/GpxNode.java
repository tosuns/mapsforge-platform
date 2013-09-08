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
package de.fub.gpxmodule.nodes;

import de.fub.gpxmodule.GPXDataObject;
import de.fub.gpxmodule.factories.GPXChildNodeFactory;
import de.fub.gpxmodule.service.GPXProvider;
import de.fub.gpxmodule.xml.Gpx;
import java.awt.Image;
import java.beans.IntrospectionException;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.loaders.DataNode;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Serdar
 */
public class GpxNode extends DataNode {

    @StaticResource
    private static final String IMAGE_PATH_GPX_1_0 = "de/fub/gpxmodule/gpx.png";
    @StaticResource
    private static final String IMAGE_PATH_GPX_1_1 = "de/fub/gpxmodule/gpx_1_1.png";
    private final GPXDataObject gpxDataObject;

    public GpxNode(GPXDataObject dataObject) throws IntrospectionException {
        this(dataObject, Children.create(new GPXChildNodeFactory(dataObject), true));
    }

    public GpxNode(GPXDataObject dataObject, Children chldrn) throws IntrospectionException {
        this(dataObject, chldrn, new ProxyLookup(dataObject.getLookup()));
    }

    public GpxNode(GPXDataObject dataObject, Children chldrn, Lookup lkp) throws IntrospectionException {
        super(dataObject, chldrn, lkp);
        this.gpxDataObject = dataObject;
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage(
                gpxDataObject.getGpxVersion() == GPXDataObject.GpxVersion.GPX_1_0
                ? IMAGE_PATH_GPX_1_0
                : IMAGE_PATH_GPX_1_1);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        GPXProvider gpxProvider = getLookup().lookup(GPXProvider.class);
        Gpx gpx = gpxProvider.getGpx();
        if (gpx != null) {
            try {

                Node node = new BeanNode<Gpx>(gpx);
                sheet = Sheet.createDefault();

                for (PropertySet propertySet : node.getPropertySets()) {

                    Sheet.Set set = Sheet.createPropertiesSet();

                    set.setName(propertySet.getName());
                    set.setDisplayName(propertySet.getDisplayName());
                    set.setShortDescription(propertySet.getShortDescription());

                    sheet.put(set);
                    for (Property<?> property : propertySet.getProperties()) {
                        set.put(property);
                    }

                }
            } catch (IntrospectionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return sheet;
    }
}
