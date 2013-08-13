/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.utils;

import de.fub.gpxmodule.xml.Gpx;
import de.fub.gpxmodule.xml.ObjectFactory;
import de.fub.maps.project.aggregator.factories.CategoryNodeFactory;
import de.fub.maps.project.aggregator.filetype.AggregatorDataObject;
import de.fub.maps.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.maps.project.aggregator.xml.AggregatorDescriptor;
import de.fub.maps.project.aggregator.xml.ProcessDescriptor;
import de.fub.maps.project.aggregator.xml.Property;
import de.fub.maps.project.models.Aggregator;
import de.fub.utilsmodule.xml.jax.JAXBUtil;
import java.awt.Color;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.xml.bind.JAXBException;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.spi.palette.DragAndDropHandler;
import org.netbeans.spi.palette.PaletteActions;
import org.netbeans.spi.palette.PaletteController;
import org.netbeans.spi.palette.PaletteFactory;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileSystem;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.datatransfer.ExTransferable;

/**
 *
 * @author Serdar
 */
public class AggregatorUtils {

    @StaticResource
    public static final String ICON_PATH_NORMAL = "de/fub/maps/project/aggregator/aggregatorIcon.png";
    @StaticResource
    public static final String ICON_PATH_BUSY = "de/fub/maps/project/aggregator/aggregatorIconBusy.png";
    @StaticResource
    public static final String ICON_PATH_ERROR = "de/fub/maps/project/aggregator/aggregatorIconError.png";
    private static PaletteController palette;
    private static FileSystem inMemoryFileSystem;
    private static Object MUTEX_CREATE_INSTANCE = new Object();

    public static PaletteController getProcessPalette() {
        if (palette == null) {
            palette = PaletteFactory.createPalette(
                    new AbstractNode(Children.create(new CategoryNodeFactory(), true)),
                    new EmptyPaletteAction(), null, new PaletteDragAndDropHandler());
        }
        return palette;
    }

    public static Aggregator createAggregator(FileObject fileObject) {
        try {
            DataObject dataObject = DataObject.find(fileObject);
            if (dataObject instanceof AggregatorDataObject) {
                AggregatorDataObject aggregatorDataObject = (AggregatorDataObject) dataObject;
                return aggregatorDataObject.getNodeDelegate().getLookup().lookup(Aggregator.class);
            }
        } catch (DataObjectNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public static <T> T createInstance(Class<T> clazz, String className) {
        synchronized (MUTEX_CREATE_INSTANCE) {
            T instance = null;
            try {
                Class<?> classInstance = null;
                ClassLoader classLoader = Lookup.getDefault().lookup(ClassLoader.class);
                if (classLoader != null) {
                    classInstance = classLoader.loadClass(className);
                } else {
                    classInstance = Class.forName(className);
                }
                if (clazz.isAssignableFrom(classInstance)) {
                    Class<T> cl = (Class<T>) classInstance;
                    instance = cl.newInstance();
                }
            } catch (InstantiationException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            } catch (ClassNotFoundException ex) {
                Exceptions.printStackTrace(ex);
            }
            return instance;
        }
    }

    public static <T> T createValue(Class<T> clazz, List<Property> properties) {
        T returnInstance = null;
        try {
            final T instance = clazz.newInstance();
            HashMap<String, Property> propertyMap = new HashMap<String, Property>();
            for (Property property : properties) {
                propertyMap.put(property.getName(), property);
            }
            for (final Field field : clazz.getDeclaredFields()) {
                final Property property = propertyMap.get(field.getName());
                if (property != null) {
                    AccessController.doPrivileged(new PrivilegedAction<Void>() {
                        @Override
                        @SuppressWarnings("empty-statement")
                        public Void run() {
                            boolean accessible = field.isAccessible();
                            try {
                                field.setAccessible(true);
                                field.set(instance, getValue(Class.forName(property.getJavaType()), property));
                            } catch (IllegalArgumentException ex) {
                                Exceptions.printStackTrace(ex);
                            } catch (ReflectiveOperationException ex) {
                                Exceptions.printStackTrace(ex);
                            } finally {
                                field.setAccessible(accessible);
                            }
                            return null;
                        }
                    });
                }
            }
            returnInstance = instance;
        } catch (ReflectiveOperationException ex) {
            Exceptions.printStackTrace(ex);
        }
        return returnInstance;
    }

    @SuppressWarnings({"unchecked"})
    public static <T> T getValue(Class<T> clazz, Property property) {
        T instance = null;
        if (clazz.getName().equals(Boolean.class.getName())) {
            instance = (T) Boolean.valueOf(property.getValue());
        } else if (clazz.getName().equals(Double.class.getName())) {
            instance = (T) Double.valueOf(property.getValue());
        } else if (clazz.getName().equals(Integer.class.getName())) {
            instance = (T) Integer.valueOf(property.getValue());
        } else if (clazz.getName().equals(String.class.getName())) {
            instance = (T) property.getValue();
        } else if (clazz.getName().equals(Color.class.getName())) {
            instance = (T) new Color(Integer.parseInt(property.getValue(), 16));
        } else if (clazz.getName().equals(Long.class.getName())) {
            instance = (T) Long.valueOf(property.getValue());
        }


        return instance;
    }

    public static Project findProject(FileObject fileObject) {
        return MapsProjectUtils.findProject(fileObject);
    }

    @NbBundle.Messages({
        "# {0} - filepath",
        "CLT_File_not_found=Couldn't find associated xml process descriptor file at path: {0}"
    })
    public static ProcessDescriptor getProcessDescriptor(Class<? extends AbstractAggregationProcess> processClass) throws IOException {
        ProcessDescriptor descriptor = null;
        String filePatn = MessageFormat.format("/{0}.xml", processClass.getName().replaceAll("\\.", "/"));
        InputStream resourceAsStream = processClass.getResourceAsStream(filePatn);
        if (resourceAsStream != null) {
            try {
                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(ProcessDescriptor.class);
                javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
                descriptor = (ProcessDescriptor) unmarshaller.unmarshal(resourceAsStream); //NOI18N
            } catch (javax.xml.bind.JAXBException ex) {
                throw new IOException(ex);
            } finally {
                resourceAsStream.close();
            }
        } else {
            throw new FileNotFoundException(Bundle.CLT_File_not_found(filePatn));
        }

        return descriptor;
    }

    public static void saveGpxToFile(File destFile, Gpx content) throws JAXBException {
        javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(ObjectFactory.class);
        javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
        marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
        marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        marshaller.marshal(new ObjectFactory().createGpx(content), destFile);
    }

    public static AggregatorDescriptor getAggregatorDescritpor(DataObject dataObject) throws JAXBException, IOException {
        return getAggregatorDescriptor(dataObject.getPrimaryFile());
    }

    public static AggregatorDescriptor getAggregatorDescriptor(FileObject fileObject) throws JAXBException, IOException {
        return JAXBUtil.createDescriptor(AggregatorDescriptor.class, fileObject);
    }

    private static FileSystem getFilesystem() {
        if (inMemoryFileSystem == null) {
            inMemoryFileSystem = FileUtil.createMemoryFileSystem();
        }
        return inMemoryFileSystem;
    }

    private static class PaletteDragAndDropHandler extends DragAndDropHandler {

        private static final Logger LOG = Logger.getLogger(PaletteDragAndDropHandler.class.getName());

        public PaletteDragAndDropHandler() {
        }

        @Override
        public void customize(ExTransferable et, Lookup lkp) {
            final AbstractAggregationProcess process = lkp.lookup(AbstractAggregationProcess.class);
            if (process != null) {
                et.put(new ExTransferable.Single(AbstractAggregationProcess.PROCESS_FLAVOR) {
                    @Override
                    protected Object getData() throws IOException, UnsupportedFlavorException {
                        return process;
                    }
                });
            }

            LOG.log(Level.FINE, "drag and drop handler: cusomize");
        }
    }

    private static class EmptyPaletteAction extends PaletteActions {

        @Override
        public Action[] getImportActions() {
            return new Action[0];
        }

        @Override
        public Action[] getCustomPaletteActions() {
            return new Action[0];
        }

        @Override
        public Action[] getCustomCategoryActions(Lookup lkp) {
            return new Action[0];
        }

        @Override
        public Action[] getCustomItemActions(Lookup lkp) {
            return new Action[0];
        }

        @Override
        public Action getPreferredAction(Lookup lkp) {
            return null;
        }
    }
}
