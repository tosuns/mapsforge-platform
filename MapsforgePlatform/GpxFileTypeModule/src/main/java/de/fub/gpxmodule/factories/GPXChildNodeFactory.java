/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.gpxmodule.factories;

import de.fub.gpxmodule.nodes.RteNode;
import de.fub.gpxmodule.nodes.TrkNode;
import de.fub.gpxmodule.xml.gpx.Gpx;
import de.fub.gpxmodule.xml.gpx.Rte;
import de.fub.gpxmodule.xml.gpx.Trk;
import de.fub.gpxmodule.xml.gpx.Wpt;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.util.List;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.BeanNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Serdar
 */
public class GPXChildNodeFactory extends ChildFactory<Node> {

    private final Gpx gpx;

    public GPXChildNodeFactory(Gpx gpx) {
        assert gpx != null;
        this.gpx = gpx;
    }

    @Override
    protected boolean createKeys(List<Node> list) {

        if (gpx.getTrk() != null) {
            for (Trk trk : gpx.getTrk()) {
                try {
                    list.add(
                            new TrkNode(
                            trk,
                            ((trk.getTrkseg() == null
                            || trk.getTrkseg().isEmpty())
                            ? Children.LEAF : Children.create(new TrackSegmentNodeFactory(trk.getTrkseg()), true)),
                            Lookup.EMPTY));
                } catch (IntrospectionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }

        if (gpx.getWpt() != null) {
            list.add(new WptRootNode(gpx.getWpt()));
        }

        if (gpx.getRte() != null) {
            for (Rte rte : gpx.getRte()) {
                try {
                    list.add(new RteNode(
                            rte,
                            rte.getRtept() == null
                            || rte.getRtept().isEmpty()
                            ? Children.LEAF
                            : Children.create(new WptNodeFactory(rte.getRtept()), true)));
                } catch (IntrospectionException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }

        return true;
    }

    @Override
    protected Node createNodeForKey(Node t) {
        return t;
    }

    private static class WptRootNode extends AbstractNode {

        private static final Node NODE = createEmptyNode();

        public WptRootNode(List<Wpt> wpts) {
            super(Children.create(new WptNodeFactory(wpts), true));
            setDisplayName("Waypoints"); // NO18N

        }

        private static Node createEmptyNode() {
            Node node = null;
            try {
                node = new BeanNode<Object>(new Object());
            } catch (IntrospectionException ex) {
                node = new AbstractNode(Children.LEAF);
            }
            return node;
        }

        @Override
        public Image getIcon(int type) {
            return NODE.getIcon(type);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return NODE.getIcon(type);
        }
    }
}
