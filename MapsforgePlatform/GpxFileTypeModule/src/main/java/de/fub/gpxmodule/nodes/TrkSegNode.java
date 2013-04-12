/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.gpxmodule.nodes;

import de.fub.gpxmodule.xml.gpx.Trkseg;
import java.beans.IntrospectionException;
import java.util.List;
import javax.swing.Action;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Serdar
 */
@ActionReferences(
        @ActionReference(id =
        @ActionID(
        category = "GPX",
        id = "de.fub.gpxmodule.actions.StatisticsAction"),
        path = "Projects/Mapsforge/Module/GpxFile/Trknode/Actions"))
public class TrkSegNode extends BeanNode<Trkseg> {

    public TrkSegNode(Trkseg t) throws IntrospectionException {
        this(t, Children.LEAF);
    }

    public TrkSegNode(Trkseg t, Children chldrn) throws IntrospectionException {
        this(t, chldrn, Lookups.singleton(t));
    }

    public TrkSegNode(Trkseg t, Children chldrn, Lookup lkp) throws IntrospectionException {
        super(t, chldrn, lkp);
    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> actionsForPath = Utilities.actionsForPath("Projects/Mapsforge/Module/GpxFile/Trknode/Actions");
        return actionsForPath.toArray(new Action[actionsForPath.size()]);
    }
}
