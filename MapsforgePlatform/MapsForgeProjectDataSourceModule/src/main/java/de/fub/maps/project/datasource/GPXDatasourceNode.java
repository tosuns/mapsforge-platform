/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.datasource;

import de.fub.maps.project.MapsProject;
import java.awt.Image;
import java.util.List;
import javax.swing.Action;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.loaders.DataObject;
import org.openide.nodes.FilterNode;
import org.openide.util.ImageUtilities;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Serdar
 */
public class GPXDatasourceNode extends FilterNode {

    @StaticResource
    private static final String DATASOURCE_ICON_PATH = "de/fub/maps/project/datasource/datasourceIcon.png";

    public GPXDatasourceNode(DataObject gpxDataSourceObject, MapsProject project) {
        super(gpxDataSourceObject.getNodeDelegate(),
                new FilterNode.Children(gpxDataSourceObject.getNodeDelegate()),
                new ProxyLookup(
                gpxDataSourceObject.getNodeDelegate().getLookup(),
                Lookups.singleton(project)));
        setDisplayName(MapsDatasourceNodeFactory.DATASOURCE_FILENAME);
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage(DATASOURCE_ICON_PATH);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> actionsForPath = Utilities.actionsForPath("Projects/org-maps-project/GPXDataSource/Actions");
        return actionsForPath.toArray(new Action[actionsForPath.size()]);
    }
}
