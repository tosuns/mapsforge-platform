/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.tasks;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;

@ActionID(
        category = "Detector",
        id = "de.fub.mapsforge.plugins.tasks.OpenMapsAction")
@ActionRegistration(lazy = false,
        displayName = "#CTL_OpenMapsAction")
@ActionReference(path = "Projects/Mapsforge/Detector/Tasks/MapRenderer/Actions", position = 1000)
@Messages("CTL_OpenMapsAction=View Maps")
public final class OpenMapsAction extends AbstractAction {

    private static final long serialVersionUID = 1L;
    private final MapRenderer context;

    public OpenMapsAction() {
        this(Utilities.actionsGlobalContext());
    }

    public OpenMapsAction(Lookup lookup) {
        super(Bundle.CTL_OpenMapsAction());
        this.context = lookup.lookup(MapRenderer.class);
        setEnabled(this.context != null && !this.context.getAggregatorList().isEmpty());
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        new MapRenderer.OpenEditorTask(this.context.getAggregatorList()).run();
    }
}
