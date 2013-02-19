/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.utils;

import de.fub.mapsforge.project.aggregator.factories.CategoryNodeFactory;
import de.fub.mapsforge.project.aggregator.pipeline.AbstractAggregationProcess;
import de.fub.mapsforge.project.aggregator.xml.ProcessDescriptor;
import de.fub.mapsforge.project.aggregator.xml.Property;
import java.awt.Color;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import org.netbeans.api.annotations.common.StaticResource;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ProjectManager;
import org.netbeans.spi.palette.DragAndDropHandler;
import org.netbeans.spi.palette.PaletteActions;
import org.netbeans.spi.palette.PaletteController;
import org.netbeans.spi.palette.PaletteFactory;
import org.openide.filesystems.FileObject;
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
public class AggregateUtils {

    @StaticResource
    public static final String ICON_PATH_NORMAL = "de/fub/mapsforge/project/aggregator/aggregatorIcon.png";
    @StaticResource
    public static final String ICON_PATH_BUSY = "de/fub/mapsforge/project/aggregator/aggregatorIconBusy.png";
    @StaticResource
    public static final String ICON_PATH_ERROR = "de/fub/mapsforge/project/aggregator/aggregatorIconError.png";
    private static HashMap<String, Class<? extends AbstractAggregationProcess>> hashMap = new HashMap<String, Class<? extends AbstractAggregationProcess>>();
    private static final PaletteController palette = PaletteFactory.createPalette(
            new AbstractNode(Children.create(new CategoryNodeFactory(), true)),
            new EmptyPaletteAction(), null, new PaletteDragAndDropHandler());

    public static PaletteController getProcessPalette() {
        return palette;
    }

    @SuppressWarnings("unchecked")
    public static <T> T createInstance(Class<T> clazz, String className) {
        T instance = null;
        try {
            Class<?> forName = Class.forName(className);
//            if (!clazz.equals(forName)) {
//                throw new IllegalArgumentException();
//            }
            Class<T> cl = (Class<T>) forName;
            instance = cl.newInstance();
        } catch (InstantiationException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        }
        return instance;
    }

    public static <T> T createValue(Class<T> clazz, List<Property> properties) {
        T instance = null;
        try {
            instance = clazz.newInstance();
            HashMap<String, Property> propertyMap = new HashMap<String, Property>();
            for (Property property : properties) {
                propertyMap.put(property.getName(), property);
            }
            for (Field field : clazz.getFields()) {
                Property property = propertyMap.get(field.getName());
                if (property != null) {
                    try {
                        field.set(instance, getValue(Class.forName(property.getJavaType()), property));
                    } catch (ClassNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    } catch (Exception ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }

        } catch (InstantiationException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        }
        return instance;
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
        Project project = null;

        while (project == null && !fileObject.isRoot()) {
            try {
                project = ProjectManager.getDefault().findProject(fileObject);
            } catch (IllegalArgumentException ex) {
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
            fileObject = fileObject.getParent();
        }

        return project;
    }

    @NbBundle.Messages({"# {0} - filepath", "CLT_File_not_found=Couldn't find associated xml process descriptor file at path: {0}"})
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
            }
        } else {
            throw new FileNotFoundException(Bundle.CLT_File_not_found(filePatn));
        }

        return descriptor;
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
            return null;
        }

        @Override
        public Action[] getCustomPaletteActions() {
            return null;
        }

        @Override
        public Action[] getCustomCategoryActions(Lookup lkp) {
            return null;
        }

        @Override
        public Action[] getCustomItemActions(Lookup lkp) {
            return null;
        }

        @Override
        public Action getPreferredAction(Lookup lkp) {
            return null;
        }
    }
}
