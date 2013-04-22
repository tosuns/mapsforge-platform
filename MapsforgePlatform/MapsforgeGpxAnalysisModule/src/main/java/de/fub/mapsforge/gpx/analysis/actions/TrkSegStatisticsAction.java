/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.gpx.analysis.actions;

import de.fub.gpxmodule.xml.Trkseg;
import de.fub.mapsforge.gpx.analysis.models.GpxTrackSegmentStatistic;
import de.fub.mapsforge.gpx.analysis.ui.GpxTrkSegAnalysizerTopComponent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.SwingUtilities;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "GPX",
        id = "de.fub.gpxmodule.actions.StatisticsAction")
@ActionReferences(
        @ActionReference(id =
        @ActionID(
        category = "GPX",
        id = "de.fub.gpxmodule.actions.StatisticsAction"),
        path = "Projects/Mapsforge/Module/GpxFile/Trknode/Actions"))
@ActionRegistration(
        displayName = "#CTL_StatisticsAction")
@Messages("CTL_StatisticsAction=Statistics")
public final class TrkSegStatisticsAction implements ActionListener {

    private final Trkseg context;

    public TrkSegStatisticsAction(Trkseg context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                GpxTrkSegAnalysizerTopComponent tc = new GpxTrkSegAnalysizerTopComponent(new GpxTrackSegmentStatistic(context));
                tc.open();
                tc.requestVisible();
            }
        });
    }
}
