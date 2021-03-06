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
package de.fub.maps.project.aggregator.filetype;

import de.fub.maps.project.aggregator.factories.nodes.AggregatorNode;
import de.fub.maps.project.aggregator.xml.AggregatorDescriptor;
import de.fub.maps.project.models.Aggregator;
import de.fub.maps.project.utils.AggregatorUtils;
import de.fub.utilsmodule.actions.SaveAsTemplateAction;
import de.fub.utilsmodule.xml.jax.JAXBUtil;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.MessageFormat;
import java.util.Collections;
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
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;
import org.xml.sax.InputSource;

@Messages({
    "LBL_AggregationBuilder_LOADER=Files of AggregationBuilder"
})
@MIMEResolver.ExtensionRegistration(
        displayName = "#LBL_AggregationBuilder_LOADER",
        mimeType = "text/aggregationbuilder+xml",
        extension = {"agg", "Agg", "AGG"})
@MIMEResolver.NamespaceRegistration(
        displayName = "#LBL_AggregationBuilder_LOADER",
        checkedExtension = {"agg", "Agg", "AGG"},
        mimeType = "text/aggregationbuilder+xml",
        elementNS = {"http://inf.fu-berlin.de/mapsforge/aggregation/schema"},
        elementName = "aggregator")
@DataObject.Registration(
        mimeType = "text/aggregationbuilder+xml",
        iconBase = "de/fub/maps/project/aggregator/filetype/aggregationBuilderIcon.png",
        displayName = "#LBL_AggregationBuilder_LOADER",
        position = 300)
@ActionReferences({
    @ActionReference(
            path = "Loaders/text/aggregationbuilder+xml/Actions",
            id
            = @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100,
            separatorAfter = 200),
    @ActionReference(
            path = "Loaders/text/aggregationbuilder+xml/Actions",
            id
            = @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300),
    @ActionReference(
            path = "Loaders/text/aggregationbuilder+xml/Actions",
            id
            = @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500),
    @ActionReference(
            path = "Loaders/text/aggregationbuilder+xml/Actions",
            id
            = @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600),
//    @ActionReference(
//            path = "Loaders/text/aggregationbuilder+xml/Actions",
//            id =
//            @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
//            position = 700,
//            separatorAfter = 800),
    @ActionReference(
            path = "Loaders/text/aggregationbuilder+xml/Actions",
            id
            = @ActionID(
                    category = "System",
                    id = "de.fub.utilsmodule.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000),
    @ActionReference(
            path = "Loaders/text/aggregationbuilder+xml/Actions",
            id
            = @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200),
//    @ActionReference(
//            path = "Loaders/text/aggregationbuilder+xml/Actions",
//            id =
//            @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
//            position = 1300),
    @ActionReference(
            path = "Loaders/text/aggregationbuilder+xml/Actions",
            id
            = @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400)
})
public class AggregatorDataObject extends MultiDataObject {

    private static final long serialVersionUID = 1L;
    private final InstanceContent ic = new InstanceContent();
    private transient Lookup lookup;
    private transient final ChangeSupport cs = new ChangeSupport(this);
    private transient AggregatorNode delegateNode = null;
    private transient AggregatorDescriptor aggregator = null;
    private transient final FileChangeAdapter fileChangeListener = new FileChangeAdapterImpl();

    public AggregatorDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        registerEditor("text/aggregationbuilder+xml", true);
        pf.addFileChangeListener(FileUtil.weakFileChangeListener(fileChangeListener, pf));

        InputSource inputSource = DataObjectAdapters.inputSource(AggregatorDataObject.this);
        CheckXMLSupport checkXMLSupport = new CheckXMLSupport(inputSource);
        ValidateXMLSupport validateXMLSupport = new ValidateXMLSupport(inputSource);
        getCookieSet().add(checkXMLSupport);
        getCookieSet().add(validateXMLSupport);
        getCookieSet().add(new SaveAsTemplateHandlerImpl());
    }

    @Override
    protected void handleDelete() throws IOException {
        try {
            String cacheFolderPath = getAggregatorDescriptor().getCacheFolderPath();
            if (cacheFolderPath != null) {
                File file = new File(cacheFolderPath);
                if (file.exists()) {
                    DataObject.find(FileUtil.toFileObject(file)).delete();
                }
            }
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (JAXBException ex) {
            Exceptions.printStackTrace(ex);
        }
        super.handleDelete(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected FileObject handleRename(String name) throws IOException {
        try {
            getAggregatorDescriptor().setName(name);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (JAXBException ex) {
            Exceptions.printStackTrace(ex);
        }
        return super.handleRename(name);
    }

    @Override
    public Lookup getLookup() {
        if (lookup == null) {
            lookup = new ProxyLookup(new AbstractLookup(ic), super.getLookup());
        }
        return lookup;
    }

    public synchronized AggregatorDescriptor getAggregatorDescriptor() throws IOException, JAXBException {
        if (aggregator == null) {
            InputStream inputStream = null;
            try {
                inputStream = getPrimaryFile().getInputStream();
                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(AggregatorDescriptor.class);
                javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
                aggregator = (AggregatorDescriptor) unmarshaller.unmarshal(inputStream);
                aggregator.setDatasources(Collections.synchronizedList(aggregator.getDatasources()));
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
        }
        return aggregator;
    }

    @Override
    protected int associateLookup() {
        return 1;
    }

    public void save() {
        FileUtil.runAtomicAction(new Runnable() {
            @Override
            public void run() {
                try {

                    File file = FileUtil.toFile(getPrimaryFile());
                    if (file != null) {
                        javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(AggregatorDescriptor.class);
                        javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
                        marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
                        marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                        marshaller.marshal(aggregator, file);
                        FileUtil.refreshFor(file);
                    }
                } catch (JAXBException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });

    }

    public void modifySourceEditor() {
        FileUtil.runAtomicAction(new Runnable() {
            @Override
            public void run() {
                try {
                    DataEditorSupport editorSupport = getLookup().lookup(DataEditorSupport.class);
                    if (editorSupport.isDocumentLoaded()) {
                        javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(AggregatorDescriptor.class);
                        javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
                        marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
                        marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

                        StringWriter stringWriter = new StringWriter();
                        marshaller.marshal(aggregator, stringWriter);
                        StyledDocument document = editorSupport.getDocument();
                        if (document != null) {
                            document.remove(0, document.getLength());
                            document.insertString(0, stringWriter.toString(), null);
                            editorSupport.saveDocument();
                            setModified(false);
                        }
                    } else {
                        File file = FileUtil.toFile(getPrimaryFile());
                        if (file != null) {
                            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(AggregatorDescriptor.class);
                            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
                            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
                            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                            marshaller.marshal(aggregator, file);
                            FileUtil.refreshFor(file);
                        }
                    }
                } catch (JAXBException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (BadLocationException ex) {
                    Exceptions.printStackTrace(ex);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        });
    }

    public void updateSourceEditor() {
        DataEditorSupport editorSupport = getLookup().lookup(DataEditorSupport.class);
        if (editorSupport.isDocumentLoaded()) {
            try {
                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(AggregatorDescriptor.class);
                javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
                marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
                marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

                StringWriter stringWriter = new StringWriter();
                marshaller.marshal(aggregator, stringWriter);
                StyledDocument document = editorSupport.getDocument();
                document.remove(0, document.getLength());
                document.insertString(0, stringWriter.toString(), null);
            } catch (JAXBException ex) {
                Exceptions.printStackTrace(ex);
            } catch (BadLocationException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    @MultiViewElement.Registration(
            displayName = "#LBL_AggregationBuilder_EDITOR",
            iconBase = "de/fub/maps/project/aggregator/filetype/aggregationBuilderIcon.png",
            mimeType = "text/aggregationbuilder+xml",
            persistenceType = TopComponent.PERSISTENCE_NEVER,
            preferredID = "AggregationBuilder",
            position = 3000)
    @Messages("LBL_AggregationBuilder_EDITOR=Source")
    public static MultiViewEditorElement createEditor(Lookup lkp) {
        return new MultiViewEditorElement(lkp);
    }

    @Override
    protected Node createNodeDelegate() {
        if (delegateNode == null) {
            delegateNode = new AggregatorNode(new Aggregator(AggregatorDataObject.this));
        }
        return delegateNode;
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
            try {
                aggregator = null;
                getAggregatorDescriptor();
                cs.fireChange();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            } catch (JAXBException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
    }

    private class SaveAsTemplateHandlerImpl implements SaveAsTemplateAction.SaveAsTemplateHandler {

        @Override
        public void createTemplate(String templateName, DataObject templateFromThisDataObject) throws IOException {
            if (templateFromThisDataObject instanceof AggregatorDataObject) {
                try {
                    AggregatorDataObject dataObject = (AggregatorDataObject) templateFromThisDataObject;
                    AggregatorDescriptor aggregatorDescriptor = AggregatorUtils.getAggregatorDescritpor(dataObject);

                    aggregatorDescriptor.setName(templateName);

                    if (aggregatorDescriptor.getDatasources() != null) {
                        aggregatorDescriptor.getDatasources().clear();
                    }

                    FileObject aggregatorTemplates = FileUtil.getConfigFile("Templates/Aggregators");
                    if (aggregatorTemplates == null) {
                        FileObject templateFolder = FileUtil.getConfigFile("Tamplates");
                        if (templateFolder != null) {
                            aggregatorTemplates = templateFolder.createFolder("Aggregators");
                        } else {
                            throw new IOException("Couldn't find Template folder!");
                        }
                    }

                    templateName = templateName.endsWith(".agg") ? templateName : MessageFormat.format("{0}.agg", templateName);

                    FileObject aggregatorTemplate = aggregatorTemplates.createData(templateName);
                    DataObject.find(aggregatorTemplate).setTemplate(true);
                    JAXBUtil.saveDetector(aggregatorTemplate, aggregatorDescriptor);
                } catch (JAXBException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }
}
