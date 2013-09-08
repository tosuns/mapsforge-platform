/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.fub.maps.gpx.analysis.actions;

import de.fub.gpxmodule.xml.Trkseg;
import de.fub.maps.gpx.analysis.models.GpxTrackSegmentStatistic;
import de.fub.maps.gpx.analysis.ui.GpxTrkSegAnalysizerTopComponent;
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
        @ActionReference(id
                = @ActionID(
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
