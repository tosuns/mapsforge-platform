/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.plugins.tasks.map;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Detector",
        id = "de.fub.mapsforge.plugins.tasks.map.ClearMapRendererAction")
@ActionRegistration(
        displayName = "#CTL_ClearMapRendererAction")
@ActionReference(path = "Project/Mapsforge/Plugin/Maprenderer/Actions")
@Messages("CTL_ClearMapRendererAction=Clear")
public final class ClearMapRendererAction implements ActionListener {

    private final MapRenderer context;

    public ClearMapRendererAction(MapRenderer context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        context.clear();
    }
}
