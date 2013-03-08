/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories;

import de.fub.mapsforge.project.detector.model.inference.features.FeatureProcess;
import de.fub.utilsmodule.Collections.ObservableArrayList;
import de.fub.utilsmodule.Collections.ObservableList;
import java.util.List;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.WeakListeners;

/**
 *
 * @author Serdar
 */
public class FeatureNodeFactory extends ChildFactory<FeatureProcess> implements ChangeListener {

    private final List<FeatureProcess> featureList;

    public FeatureNodeFactory(ObservableList<FeatureProcess> featureList) {
        assert featureList != null;
        this.featureList = featureList;
        featureList.addChangeListener(WeakListeners.change(FeatureNodeFactory.this, featureList));

    }

    public FeatureNodeFactory() {
        this(new ObservableArrayList<FeatureProcess>(Lookup.getDefault().lookupResult(FeatureProcess.class).allInstances()));
    }

    @Override
    protected boolean createKeys(List<FeatureProcess> toPopulate) {
        toPopulate.addAll(featureList);
        return true;
    }

    @Override
    protected Node createNodeForKey(FeatureProcess feature) {
        return new FeatureFilterNode(feature.getNodeDelegate());
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        refresh(true);
    }

    private static class FeatureFilterNode extends FilterNode {

        public FeatureFilterNode(Node original) {
            super(original);
        }

        @Override
        public Action[] getActions(boolean context) {
            return new Action[0];
        }
    }
}
