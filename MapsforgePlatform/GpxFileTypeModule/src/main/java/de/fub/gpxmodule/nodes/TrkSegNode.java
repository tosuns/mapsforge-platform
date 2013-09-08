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

import de.fub.gpxmodule.xml.Trkseg;
import java.beans.IntrospectionException;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.BeanNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Serdar
 */
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
