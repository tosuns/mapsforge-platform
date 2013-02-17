/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graphui.actions;

import de.fub.agg2graphui.AggTopComponent;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
public class LoadAction extends NodeAction {

    private final AggTopComponent component;

    public LoadAction(AggTopComponent component) {
        super(Children.LEAF);
        this.component = component;
    }

    @NbBundle.Messages({"CLT_FileChoose_Title=Load GPX File"})
    @Override
    public void actionPerformed(ActionEvent e) {
        FileChooserBuilder fileChooser = new FileChooserBuilder(LoadAction.class);
        fileChooser.setTitle(Bundle.CLT_FileChoose_Title()).addFileFilter(new FileFilter() {
            @Override
            public boolean accept(File f) {
                FileObject fileObject = FileUtil.toFileObject(f);
                return fileObject != null && "gpx".equalsIgnoreCase(fileObject.getExt());
            }

            @Override
            public String getDescription() {
                return "GPX Files";
            }
        });
        JFileChooser jFileChooser = fileChooser.createFileChooser();
        jFileChooser.setMultiSelectionEnabled(false);
        int returnValue = jFileChooser.showOpenDialog(component);
        if (JFileChooser.APPROVE_OPTION == returnValue) {
            File selectedFile = jFileChooser.getSelectedFile();
            FileObject fileObject = FileUtil.toFileObject(selectedFile);
            try {
                DataObject dataObject = DataObject.find(fileObject);
//                component.getExplorerManager().setRootContext(dataObject.getNodeDelegate());
            } catch (DataObjectNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }
}
