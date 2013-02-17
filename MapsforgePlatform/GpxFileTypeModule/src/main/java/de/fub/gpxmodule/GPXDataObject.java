/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.gpxmodule;

import de.fub.gpxmodule.nodes.GpxNode;
import de.fub.gpxmodule.service.GPXProvider;
import de.fub.gpxmodule.xml.Gpx;
import de.fub.gpxmodule.xml.Trk;
import de.fub.gpxmodule.xml.Trkseg;
import de.fub.gpxmodule.xml.Trkseg.Trkpt;
import geofiletypeapi.GeoDataObject;
import java.awt.geom.Rectangle2D;
import java.beans.IntrospectionException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.text.MultiViewEditorElement;
import org.netbeans.spi.xml.cookies.CheckXMLSupport;
import org.netbeans.spi.xml.cookies.DataObjectAdapters;
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
@MIMEResolver.ExtensionRegistration(
        displayName = "#LBL_GPX_LOADER",
        mimeType = "text/gpx+xml",
        extension = {"gpx", "Gpx", "GPX"})
@MIMEResolver.NamespaceRegistration(
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
    private Lookup lookup = null;
    protected final Set<ChangeListener> changeListenerSet = Collections.synchronizedSet(new HashSet<ChangeListener>());
    private Gpx gpx = null;
    private final PropertyChangeListener pcl = new PropertyChangeListener() {
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
    };

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
            position = 1000)
    @Messages("LBL_GPX_EDITOR=Source")
    public static MultiViewEditorElement createEditor(Lookup lkp) {
        return new MultiViewEditorElement(lkp);
    }

    private void updateGpx() throws IOException {
        updateGpx(getPrimaryFile().getInputStream());
    }

    private void updateGpx(InputStream inputStream) throws IOException {
        try {
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(Gpx.class);
            javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            gpx = (Gpx) unmarshaller.unmarshal(inputStream); //NOI18N
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
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
        try {
            node = new GpxNode(GPXDataObject.this);
        } catch (IntrospectionException ex) {
            Exceptions.printStackTrace(ex);
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
                for (Trkseg trackSegment : track.getTrkseg()) {
                    for (Trkpt trackPoint : trackSegment.getTrkpt()) {
                        if (boundingBox == null) {
                            boundingBox = new Rectangle2D.Double(trackPoint.getLat(), trackPoint.getLon(), trackPoint.getLat(), trackPoint.getLon());
                        } else {
                            boundingBox.add(trackPoint.getLat(), trackPoint.getLon());
                        }
                    }
                }
            }
        }
        return boundingBox;
    }
}
