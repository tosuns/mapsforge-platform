/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.snapshot.api;

import de.fub.utilsmodule.beans.BeanUtil;
import java.awt.Component;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
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

    protected Component copyComponent3(Component component) {
        Component componentCopy = null;
        componentCopy = BeanUtil.copyBean(component);
        return componentCopy;
    }

    protected Component copyComponent2(Component component) {

        Component copy = null;
        if (component != null) {
            ObjectOutputStream out = null;
            ByteArrayOutputStream bos = null;
            ByteArrayInputStream bis = null;
            ObjectInputStream in = null;
            try {

                bos = new ByteArrayOutputStream();

                out = new ObjectOutputStream(bos);

                out.writeObject(component);
                out.flush();
                out.close();

                bis = new ByteArrayInputStream(bos.toByteArray());
                in = new ObjectInputStream(bis);
                Object readObject = in.readObject();
                if (readObject instanceof Component) {
                    copy = (Component) readObject;
                }
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ClassNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                try {
                    if (bos != null) {
                        bos.close();
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                try {
                    if (bis != null) {
                        bis.close();
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return copy;
    }

    protected Component copyComponent(Component component) {

        Component copy = null;
        if (component != null) {
            ObjectOutputStream out = null;
            ByteArrayOutputStream bos = null;
            ByteArrayInputStream bis = null;
            ObjectInputStream in = null;
            try {
                XMLEncoder ee = new XMLEncoder(System.out);
                ee.writeObject(component);
                ee.close();
                bos = new ByteArrayOutputStream();
                XMLEncoder e = new XMLEncoder(bos);
                e.writeObject(component);
                e.close();


                bis = new ByteArrayInputStream(bos.toByteArray());
                XMLDecoder d = new XMLDecoder(bis);
                Object readObject = d.readObject();

                if (readObject instanceof Component) {
                    copy = (Component) readObject;
                }
            } finally {
                try {
                    if (bos != null) {
                        bos.close();
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                try {
                    if (bis != null) {
                        bis.close();
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return copy;
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
