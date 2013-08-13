/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapforgeproject.xml;

import de.fub.maps.project.xml.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import org.junit.Assert;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author Serdar
 */
public class MapsXmlTest {

    public MapsXmlTest() {
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //

    @Test
    public void unmarshallTest() {
        Assert.assertNotNull(create());
    }

    private Maps create() {
        Maps project = null;
        InputStream inputStream = null;
        try {
            inputStream = Maps.class.getResourceAsStream(MessageFormat.format("/{0}/mapsforge.xml", Maps.class.getPackage().getName().replaceAll("\\.", "/")));
            Assert.assertNotNull(inputStream);

            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(Maps.class);
            javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            project = (Maps) unmarshaller.unmarshal(inputStream); //NOI18N


        } catch (javax.xml.bind.JAXBException ex) {
            // XXXTODO Handle exception
            java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
            Assert.fail(ex.getMessage());
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
        return project;
    }

    @Test
    public void marshallTest() {
        Maps project = create();
        Assert.assertNotNull(project);
        try {
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(Maps.class);
            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(project, System.out);
        } catch (javax.xml.bind.JAXBException ex) {
            // XXXTODO Handle exception
            java.util.logging.Logger.getLogger("global").log(java.util.logging.Level.SEVERE, null, ex); //NOI18N
            Assert.fail(ex.getMessage());
        }

    }
}
