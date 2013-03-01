/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories.nodes.datasets;

import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.xmls.DataSet;
import java.text.MessageFormat;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;

/**
 *
 * @author Serdar
 */
public class DataSetNode extends AbstractNode {

    private final static String HTML_PATTERN = "<html><font color='gray'><i>{0}</i></font></html>";
    private final DataSet dataSet;
    private final Detector detector;

    public DataSetNode(Detector detector, DataSet dataSet) {
        super(Children.LEAF, Lookup.EMPTY);
        this.detector = detector;
        this.dataSet = dataSet;
    }

    @Override
    public String getDisplayName() {
        if (dataSet != null) {
            String url = dataSet.getUrl();
            if (url != null) {
                url = url.replaceAll("\\\\", "/");
                url = url.substring(url.lastIndexOf("/") + 1);
            }
            return url;
        }
        return super.getDisplayName();
    }

    @Override
    public String getHtmlDisplayName() {
        return MessageFormat.format(HTML_PATTERN, getDisplayName());
    }
}
