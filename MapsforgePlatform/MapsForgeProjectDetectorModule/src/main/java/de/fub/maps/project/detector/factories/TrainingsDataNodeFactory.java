/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
