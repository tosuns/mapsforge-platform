/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fub.utilsmodule.xml.generator;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.dom.DOMResult;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;

/**
 *
 * @author Serdar
 */
public class SchemaGenertator<T> {

    private final Class<T> clazz;
    private static final Logger LOG = Logger.getLogger(SchemaGenertator.class.getName());

    public SchemaGenertator(Class<T> clazz) {
        this.clazz = clazz;
    }

    public List<String> generatateSchemas() throws JAXBException, IOException {
        ArrayList<String> schemas = new ArrayList<String>();

        // grab the context
        JAXBContext context = JAXBContext.newInstance(clazz);

        final List<Result> results = new ArrayList<Result>();

        // generate the schema
        context.generateSchema(new SchemaOutputResolverImpl(results));

        // output schema via System.out
        for (Object domr : results) {
            if (domr instanceof DOMResult) {
                DOMResult domResult = (DOMResult) domr;
                Document doc = (Document) domResult.getNode();
                OutputFormat format = new OutputFormat(doc);
                format.setIndenting(true);
                StringWriter stringWriter = new StringWriter();
                XMLSerializer serializer = new XMLSerializer(stringWriter, format);
                serializer.serialize(doc);
                schemas.add(stringWriter.toString());
            }
        }
        return schemas;
    }

    private static class SchemaOutputResolverImpl extends SchemaOutputResolver {

        private final List<Result> results;

        public SchemaOutputResolverImpl(List<Result> results) {
            this.results = results;
        }

        @Override
        @SuppressWarnings("unchecked")
        public Result createOutput(String ns, String file) throws IOException {
            // save the schema to the list
            DOMResult result = new DOMResult();
            result.setSystemId(file);
            results.add(result);
            return result;
        }
    }
}
