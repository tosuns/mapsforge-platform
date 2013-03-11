/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.gpxmodule.actions;

import de.fub.gpxmodule.GPXDataObject;
import de.fub.gpxmodule.actions.ui.MergeForm;
import de.fub.gpxmodule.xml.Gpx;
import java.awt.Dialog;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.logging.Logger;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.progress.ProgressHandle;
import org.netbeans.api.progress.ProgressHandleFactory;
import org.openide.DialogDescriptor;
import org.openide.DialogDisplayer;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileAlreadyLockedException;
import org.openide.filesystems.FileObject;
import org.openide.util.ContextAwareAction;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;

@ActionID(
        category = "GPX",
        id = "de.fub.mapforgeproject.actions.GpxMergeAction")
@ActionRegistration(
        //    iconBase = "de/fub/gpxmodule/icons/gpxmerge.png",
        displayName = "#CTL_GpxMergeAction", lazy = false)
@ActionReferences({
    @ActionReference(path = "Menu/GPX", position = 0),
    @ActionReference(path = "Toolbars/GPX", position = 0),
    @ActionReference(path = "Loaders/text/gpx+xml/Actions", position = 210, separatorAfter = 250)
})
@Messages({"CTL_GpxMergeAction=Merge Gpx",
    "CLT_Merge_Filename_Dialog_Message=Please specify a filename for the to be merged gpx file.",
    "CLT_Merge_Filename_Dialog_Title=Gpx Merge Dialog",
    "CLT_Merge_Procedure=Merging..."
})
public final class GpxMergeAction extends AbstractAction implements ContextAwareAction, LookupListener {

    private static final long serialVersionUID = 1L;
    private final Lookup context;
    private Lookup.Result<GPXDataObject> result;

    public GpxMergeAction() {
        this(Utilities.actionsGlobalContext());
        result = Utilities.actionsGlobalContext().lookupResult(GPXDataObject.class);
        result.addLookupListener(WeakListeners.create(LookupListener.class, GpxMergeAction.this, result));
    }

    public GpxMergeAction(Lookup actionContext) {
        super(Bundle.CTL_GpxMergeAction());
        putValue("iconBase", "de/fub/gpxmodule/icons/gpxmerge.png");
        this.context = actionContext;
        setEnabled(this.context.lookupResult(GPXDataObject.class).allInstances().size() > 1);
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        final Collection<? extends GPXDataObject> allInstances = this.context.lookupResult(GPXDataObject.class).allInstances();

        if (!allInstances.isEmpty()) {

            FileObject parentFolder = allInstances.iterator().next().getPrimaryFile().getParent();
            MergeForm mergeForm = new MergeForm(parentFolder);
            DialogDescriptor descriptor = mergeForm.getDescriptor();
            Dialog dialog = DialogDisplayer.getDefault().createDialog(descriptor);
            dialog.setVisible(true);

            if (mergeForm.checkIfValid()) {
                synchronized (this) { // synchronization only because of wait method.
                    // need fileObject to merge to
                    String filename = mergeForm.getFilename().getText();
                    String foldername = mergeForm.getFoldername().getText();
                    try {
                        if (parentFolder.getFileObject(foldername) == null) {
                            parentFolder = parentFolder.createFolder(foldername);
                        }
                        if (parentFolder.getFileObject(filename) == null) {
                            parentFolder = parentFolder.createData(filename);
                            try {
                                // need some time until the file lock is released
                                wait(200);
                            } catch (InterruptedException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }
                        final FileObject destFileObject = parentFolder;
                        RequestProcessor.getDefault().post(new Runnable() {
                            @Override
                            public void run() {
                                merge(allInstances, destFileObject);
                            }
                        });
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }
    }

    private void merge(Collection<? extends GPXDataObject> list, FileObject destinationFileObject) {
        Gpx gpx = new Gpx();
        OutputStream outputStream = null;
        ProgressHandle handle = ProgressHandleFactory.createHandle(Bundle.CLT_Merge_Procedure());
        try {
            handle.start(list.size());
            int workunit = 0;
            for (GPXDataObject dataObject : list) {

                Gpx toMergeGpx = dataObject.getGpx();

                gpx.getTrk().addAll(toMergeGpx.getTrk());
                gpx.getRte().addAll(toMergeGpx.getRte());
                gpx.getWpt().addAll(toMergeGpx.getWpt());
                gpx.getAny().addAll(toMergeGpx.getAny());

                handle.progress(++workunit);
            }

            outputStream = destinationFileObject.getOutputStream();
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(Gpx.class);
            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(gpx, outputStream);
        } catch (FileAlreadyLockedException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (javax.xml.bind.JAXBException ex) {
            Exceptions.printStackTrace(ex);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            handle.finish();
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    @Override
    public Object getValue(String key) {
        if (Action.LARGE_ICON_KEY.equals(key)) {
            Logger.getLogger(getClass().getName()).info(key);
        }
        return super.getValue(key); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Action createContextAwareInstance(Lookup actionContext) {
        return new GpxMergeAction(actionContext);
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        if (result != null) {
            Collection<? extends GPXDataObject> allInstances = result.allInstances();
            setEnabled(allInstances.size() > 1);
        }
    }
}
