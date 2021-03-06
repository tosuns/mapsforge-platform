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
package de.fub.utilsmodule.xml.jax;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.bind.JAXBException;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;

/**
 *
 * @author Serdar
 */
public final class JAXBUtil {

    /**
     * Creates the java model of the underlying fileObject of the specified
     * DataObject.
     *
     * @param <T>
     * @param targetClazz The target type, null not permitted.
     * @param dataObject DataObject instance, null not permitted.
     * @return instance of T
     * @throws JAXBException
     * @throws IOException
     */
    public static <T> T createDescriptor(Class<T> targetClazz, DataObject dataObject) throws JAXBException, IOException {
        return createDescriptor(targetClazz, dataObject.getPrimaryFile());
    }

    /**
     *
     * @param <T>
     * @param targetClazz
     * @param fileObject
     * @return
     * @throws JAXBException
     * @throws IOException
     */
    @SuppressWarnings("unchecked")
    public synchronized static <T> T createDescriptor(Class<T> targetClazz, FileObject fileObject) throws JAXBException, IOException {

        T descriptor = null;
        InputStream inputStream = null;
        inputStream = fileObject.getInputStream();

        try {
            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(targetClazz);
            javax.xml.bind.Unmarshaller unmarshaller = jaxbCtx.createUnmarshaller();
            descriptor = (T) unmarshaller.unmarshal(inputStream); //NOI18N
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return descriptor;

    }

    public static <T> void saveDetector(final DataObject dataObject, final T descriptor) throws JAXBException {
        saveDetector(dataObject.getPrimaryFile(), descriptor);
    }

    public synchronized static <T> void saveDetector(final FileObject fileObject, final T descriptor) throws JAXBException {

        File file = FileUtil.toFile(fileObject);

        if (file != null) {

            javax.xml.bind.JAXBContext jaxbCtx = javax.xml.bind.JAXBContext.newInstance(descriptor.getClass());
            javax.xml.bind.Marshaller marshaller = jaxbCtx.createMarshaller();

            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_ENCODING, "UTF-8"); //NOI18N
            marshaller.setProperty(javax.xml.bind.Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            marshaller.marshal(descriptor, file);
        }
    }
}
