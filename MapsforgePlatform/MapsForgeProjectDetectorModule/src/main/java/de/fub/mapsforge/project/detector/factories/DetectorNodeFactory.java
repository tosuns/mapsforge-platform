/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.factories;

import de.fub.mapsforge.project.detector.filetype.DetectorDataObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.FilterNode;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 *
 * @author Serdar
 */
public class DetectorNodeFactory extends ChildFactory<DetectorDataObject> implements FileChangeListener {

    private final DataObject dataObject;

    public DetectorNodeFactory(DataObject dataObject) {
        assert dataObject != null;
        this.dataObject = dataObject;
        this.dataObject.getPrimaryFile().addFileChangeListener(FileUtil.weakFileChangeListener(DetectorNodeFactory.this, this.dataObject.getPrimaryFile()));
    }

    @Override
    protected boolean createKeys(List<DetectorDataObject> toPopulate) {
        ArrayList<DetectorDataObject> list = new ArrayList<DetectorDataObject>();
        for (FileObject fileObject : dataObject.getPrimaryFile().getChildren()) {
            try {
                if (fileObject.isData() && "dec".equalsIgnoreCase(fileObject.getExt())) {
                    DataObject db = DataObject.find(fileObject);
                    if (db instanceof DetectorDataObject) {
                        list.add((DetectorDataObject) db);
                    }
                }
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        Collections.sort(list, new Comparator<DetectorDataObject>() {
            @Override
            public int compare(DetectorDataObject o1, DetectorDataObject o2) {
                return o1.getName().compareToIgnoreCase(o2.getName());
            }
        });
        toPopulate.addAll(list);
        return true;
    }

    @Override
    protected Node createNodeForKey(DetectorDataObject detectorDataObject) {
        return new FilterNode(detectorDataObject.getNodeDelegate());
    }

    @Override
    public void fileFolderCreated(FileEvent fe) {
        refresh(true);
    }

    @Override
    public void fileDataCreated(FileEvent fe) {
        refresh(true);
    }

    @Override
    public void fileChanged(FileEvent fe) {
        refresh(true);
    }

    @Override
    public void fileDeleted(FileEvent fe) {
        refresh(true);
    }

    @Override
    public void fileRenamed(FileRenameEvent fe) {
        refresh(true);
    }

    @Override
    public void fileAttributeChanged(FileAttributeEvent fe) {
    }
}
