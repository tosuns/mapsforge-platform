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
package de.fub.maps.project.aggregator.factories;

import de.fub.maps.project.models.Aggregator;
import java.awt.Image;
import java.util.List;
import org.netbeans.api.annotations.common.StaticResource;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
public class AggregatorSubFolderFactory extends ChildFactory<Node> {

    @StaticResource
    private static final String DATASET_ICON_PATH = "de/fub/maps/project/aggregator/datasetIcon.png";
    @StaticResource
    private static final String PROCESS_ICON_PATH = "de/fub/maps/project/aggregator/processFolderIcon.png";
    private final Aggregator aggregator;

    public AggregatorSubFolderFactory(Aggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    protected boolean createKeys(List<Node> toPopulate) {
        toPopulate.add(new DatasetFolderNode(aggregator));
        toPopulate.add(new ProcessFolderNode(aggregator));
        return true;
    }

    @Override
    protected Node createNodeForKey(Node node) {
        return node;
    }

    private static class DatasetFolderNode extends AbstractNode {

        @NbBundle.Messages({"CLT_DatasetFolderNode_Name=Dataset", "CLT_DatasetFolderNode_Description=List all files that will be proceeded."})
        public DatasetFolderNode(Aggregator agg) {
            super(Children.create(new SourceChildFactory(agg), true));
            setDisplayName(Bundle.CLT_DatasetFolderNode_Name());
            setShortDescription(Bundle.CLT_DatasetFolderNode_Description());
        }

        @Override
        public Image getIcon(int type) {
            return ImageUtilities.loadImage(DATASET_ICON_PATH);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }
    }

    private static class ProcessFolderNode extends AbstractNode {

        @NbBundle.Messages({"CLT_ProcessFolderNode_Name=Process Pipeline", "CLT_ProcessFolderNode_Description=List of all current process which will be proceeded."})
        public ProcessFolderNode(Aggregator agg) {
            super(Children.create(new ProcessNodeFactory(agg), true));
            setDisplayName(Bundle.CLT_ProcessFolderNode_Name());
            setShortDescription(Bundle.CLT_ProcessFolderNode_Description());
        }

        @Override
        public Image getIcon(int type) {
            return ImageUtilities.loadImage(PROCESS_ICON_PATH);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }
    }
}
