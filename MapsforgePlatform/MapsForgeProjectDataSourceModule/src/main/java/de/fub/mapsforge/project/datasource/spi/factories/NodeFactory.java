/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.datasource.spi.factories;

import de.fub.mapsforge.project.datasource.spi.TrksegWrapper;
import de.fub.mapsforge.project.datasource.spi.nodes.TrackSegmentNode;
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
