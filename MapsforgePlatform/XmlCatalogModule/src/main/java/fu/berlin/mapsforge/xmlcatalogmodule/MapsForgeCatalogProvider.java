/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fu.berlin.mapsforge.xmlcatalogmodule;

import de.fub.utilsmodule.icons.IconRegister;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.netbeans.modules.xml.catalog.spi.CatalogDescriptor;
import org.netbeans.modules.xml.catalog.spi.CatalogListener;
import org.netbeans.modules.xml.catalog.spi.CatalogReader;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.util.NbBundle;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * This class collects all xml schema files, that are registered under the path
 * <code>MapsForge/Catalog/</code>. To register a xml schema file every file
 * entry should have the structure:
 *
 * <file name="device.xsd" url="<resource path>"> <attr name="schemaId"
 * stringvalue="<example namespace>"/> <attr name="resource"
 * stringvalue="nbres:<resource path>"/> </file>
 *
 * @author Serdar
 */
@NbBundle.Messages({"CLT_Catalog_Name=MapsForge Xml Catalog",
    "CLT_Catalog_Description=Provides all registered xml schema files that are associated with the MapsForge project."})
public class MapsForgeCatalogProvider implements CatalogReader, CatalogDescriptor, EntityResolver {

    public static final String MAPSFORGE_XML_CATALOG_PATH = "MapsForge/Catalog";
    public static final String ATTRIBUTE_SCHEMA_ID = "schemaId";
    public static final String ATTRIBUTE_RESOURCE = "resource";
    private static final String SCHEMA_PREFIX = "SCHEMA:";
    private Map<String, FileObject> schemaFiles = new HashMap<String, FileObject>();

    /**
     * Creates a new instance of RegisterCatalog
     */
    public MapsForgeCatalogProvider() {
    }

    private Map<String, FileObject> getSchemaFiles() {
        schemaFiles.clear();
        FileObject configObject = FileUtil.getConfigRoot().getFileObject(MAPSFORGE_XML_CATALOG_PATH);
        if (configObject != null) {
            FileObject[] children = configObject.getChildren();
            for (FileObject file : children) {
                Object attribute = file.getAttribute(ATTRIBUTE_SCHEMA_ID);
                if (attribute instanceof String) {
                    schemaFiles.put(MessageFormat.format("{1}", SCHEMA_PREFIX, attribute), file);
                }
            }
        }
        return schemaFiles;
    }

    @Override
    public Iterator<String> getPublicIDs() {
        List<String> list = new ArrayList<String>(getSchemaFiles().keySet());
        return list.listIterator();
    }

    @Override
    public void refresh() {
    }

    @Override
    public String getSystemID(String publicId) {
        FileObject file = getSchemaFiles().get(publicId);
        if (file != null) {
            Object attribute = file.getAttribute(ATTRIBUTE_RESOURCE);
            if (attribute instanceof String) {
                return (String) attribute;
            }
        }
        return null;
    }

    @Override
    public String resolveURI(String string) {
        return string;
    }

    @Override
    public String resolvePublic(String string) {
        return null;
    }

    @Override
    public void addCatalogListener(CatalogListener catalogListener) {
    }

    @Override
    public void removeCatalogListener(CatalogListener catalogListener) {
    }

    @Override
    public Image getIcon(int i) {
        return IconRegister.findRegisteredIcon("mapsforgeIcon16.png");
    }

    @Override
    public String getDisplayName() {
        return Bundle.CLT_Catalog_Name();
    }

    @Override
    public String getShortDescription() {
        return Bundle.CLT_Catalog_Name();
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    }

    @Override
    public void removePropertyChangeListener(PropertyChangeListener propertyChangeListener) {
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        FileObject fileObject = getSchemaFiles().get(systemId);
        if (fileObject != null) {
            URL urL = new URL((String) fileObject.getAttribute(ATTRIBUTE_RESOURCE));
            return new org.xml.sax.InputSource(urL.openStream());
        }
        fileObject = getSchemaFiles().get(publicId);
        if (fileObject != null) {
            URL urL = new URL((String) fileObject.getAttribute(ATTRIBUTE_RESOURCE));
            return new org.xml.sax.InputSource(urL.openStream());
        }
        return null;
    }
}
