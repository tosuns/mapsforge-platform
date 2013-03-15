/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.factories.nodes;

import de.fub.mapsforge.project.aggregator.xml.Source;
import de.fub.mapsforge.project.models.Aggregator;
import de.fub.mapsforge.project.models.AggregatorSource;
import java.awt.Image;
import java.io.File;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.List;
import javax.swing.Action;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.FilterNode;
import org.openide.util.Exceptions;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;

/**
 * TODO Validation check and approciated display in error case.
 *
 *
 * @author Serdar
 */
public class SourceNode extends FilterNode {

    private final static String HTML_PATTERN = "<html><font color='808080'><i>&lt;{0}&gt;</i></font></html>";

    public SourceNode(Source source, Aggregator aggregator) throws DataObjectNotFoundException, URISyntaxException {
        this(new AggregatorSource(aggregator, source));
    }

    private SourceNode(AggregatorSource aggregatorSource) {
        super(getDataObject(aggregatorSource).getNodeDelegate(), Children.LEAF, Lookups.fixed(aggregatorSource));
    }

    @Override
    public String getHtmlDisplayName() {
        return MessageFormat.format(HTML_PATTERN, getDisplayName());
    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> actionsForPath = Utilities.actionsForPath("Projects/org-mapsforge-project/Aggregator/Source/Actions");
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

    private static DataObject getDataObject(AggregatorSource aggregatorSource) {
        DataObject dataObject = null;
        Source source = aggregatorSource.getSource();
        File file = new File(source.getUrl());
        FileObject fileObject = FileUtil.toFileObject(file);
        if (fileObject != null) {
            try {
                dataObject = DataObject.find(fileObject);
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return dataObject;
    }
}
