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

import de.fub.maps.project.detector.factories.nodes.datasets.TransportModeNode;
import de.fub.maps.project.detector.model.Detector;
import de.fub.maps.project.detector.model.xmls.TrainingSet;
import de.fub.maps.project.detector.model.xmls.TransportMode;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class TrainingsDataNodeFactory extends ChildFactory<TransportMode> {

    private final Detector detector;

    public TrainingsDataNodeFactory(Detector detector) {
        this.detector = detector;
    }

    @Override
    protected boolean createKeys(List<TransportMode> toPopulate) {
        TrainingSet trainingsSet = detector.getDetectorDescriptor().getDatasets().getTrainingSet();

        if (trainingsSet != null) {
            for (TransportMode transportMode : trainingsSet.getTransportModeList()) {
                if (transportMode.getName() != null) {
                    toPopulate.add(transportMode);
                }
            }
        }

        Collections.sort(toPopulate, new Comparator<TransportMode>() {
            @Override
            public int compare(TransportMode o1, TransportMode o2) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        });

        return true;
    }

    @Override
    protected Node createNodeForKey(TransportMode transportMode) {
        return new TransportModeNode(detector, transportMode);
    }
}
