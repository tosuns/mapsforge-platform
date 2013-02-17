/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.factories;

import de.fub.mapsforge.project.aggregator.filetype.AggregatorDataObject;
import java.util.ArrayList;
import java.util.List;
import org.openide.filesystems.FileAttributeEvent;
import org.openide.filesystems.FileChangeListener;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileRenameEvent;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.nodes.ChildFactory;
import org.openide.nodes.Node;

/**
 *
 * @author Serdar
 */
public class AggregatorNodeFactory extends ChildFactory<AggregatorDataObject> implements FileChangeListener {

    private final DataObject dataObject;

    public AggregatorNodeFactory(DataObject dataObject) {
        assert dataObject != null;
        this.dataObject = dataObject;
        this.dataObject.getPrimaryFile().addFileChangeListener(FileUtil.weakFileChangeListener(AggregatorNodeFactory.this, this.dataObject.getPrimaryFile()));
    }

    @Override
    protected boolean createKeys(List<AggregatorDataObject> toPopulate) {
        ArrayList<AggregatorDataObject> list = new ArrayList<AggregatorDataObject>();
        for (FileObject fileObject : dataObject.getPrimaryFile().getChildren()) {
            try {
                if ("agg".equalsIgnoreCase(fileObject.getExt())) {
                    DataObject db = DataObject.find(fileObject);
                    if (db instanceof AggregatorDataObject) {
                        list.add((AggregatorDataObject) db);
                    }
                }
            } catch (Exception ex) {
            }
        }
        toPopulate.addAll(list);
        return true;
    }

    @Override
    protected Node createNodeForKey(AggregatorDataObject aggregatorDataObject) {
        return aggregatorDataObject.getNodeDelegate();
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
