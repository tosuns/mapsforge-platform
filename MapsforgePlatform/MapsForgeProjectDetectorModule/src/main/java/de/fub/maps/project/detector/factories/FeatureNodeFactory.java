/*
 * Copyright (C) 2013 Serdar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fub.maps.project.detector.factories;

import de.fub.maps.project.detector.model.inference.features.FeatureProcess;
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
