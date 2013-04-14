/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.tasks;

import java.awt.event.ActionEvent;
import java.util.Collection;
import javax.swing.AbstractAction;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle.Messages;
import org.openide.util.Utilities;

@ActionID(
        category = "Detector",
        id = "de.fub.mapsforge.plugins.tasks.OpenMapsAction")
@ActionRegistration(lazy = false,
        displayName = "#CTL_OpenMapsAction")
@ActionReference(path = "Projects/Mapsforge/Detector/Tasks/MapRenderer/Actions", position = 1000)
@Messages("CTL_OpenMapsAction=View Maps")
public final class OpenMapsAction extends AbstractAction implements LookupListener {

    private static final long serialVersionUID = 1L;
    private Lookup.Result<MapRenderer> lookupResult;

    public OpenMapsAction() {
        this(Utilities.actionsGlobalContext());
    }

    public OpenMapsAction(Lookup lookup) {
        super(Bundle.CTL_OpenMapsAction());
        lookupResult = lookup.lookupResult(MapRenderer.class);
        lookupResult.addLookupListener(OpenMapsAction.this);
        resultChanged(new LookupEvent(lookupResult));
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        MapRenderer mapRenderer = lookupResult.allInstances().iterator().next();
        new MapRenderer.OpenEditorTask(mapRenderer.getAggregatorList()).run();
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        Collection<? extends MapRenderer> allInstances = lookupResult.allInstances();
        if (allInstances.isEmpty()) {
            setEnabled(false);
        } else {
            MapRenderer mapRenderer = allInstances.iterator().next();
            setEnabled(mapRenderer != null && !mapRenderer.getAggregatorList().isEmpty());
        }
    }
}
