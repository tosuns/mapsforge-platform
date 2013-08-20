/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.actions;

import java.awt.event.ActionEvent;
import java.io.IOException;
import javax.swing.AbstractAction;
import javax.swing.SwingUtilities;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node.Cookie;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;

/**
 * Action implementation, which handles creation of a templates of a selected
 * DataObject. The DataObject must have a SaveAsTemplateHandler implementation
 * within its lookup.
 *
 * @author Serdar
 */
@ActionID(
        category = "System",
        id = "de.fub.utilsmodule.actions.SaveAsTemplateAction")
@ActionRegistration(
        displayName = "#CTL_SaveAsTemplateAction",
        lazy = false)
@Messages({
    "CTL_SaveAsTemplateAction=Save As Template...",
    "CLT_SaveAsTemplateAction_Dialog_Label_Text=Template Name:",
    "CLT_SaveAsTemplateAction_Dialog_Title_Text=Template Dialog"
})
public final class SaveAsTemplateAction extends AbstractAction implements LookupListener {

    private static final long serialVersionUID = 1L;
    private Lookup.Result<SaveAsTemplateHandler> result;

    public SaveAsTemplateAction() {
        super(Bundle.CTL_SaveAsTemplateAction());
        result = Utilities.actionsGlobalContext().lookupResult(SaveAsTemplateHandler.class);
        result.addLookupListener(SaveAsTemplateAction.this);
        resultChanged(new LookupEvent(result));
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        if (!result.allInstances().isEmpty()) {
            SaveAsTemplateHandler handler = result.allInstances().iterator().next();
            if (handler != null) {
                NotifyDescriptor.InputLine dd = new NotifyDescriptor.InputLine(
                        Bundle.CLT_SaveAsTemplateAction_Dialog_Label_Text(),
                        Bundle.CLT_SaveAsTemplateAction_Dialog_Title_Text());
                Object notify = DialogDisplayer.getDefault().notify(dd);
                if (NotifyDescriptor.InputLine.OK_OPTION.equals(notify)) {
                    try {
                        handler.createTemplate(dd.getInputText(), Utilities.actionsGlobalContext().lookup(DataObject.class));
                    } catch (IOException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            } else {
                // should never reach this!
            }
        }
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                setEnabled(!result.allInstances().isEmpty());
            }
        });
    }

    public interface SaveAsTemplateHandler extends Cookie {

        public void createTemplate(String templateName, DataObject templateFromThisDataObject) throws IOException;
    }
}
