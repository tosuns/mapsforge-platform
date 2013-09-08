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

import de.fub.gpxmodule.nodes.TrkPointNode;
import de.fub.gpxmodule.xml.Wpt;
import java.beans.IntrospectionException;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Serdar
 */
public class TrkpointNodeFactory extends ChildFactory<Wpt> {

    private final List<Wpt> trkptList;

    public TrkpointNodeFactory(List<Wpt> list) {
        assert list != null;
        this.trkptList = list;
    }

    @Override
    protected boolean createKeys(List<Wpt> list) {
        list.addAll(this.trkptList);
        return true;
    }

    @Override
    protected Node createNodeForKey(Wpt t) {
        Node node = null;
        try {
            node = new TrkPointNode(t);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
        }
        return node;
    }
}
