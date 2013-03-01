/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.models.xmls;

import de.fub.mapsforge.project.detector.model.xmls.DetectorDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Serdar
 */
public class DetectorDescriptorTest {

    private static final Logger LOG = Logger.getLogger(DetectorDescriptorTest.class.getName());

    public DetectorDescriptorTest() {
    }

    @Test
    public void unmarshallTest() {
        DetectorDescriptor detectorDescriptor = null;
        try {
            detectorDescriptor = unmarshall();
        } catch (JAXBException ex) {
            LOG.log(Level.INFO, ex.getMessage(), ex);
            Assert.fail(ex.getMessage());
        } catch (IOException ex) {
            LOG.log(Level.INFO, ex.getMessage(), ex);
            Assert.fail(ex.getMessage());
        }
        Assert.assertNotNull(detectorDescriptor);
    }

    private DetectorDescriptor unmarshall() throws JAXBException, IOException {
        DetectorDescriptor detector = null;
        InputStream inputStream = DetectorDescriptor.class.getResourceAsStream("/de/fub/mapsforge/project/detector/models/xmls/DetectorTestTemplate.xml");
        if (inputStream != null) {
            try {
                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(DetectorDescriptor.class);
                javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
                detector = (DetectorDescriptor) unmarshaller.unmarshal(inputStream); //NOI18N
            } finally {
                inputStream.close();
            }
        } else {
            throw new FileNotFoundException("Couldn't find DetectorTestTemplate.xml file.");
        }
        return detector;
    }

    @Test
    public void marshallTest() {
        try {
            DetectorDescriptor detector = unmarshall();
            StringWriter stringWriter = new StringWriter();
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(DetectorDescriptor.class);
            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(detector, stringWriter);
            LOG.info(stringWriter.toString());
        } catch (JAXBException ex) {
            LOG.log(Level.INFO, ex.getMessage(), ex);
            Assert.fail(ex.getMessage());
        } catch (IOException ex) {
            LOG.log(Level.INFO, ex.getMessage(), ex);
            Assert.fail(ex.getMessage());
        }
    }
}
