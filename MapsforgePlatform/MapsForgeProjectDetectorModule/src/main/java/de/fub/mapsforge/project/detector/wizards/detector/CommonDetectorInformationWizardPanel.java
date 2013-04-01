/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.wizards.detector;

import de.fub.mapsforge.project.detector.actions.NewDetectorWizardAction;
import java.io.File;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;
import org.openide.WizardDescriptor;
import org.openide.loaders.DataObject;
import org.openide.util.ChangeSupport;
import org.openide.util.HelpCtx;
import org.openide.util.WeakListeners;

public class CommonDetectorInformationWizardPanel implements WizardDescriptor.Panel<WizardDescriptor>, DocumentListener {

    /**
     * The visual component that displays this panel. If you need to access the
     * component from this class, just use getComponent().
     */
    private CommonDetectorInformationVisualPanel component;
    private final ChangeSupport cs = new ChangeSupport(this);
    public static final String PROP_NAME_DETECTOR_NAME = "wizard.detector.name";
    public static final String PROP_NAME_DETECTOR_DESCRIPTION = "wizard.detector.description";
    // Get the visual component for the panel. In this template, the component
    // is kept separate. This can be more efficient: if the wizard is created
    // but never displayed, or not all panels are displayed, it is better to
    // create only those which really need to be visible.
    private WizardDescriptor wiz;

    @Override
    public CommonDetectorInformationVisualPanel getComponent() {
        if (component == null) {

            component = new CommonDetectorInformationVisualPanel();
            Document document = component.getDetectorName().getDocument();
            document.addDocumentListener(WeakListeners.create(DocumentListener.class, CommonDetectorInformationWizardPanel.this, document));
        }
        return component;
    }

    @Override
    public HelpCtx getHelp() {
        // Show no Help button for this panel:
        return HelpCtx.DEFAULT_HELP;
        // If you have context help:
        // return new HelpCtx("help.key.here");
    }

    @Override
    public boolean isValid() {
        wiz.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);
        if (getComponent().getDetectorName().getText() != null
                && getComponent().getDetectorName().getText().length() > 0) {
            String filelocation = getComponent().getFilelocation().getText();
            if (filelocation != null) {
                File file = new File(filelocation);
                if (file.exists()) {
                    return true;
                }
            }
        }
        wiz.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, "The specified file exists already!");
        return false;
    }

    @Override
    public void addChangeListener(ChangeListener l) {
        cs.addChangeListener(l);
    }

    @Override
    public void removeChangeListener(ChangeListener l) {
        cs.removeChangeListener(l);
    }

    @Override
    public void readSettings(WizardDescriptor wiz) {
        this.wiz = wiz;
        wiz.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);
        getComponent().setFolder((DataObject) wiz.getProperty(NewDetectorWizardAction.PROP_NAME_DATAOBJECT));
        Object property = wiz.getProperty(PROP_NAME_DETECTOR_NAME);
        if (property instanceof String) {
            getComponent().getDetectorName().setText((String) property);
        }
        property = wiz.getProperty(PROP_NAME_DETECTOR_DESCRIPTION);
        if (property instanceof String) {
            getComponent().getDetectorDescription().setText((String) property);
        }
    }

    @Override
    public void storeSettings(WizardDescriptor wiz) {
        // use wiz.putProperty to remember current panel state
        wiz.putProperty(NewDetectorWizardAction.PROP_NAME_CREATE_VIA_TEMPLATE, getComponent().getViaDetectorTemplate().isSelected());
        wiz.putProperty(PROP_NAME_DETECTOR_NAME, getComponent().getDetectorName().getText());
        wiz.putProperty(PROP_NAME_DETECTOR_DESCRIPTION, getComponent().getDetectorDescription().getText());
        wiz.putProperty(WizardDescriptor.PROP_ERROR_MESSAGE, null);
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        cs.fireChange();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        cs.fireChange();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        cs.fireChange();
    }
}
