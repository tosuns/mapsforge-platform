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
package de.fub.gpxmodule.factories;

import de.fub.gpxmodule.GPXDataObject;
import de.fub.gpxmodule.nodes.RteNode;
import de.fub.gpxmodule.nodes.TrkNode;
import de.fub.gpxmodule.xml.Gpx;
import de.fub.gpxmodule.xml.Rte;
import de.fub.gpxmodule.xml.Trk;
import de.fub.gpxmodule.xml.Wpt;
import java.awt.Image;
import java.beans.IntrospectionException;
import java.util.List;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileUtil;
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

    private final GPXDataObject dataObject;
    private final FileChangeListener fcl = new FileChangeAdapter() {
        @Override
        public void fileChanged(FileEvent fe) {
            refresh(true);
        }
    };

    public GPXChildNodeFactory(GPXDataObject dataObject) {
        assert dataObject != null;
        this.dataObject = dataObject;
        this.dataObject.getPrimaryFile().addFileChangeListener(FileUtil.weakFileChangeListener(fcl, this.dataObject.getPrimaryFile()));
    }

    @Override
    protected boolean createKeys(List<Node> list) {
        Gpx gpx = dataObject.getGpx();
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
