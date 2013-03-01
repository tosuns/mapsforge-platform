/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories;

import de.fub.mapsforge.project.detector.factories.nodes.datasets.TransportModeNode;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.xmls.DataSet;
import de.fub.mapsforge.project.detector.model.xmls.TrainingsSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class TrainingsDataNodeFactory extends ChildFactory<List<DataSet>> {

    private final Detector detector;

    public TrainingsDataNodeFactory(Detector detector) {
        this.detector = detector;
    }

    @Override
    protected boolean createKeys(List<List<DataSet>> toPopulate) {
        HashMap<String, List<DataSet>> map = new HashMap<String, List<DataSet>>();
        TrainingsSet trainingsSet = detector.getDetectorDescriptor().getDatasets().getTrainingsSet();

        if (trainingsSet != null) {
            for (DataSet dataSet : trainingsSet.getDataset()) {
                if (dataSet.getTransportmode() != null) {
                    if (!map.containsKey(dataSet.getTransportmode())) {
                        map.put(dataSet.getTransportmode(), new ArrayList<DataSet>());
                    }
                    map.get(dataSet.getTransportmode()).add(dataSet);
                }
            }
        }

        ArrayList<String> keyList = new ArrayList<String>(map.keySet());

        Collections.sort(keyList);

        for (String transportMode : keyList) {
            toPopulate.add(map.get(transportMode));
        }

        return true;
    }

    @Override
    protected Node createNodeForKey(List<DataSet> key) {
        return new TransportModeNode(detector, key);
    }
}
