/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
