/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model;

import de.fub.mapforgeproject.api.process.AbstractProcess;
import static de.fub.mapforgeproject.api.process.ProcessState.ERROR;
import static de.fub.mapforgeproject.api.process.ProcessState.INACTIVE;
import static de.fub.mapforgeproject.api.process.ProcessState.RUNNING;
import de.fub.mapsforge.project.detector.model.Detector;
import de.fub.utilsmodule.icons.IconRegister;
import java.awt.Image;
import org.openide.nodes.Node;
import org.openide.util.Cancellable;
import org.openide.util.ImageUtilities;

/**
 *
 * @author Serdar
 */
public abstract class DetectorProcess<I, O> extends AbstractProcess<I, O> implements Cancellable {

    private final Detector detector;
    private Node node;

    public DetectorProcess(Detector detector) {
        this.detector = detector;
    }

    protected Detector getDetector() {
        return detector;
    }

    @Override
    public final Node getNodeDelegate() {
        if (node == null) {
            node = createNodeDelegate();
        }
        return node;
    }

    @Override
    public Image getIcon() {
        Image image = null;
        switch (getProcessState()) {
            case ERROR:
                image = IconRegister.findRegisteredIcon("errorHintIcon.png");
                if (image != null) {
                    return ImageUtilities.mergeImages(getDefaultImage(), image, 0, 0);
                }
                break;
            case INACTIVE:
                break;
            case RUNNING:
                image = IconRegister.findRegisteredIcon("playHintIcon.png");
                if (image != null) {
                    return ImageUtilities.mergeImages(getDefaultImage(), image, 0, 0);
                }
                break;
            default:

                break;
        }

        return getDefaultImage();
    }

    protected abstract Node createNodeDelegate();

    protected abstract Image getDefaultImage();
}
