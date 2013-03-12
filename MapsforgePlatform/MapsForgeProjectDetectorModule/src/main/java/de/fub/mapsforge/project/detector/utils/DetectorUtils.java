/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.utils;

import de.fub.mapforgeproject.utils.MapsForgeProjectUtils;
import de.fub.mapsforge.project.detector.filetype.DetectorDataObject;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.DetectorProcess;
import de.fub.mapsforge.project.detector.model.inference.AbstractInferenceModel;
import de.fub.mapsforge.project.detector.model.inference.processhandler.InferenceModelProcessHandler;
import de.fub.mapsforge.project.detector.model.xmls.DetectorDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.InferenceModelDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.ProcessHandlerDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.Property;
import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import javax.xml.bind.JAXBException;
import org.netbeans.api.project.Project;
import org.netbeans.api.project.ui.OpenProjects;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
public class DetectorUtils {

    private static final Object DETECTOR_FILE_OPERATION_MUTEX = new Object();

    @SuppressWarnings("unchecked")
    public static <T> T createInstance(Class<T> clazz, String className) {
        T instance = null;
        try {
            Class<?> forName = Class.forName(className);
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

    @NbBundle.Messages({"# {0} - filepath", "CLT_File_not_found=Couldn't find associated xml process descriptor file at path: {0}"})
    public static ProcessDescriptor getProcessDescriptor(Class<? extends DetectorProcess> processClass) throws IOException {
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

    public static InferenceModelDescriptor getInferenceModelDescriptor(Class<? extends AbstractInferenceModel> inferenceModelClass) throws IOException {
        InferenceModelDescriptor descriptor = null;
        String filePatn = MessageFormat.format("/{0}.xml", inferenceModelClass.getName().replaceAll("\\.", "/"));
        InputStream resourceAsStream = inferenceModelClass.getResourceAsStream(filePatn);
        if (resourceAsStream != null) {
            try {
                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(InferenceModelDescriptor.class);
                javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
                descriptor = (InferenceModelDescriptor) unmarshaller.unmarshal(resourceAsStream); //NOI18N
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

    @SuppressWarnings("unchecked")
    public static InferenceModelProcessHandler createProcessHandler(ProcessHandlerDescriptor descriptor, Detector detector) {
        InferenceModelProcessHandler model = null;
        try {
            if (descriptor != null && detector != null) {
                Class<?> clazz = Class.forName(descriptor.getJavaType());
                if (AbstractInferenceModel.class.isAssignableFrom(clazz)) {
                    Class<? extends InferenceModelProcessHandler> abstractInferenceModel = (Class<? extends InferenceModelProcessHandler>) clazz;
                    Constructor<? extends InferenceModelProcessHandler> constructor = abstractInferenceModel.getConstructor(Detector.class);
                    model = constructor.newInstance(detector);
                }
            }
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        } catch (SecurityException ex) {
            Exceptions.printStackTrace(ex);
        } catch (InstantiationException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IllegalArgumentException ex) {
            Exceptions.printStackTrace(ex);
        } catch (InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }

        return model;
    }

    @SuppressWarnings("unchecked")
    public static AbstractInferenceModel createInferenceModel(InferenceModelDescriptor descriptor, Detector detector) {
        AbstractInferenceModel model = null;
        try {
            if (descriptor != null && detector != null) {
                Class<?> clazz = Class.forName(descriptor.getJavaType());
                if (AbstractInferenceModel.class.isAssignableFrom(clazz)) {
                    Class<? extends AbstractInferenceModel> abstractInferenceModel = (Class<? extends AbstractInferenceModel>) clazz;
                    Constructor<? extends AbstractInferenceModel> constructor = abstractInferenceModel.getConstructor(Detector.class);
                    model = constructor.newInstance(detector);
                }
            }
        } catch (ClassNotFoundException ex) {
            Exceptions.printStackTrace(ex);
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        } catch (SecurityException ex) {
            Exceptions.printStackTrace(ex);
        } catch (InstantiationException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IllegalAccessException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IllegalArgumentException ex) {
            Exceptions.printStackTrace(ex);
        } catch (InvocationTargetException ex) {
            Exceptions.printStackTrace(ex);
        }

        return model;
    }

    public static synchronized Project findProject(FileObject fileObject) {
        return MapsForgeProjectUtils.findProject(fileObject);
    }

    public static FileObject findFileObject(FileObject detectorFileObject, String relativePathInProject) {
        FileObject fileObject = null;
        if (detectorFileObject != null && detectorFileObject.getParent() != null) {
            try {
                Project project = findProject(detectorFileObject);

                if (project != null) {
                    fileObject = project.getProjectDirectory().getFileObject(relativePathInProject);
                }
            } catch (IllegalArgumentException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            for (Project project : OpenProjects.getDefault().getOpenProjects()) {
                fileObject = project.getProjectDirectory().getFileObject(relativePathInProject);
                if (fileObject != null) {
                    break;
                }
            }
        }
        return fileObject;
    }

    /**
     * Method to override the original detector description with the copy
     * instance.
     *
     * @param original - the detector instance that will be over written with
     * the copy instance.
     * @param copy - detector copy instance.
     */
    public static void mergeDetector(Detector original, Detector copy) {
        saveDetector(original.getDataObject(), copy.getDetectorDescriptor());
    }

    public static void saveDetector(DetectorDataObject dataObject) throws JAXBException, IOException {
        saveDetector(dataObject, dataObject.getDetectorDescriptor());
    }

    public static void saveDetector(final DataObject dataObject, final DetectorDescriptor descriptor) {
        saveDetector(dataObject.getPrimaryFile(), descriptor);
    }

    public static void saveDetector(final FileObject fileObject, final DetectorDescriptor descriptor) {
        synchronized (DETECTOR_FILE_OPERATION_MUTEX) {
            FileUtil.runAtomicAction(new Runnable() {
                @Override
                public void run() {
                    File file = FileUtil.toFile(fileObject);
                    if (file != null) {
                        try {
                            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(DetectorDescriptor.class);
                            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
                            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
                            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                            marshaller.marshal(descriptor, file);
                        } catch (javax.xml.bind.JAXBException ex) {
                            Exceptions.printStackTrace(ex);
                        }
                    }
                }
            });
        }
    }

    public static DetectorDescriptor getDetectorDescriptor(DataObject dataObject) throws JAXBException, IOException {
        return getDetectorDescriptor(dataObject.getPrimaryFile());
    }

    public static DetectorDescriptor getDetectorDescriptor(FileObject fileObject) throws JAXBException, IOException {
        synchronized (DETECTOR_FILE_OPERATION_MUTEX) {
            DetectorDescriptor detectorDescriptor = null;
            InputStream inputStream = null;
            inputStream = fileObject.getInputStream();
            try {
                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(DetectorDescriptor.class);
                javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
                detectorDescriptor = (DetectorDescriptor) unmarshaller.unmarshal(inputStream); //NOI18N
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
            }
            return detectorDescriptor;
        }
    }

    public static Detector copyInstance(Detector detector) throws DetectorCopyException {
        Detector copy = null;
        DetectorDescriptor detectorDescriptor = detector.getDetectorDescriptor();
        try {
            if (detectorDescriptor == null) {
                throw new DetectorCopyException("descriptor of detector is null!");
            }
            File copyFile = File.createTempFile("detector", ".dec");
            FileObject fileObject = FileUtil.toFileObject(copyFile);
            saveDetector(fileObject, detectorDescriptor);
            DataObject dataObject = DataObject.find(fileObject);
            copy = dataObject.getNodeDelegate().getLookup().lookup(Detector.class);
            if (copy == null) {
                throw new DetectorCopyException("Failed to create a copy of detector " + detector.getDetectorDescriptor().getName());
            }
        } catch (IOException ex) {
            throw new DetectorCopyException(ex.getMessage(), ex);
        }

        return copy;
    }

    public static class DetectorCopyException extends Exception {

        private static final long serialVersionUID = 1L;

        public DetectorCopyException() {
        }

        public DetectorCopyException(String message) {
            super(message);
        }

        public DetectorCopyException(String message, Throwable cause) {
            super(message, cause);
        }

        public DetectorCopyException(Throwable cause) {
            super(cause);
        }

        public DetectorCopyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
            super(message, cause, enableSuppression, writableStackTrace);
        }
    }
}
