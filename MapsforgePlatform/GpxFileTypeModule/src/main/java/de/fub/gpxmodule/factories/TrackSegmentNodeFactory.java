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

import de.fub.gpxmodule.nodes.TrkSegNode;
import de.fub.gpxmodule.xml.Trkseg;
import java.beans.IntrospectionException;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Serdar
 */
public class TrackSegmentNodeFactory extends ChildFactory<Trkseg> {

    private final List<Trkseg> trkSegList;

    public TrackSegmentNodeFactory(List<Trkseg> list) {
        assert list != null;
        this.trkSegList = list;
    }

    @Override
    protected boolean createKeys(List<Trkseg> list) {
        list.addAll(this.trkSegList);
        return true;
    }

    @Override
    protected Node createNodeForKey(Trkseg trkseg) {
        Node node = null;
        try {
            node = new TrkSegNode(trkseg, trkseg.getTrkpt() == null || trkseg.getTrkpt().isEmpty() ? Children.LEAF : Children.create(new TrkpointNodeFactory(trkseg.getTrkpt()), true));
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }

        return node;
    }
}
