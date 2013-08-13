/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforgeplatform.openstreetmap.xml.osm;

import de.fub.maps.project.openstreetmap.xml.osm.Osm;
import de.fub.maps.project.openstreetmap.service.OpenstreetMapService;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.bind.JAXBException;
import org.junit.Assert;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author Serdar
 */
public class OsmTest {

    public OsmTest() {
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //

    @Test
    public void unmarshallTest() {
        InputStream resourceAsStream = OsmTest.class.getResourceAsStream("/de/fub/mapsforgeplatform/OsmTest.xml");
        if (resourceAsStream != null) {
            try {
                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(Osm.class);
                javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
                Osm osm = (Osm) unmarshaller.unmarshal(resourceAsStream); //NOI18N
                javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
                marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
                marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
                marshaller.marshal(osm, System.out);
            } catch (JAXBException ex) {
                Exceptions.printStackTrace(ex);
                Assert.fail(ex.getMessage());
            } finally {
                try {
                    resourceAsStream.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    @Test
    public void serviceTest() {
        try {
            OpenstreetMapService openstreetMapService = new OpenstreetMapService();
            Osm osmMap = openstreetMapService.getOSMHighwayMap(Osm.class, "13.5187958", "52.5321076", "13.51898731", "52.53313301");
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(Osm.class);
            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(osmMap, System.out);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
            Assert.fail(ex.getMessage());
        }
    }
}
