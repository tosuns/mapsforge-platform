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
package de.fub.maps.project.aggregator.factories.nodes;

import de.fub.maps.project.models.AggregatorSource;
import java.awt.Image;
import java.text.MessageFormat;
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 * TODO Validation check and appropriated display in error case.
 *
 *
 * @author Serdar
 */
public class SourceNode extends FilterNode {

    private final static String HTML_PATTERN = "<html><font color='808080'><i>&lt;{0}&gt;</i></font></html>";

    public SourceNode(Node node, AggregatorSource aggregatorSource) {
        super(node, Children.LEAF, Lookups.fixed(aggregatorSource));
    }

    @Override
    public String getHtmlDisplayName() {
        return MessageFormat.format(HTML_PATTERN, getDisplayName());
    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> actionsForPath = Utilities.actionsForPath("Projects/org-maps-project/Aggregator/Source/Actions");
        return actionsForPath.toArray(new Action[actionsForPath.size()]);
    }

    @Override
    public Image getIcon(int type) {
        return getOriginal().getIcon(type);
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }
}
