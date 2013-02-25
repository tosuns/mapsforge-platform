/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.models.xmls;

import de.fub.utilsmodule.xml.generator.SchemaGenertator;
import java.io.IOException;
import javax.xml.bind.JAXBException;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Serdar
 */
public class DetectorDescriptorGeneratorTest {

    @Test
    public void generateTest() {
        SchemaGenertator<DetectorDescriptor> schemaGenertator = new de.fub.utilsmodule.xml.generator.SchemaGenertator<DetectorDescriptor>(DetectorDescriptor.class);
        try {
            schemaGenertator.generatateSchemas();
        } catch (JAXBException ex) {
            Assert.fail(ex.getMessage());
        } catch (IOException ex) {
            Assert.fail(ex.getMessage());
        }
    }
}
