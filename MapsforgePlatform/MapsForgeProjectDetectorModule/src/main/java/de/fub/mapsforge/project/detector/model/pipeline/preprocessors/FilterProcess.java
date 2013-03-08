/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.pipeline.preprocessors;

import de.fub.mapsforge.project.detector.model.AbstractDetectorProcess;
import de.fub.mapsforge.project.detector.model.Detector;
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
import org.openide.util.Cancellable;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Serdar
 */
public abstract class FilterProcess<I, O> extends AbstractDetectorProcess<I, O> implements Cancellable {

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

    private static class FilterProcessNode extends AbstractNode {

        private final FilterProcess<?, ?> filterProcess;

        public FilterProcessNode(FilterProcess<?, ?> filterProcess) {
            super(Children.LEAF, Lookups.fixed(filterProcess));
            this.filterProcess = filterProcess;
            setDisplayName(filterProcess.getName());
            setShortDescription(filterProcess.getDescription());
        }
    }
}
