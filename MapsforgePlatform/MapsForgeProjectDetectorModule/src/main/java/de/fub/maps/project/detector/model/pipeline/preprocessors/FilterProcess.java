/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.model.pipeline.preprocessors;

import de.fub.maps.project.detector.model.Detector;
import de.fub.maps.project.detector.model.gpx.TrackSegment;
import de.fub.maps.project.detector.model.inference.InferenceMode;
import de.fub.maps.project.detector.model.process.AbstractDetectorProcess;
import de.fub.maps.project.detector.model.process.DetectorProcess;
import de.fub.maps.project.detector.model.xmls.ProcessDescriptor;
import de.fub.utilsmodule.icons.IconRegister;
import de.fub.utilsmodule.node.CustomAbstractnode;
import de.fub.utilsmodule.node.property.ProcessProperty;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.Cancellable;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Serdar
 */
public abstract class FilterProcess extends AbstractDetectorProcess<List<TrackSegment>, List<TrackSegment>> implements Cancellable {

    protected static String PROP_NAME_FILTER_SCOPE = "filterprocess.scope";
    private static Image defaultImage;
    protected InferenceMode scope = InferenceMode.ALL_MODE;

    public FilterProcess() {
    }

    @Override
    protected Node createNodeDelegate() {
        return new FilterProcessNode(this);
    }

    public InferenceMode getScope() {
        return scope;
    }

    @Override
    public JComponent getSettingsView() {
        return null;
    }

    public FilterScope getFilterScope() {
        return FilterScope.TRAINING_AND_INFERENCE;
    }

    @Override
    protected Image getDefaultImage() {
        return createDefaultImage();
    }

    private static synchronized Image createDefaultImage() {
        defaultImage = IconRegister.findRegisteredIcon("filterIcon16.png");
        if (defaultImage == null) {
            defaultImage = new BufferedImage(16, 16, BufferedImage.TYPE_4BYTE_ABGR);
            Graphics graphics = defaultImage.getGraphics();
            if (graphics instanceof Graphics2D) {
                Graphics2D g2D = (Graphics2D) graphics;
                g2D.setPaint(new Color(255, 255, 255, 0));
                g2D.fillRect(0, 0, 16, 16);
                g2D.dispose();
            }
        }
        return defaultImage;
    }

    @Override
    public boolean cancel() {
        return false;
    }

    /**
     * This is a factory method to find and create all via
     * <code>@ServiceProvider</code> registered FilterProcesses.
     * @return A list of FilterProcess instances.
     */
    public static synchronized Collection<FilterProcess> findAll() {
        return findAll(FilterProcess.class);
    }

    /**
     *
     * @param descriptor
     * @param detector
     * @return
     * @throws
     * de.fub.maps.project.detector.model.process.DetectorProcess.DetectorProcessNotFoundException
     */
    public static synchronized FilterProcess find(ProcessDescriptor descriptor, Detector detector) throws DetectorProcessNotFoundException {
        assert descriptor != null;
        FilterProcess filterProcess = find(descriptor.getJavaType(), detector);
        if (filterProcess != null) {
            filterProcess.setProcessDescriptor(descriptor);
        }
        return filterProcess;
    }

    /**
     *
     * @param qualifiedInstanceName
     * @param detector
     * @return
     * @throws
     * de.fub.maps.project.detector.model.process.DetectorProcess.DetectorProcessNotFoundException
     */
    public static synchronized FilterProcess find(String qualifiedInstanceName, Detector detector) throws DetectorProcessNotFoundException {
        FilterProcess filterProcess = null;
        try {
            Class<?> clazz = null;
            ClassLoader classLoader = Lookup.getDefault().lookup(ClassLoader.class);
            // prefer netbeans classloader
            if (classLoader != null) {
                clazz = classLoader.loadClass(qualifiedInstanceName);
            } else {
                // fall back
                clazz = Class.forName(qualifiedInstanceName);
            }
            if (FilterProcess.class.isAssignableFrom(clazz)) {
                @SuppressWarnings("unchecked")
                Class<FilterProcess> filterProcessClass = (Class<FilterProcess>) clazz;
                filterProcess = DetectorProcess.find(filterProcessClass, detector);
            } else {
                throw new DetectorProcessNotFoundException(MessageFormat.format("{0} is not type of {1}", clazz.getSimpleName(), FilterProcess.class.getSimpleName()));
            }
        } catch (Throwable ex) {
            throw new DetectorProcessNotFoundException(ex);
        }
        return filterProcess;
    }

    /**
     * Default visual representer implementation of Filterprocess instances.
     */
    @NbBundle.Messages({"CLT_Filter_Parameter=Parameters"})
    private static class FilterProcessNode extends CustomAbstractnode implements PropertyChangeListener, ChangeListener {

        private final FilterProcess filterProcess;
        private Sheet.Set set;
        private ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;

        public FilterProcessNode(FilterProcess filterProcess) {
            super(Children.LEAF, Lookups.fixed(filterProcess));
            this.filterProcess = filterProcess;
            this.filterProcess.addPropertyChangeListener(WeakListeners.propertyChange(FilterProcessNode.this, this.filterProcess));
            setDisplayName(filterProcess.getName());
            setShortDescription(filterProcess.getDescription());

        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();
            if (filterProcess.getDetector() != null) {
                modelSynchronizerClient = filterProcess.getDetector().create(FilterProcessNode.this);
                set = Sheet.createPropertiesSet();
                set.setDisplayName(Bundle.CLT_Filter_Parameter());
                sheet.put(set);
                reinitSet();
            }
            return sheet;
        }

        private void reinitSet() {
            List<de.fub.maps.project.detector.model.xmls.Property> propertyList = filterProcess.getProcessDescriptor().getProperties().getPropertyList();
            ProcessProperty property = null;
            for (de.fub.maps.project.detector.model.xmls.Property xmlProperty : propertyList) {
                property = new ProcessProperty(modelSynchronizerClient, xmlProperty);
                set.put(property);
            }
        }

        @Override
        public Image getIcon(int type) {
            Image image = null;
            switch (this.filterProcess.getProcessState()) {
                case ERROR:
                    image = IconRegister.findRegisteredIcon("processIconError.png");
                    break;
                case INACTIVE:
                    image = IconRegister.findRegisteredIcon("processIconNormal.png");
                    break;
                case RUNNING:
                    image = IconRegister.findRegisteredIcon("processIconRun.png");
                    break;
                default:
                    throw new AssertionError();
            }

            return image != null ? image : super.getIcon(type);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (FilterProcess.PROP_NAME_PROCESS_STATE.equals(evt.getPropertyName())) {
                fireIconChange();
            }
        }

        @Override
        public void stateChanged(ChangeEvent e) {
            if (modelSynchronizerClient != null) {
                reinitSet();
            }
        }
    }

    public enum FilterScope {

        // Training = CrossValidation + Trainings
        TRAINING,
        // only inference
        INFERENCE,
        // training and inference
        TRAINING_AND_INFERENCE;
    }
}
