/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories.nodes.datasets;

import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.xmls.DataSet;
import de.fub.mapsforge.project.detector.utils.DetectorUtils;
import de.fub.utilsmodule.icons.IconRegister;
import java.awt.Image;
import java.text.MessageFormat;
import javax.swing.Action;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;

/**
 *
 * @author Serdar
 */
public class DataSetNode extends AbstractNode {

    private final static String HTML_PATTERN = "<html><font color='gray'><i>{0}</i></font></html>";
    private final DataSet dataSet;
    private final Detector detector;
    private boolean fileValied = true;
    private DataObject dataObject;

    public DataSetNode(Detector detector, DataSet dataSet) {
        super(Children.LEAF, Lookup.EMPTY);
        this.detector = detector;
        this.dataSet = dataSet;
        init();
    }

    private void init() {
        if (dataSet.getUrl() != null) {
            FileObject fileObject = DetectorUtils.findFileObject(detector.getDataObject().getPrimaryFile(), dataSet.getUrl());
            if (fileObject == null) {
                fileValied = false;
                fireIconChange();
            } else {
                try {
                    dataObject = DataObject.find(fileObject);
                } catch (DataObjectNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    @Override
    protected Sheet createSheet() {
        return super.createSheet();
    }

    @Override
    public Action[] getActions(boolean context) {
        if (dataObject != null) {
            return dataObject.getNodeDelegate().getActions(context);
        }
        return super.getActions(context);
    }

    @Override
    public String getDisplayName() {
        if (dataSet != null) {
            if (dataObject != null) {
                return dataObject.getNodeDelegate().getDisplayName();
            } else {
                String url = dataSet.getUrl();
                if (url != null) {
                    url = url.replaceAll("\\\\", "/");
                    url = url.substring(url.lastIndexOf("/") + 1);
                }
                return url;
            }
        }
        return super.getDisplayName();
    }

    @Override
    public String getHtmlDisplayName() {
        return MessageFormat.format(HTML_PATTERN, getDisplayName());
    }

    @Override
    public Image getIcon(int type) {
        Image image = super.getIcon(type);
        if (!fileValied) {
            Image errorHint = IconRegister.findRegisteredIcon("errorHintIcon.png");
            if (errorHint != null) {
                image = ImageUtilities.mergeImages(image, errorHint, 0, 0);
            }
        } else {
            image = dataObject.getNodeDelegate().getIcon(type);
        }
        return image;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }
}
