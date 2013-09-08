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
package de.fub.maps.project.plugins.tasks.map;

import de.fub.maps.project.models.Aggregator;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.cookies.OpenCookie;
import org.openide.loaders.DataObject;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;

@ActionID(
        category = "Detector",
        id = "de.fub.maps.project.plugins.tasks.OpenMapsAction")
@ActionRegistration(
        displayName = "#CTL_OpenMapsAction")
@ActionReference(path = "Projects/Maps/Detector/Tasks/MapRenderer/Actions", position = 1000)
@Messages("CTL_OpenMapsAction=View Maps")
public final class OpenMapsAction implements ActionListener {

    private final Aggregator aggregator;
    private static final RequestProcessor requestProcessor = new RequestProcessor(OpenMapsAction.class.getName());

    public OpenMapsAction(Aggregator aggregator) {
        this.aggregator = aggregator;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        requestProcessor.post(new OpenEditorTask(aggregator));
    }

    private static class OpenEditorTask implements Runnable {

        private final Aggregator aggregator;

        public OpenEditorTask(Aggregator aggregator) {
            this.aggregator = aggregator;
        }

        @Override
        public void run() {
            DataObject dataObject = aggregator.getDataObject();
            dataObject.getNodeDelegate();
            OpenCookie openCookie = dataObject.getLookup().lookup(OpenCookie.class);
            if (openCookie != null) {
                openCookie.open();
            }
        }
    }
}
