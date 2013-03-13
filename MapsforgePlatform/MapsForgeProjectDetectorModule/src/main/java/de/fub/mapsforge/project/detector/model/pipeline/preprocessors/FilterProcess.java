/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.pipeline.preprocessors;

import de.fub.agg2graph.structs.GPSTrack;
import de.fub.mapsforge.project.detector.model.AbstractDetectorProcess;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.utilsmodule.icons.IconRegister;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import javax.swing.JComponent;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Cancellable;
import org.openide.util.ImageUtilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Serdar
 */
public abstract class FilterProcess extends AbstractDetectorProcess<List<GPSTrack>, List<GPSTrack>> implements Cancellable {

    private static Image defaultImage;

    public FilterProcess() {
        super(null);
    }

    public FilterProcess(Detector detector) {
        super(detector);
    }

    @Override
    protected Node createNodeDelegate() {
        return new FilterProcessNode(this);
    }

    @Override
    public JComponent getSettingsView() {
        return null;
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

    private static class FilterProcessNode extends AbstractNode implements PropertyChangeListener {

        private final FilterProcess filterProcess;

        public FilterProcessNode(FilterProcess filterProcess) {
            super(Children.LEAF, Lookups.fixed(filterProcess));
            this.filterProcess = filterProcess;
            this.filterProcess.addPropertyChangeListener(WeakListeners.propertyChange(FilterProcessNode.this, this.filterProcess));
            setDisplayName(filterProcess.getName());
            setShortDescription(filterProcess.getDescription());
        }

        @Override
        public Image getIcon(int type) {
            Image image = null;
            Image backgroundIcon = null;
            Image overlayIcon = null;
            switch (this.filterProcess.getProcessState()) {
                case ERROR:
                    backgroundIcon = IconRegister.findRegisteredIcon("processIconError.png");
                    overlayIcon = IconRegister.findRegisteredIcon("errorHintIcon.png");
                    break;
                case INACTIVE:
                    backgroundIcon = IconRegister.findRegisteredIcon("processIconNormal.png");
                    break;
                case RUNNING:
                    backgroundIcon = IconRegister.findRegisteredIcon("processIconRun.png");
                    overlayIcon = IconRegister.findRegisteredIcon("playHintIcon.png");
                    break;
                default:
                    throw new AssertionError();
            }

            if (backgroundIcon != null && overlayIcon != null) {
                image = ImageUtilities.mergeImages(backgroundIcon, overlayIcon, 0, 0);
            } else if (backgroundIcon != null) {
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
            if (FilterProcess.PROP_NAME_PROCESS_STATE.equals(evt.getPropertyName())) {
                fireIconChange();
            }
        }
    }
}
