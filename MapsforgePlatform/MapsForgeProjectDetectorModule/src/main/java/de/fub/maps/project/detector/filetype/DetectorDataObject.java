/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.filetype;

import de.fub.maps.project.detector.factories.nodes.DetectorNode;
import de.fub.maps.project.detector.model.Detector;
import de.fub.maps.project.detector.model.xmls.DataSets;
import de.fub.maps.project.detector.model.xmls.DetectorDescriptor;
import de.fub.maps.project.detector.utils.DetectorUtils;
import de.fub.utilsmodule.actions.SaveAsTemplateAction;
import java.io.IOException;
import java.io.StringWriter;
import java.text.MessageFormat;
import javax.swing.JComponent;
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
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
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
        checkedExtension = {"dec", "Dec", "DEC"},
        mimeType = "text/detector+xml",
        elementNS = {"http://inf.fu-berlin.de/mapsforge/detector/schema"},
        elementName = "detector")
@DataObject.Registration(
        mimeType = "text/detector+xml",
        iconBase = "de/fub/maps/project/detector/filetype/detector.png",
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
    @ActionReference(
            path = "Loaders/text/detector+xml/Actions",
            id =
            @ActionID(
            category = "System",
            id = "de.fub.utilsmodule.actions.SaveAsTemplateAction"),
            position = 900),
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
    private transient Node delegateNode = null;
    private transient final ChangeSupport cs = new ChangeSupport(this);
    private transient DetectorDescriptor detectorDescriptor = null;
    private transient final FileChangeAdapter fileChangeListener = new FileChangeAdapterImpl();

    public DetectorDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        registerEditor("text/detector+xml", true);
        InputSource inputSource = DataObjectAdapters.inputSource(DetectorDataObject.this);
        CheckXMLSupport checkXMLSupport = new CheckXMLSupport(inputSource);
        ValidateXMLSupport validateXMLSupport = new ValidateXMLSupport(inputSource);
        getCookieSet().add(new SaveAsTamplateHandlerImpl());
        getCookieSet().add(checkXMLSupport);
        getCookieSet().add(validateXMLSupport);
        pf.addFileChangeListener(fileChangeListener);
    }

    @Override
    protected int associateLookup() {
        return 1;
    }

    @Override
    protected Node createNodeDelegate() {
        if (delegateNode == null) {
            Detector detector = new Detector(this);
            getCookieSet().add(detector);
            DetectorDescriptor detectorDescr = detector.getDetectorDescriptor();
            if (detectorDescr != null) {
                delegateNode = new DetectorNode(detector);
            } else {
                delegateNode = Node.EMPTY;
            }

        }
        return delegateNode;
    }

    @MultiViewElement.Registration(
            displayName = "#LBL_Detector_EDITOR",
            iconBase = "de/fub/maps/project/detector/filetype/detector.png",
            mimeType = "text/detector+xml",
            persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED,
            preferredID = "Detector",
            position = 3000)
    @Messages("LBL_Detector_EDITOR=Source")
    public static MultiViewEditorElement createEditor(Lookup lkp) {
        return new DetectorEditorElement(lkp);
    }

    public synchronized DetectorDescriptor getDetectorDescriptor() throws JAXBException, IOException {
        if (detectorDescriptor == null) {
            detectorDescriptor = DetectorUtils.getDetectorDescriptor(DetectorDataObject.this);
        }
        return detectorDescriptor;
    }

    public void save() {
        try {
            DetectorUtils.saveDetector(DetectorDataObject.this);
        } catch (JAXBException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
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

    public void updateEditor() {
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
            }
        } catch (javax.xml.bind.JAXBException ex) {
            Exceptions.printStackTrace(ex);
        } catch (BadLocationException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @Override
    protected FileObject handleRename(String name) throws IOException {
        try {
            getDetectorDescriptor().setName(name);
            save();
        } catch (JAXBException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
        return super.handleRename(name); //To change body of generated methods, choose Tools | Templates.
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

    private class SaveAsTamplateHandlerImpl implements SaveAsTemplateAction.SaveAsTemplateHandler {

        @Override
        public void createTemplate(String templateName, DataObject templateFromThisDataObject) throws IOException {
            if (templateFromThisDataObject instanceof DetectorDataObject) {
                try {
                    DetectorDataObject detectorDataObject = (DetectorDataObject) templateFromThisDataObject;
                    DetectorDescriptor detectorDescr = DetectorUtils.getDetectorDescriptor(detectorDataObject);
                    detectorDescr.setName(templateName);
                    DataSets datasets = detectorDescr.getDatasets();
                    datasets.getInferenceSet().getDatasetList().clear();
                    datasets.getTrainingSet().getTransportModeList().clear();
                    FileObject detectorTemplates = FileUtil.getConfigFile("Templates/Detectors");
                    if (detectorTemplates == null) {
                        FileObject templateFolder = FileUtil.getConfigFile("Templates");
                        if (templateFolder != null) {
                            detectorTemplates = templateFolder.createFolder("Detectors");
                        } else {
                            throw new IOException("Couldn't Templates folder!");
                        }
                    }
                    templateName = templateName.endsWith(".dec") ? templateName : MessageFormat.format("{0}.dec", templateName);
                    FileObject templateFile = detectorTemplates.createData(templateName);
                    DataObject.find(templateFile).setTemplate(true);
                    DetectorUtils.saveDetector(templateFile, detectorDescr);
                } catch (JAXBException ex) {
                    throw new IOException(ex);
                }
            }
        }
    }

    private static class DetectorEditorElement extends MultiViewEditorElement {

        private static final long serialVersionUID = 1L;
        private Lookup lkp = null;

        public DetectorEditorElement(Lookup lookup) {
            super(lookup);
        }

        @Override
        public Lookup getLookup() {
            if (lkp == null) {
                JComponent visualRepresentation = getVisualRepresentation();
                lkp = new ProxyLookup(Lookups.singleton(visualRepresentation), super.getLookup());
            }
            return lkp;
        }
    }
}
