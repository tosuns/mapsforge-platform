/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapforgeproject.xml;

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
public class MapsForgeXmlTest {

    public MapsForgeXmlTest() {
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //

    @Test
    public void unmarshallTest() {
        Assert.assertNotNull(create());
    }

    private MapsForge create() {
        MapsForge project = null;
        InputStream inputStream = null;
        try {
            inputStream = MapsForge.class.getResourceAsStream(MessageFormat.format("/{0}/mapsforge.xml", MapsForge.class.getPackage().getName().replaceAll("\\.", "/")));
            Assert.assertNotNull(inputStream);

            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(MapsForge.class);
            javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            project = (MapsForge) unmarshaller.unmarshal(inputStream); //NOI18N


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
        MapsForge project = create();
        Assert.assertNotNull(project);
        try {
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(MapsForge.class);
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
