/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.filetype;

import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.xmls.DetectorDescriptor;
import de.fub.mapsforge.project.detector.factories.nodes.DetectorNode;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import javax.swing.event.ChangeListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.xml.bind.JAXBException;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.text.MultiViewEditorElement;
import org.netbeans.spi.xml.cookies.CheckXMLSupport;
import org.netbeans.spi.xml.cookies.DataObjectAdapters;
import org.netbeans.spi.xml.cookies.ValidateXMLSupport;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.FileChangeAdapter;
import org.openide.filesystems.FileEvent;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Node;
import org.openide.text.DataEditorSupport;
import org.openide.util.ChangeSupport;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.TopComponent;
import org.xml.sax.InputSource;

@Messages({
    "LBL_Detector_LOADER=Files of Detector"
})
@MIMEResolver.ExtensionRegistration(
        displayName = "#LBL_Detector_LOADER",
        mimeType = "text/detector+xml",
        extension = {"dec", "Dec", "DEC"})
@MIMEResolver.NamespaceRegistration(
        displayName = "#LBL_Detector_LOADER",
        mimeType = "text/detector+xml",
        elementNS = {"http://inf.fu-berlin.de/mapsforge/detector/schema"},
        elementName = "detector")
@DataObject.Registration(
        mimeType = "text/detector+xml",
        iconBase = "de/fub/mapsforge/project/detector/filetype/detector.png",
        displayName = "#LBL_Detector_LOADER",
        position = 300)
@ActionReferences({
    @ActionReference(
            path = "Loaders/text/detector+xml/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100,
            separatorAfter = 200),
    @ActionReference(
            path = "Loaders/text/detector+xml/Actions",
            id =
            @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300),
    @ActionReference(
            path = "Loaders/text/detector+xml/Actions",
            id =
            @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500),
    @ActionReference(
            path = "Loaders/text/detector+xml/Actions",
            id =
            @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600),
//    @ActionReference(
//            path = "Loaders/text/detector+xml/Actions",
//            id =
//            @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
//            position = 700,
//            separatorAfter = 800),
    @ActionReference(
            path = "Loaders/text/detector+xml/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000),
    @ActionReference(
            path = "Loaders/text/detector+xml/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200),
//    @ActionReference(
//            path = "Loaders/text/detector+xml/Actions",
//            id =
//            @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
//            position = 1300),
    @ActionReference(
            path = "Loaders/text/detector+xml/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400)
})
public class DetectorDataObject extends MultiDataObject {

    private static final long serialVersionUID = 1L;
    private transient DetectorNode delegateNode = null;
    private transient final ChangeSupport cs = new ChangeSupport(this);
    private transient DetectorDescriptor detectorDescriptor = null;
    private transient final FileChangeAdapter fileChangeListener = new FileChangeAdapterImpl();

    public DetectorDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        registerEditor("text/detector+xml", true);
        InputSource inputSource = DataObjectAdapters.inputSource(DetectorDataObject.this);
        CheckXMLSupport checkXMLSupport = new CheckXMLSupport(inputSource);
        ValidateXMLSupport validateXMLSupport = new ValidateXMLSupport(inputSource);
        getCookieSet().add(checkXMLSupport);
        getCookieSet().add(validateXMLSupport);
    }

    @Override
    protected int associateLookup() {
        return 1;
    }

    @Override
    protected Node createNodeDelegate() {
        if (delegateNode == null) {
            delegateNode = new DetectorNode(new Detector(this));
        }
        return delegateNode;
    }

    @MultiViewElement.Registration(
            displayName = "#LBL_Detector_EDITOR",
            iconBase = "de/fub/mapsforge/project/detector/filetype/detector.png",
            mimeType = "text/detector+xml",
            persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED,
            preferredID = "Detector",
            position = 1000)
    @Messages("LBL_Detector_EDITOR=Source")
    public static MultiViewEditorElement createEditor(Lookup lkp) {
        return new MultiViewEditorElement(lkp);
    }

    public synchronized DetectorDescriptor getDetectorDescriptor() throws JAXBException, IOException {
        if (detectorDescriptor == null) {
            InputStream inputStream = null;
            inputStream = getPrimaryFile().getInputStream();
            try {
                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(DetectorDescriptor.class);
                javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
                detectorDescriptor = (DetectorDescriptor) unmarshaller.unmarshal(inputStream); //NOI18N
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
        return detectorDescriptor;
    }

    public void save() {
        FileUtil.runAtomicAction(new Runnable() {
            @Override
            public void run() {
                File file = FileUtil.toFile(getPrimaryFile());
                if (file != null) {
                    try {
                        javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(DetectorDescriptor.class);
                        javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
                        marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
                        marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                        marshaller.marshal(detectorDescriptor, file);
                    } catch (javax.xml.bind.JAXBException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        });
    }

    public void modifySourceEditor() {
        FileUtil.runAtomicAction(new Runnable() {
            @Override
            public void run() {
                DataEditorSupport editorSupport = getLookup().lookup(DataEditorSupport.class);

                try {
                    if (editorSupport.isDocumentLoaded()) {
                        javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(DetectorDescriptor.class);
                        javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
                        marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
                        marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

                        StringWriter stringWriter = new StringWriter();
                        marshaller.marshal(detectorDescriptor, stringWriter);
                        StyledDocument document = editorSupport.getDocument();
                        document.remove(0, document.getLength());
                        document.insertString(0, stringWriter.toString(), null);
                        editorSupport.saveDocument();
                        setModified(false);
                    } else {
                        save();
                    }
                } catch (javax.xml.bind.JAXBException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }

            }
        });
    }

    public void addChangeListener(ChangeListener listener) {
        cs.addChangeListener(listener);
    }

    public void removeChangeListener(ChangeListener listener) {
        cs.removeChangeListener(listener);
    }

    private class FileChangeAdapterImpl extends FileChangeAdapter {

        public FileChangeAdapterImpl() {
        }

        @Override
        public void fileChanged(FileEvent fe) {
            detectorDescriptor = null;
            cs.fireChange();
        }
    }
}
