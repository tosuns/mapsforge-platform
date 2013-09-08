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
package de.fub.maps.project;

import de.fub.maps.project.aggregator.factories.nodes.AggregatorFolderNode;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class MapsAggregatorHelper {

    private AggregatorFolderNode aggregatorFolderNode;

    private MapsAggregatorHelper() {
    }

    protected Node createAggregatorFolderNode(DataObject dataObject, MapsProject project) {
        if (aggregatorFolderNode == null) {
            aggregatorFolderNode = new AggregatorFolderNode(dataObject, project);
        }
        return aggregatorFolderNode;
    }

    public Node getAggregatorFolderNode() {
        return aggregatorFolderNode;
    }

    public static MapsAggregatorHelper getInstance() {
        return MapsAggregatorHelperHolder.INSTANCE;
    }

    private static class MapsAggregatorHelperHolder {

        private static final MapsAggregatorHelper INSTANCE = new MapsAggregatorHelper();
    }
}
