/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.actions;

import de.fub.maps.project.MapsProject;
import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.netbeans.api.project.Project;
import org.netbeans.spi.project.ui.support.CommonProjectActions;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Project",
        id = "de.fub.maps.project.actions.CloseProjectAction")
@ActionRegistration(
        displayName = "#CTL_CloseProjectAction")
@ActionReference(id =
        @ActionID(
        category = "Project",
        id = "de.fub.maps.project.actions.CloseProjectAction"),
        path = "Loaders/text/mapsproject+xml/Actions",
        position = 900, separatorAfter = 1000)
@Messages("CTL_CloseProjectAction=Close")
public final class CloseProjectAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final Action delegateCloseAction = CommonProjectActions.closeProjectAction();
    private transient final MapsProject context;

    public CloseProjectAction(Project context) {
        super();
        super.putValue(NAME, delegateCloseAction.getValue(NAME));
        if (context instanceof MapsProject) {
            this.context = (MapsProject) context;
            setEnabled(true);
        } else {
            this.context = null;
            setEnabled(false);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        delegateCloseAction.actionPerformed(e);
    }
}
