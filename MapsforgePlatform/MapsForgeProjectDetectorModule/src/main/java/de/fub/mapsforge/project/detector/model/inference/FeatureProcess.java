/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.inference;

import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.mapsforge.project.detector.model.pipeline.AbstractDetectorProcess;
import de.fub.utilsmodule.icons.IconRegister;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Serdar
 */
public abstract class FeatureProcess<I, O> extends AbstractDetectorProcess<I, O> {

    private static Image defaultImage;

    public FeatureProcess() {
        super(null);
    }

    public FeatureProcess(Detector detector) {
        super(detector);
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

    private static class FeatureNode extends AbstractNode {

        private final FeatureProcess<?, ?> filterProcess;

        public FeatureNode(FeatureProcess<?, ?> filterProcess) {
            super(Children.LEAF, Lookups.fixed(filterProcess));
            this.filterProcess = filterProcess;
            setDisplayName(filterProcess.getName());
            setShortDescription(filterProcess.getDescription());
        }
    }
}
