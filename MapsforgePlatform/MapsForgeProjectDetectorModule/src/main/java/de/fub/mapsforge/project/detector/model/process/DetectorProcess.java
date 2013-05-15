/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.model.process;

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

    private Detector detector;
    private Node node;

    public DetectorProcess() {
    }

    protected Detector getDetector() {
        return detector;
    }

    protected void setDetector(Detector detector) {
        this.detector = detector;
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
            case SETTING_ERROR:
                image = IconRegister.findRegisteredIcon("errorHintIcon.png");
                if (image != null) {
                    return ImageUtilities.mergeImages(getDefaultImage(), image, 0, 0);
                }
                break;
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

    protected static synchronized <T extends DetectorProcess> T find(Class<T> clazz, Detector detector) throws DetectorProcessNotFoundException {
        T detectorProcess = null;
        try {
            detectorProcess = clazz.newInstance();
            detectorProcess.setDetector(detector);
        } catch (Throwable ex) {
            throw new DetectorProcessNotFoundException(ex);
        }

        return detectorProcess;
    }

    public static class DetectorProcessNotFoundException extends Exception {

        private static final long serialVersionUID = 1L;

        public DetectorProcessNotFoundException() {
        }

        public DetectorProcessNotFoundException(String message) {
            super(message);
        }

        public DetectorProcessNotFoundException(String message, Throwable cause) {
            super(message, cause);
        }

        public DetectorProcessNotFoundException(Throwable cause) {
            super(cause);
        }
    }
}
