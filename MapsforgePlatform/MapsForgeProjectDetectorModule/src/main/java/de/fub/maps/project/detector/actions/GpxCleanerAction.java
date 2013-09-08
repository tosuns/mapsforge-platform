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
package de.fub.maps.project.detector.actions;

import de.fub.gpxmodule.GPXDataObject;
import de.fub.gpxmodule.xml.Gpx;
import de.fub.gpxmodule.xml.Trk;
import de.fub.gpxmodule.xml.Trkseg;
import de.fub.gpxmodule.xml.Wpt;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBException;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle.Messages;
import org.openide.util.RequestProcessor;

@ActionID(
        category = "Detector",
        id = "de.fub.maps.project.detector.actions.GpxCleanerAction")
@ActionRegistration(
        displayName = "#CTL_GpxCleanerAction")
@ActionReferences({
    @ActionReference(path = "Loaders/text/gpx+xml/Actions", position = 220)
})
@Messages("CTL_GpxCleanerAction=Clean")
public final class GpxCleanerAction implements ActionListener {

    private final List<GPXDataObject> context;

    public GpxCleanerAction(List<GPXDataObject> context) {
        this.context = context;
    }

    @Override
    public void actionPerformed(ActionEvent ev) {
        RequestProcessor.getDefault().post(new Runnable() {
            @Override
            public void run() {
                cleanGpxDataList();
            }
        });
    }

    private synchronized void cleanGpxDataList() {
        for (GPXDataObject gpxDataObject : context) {
            cleanGpx(gpxDataObject);
        }
    }

    private void cleanGpx(GPXDataObject dataObject) {
        Gpx gpx = dataObject.getGpx();
        if (gpx != null) {
            for (Trk track : gpx.getTrk()) {
                ArrayList<Trkseg> arrayList = new ArrayList<Trkseg>(track.getTrkseg().size());
                for (Trkseg trkseg : track.getTrkseg()) {
                    if (!trkseg.getTrkpt().isEmpty()) {
                        Wpt wpt = trkseg.getTrkpt().iterator().next();
                        if (wpt.getTime() != null) {
                            arrayList.add(trkseg);
                        }
                    }
                }
                track.getTrkseg().clear();
                track.getTrkseg().addAll(arrayList);
            }
        }
        saveGox(gpx, dataObject);

    }

    private void saveGox(Gpx gpx, GPXDataObject dataObject) {
        OutputStream out = null;

        try {
            FileObject fileObject = dataObject.getPrimaryFile();
            out = fileObject.getOutputStream();

            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(Gpx.class);
            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(gpx, out);

        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        } catch (JAXBException ex) {
            Exceptions.printStackTrace(ex);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }
}
