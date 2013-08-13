/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.wizards.detector.actions;

import de.fub.maps.project.detector.ui.FolderExplorer;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Detector",
        id = "de.fub.maps.project.detector.wizards.detector.actions.AddDatasetAction")
@ActionRegistration(
        displayName = "#CTL_AddDatasetAction")
@ActionReferences({
    @ActionReference(
            path = "maps/Detector/Wizard/Traningsset/Transportmode/Actions",
            position = 0),
    @ActionReference(
            path = "maps/Detector/Wizard/Inferenceset/Actions",
            position = 0)})
@Messages("CTL_AddDatasetAction=Add Dataset ...")
public final class AddDatasetAction implements ActionListener {

    private final Node context;
    private FolderExplorer folderExplorer;

    public AddDatasetAction(Node context) {
        this.context = context;
    }

    @SuppressWarnings("unchecked")
    @Override
    public void actionPerformed(ActionEvent ev) {
        folderExplorer = new FolderExplorer();
        DialogDescriptor dd = new DialogDescriptor(folderExplorer, "Datasources");
        DialogDisplayer.getDefault().createDialog(dd).setVisible(true);
        if (DialogDescriptor.OK_OPTION.equals(dd.getValue())) {
            Node[] selectedNodes = folderExplorer.getSelectedFiles();

            List<DataObject> observableList = context.getLookup().lookup(List.class);
            if (observableList != null) {

                for (Node node : selectedNodes) {
                    DataObject dataObject = node.getLookup().lookup(DataObject.class);
                    observableList.addAll(collectData(dataObject));
                }
            }
        }
    }

    private List<DataObject> collectData(DataObject dataObject) {
        List<DataObject> result = new LinkedList<DataObject>();
        if (dataObject.getPrimaryFile().isFolder()) {
            for (FileObject fileObject : dataObject.getPrimaryFile().getChildren()) {
                try {
                    result.addAll(collectData(DataObject.find(fileObject)));
                } catch (DataObjectNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        } else if (dataObject.getPrimaryFile().isData()) {
            result.add(dataObject);
        }
        return result;
    }
}
