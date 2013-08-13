/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.factories.nodes.datasets;

import de.fub.maps.project.detector.model.Detector;
import de.fub.maps.project.detector.model.xmls.DataSet;
import de.fub.utilsmodule.icons.IconRegister;
import java.awt.Image;
import java.io.File;
import java.text.MessageFormat;
import javax.swing.Action;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
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

    private final static String HTML_PATTERN = "<html><font color='808080'><i>&lt;{0}&gt;</i></font></html>";
    private final DataSet dataSet;
    private final Detector detector;
    private boolean fileValid = true;
    private DataObject dataObject;
    private final FileChangeListener fcl = new FileChangeAdapter() {
        @Override
        public void fileDeleted(FileEvent fe) {
            fireIconChange();
        }

        @Override
        public void fileRenamed(FileRenameEvent fe) {
            fireIconChange();
        }
    };

    public DataSetNode(Detector detector, DataSet dataSet) {
        super(Children.LEAF, Lookup.EMPTY);
        this.detector = detector;
        this.dataObject = this.detector.getDataObject();
        this.dataSet = dataSet;
        init();
    }

    private void init() {
        if (dataSet.getUrl() != null) {
            File file = new File(dataSet.getUrl());
            if (file.exists()) {
                try {
                    dataObject = DataObject.find(FileUtil.toFileObject(FileUtil.normalizeFile(file)));
                    dataObject.getPrimaryFile().addFileChangeListener(FileUtil.weakFileChangeListener(fcl, dataObject.getPrimaryFile()));
                    setShortDescription(dataObject.getPrimaryFile().getPath());
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
//        if (dataObject != null) {
//            return dataObject.getNodeDelegate().getActions(context);
//        }
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
        if (!fileValid) {
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
