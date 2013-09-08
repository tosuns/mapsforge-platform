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

import de.fub.maps.project.aggregator.filetype.AggregatorDataObject;
import java.util.ArrayList;
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
                if (fileObject.isData() && "agg".equalsIgnoreCase(fileObject.getExt())) {
                    DataObject db = DataObject.find(fileObject);
                    if (db instanceof AggregatorDataObject) {
                        list.add((AggregatorDataObject) db);
                    }
                }
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        toPopulate.addAll(list);
        return true;
    }

    @Override
    protected Node createNodeForKey(AggregatorDataObject aggregatorDataObject) {
        return new FilterNode(aggregatorDataObject.getNodeDelegate());
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
