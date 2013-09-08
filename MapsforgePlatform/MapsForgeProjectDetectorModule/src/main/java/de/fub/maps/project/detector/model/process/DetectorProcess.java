/*
 * Copyright (C) 2013 Serdar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fub.maps.project.detector.model.process;

import de.fub.maps.project.api.process.AbstractProcess;
import static de.fub.maps.project.api.process.ProcessState.ERROR;
import static de.fub.maps.project.api.process.ProcessState.INACTIVE;
import static de.fub.maps.project.api.process.ProcessState.RUNNING;
import de.fub.maps.project.detector.model.Detector;
import de.fub.utilsmodule.icons.IconRegister;
import java.awt.Image;
import org.openide.nodes.Node;
import org.openide.util.Cancellable;
import org.openide.util.ImageUtilities;

/**
 * The Root interface for all detector processes.
 *
 * @author Serdar
 */
public abstract class DetectorProcess<I, O> extends AbstractProcess<I, O> implements Cancellable {

    private Detector detector;
    private Node node;

    public DetectorProcess() {
    }

    /**
     * The Dector with which this DetectorProcess is associated with.
     *
     * @return A Detector instance or null.
     */
    protected Detector getDetector() {
        return detector;
    }

    /**
     * Set the Detector instance with which this Process instance will be
     * associated with.
     *
     * @param detector A Detector instance.
     */
    protected void setDetector(Detector detector) {
        this.detector = detector;
    }

    /**
     * Returns the visual representer of this DetectorProcess, which will be
     * used by the ui.
     *
     * @return a Node instance.
     */
    @Override
    public final Node getNodeDelegate() {
        if (node == null) {
            node = createNodeDelegate();
        }
        return node;
    }

    /**
     * The Icon which represent the this DetectorProcess. in the default
     * implementation this method provides an icon which indicates the current
     * state of this Process.
     *
     * @return an Image instance.
     */
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

    /**
     * Creates the visual representer for the ui.
     *
     * @return a Node instance.
     */
    protected abstract Node createNodeDelegate();

    /**
     * Provides the default icon for the visual representer.
     *
     * @return an Image instance.
     */
    protected abstract Image getDefaultImage();

    /**
     * Generic Factory method to create a DetectorProcess instance from the
     * provided class instance. The provided Detector instance will be
     * associated with the created DetectorProcess.
     *
     * @param <T> extends DetectorProcess
     * @param clazz The concrete type ot the to be created DetectorProcess.
     * @param detector The parent Detector that will be associated with the
     * process.
     * @return A DetectorProcess
     * @throws
     * de.fub.maps.project.detector.model.process.DetectorProcess.DetectorProcessNotFoundException
     * if the DetectorProcess couldn't be instanciated.
     */
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
