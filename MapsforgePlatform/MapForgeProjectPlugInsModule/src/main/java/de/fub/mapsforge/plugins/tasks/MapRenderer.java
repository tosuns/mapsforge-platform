/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.tasks;

import de.fub.gpxmodule.xml.Gpx;
import de.fub.mapforgeproject.api.process.ProcessState;
import de.fub.mapsforge.project.aggregator.pipeline.AggregatorProcessPipeline;
import de.fub.mapsforge.project.aggregator.xml.Source;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.inference.InferenceModelResultDataSet;
import de.fub.mapsforge.project.detector.model.pipeline.postprocessors.tasks.Task;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.Properties;
import de.fub.mapsforge.project.detector.model.xmls.Property;
import de.fub.mapsforge.project.models.Aggregator;
import de.fub.mapsforge.project.utils.AggregatorUtils;
import de.fub.utilsmodule.node.property.ProcessProperty;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.awt.Component;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyEditor;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import javax.swing.Action;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.xml.bind.JAXBException;
import org.openide.cookies.OpenCookie;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectNotFoundException;
import org.openide.nodes.Node;
import org.openide.nodes.PropertySupport.ReadWrite;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Serdar
 */
@NbBundle.Messages({
    "CLT_MapRenderer_Name=Map Renderer",
    "CLT_MapRenderer_Description=Renders Gps data with the help of an Aggregator"
})
@ServiceProvider(service = Task.class)
public class MapRenderer extends Task {

    private static final String PROP_NAME_AGGREGATOR_FILE_PATH = "maprenderer.aggregator.file.path";
    private static final String PROP_NAME_OPEN_AFTER_FINISHED = "maprenderer.open.after.finished";
    private FileObject aggregatorFileObject = null;
    private List<Aggregator> aggregatorList = new ArrayList<Aggregator>();

    public MapRenderer() {
        this(null);
    }

    public MapRenderer(Detector detector) {
        super(detector);
        validate();
    }

    public List<Aggregator> getAggregatorList() {
        return aggregatorList;
    }

    private void validate() {
        setProcessState(ProcessState.INACTIVE);
        try {
            Aggregator createAggregator = createAggregator();
            if (createAggregator == null) {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException ex) {
            setProcessState(ProcessState.SETTING_ERROR);
        }
    }

    private boolean isOpenAfterFinished() {
        ProcessDescriptor processDescriptor = getProcessDescriptor();
        if (processDescriptor != null) {
            for (Property property : processDescriptor.getProperties().getPropertyList()) {
                if (PROP_NAME_OPEN_AFTER_FINISHED.equals(property.getId())) {
                    return Boolean.parseBoolean(property.getValue());
                }
            }
        }
        return false;
    }

    private Aggregator createAggregator() throws FileNotFoundException {
        ProcessDescriptor processDescriptor = getProcessDescriptor();
        if (processDescriptor != null) {
            for (Property propery : processDescriptor.getProperties().getPropertyList()) {
                if (PROP_NAME_AGGREGATOR_FILE_PATH.equalsIgnoreCase(propery.getId())) {
                    String pathString = propery.getValue();
                    if (pathString != null) {
                        FileObject aggregatorFile = FileUtil.getConfigFile(pathString);
                        if (aggregatorFile.isValid()) {
                            File createAggregatorCopy = createAggregatorCopy(aggregatorFile);
                            if (createAggregatorCopy != null) {
                                aggregatorFileObject = FileUtil.toFileObject(createAggregatorCopy);

                                if (aggregatorFileObject != null) {
                                    return AggregatorUtils.createAggregator(aggregatorFileObject);
                                }
                            } else {
                                propery.setValue(null);
                                throw new FileNotFoundException(MessageFormat.format("aggregator {0} does not exist!", aggregatorFile.getPath()));
                            }
                        } else {
                            propery.setValue(null);
                            throw new FileNotFoundException(MessageFormat.format("aggregator {0} does not exist!", aggregatorFile.getPath()));
                        }
                    }
                }
            }
        }
        return null;
    }

    private File createAggregatorCopy(FileObject fileObject) {
        File copyFileObject = null;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            copyFileObject = File.createTempFile("tmp", ".agg");
            inputStream = fileObject.getInputStream();
            outputStream = new FileOutputStream(copyFileObject);
            FileUtil.copy(inputStream, outputStream);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return copyFileObject;
    }

    @Override
    protected void start() {
        aggregatorList.clear();
        try {
            Aggregator aggregator = createAggregator();
            if (aggregator != null && aggregator.getAggregatorDescriptor() != null) {

                InferenceModelResultDataSet resultDataSet = getResultDataSet();
                for (Entry<String, List<Gpx>> entry : resultDataSet.entrySet()) {
                    try {
                        if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                            aggregator.getAggregatorDescriptor().setName(entry.getKey());

                            List<Source> sourceList = aggregator.getSourceList();
                            sourceList.clear();

                            FileObject createTempFolder = createTempFolder(URLEncoder.encode(MessageFormat.format("Transportation: {0}", entry.getKey()), "UTF-8"));

                            for (Gpx gpx : entry.getValue()) {
                                File tmpFile = createTmpfile(createTempFolder);
                                try {
                                    AggregatorUtils.saveGpxToFile(tmpFile, gpx);
                                    sourceList.add(new Source(tmpFile.getAbsolutePath()));
                                } catch (JAXBException ex) {
                                    Exceptions.printStackTrace(ex);
                                }
                            }
                            aggregator.updateSource();
                            aggregator.start();
                            aggregatorList.add(aggregator);
                        }
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    } finally {
                        aggregator = createAggregator();
                    }
                }

                if (isOpenAfterFinished()) {
                    new OpenEditorTask(aggregatorList).run();
                }
            } else {
                setProcessState(ProcessState.SETTING_ERROR);
            }
        } catch (FileNotFoundException ex) {
            throw new AggregatorProcessPipeline.PipelineException(ex.getMessage(), ex);
        }
    }

    private File createTmpfile(FileObject parentFolder) throws IOException {
        File tmpFile = File.createTempFile("tmp", ".gpx", FileUtil.toFile(parentFolder));
        tmpFile.deleteOnExit();
        return tmpFile;
    }

    private FileObject createTempFolder(String name) throws IOException {
        String tmpdir = System.getProperty("java.io.tmpdir");
        if (tmpdir != null) {
            File tmpdirFile = new File(tmpdir);
            if (tmpdirFile.exists()) {
                FileObject fileObject = FileUtil.toFileObject(tmpdirFile);
                if (fileObject.getFileObject(hashCode() + name) == null) {
                    return fileObject.createFolder(hashCode() + name);
                } else {
                    return fileObject.getFileObject(hashCode() + name);
                }
            }
        }
        throw new FileNotFoundException("Couldn't find temp dir!"); // NO18N
    }

    @Override
    public JComponent getSettingsView() {
        return null;
    }

    @Override
    public String getName() {
        if (getProcessDescriptor() != null && getProcessDescriptor().getName() != null) {
            return getProcessDescriptor().getName();
        }
        return Bundle.CLT_MapRenderer_Name();
    }

    @Override
    public String getDescription() {
        if (getProcessDescriptor() != null && getProcessDescriptor().getDescription() != null) {
            return getProcessDescriptor().getDescription();
        }
        return Bundle.CLT_MapRenderer_Description();
    }

    @Override
    protected Node createNodeDelegate() {
        return new MapRendererNode(MapRenderer.this);
    }

    static class OpenEditorTask implements Runnable {

        private final List<Aggregator> aggregatorList;

        public OpenEditorTask(List<Aggregator> aggregatorList) {
            this.aggregatorList = aggregatorList;
        }

        @Override
        public void run() {
            for (Aggregator aggregator : aggregatorList) {
                DataObject dataObject = aggregator.getDataObject();
                dataObject.getNodeDelegate();
                OpenCookie openCookie = dataObject.getLookup().lookup(OpenCookie.class);
                if (openCookie != null) {
                    openCookie.open();
                }
            }
        }
    }

    private static class MapRendererNode extends TaskProcessNode implements ChangeListener {

        private final MapRenderer mapRendererProcess;
        private final ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;

        public MapRendererNode(MapRenderer mapRendererProcess) {
            super(mapRendererProcess);
            this.mapRendererProcess = mapRendererProcess;
            modelSynchronizerClient = this.mapRendererProcess.getDetector().create(MapRendererNode.this);
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();
            Sheet.Set set = Sheet.createPropertiesSet();
            sheet.put(set);
            ProcessDescriptor processDescriptor = mapRendererProcess.getProcessDescriptor();
            Properties properties = processDescriptor.getProperties();
            Property<?> property = null;
            for (de.fub.mapsforge.project.detector.model.xmls.Property xmlProperty : properties.getPropertyList()) {
                if (MapRenderer.PROP_NAME_AGGREGATOR_FILE_PATH.equals(xmlProperty.getId())) {
                    property = new AggregatorDataObjectProperty(modelSynchronizerClient, xmlProperty);
                    set.put(property);
                } else if (MapRenderer.PROP_NAME_OPEN_AFTER_FINISHED.equals(xmlProperty.getId())) {
                    property = new ProcessProperty(modelSynchronizerClient, xmlProperty);
                    set.put(property);
                }
            }
            return sheet;
        }

        @Override
        public Action[] getActions(boolean context) {
            List<? extends Action> actionsForPath = Utilities.actionsForPath("Projects/Mapsforge/Detector/Tasks/MapRenderer/Actions");
            return actionsForPath.toArray(new Action[actionsForPath.size()]);
        }

        @Override
        public void stateChanged(ChangeEvent e) {
        }
    }

    private static class AggregatorDataObjectProperty extends ReadWrite<DataObject> implements PropertyChangeListener {

        private final ModelSynchronizer.ModelSynchronizerClient client;
        private final Property property;
        private DataObject value;
        private AggregatorChooserPanel chooserPanel;
        private AggregatorDataObjectPropertyEditor editor;

        public AggregatorDataObjectProperty(ModelSynchronizer.ModelSynchronizerClient client, de.fub.mapsforge.project.detector.model.xmls.Property xmlProperty) {
            super(xmlProperty.getId(), DataObject.class, xmlProperty.getName(), xmlProperty.getDescription());
            this.client = client;
            this.property = xmlProperty;
            init();
        }

        private void init() {
            if (property.getValue() != null) {
                String normalizePath = FileUtil.normalizePath(property.getValue());
                File file = new File(normalizePath);
                FileObject fileObject = null;
                if (file.exists()) {
                    fileObject = FileUtil.toFileObject(file);
                } else {
                    // the file exists not as a last attempt look into
                    // the fsf of netbeans.
                    fileObject = FileUtil.getConfigFile(property.getValue());
                }
                if (fileObject != null && fileObject.isValid()) {
                    try {
                        value = DataObject.find(fileObject);
                    } catch (DataObjectNotFoundException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }
        }

        @Override
        public PropertyEditor getPropertyEditor() {
            if (editor == null) {
                editor = new AggregatorDataObjectPropertyEditor();
                Component customEditor = editor.getCustomEditor();
                if (customEditor instanceof AggregatorChooserPanel) {
                    chooserPanel = (AggregatorChooserPanel) customEditor;
                    chooserPanel.addPropertyChangeListener(WeakListeners.propertyChange(AggregatorDataObjectProperty.this, chooserPanel));
                }
            }
            return editor;
        }

        @Override
        public DataObject getValue() throws IllegalAccessException, InvocationTargetException {
            return value;
        }

        @Override
        public void setValue(DataObject val) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
            if (this.value != val) {
                this.value = val;
                if (value != null) {
                    property.setValue(value.getPrimaryFile().getPath());
                } else {
                    property.setValue(null);
                }
            }
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (AggregatorChooserPanel.PROP_NAME_ACTIVE.equals(evt.getPropertyName())) {
                if (evt.getNewValue() instanceof Boolean) {
                    boolean active = (Boolean) evt.getNewValue();
                    if (!active) {
                        client.modelChangedFromGui();
                    }
                }
            }
        }
    }
}
