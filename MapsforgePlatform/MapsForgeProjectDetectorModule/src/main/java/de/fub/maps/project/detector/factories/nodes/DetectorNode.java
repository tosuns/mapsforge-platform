/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.detector.factories.nodes;

import de.fub.maps.project.detector.factories.DetectorSubNodeFactory;
import de.fub.maps.project.detector.model.Detector;
import de.fub.utilsmodule.icons.IconRegister;
import de.fub.utilsmodule.synchronizer.ModelSynchronizer;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import javax.swing.Action;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.openide.loaders.DataNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.WeakListeners;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

/**
 *
 * @author Serdar
 */
public class DetectorNode extends DataNode implements PropertyChangeListener, ChangeListener {

    private final ModelSynchronizer.ModelSynchronizerClient modelSynchronizerClient;
    private HashMap<String, Sheet.Set> setMap = new HashMap<String, Sheet.Set>();
    private Sheet sheet;
    private final Detector detector;

    public DetectorNode(Detector detector) {
        super(detector.getDataObject(), Children.create(new DetectorSubNodeFactory(detector), true),
                new ProxyLookup(Lookups.fixed(detector, detector.getDataObject()), detector.getLookup()));
        this.detector = detector;
        detector.addPropertyChangeListener(WeakListeners.propertyChange(DetectorNode.this, detector));
        modelSynchronizerClient = detector.create(DetectorNode.this);
        update();
        sheet = Sheet.createDefault();
    }

    private void update() {
        if (detector.getDetectorDescriptor() != null) {
            setDisplayName(detector.getDetectorDescriptor().getName());
        }
    }

    @Override
    public String getDisplayName() {
        String name = super.getDisplayName();
        if (detector != null && detector.getDetectorDescriptor() != null) {
            name = detector.getDetectorDescriptor().getName();
        }
        return name;
    }

    @Override
    public String getShortDescription() {
        String description = super.getShortDescription();
        if (detector != null && detector.getDetectorDescriptor() != null) {
            description = detector.getDetectorDescriptor().getDescription();
        }
        return description;
    }

    @Override
    public Action[] getActions(boolean context) {
        return super.getActions(context); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Action getPreferredAction() {
        return getActions(true)[0];
    }

    @Override
    public Image getIcon(int type) {
        Image image = super.getIcon(type);
        if (detector != null) {
            String hintName = null;

            switch (detector.getDetectorState()) {
                case ERROR:
                    hintName = "errorHintIcon.png";
                    break;
                case RUNNING:
                    hintName = "playHintIcon.png";
                    break;
                case INACTIVE:
                    break;
                default:
                    throw new AssertionError();
            }
            if (hintName != null) {
                Image hint = IconRegister.findRegisteredIcon(hintName);
                if (hint != null) {
                    image = ImageUtilities.mergeImages(image, hint, 0, 0);
                }
            }
        }
        return image;
    }

    @Override
    public Image getOpenedIcon(int type) {
        return getIcon(type);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (Detector.PROP_NAME_DETECTOR_STATE.equals(evt.getPropertyName())) {
            fireIconChange();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        for (PropertySet set : sheet.toArray()) {
            sheet.remove(set.getName());
        }
        setMap.clear();
        createSheet();
        for (Sheet.Set set : setMap.values()) {
            sheet.put(set);
        }
        update();
        fireIconChange();
    }
}
