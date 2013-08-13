/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.maps.project.project.aggregator.xml;

import org.junit.Test;

/**
 *
 * @author Serdar
 */
public class AggregatorDescriptorSchemaGeneratorTest {

    @Test
    public void schemaGenerator() {
//        try {
//            // grab the context
//            JAXBContext context = JAXBContext.newInstance(AggregatorDescriptor.class);
//
//            final List<Result> results = new ArrayList<Result>();
//
//            // generate the schema
//            context.generateSchema(new SchemaOutputResolverImpl(results));
//
//            // output schema via System.out
//            for (Object domr : results) {
//                if (domr instanceof DOMResult) {
//                    DOMResult domResult = (DOMResult) domr;
//                    Document doc = (Document) domResult.getNode();
//                    OutputFormat format = new OutputFormat(doc);
//                    format.setIndenting(true);
//                    XMLSerializer serializer = new XMLSerializer(System.out, format);
//                    serializer.serialize(doc);
//                }
//            }
//        } catch (JAXBException ex) {
//            Assert.fail(ex.getMessage());
//        } catch (IOException ex) {
//            Assert.fail(ex.getMessage());
//        }
    }
//    private static class SchemaOutputResolverImpl extends SchemaOutputResolver {
//
//        private final List<Result> results;
//
//        public SchemaOutputResolverImpl(List<Result> results) {
//            this.results = results;
//        }
//
//        @Override
//        @SuppressWarnings("unchecked")
//        public Result createOutput(String ns, String file)
//                throws IOException {
//            // save the schema to the list
//            DOMResult result = new DOMResult();
//            result.setSystemId(file);
//            results.add(result);
//            return result;
//        }
//    }
}
