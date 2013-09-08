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
package de.fub.maps.project.detector.factories.inference;

import de.fub.maps.project.detector.factories.nodes.inference.FeatureRootNode;
import de.fub.maps.project.detector.model.Detector;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class InferenceNodeChildFactory extends ChildFactory<Node> {

    private final Detector detector;

    public InferenceNodeChildFactory(Detector detector) {
        this.detector = detector;;
    }

    @Override
    protected boolean createKeys(List<Node> toPopulate) {
        if (detector != null) {
            toPopulate.add(new FeatureRootNode(detector));
        }
        return true;
    }

    @Override
    protected Node createNodeForKey(Node node) {
        return new FilterNode(node);
    }
}
