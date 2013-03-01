/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.models.xmls;

import de.fub.mapsforge.project.detector.model.xmls.DetectorDescriptor;
import de.fub.utilsmodule.xml.generator.SchemaGenertator;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Serdar
 */
public class DetectorDescriptorGeneratorTest {

    private static final Logger LOG = Logger.getLogger(DetectorDescriptorGeneratorTest.class.getName());

    @Test
    public void generateTest() {
        SchemaGenertator<DetectorDescriptor> schemaGenertator = new de.fub.utilsmodule.xml.generator.SchemaGenertator<DetectorDescriptor>(DetectorDescriptor.class);
        try {
            List<String> generatateSchemas = schemaGenertator.generatateSchemas();
            for (String string : generatateSchemas) {
                LOG.log(Level.INFO, "\n{0}", string);
            }
        } catch (JAXBException ex) {
            Assert.fail(ex.getMessage());
        } catch (IOException ex) {
            Assert.fail(ex.getMessage());
        }
    }
}
