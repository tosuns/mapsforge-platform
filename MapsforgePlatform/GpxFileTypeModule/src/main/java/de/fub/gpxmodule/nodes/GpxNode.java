/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
    private static final String IMAGE_PATH = "de/fub/gpxmodule/gpx.png";
    private static final Image IMAGE = ImageUtilities.loadImage(IMAGE_PATH);
    private static int counter = 0;

    public GpxNode(GPXDataObject dataObject) throws IntrospectionException {
        this(dataObject, Children.create(new GPXChildNodeFactory(dataObject.getGpx()), true));
    }

    public GpxNode(GPXDataObject dataObject, Children chldrn) throws IntrospectionException {
        this(dataObject, chldrn, new ProxyLookup(dataObject.getLookup()));
    }

    public GpxNode(GPXDataObject dataObject, Children chldrn, Lookup lkp) throws IntrospectionException {
        super(dataObject, chldrn, lkp);
    }

    @Override
    public Image getIcon(int type) {
        return IMAGE;
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
