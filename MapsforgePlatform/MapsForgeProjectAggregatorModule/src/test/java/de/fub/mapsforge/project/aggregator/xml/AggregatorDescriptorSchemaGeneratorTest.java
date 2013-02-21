/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.mapsforge.project.aggregator.xml;

import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import junit.framework.Assert;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 *
 * @author Serdar
 */
public class AggregatorDescriptorSchemaGeneratorTest {

    @Test
    public void schemaGenerator() {
        try {
            // grab the context
            JAXBContext context = JAXBContext.newInstance(AggregatorDescriptor.class);

            final List results = new ArrayList();

            // generate the schema
            context.generateSchema(new SchemaOutputResolverImpl(results));

            // output schema via System.out
            for (Object domr : results) {
                if (domr instanceof DOMResult) {
                    DOMResult domResult = (DOMResult) domr;
                    Document doc = (Document) domResult.getNode();
                    OutputFormat format = new OutputFormat(doc);
                    format.setIndenting(true);
                    XMLSerializer serializer = new XMLSerializer(System.out, format);
                    serializer.serialize(doc);
                }
            }
        } catch (JAXBException ex) {
            Assert.fail(ex.getMessage());
        } catch (IOException ex) {
            Assert.fail(ex.getMessage());
        }
    }

    private static class SchemaOutputResolverImpl extends SchemaOutputResolver {

        private final List results;

        public SchemaOutputResolverImpl(List results) {
            this.results = results;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Result createOutput(String ns, String file)
                throws IOException {
            // save the schema to the list
            DOMResult result = new DOMResult();
            result.setSystemId(file);
            results.add(result);
            return result;
        }
    }
}
