/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.snapshot.api;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.filesystems.FileChooserBuilder;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
public abstract class AbstractComponentSnapShotExporter implements ComponentSnapShotExporter {

    @Override
    public int compareTo(ComponentSnapShotExporter o) {
        return getName().compareToIgnoreCase(o.getName());
    }

    protected File showFileChoose(String fileExtension) {
        File selectedFile = null;
        JFileChooser fileChooser = new FileChooserBuilder(ComponentSnapShotExporter.class)
                .addFileFilter(new FileFilterImpl(fileExtension))
                .setSelectionApprover(new SelectionApproverImpl()).createFileChooser();
        int response = fileChooser.showSaveDialog(null);
        if (JFileChooser.APPROVE_OPTION == response) {
            selectedFile = fileChooser.getSelectedFile();
            if (selectedFile != null) {
                if (!selectedFile.getName().endsWith(MessageFormat.format(".{0}", fileExtension))) {
                    selectedFile = new File(MessageFormat.format("{0}.{1}", selectedFile.getAbsolutePath(), fileExtension));
                }
                try {
                    if (!selectedFile.exists()) {
                        selectedFile.createNewFile();
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return selectedFile;
    }

    private static class FileFilterImpl extends FileFilter {

        private final String fileExt;

        public FileFilterImpl(String fileExtension) {
            this.fileExt = fileExtension;
        }

        @Override
        public boolean accept(File file) {
            FileObject fileObject = FileUtil.toFileObject(file);
            if (fileObject != null) {
                return fileObject.isFolder() || fileObject.isData() && fileExt.equalsIgnoreCase(fileObject.getExt());
            } else {
                return file.isDirectory() || fileExt.equalsIgnoreCase(file.getName().substring(file.getName().lastIndexOf(".") + 1));
            }
        }

        @Override
        public String getDescription() {
            return MessageFormat.format("*.{0}", fileExt);
        }
    }

    @NbBundle.Messages({
        "# {0} - filename",
        "CLT_Approve_Message=Do you want to overwite the file {0}"
    })
    private static class SelectionApproverImpl implements FileChooserBuilder.SelectionApprover {

        public SelectionApproverImpl() {
        }

        @Override
        public boolean approve(File[] selection) {
            boolean result = true;
            for (File file : selection) {
                if (file.exists()) {
                    NotifyDescriptor.Confirmation nd = new NotifyDescriptor.Confirmation(Bundle.CLT_Approve_Message(file.getName()));
                    Object notify = DialogDisplayer.getDefault().notify(nd);
                    if (!NotifyDescriptor.Confirmation.OK_OPTION.equals(notify)) {
                        result = false;
                    }
                }
            }
            return result;
        }
    }
}
