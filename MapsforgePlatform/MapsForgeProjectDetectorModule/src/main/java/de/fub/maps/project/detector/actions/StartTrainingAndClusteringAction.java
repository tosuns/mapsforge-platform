/*
 * Copyright (C) 2013 Serdar
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package de.fub.maps.project.detector.actions;

import de.fub.maps.project.detector.model.Detector;
import de.fub.maps.project.detector.model.inference.InferenceMode;
import de.fub.maps.project.detector.utils.DetectorUtils;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.OpenCookie;
import org.openide.util.NbBundle.Messages;

@ActionID(
        category = "Detector",
        id = "de.fub.maps.project.detector.actions.StartTrainingAndClusteringAction")
@ActionRegistration(
        displayName = "#CTL_StartTrainingAndClusteringAction")
@ActionReference(path = "Projects/org-maps-project/Detector/Start/Actions", position = 2000)
@Messages("CTL_StartTrainingAndClusteringAction=Training & Clustering")
public final class StartTrainingAndClusteringAction implements ActionListener {

    private final Detector detector;

    public StartTrainingAndClusteringAction(Detector detector) {
        this.detector = detector;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        OpenCookie openCookie = detector.getDataObject().getLookup().lookup(OpenCookie.class);
        if (openCookie != null) {
            openCookie.open();
        }
        DetectorUtils.getDefaultRequestProcessor().post(new Runnable() {
            @Override
            public void run() {
                detector.getInferenceModel().setInferenceMode(InferenceMode.ALL_MODE);
                detector.start();
            }
        });
    }
}
