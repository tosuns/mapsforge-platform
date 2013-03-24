/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.gpxmodule;

import de.fub.gpxmodule.nodes.GpxNode;
import de.fub.gpxmodule.service.GPXProvider;
import de.fub.gpxmodule.xml.gpx.Gpx;
import de.fub.gpxmodule.xml.gpx.Trk;
import de.fub.gpxmodule.xml.gpx.Trkseg;
import de.fub.gpxmodule.xml.gpx.Wpt;
import geofiletypeapi.GeoDataObject;
import java.awt.geom.Rectangle2D;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.text.MultiViewEditorElement;
import org.netbeans.spi.xml.cookies.CheckXMLSupport;
import org.netbeans.spi.xml.cookies.DataObjectAdapters;
import org.netbeans.spi.xml.cookies.TransformableSupport;
import org.netbeans.spi.xml.cookies.ValidateXMLSupport;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.MIMEResolver;
import org.openide.loaders.DataNode;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.MultiFileLoader;
import org.openide.nodes.Children;
import org.openide.nodes.CookieSet;
import org.openide.nodes.Node;
import org.openide.text.DataEditorSupport;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.openide.windows.TopComponent;
import org.xml.sax.InputSource;

@Messages({
    "LBL_GPX_LOADER=Files of GPX"
})
//@MIMEResolver.ExtensionRegistration(
//        displayName = "#LBL_GPX_LOADER",
//        mimeType = "text/gpx+xml",
//        extension = {"gpx", "Gpx", "GPX"})
@MIMEResolver.NamespaceRegistration(
        acceptedExtension = {"gpx"},
        checkedExtension = {"gpx"},
        displayName = "#LBL_GPX_LOADER",
        elementNS = {"http://www.topografix.com/GPX/1/0"},
        elementName = "gpx",
        mimeType = "text/gpx+xml")
@DataObject.Registration(
        mimeType = "text/gpx+xml",
        iconBase = "de/fub/gpxmodule/gpx.png",
        displayName = "#LBL_GPX_LOADER",
        position = 300)
@ActionReferences({
    @ActionReference(
            path = "Loaders/text/gpx+xml/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.OpenAction"),
            position = 100,
            separatorAfter = 200),
    @ActionReference(
            path = "Loaders/text/gpx+xml/Actions",
            id =
            @ActionID(category = "Edit", id = "org.openide.actions.CutAction"),
            position = 300),
    @ActionReference(
            path = "Loaders/text/gpx+xml/Actions",
            id =
            @ActionID(category = "Edit", id = "org.openide.actions.CopyAction"),
            position = 400,
            separatorAfter = 500),
    @ActionReference(
            path = "Loaders/text/gpx+xml/Actions",
            id =
            @ActionID(category = "Edit", id = "org.openide.actions.DeleteAction"),
            position = 600),
    @ActionReference(
            path = "Loaders/text/gpx+xml/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.RenameAction"),
            position = 700,
            separatorAfter = 800),
    @ActionReference(
            path = "Loaders/text/gpx+xml/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.SaveAsTemplateAction"),
            position = 900,
            separatorAfter = 1000),
    @ActionReference(
            path = "Loaders/text/gpx+xml/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.FileSystemAction"),
            position = 1100,
            separatorAfter = 1200),
    @ActionReference(
            path = "Loaders/text/gpx+xml/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.ToolsAction"),
            position = 1300),
    @ActionReference(
            path = "Loaders/text/gpx+xml/Actions",
            id =
            @ActionID(category = "System", id = "org.openide.actions.PropertiesAction"),
            position = 1400)
})
public class GPXDataObject extends GeoDataObject implements GPXProvider {

    private static final long serialVersionUID = 1L;
    private transient Lookup lookup = null;
    protected transient final Set<ChangeListener> changeListenerSet = Collections.synchronizedSet(new HashSet<ChangeListener>());
    private Gpx gpx = null;
    private transient final PropertyChangeListener pcl = new PropertyChangeListenerImpl();
    private GpxVersion gpxVersion = GpxVersion.GPX_1_1;

    public GPXDataObject(FileObject pf, MultiFileLoader loader) throws DataObjectExistsException, IOException {
        super(pf, loader);
        registerEditor("text/gpx+xml", true);
        addPropertyChangeListener(pcl);
    }

    @Override
    public Lookup getLookup() {
        if (lookup == null) {
            InputSource is = DataObjectAdapters.inputSource(GPXDataObject.this);
            Lookup spLookup = super.getLookup();
            Node.Cookie checkXMLCookie = new CheckXMLSupport(is);
            Node.Cookie validateXMLCookie = new ValidateXMLSupport(is);
            Lookup lkp = Lookups.fixed(checkXMLCookie, validateXMLCookie);
            if (spLookup != null) {
                lookup = new ProxyLookup(spLookup, lkp);
            } else {
                lookup = lkp;
            }
            CookieSet cookies = getCookieSet();
            cookies.add(validateXMLCookie);
            cookies.add(checkXMLCookie);
        }
        return lookup;
    }

    @Override
    protected int associateLookup() {
        return 1;
    }

    @MultiViewElement.Registration(
            displayName = "#LBL_GPX_EDITOR",
            iconBase = "de/fub/gpxmodule/gpx.png",
            mimeType = "text/gpx+xml",
            persistenceType = TopComponent.PERSISTENCE_ONLY_OPENED,
            preferredID = "GPX",
            position = 3000)
    @Messages("LBL_GPX_EDITOR=Source")
    public static MultiViewEditorElement createEditor(Lookup lkp) {
        return new MultiViewEditorElement(lkp);
    }

    private void updateGpx() throws IOException {
        updateGpx(getPrimaryFile().getInputStream());
    }

    @SuppressWarnings({"unchecked", "unchecked"})
    private void updateGpx(InputStream inputStream) throws IOException {
        try {
            // parse gpx version 1.1
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(Gpx.class);
            javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            gpx = ((JAXBElement<Gpx>) unmarshaller.unmarshal(inputStream)).getValue();
        } catch (Exception ex) {
            inputStream = getPrimaryFile().getInputStream();
            // fall back track to parse as gpx vsrsion 1.1
            try {
                StringWriter stringWriter = new StringWriter();
                StreamResult streamResult = new StreamResult(stringWriter);

                InputStream resourceAsStream = GPXDataObject.class.getResourceAsStream("/de/fub/gpxmodule/xslt/gpx10to11Transformer.xsl");
                StreamSource streamSource = new StreamSource(resourceAsStream);
                TransformableSupport transformableSupport = new TransformableSupport(new StreamSource(inputStream));

                transformableSupport.transform(streamSource, streamResult, null);

                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(Gpx.class);
                javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();

                Object unmarshal = unmarshaller.unmarshal(new StringReader(stringWriter.toString()));
                if (unmarshal instanceof JAXBElement<?>) {
                    gpx = ((JAXBElement<Gpx>) unmarshal).getValue();
                } else if (unmarshal instanceof Gpx) {
                    gpx = (Gpx) unmarshal;
                }
                gpxVersion = GpxVersion.GPX_1_0;
            } catch (TransformerException ex1) {
                Exceptions.printStackTrace(ex1);
            } catch (JAXBException ex1) {
                Exceptions.printStackTrace(ex1);
            }
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public GpxVersion getGpxVersion() {
        return gpxVersion;
    }

    @Override
    public Gpx getGpx() {
        if (gpx == null) {
            try {
                updateGpx();
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return gpx;
    }

    @Override
    protected Node createNodeDelegate() {
        Node node = null;
        if (getGpx() == null) {
            node = new ErrorGPXDataNode(this, Children.LEAF);
        } else {
            try {
                node = new GpxNode(GPXDataObject.this);
            } catch (IntrospectionException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return node != null ? node : new DataNode(this, Children.LEAF);
    }

    @Override
    public void addChangeListener(ChangeListener listener) {
        this.changeListenerSet.add(listener);
    }

    @Override
    public void removeChangeListener(ChangeListener listener) {
        this.changeListenerSet.remove(listener);
    }

    @Override
    public void modelChanged(Object uiComponent, Gpx model) {
        ChangeEvent changeEvent = new ChangeEvent(uiComponent);
        for (ChangeListener changeListener : changeListenerSet) {
            if (uiComponent != changeListener) {
                changeListener.stateChanged(changeEvent);
            }
        }
    }

    @Override
    public Rectangle2D getBoundingBox() {
        Rectangle2D boundingBox = null;
        Gpx gpxData = getGpx();
        if (gpxData != null) {
            for (Trk track : gpxData.getTrk()) {
                for (Trkseg tracksegment : track.getTrkseg()) {
                    for (Wpt trackPoint : tracksegment.getTrkpt()) {
                        if (boundingBox == null) {
                            boundingBox = new Rectangle2D.Double(
                                    trackPoint.getLat().doubleValue(),
                                    trackPoint.getLon().doubleValue(),
                                    trackPoint.getLat().doubleValue(),
                                    trackPoint.getLon().doubleValue());
                        } else {
                            boundingBox.add(
                                    trackPoint.getLat().doubleValue(),
                                    trackPoint.getLon().doubleValue());
                        }
                    }
                }
            }
        }
        return boundingBox;
    }

    private class PropertyChangeListenerImpl implements PropertyChangeListener {

        public PropertyChangeListenerImpl() {
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (DataObject.PROP_MODIFIED.equals(evt.getPropertyName())) {
                try {
                    DataEditorSupport editorSupport = getLookup().lookup(DataEditorSupport.class);
                    updateGpx(editorSupport.getInputStream());
                    modelChanged(GPXDataObject.this, gpx);
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    public enum GpxVersion {

        GPX_1_0, GPX_1_1;
    }

    private static class ErrorGPXDataNode extends DataNode {

        public ErrorGPXDataNode(DataObject obj, Children ch) {
            super(obj, ch);
        }
    }
}
