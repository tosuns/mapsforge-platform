package de.fub.maps.project.project.aggregator.xml;

import de.fub.maps.project.aggregator.xml.ProcessDescriptor;
import de.fub.maps.project.aggregator.xml.PropertySection;
import de.fub.maps.project.aggregator.xml.Property;
import de.fub.maps.project.aggregator.xml.PropertySet;
import de.fub.maps.project.aggregator.xml.AggregatorDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import org.junit.Assert;
import org.junit.Test;
import org.openide.util.Exceptions;

/**
 *
 * @author Serdar
 */
public class AggregatorDescriptorTest {

    private static final Logger LOG = Logger.getLogger(AggregatorDescriptorTest.class.getName());

    public AggregatorDescriptorTest() {
    }

    @Test
    public void marshallAggregationDescriptorTest() {
        AggregatorDescriptor descriptor = new AggregatorDescriptor("Test", "Test Description");
        descriptor.setAggregationStrategy("de.fub.agg2graph.agg.strategy.DefaultAggregationStrategy");
        descriptor.setTileCachingStrategy("de.fub.agg2graph.agg.tiling.DefaultCachingStrategy");
        ProcessDescriptor processDescriptor = new ProcessDescriptor("de.fub.mapsforge.project.aggregator.pipeline.processes.CleanProcess", "Cleaner", "A simple GPS segment cleaner");
        PropertySection propertySection = new PropertySection("propsection", "description");
        PropertySet propertySet = new PropertySet("propSet", "description");
        Property property = new Property("propname", "propvalue");
        propertySet.getProperties().add(property);
        propertySection.getPropertySet().add(propertySet);
        processDescriptor.getProperties().getSections().add(propertySection);
        descriptor.getPipeline().getList().add(processDescriptor);
        descriptor.getPipeline().getList().add(new ProcessDescriptor("de.fub.mapsforge.project.aggregator.pipeline.processes.AggregationProcess", "Aggregation", "Aggregation process"));
        descriptor.getPipeline().getList().add(new ProcessDescriptor("de.fub.mapsforge.project.aggregator.pipeline.processes.RoadNetworkProcess", "Road Generator", "Road Generation process"));
        try {
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(AggregatorDescriptor.class);
            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            StringWriter stringWriter = new StringWriter();
            marshaller.marshal(descriptor, stringWriter);
            LOG.info(stringWriter.toString());
            javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            StringReader stringReader = new StringReader(stringWriter.toString());
            descriptor = (AggregatorDescriptor) unmarshaller.unmarshal(stringReader); //NOI18N
            marshaller.marshal(descriptor, stringWriter);
            LOG.info(stringWriter.toString());
        } catch (javax.xml.bind.JAXBException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void unmarshallAggregationDescriptorTest() {
        InputStream resourceAsStream = null;
        try {
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(AggregatorDescriptor.class);
            javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            resourceAsStream = AggregatorDescriptorTest.class.getResourceAsStream("/de/fub/mapsforge/project/aggregator/filetype/AggregationBuilderTemplate.agg");
            Assert.assertNotNull(resourceAsStream);
            AggregatorDescriptor descriptor = (AggregatorDescriptor) unmarshaller.unmarshal(resourceAsStream); //NOI18N
            Assert.assertNotNull(descriptor);
            Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(descriptor, System.out);
        } catch (javax.xml.bind.JAXBException ex) {
            Logger.getLogger(AggregatorDescriptorTest.class.getName()).log(Level.INFO, ex.getMessage(), ex);
            Assert.fail(ex.getMessage());
        } finally {
            if (resourceAsStream != null) {
                try {
                    resourceAsStream.close();
                } catch (IOException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }

    @Test
    public void unmarshallProcessDescriptorTest() {
        try {
            ProcessDescriptor unmarshallProcessDescriptor = unmarshallProcessDescriptor();
            Assert.assertNotNull(unmarshallProcessDescriptor);
        } catch (IOException ex) {
            Assert.fail(ex.getMessage());
        } catch (JAXBException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    @Test
    public void marshallProcessDescriptorTest() {
        try {
            ProcessDescriptor processDescriptor = unmarshallProcessDescriptor();
            Assert.assertNotNull(processDescriptor);
            StringWriter stringWriter = new StringWriter();
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(ProcessDescriptor.class);
            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(processDescriptor, stringWriter);
            LOG.info(stringWriter.toString());
        } catch (IOException ex) {
            Assert.fail(ex.getMessage());
        } catch (JAXBException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    private ProcessDescriptor unmarshallProcessDescriptor() throws IOException, JAXBException {
        ProcessDescriptor processDescriptor;
        InputStream inputStream = ProcessDescriptor.class.getResourceAsStream("/de/fub/mapsforge/project/aggregator/pipeline/processes/CleanProcess.xml");
        if (inputStream != null) {
            try {
                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(ProcessDescriptor.class);
                javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
                processDescriptor = (ProcessDescriptor) unmarshaller.unmarshal(inputStream); //NOI18N
            } finally {
                inputStream.close();
            }
        } else {
            throw new FileNotFoundException("/de/fub/mapsforge/project/aggregator/pipeline/processes/CleanProcess.xml not found.");
        }


        return processDescriptor;
    }
}
