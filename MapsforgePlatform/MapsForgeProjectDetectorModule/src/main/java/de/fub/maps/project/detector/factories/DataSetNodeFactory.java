/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.factories;

import de.fub.maps.project.detector.factories.nodes.datasets.DataSetNode;
import de.fub.maps.project.detector.model.Detector;
import de.fub.maps.project.detector.model.xmls.DataSet;
import java.util.List;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class DataSetNodeFactory extends ChildFactory<DataSet> {

    private final Detector detector;
    private final List<DataSet> datasetList;

    public DataSetNodeFactory(Detector detector, List<DataSet> datasetList) {
        this.detector = detector;
        this.datasetList = datasetList;
    }

    @Override
    protected boolean createKeys(List<DataSet> toPopulate) {
        toPopulate.addAll(datasetList);
        return true;
    }

    @Override
    protected Node createNodeForKey(DataSet deteset) {
        return new DataSetNode(detector, deteset);
    }
}
