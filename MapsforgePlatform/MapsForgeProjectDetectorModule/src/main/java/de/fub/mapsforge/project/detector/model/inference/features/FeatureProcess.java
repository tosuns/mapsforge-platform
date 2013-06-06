/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference.features;

import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.gpx.TrackSegment;
import de.fub.mapsforge.project.detector.model.process.AbstractDetectorProcess;
import de.fub.mapsforge.project.detector.model.process.DetectorProcess;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
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
import javax.swing.JComponent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Serdar
 */
public abstract class FeatureProcess extends AbstractDetectorProcess<TrackSegment, Double> {

    private static Image defaultImage;

    public FeatureProcess() {
    }

    @Override
    protected Node createNodeDelegate() {
        return new FeatureNode(this);
    }

    @Override
    protected Image getDefaultImage() {
        return createDefaultImage();
    }

    private static synchronized Image createDefaultImage() {
        defaultImage = IconRegister.findRegisteredIcon("featureIcon.png");
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
    public JComponent getSettingsView() {
        return null;
    }

    @Override
    public boolean cancel() {
        return false;
    }

    public static Collection<FeatureProcess> findAll() {
        return findAll(FeatureProcess.class);
    }

    public static FeatureProcess find(ProcessDescriptor processDescriptor, Detector detector) throws DetectorProcessNotFoundException {
        assert processDescriptor != null;
        FeatureProcess featureProcess = find(processDescriptor.getJavaType(), detector);
        if (featureProcess != null) {
            featureProcess.setProcessDescriptor(processDescriptor);
        }
        return featureProcess;
    }

    public static synchronized FeatureProcess find(String qualifiedInstanceName, Detector detector) throws DetectorProcessNotFoundException {
        FeatureProcess featureProcess = null;
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
            if (FeatureProcess.class.isAssignableFrom(clazz)) {
                @SuppressWarnings("unchecked")
                Class<FeatureProcess> featureProcessClass = (Class<FeatureProcess>) clazz;
                featureProcess = DetectorProcess.find(featureProcessClass, detector);
            } else {
                throw new DetectorProcessNotFoundException(MessageFormat.format("{0} is not type of {1}", clazz.getSimpleName(), FeatureProcess.class.getSimpleName()));
            }

        } catch (Throwable ex) {
            throw new DetectorProcessNotFoundException(ex);
        }
        return featureProcess;
    }

    private static class FeatureNode extends CustomAbstractnode implements ChangeListener, PropertyChangeListener {

        private final FeatureProcess featureProcess;

        public FeatureNode(FeatureProcess filterProcess) {
            super(Children.LEAF, Lookups.fixed(filterProcess));
            this.featureProcess = filterProcess;
            setDisplayName(filterProcess.getProcessDescriptor().getName());
            setShortDescription(filterProcess.getProcessDescriptor().getDescription());
            this.featureProcess.addPropertyChangeListener(WeakListeners.propertyChange(FeatureNode.this, this.featureProcess));
        }

        @Override
        protected Sheet createSheet() {
            Sheet sheet = Sheet.createDefault();
            Sheet.Set set = Sheet.createPropertiesSet();
            sheet.put(set);
            if (featureProcess.getDetector() != null) {
                ModelSynchronizer.ModelSynchronizerClient msClient = featureProcess.getDetector().create(FeatureNode.this);
                ProcessDescriptor processDescriptor = featureProcess.getProcessDescriptor();
                if (processDescriptor != null) {
                    for (de.fub.mapsforge.project.detector.model.xmls.Property property : processDescriptor.getProperties().getPropertyList()) {
                        new ProcessProperty(msClient, property);
                    }
                }
            }
            return sheet;
        }

        @Override
        public Image getIcon(int type) {
            Image image = null;
            Image backgroundIcon = null;
            switch (this.featureProcess.getProcessState()) {
                case ERROR:
                    backgroundIcon = IconRegister.findRegisteredIcon("processIconError.png");
                    break;
                case INACTIVE:
                    backgroundIcon = IconRegister.findRegisteredIcon("processIconNormal.png");
                    break;
                case RUNNING:
                    backgroundIcon = IconRegister.findRegisteredIcon("processIconRun.png");
                    break;
                default:
                    throw new AssertionError();
            }

            if (backgroundIcon != null) {
                image = backgroundIcon;
            }

            return image != null ? image : super.getIcon(type);
        }

        @Override
        public Image getOpenedIcon(int type) {
            return getIcon(type);
        }

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (FeatureProcess.PROP_NAME_PROCESS_STATE.equals(evt.getPropertyName())) {
                fireIconChange();
            }
        }

        @Override
        public void stateChanged(ChangeEvent e) {
        }
    }
}
