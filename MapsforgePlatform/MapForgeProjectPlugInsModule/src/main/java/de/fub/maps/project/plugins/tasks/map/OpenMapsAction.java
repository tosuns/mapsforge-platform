/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
