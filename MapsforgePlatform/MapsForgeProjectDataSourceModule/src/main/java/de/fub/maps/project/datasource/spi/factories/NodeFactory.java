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
package de.fub.maps.project.datasource.spi.factories;

import de.fub.maps.project.datasource.spi.TrksegWrapper;
import de.fub.maps.project.datasource.spi.nodes.TrackSegmentNode;
import de.fub.utilsmodule.Collections.ObservableList;
import java.util.List;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;
import org.openide.util.WeakListeners;

/**
 *
 * @author Serdar
 */
public class NodeFactory extends ChildFactory<TrksegWrapper> implements ChangeListener {

    private final ObservableList<TrksegWrapper> trackSegments;

    public NodeFactory(ObservableList<TrksegWrapper> trackSegments) {
        this.trackSegments = trackSegments;
        this.trackSegments.addChangeListener(WeakListeners.change(NodeFactory.this, trackSegments));
    }

    @Override
    protected boolean createKeys(List<TrksegWrapper> toPopulate) {
        toPopulate.addAll(trackSegments);
        return true;
    }

    @Override
    protected Node createNodeForKey(TrksegWrapper trkseg) {
        return new TrackSegmentNode(trkseg);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        refresh(true);
    }
}
