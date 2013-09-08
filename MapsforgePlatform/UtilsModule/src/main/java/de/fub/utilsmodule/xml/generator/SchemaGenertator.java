/*
 * Copyright 2013 Serdar.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
 * This class handles the creation of an schema of the specified class type.
 *
 * @author Serdar
 * @param <T>
 */
public class SchemaGenertator<T> {

    private final Class<T> clazz;
    private static final Logger LOG = Logger.getLogger(SchemaGenertator.class.getName());

    public SchemaGenertator(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * Generates xml schemas for the provided class instance as a list of
     * Strings.
     *
     * @return A list of xml schema Strings.
     * @throws JAXBException
     * @throws IOException
     */
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
