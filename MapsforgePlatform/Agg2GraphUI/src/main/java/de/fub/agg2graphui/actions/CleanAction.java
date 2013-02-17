/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.agg2graphui.actions;

import de.fub.agg2graphui.AggTopComponent;
import de.fub.gpxmodule.xml.Gpx;
import java.awt.event.ActionEvent;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;

/**
 *
 * @author Serdar
 */
public class CleanAction extends NodeAction implements LookupListener {

    private final AggTopComponent view;
    private Lookup.Result<Gpx> gpxResult;

    @NbBundle.Messages({"CLT_Clean_Name=Clean", "CLT_Clean_Description=Short Description"})
    public CleanAction(AggTopComponent view) {
        super(Children.LEAF);
        this.view = view;
//        gpxResult = view.getLookup().lookupResult(Gpx.class);
//        gpxResult.addLookupListener(WeakListeners.create(LookupListener.class, CleanAction.this, gpxResult));
        setDisplayName(Bundle.CLT_Clean_Name());
        setShortDescription(Bundle.CLT_Clean_Description());
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        setEnabled(gpxResult.allInstances().size() == 1);
    }
}
