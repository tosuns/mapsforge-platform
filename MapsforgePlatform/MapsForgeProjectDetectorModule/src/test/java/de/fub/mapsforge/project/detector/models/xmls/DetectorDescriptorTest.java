/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.detector.models.xmls;

import de.fub.mapsforge.project.detector.model.xmls.DetectorDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.InferenceModelDescriptor;
import de.fub.mapsforge.project.detector.model.xmls.ProcessDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.text.MessageFormat;
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
            detectorDescriptor = unmarshall(DetectorDescriptor.class, "/de/fub/mapsforge/project/detector/model/xmls/DetectorTestTemplate.xml");
        } catch (JAXBException ex) {
            LOG.log(Level.INFO, ex.getMessage(), ex);
            Assert.fail(ex.getMessage());
        } catch (IOException ex) {
            LOG.log(Level.INFO, ex.getMessage(), ex);
            Assert.fail(ex.getMessage());
        }
        Assert.assertNotNull(detectorDescriptor);
    }

    private <T> T unmarshall(Class<T> clazz, String resourcePath) throws JAXBException, IOException {
        T detector = null;
        InputStream inputStream = DetectorDescriptor.class.getResourceAsStream(resourcePath);
        if (inputStream != null) {
            try {
                javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(clazz);
                javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
                detector = clazz.cast(unmarshaller.unmarshal(inputStream)); //NOI18N
            } finally {
                inputStream.close();
            }
        } else {
            throw new FileNotFoundException(MessageFormat.format("Couldn't find {0} file.", resourcePath));
        }
        return detector;
    }

    private <T> void marshall(Class<T> clazz, String resourcePath) {
        try {
            T detector = unmarshall(clazz, resourcePath);
            StringWriter stringWriter = new StringWriter();
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(clazz);
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

    @Test
    public void marshallTest() {
        marshall(DetectorDescriptor.class, "/de/fub/mapsforge/project/detector/model/xmls/DetectorTestTemplate.xml");
    }

    @Test
    public void marshallProcessUntTest() {
        // feature test
        marshall(ProcessDescriptor.class, "/de/fub/mapsforge/project/detector/model/inference/features/AvgAccelerationFeatureProcess.xml");
        marshall(ProcessDescriptor.class, "/de/fub/mapsforge/project/detector/model/inference/features/AvgBearingChangeFeatureProcess.xml");
        marshall(ProcessDescriptor.class, "/de/fub/mapsforge/project/detector/model/inference/features/AvgPrecisionFeatureProcess.xml");
        marshall(ProcessDescriptor.class, "/de/fub/mapsforge/project/detector/model/inference/features/AvgSpeedFeatureProcess.xml");
        marshall(ProcessDescriptor.class, "/de/fub/mapsforge/project/detector/model/inference/features/AvgTransportationDistanceFeatureProcess.xml");
        marshall(ProcessDescriptor.class, "/de/fub/mapsforge/project/detector/model/inference/features/MaxAccelerationFeatureProcess.xml");
        marshall(ProcessDescriptor.class, "/de/fub/mapsforge/project/detector/model/inference/features/MaxPrecisionFeatureProcess.xml");
        marshall(ProcessDescriptor.class, "/de/fub/mapsforge/project/detector/model/inference/features/MaxVelocityFeatureProcess.xml");
        marshall(ProcessDescriptor.class, "/de/fub/mapsforge/project/detector/model/inference/features/MinPrecisionFeatureProcess.xml");

        // filters
        marshall(ProcessDescriptor.class, "/de/fub/mapsforge/project/detector/model/pipeline/preprocessors/filters/LimitWaypointFilterProcess.xml");
        marshall(ProcessDescriptor.class, "/de/fub/mapsforge/project/detector/model/pipeline/preprocessors/filters/MinDistanceWaypointFilterProcess.xml");
        marshall(ProcessDescriptor.class, "/de/fub/mapsforge/project/detector/model/pipeline/preprocessors/filters/MinTimeDiffWaypointFilterProcess.xml");


        //Classifiers
        marshall(InferenceModelDescriptor.class, "/de/fub/mapsforge/project/detector/model/inference/impl/J48InferenceModel.xml");
        marshall(InferenceModelDescriptor.class, "/de/fub/mapsforge/project/detector/model/inference/impl/REPTreeInferenceModel.xml");
        marshall(InferenceModelDescriptor.class, "/de/fub/mapsforge/project/detector/model/inference/impl/RandomForestInferenceModel.xml");
        // task
//        marshall(ProcessDescriptor.class, "/de/fub/mapsforge/project/detector/model/pipeline/postprocessors/tasks/MapRenderer.xml");
    }

    public void marshallInferenceModelTest() {
        marshall(InferenceModelDescriptor.class, "/de/fub/mapsforge/project/detector/model/inference/impl/J48InferenceModel.xml");
        marshall(InferenceModelDescriptor.class, "/de/fub/mapsforge/project/detector/model/inference/impl/REPTreeInferenceModel.xml");
        marshall(InferenceModelDescriptor.class, "/de/fub/mapsforge/project/detector/model/inference/impl/RandomForestInferenceModel.xml");
    }
}
