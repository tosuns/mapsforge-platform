/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project;

import de.fub.mapforgeproject.MapsForgeProject;
import de.fub.mapsforge.project.aggregator.factories.nodes.AggregatorFolderNode;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class MapsForgeAggregatorHelper {

    private AggregatorFolderNode aggregatorFolderNode;

    private MapsForgeAggregatorHelper() {
    }

    protected Node createAggregatorFolderNode(DataObject dataObject, MapsForgeProject project) {
        if (aggregatorFolderNode == null) {
            aggregatorFolderNode = new AggregatorFolderNode(dataObject, project);
        }
        return aggregatorFolderNode;
    }

    public Node getAggregatorFolderNode() {
        return aggregatorFolderNode;
    }

    public static MapsForgeAggregatorHelper getInstance() {
        return MapsForgeAggregatorHelperHolder.INSTANCE;
    }

    private static class MapsForgeAggregatorHelperHolder {

        private static final MapsForgeAggregatorHelper INSTANCE = new MapsForgeAggregatorHelper();
    }
}
