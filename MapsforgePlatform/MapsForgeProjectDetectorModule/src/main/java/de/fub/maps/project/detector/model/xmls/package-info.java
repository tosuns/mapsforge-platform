/*
 * This package contains the java model, which represent the Detector descriptor xml file.
 */
@XmlSchema(
        namespace = "http://inf.fu-berlin.de/mapsforge/detector/schema",
        //        elementFormDefault = XmlNsForm.QUALIFIED,
        xmlns = {
    @XmlNs(prefix = "dec", namespaceURI = "http://inf.fu-berlin.de/mapsforge/detector/schema")})
package de.fub.maps.project.detector.model.xmls;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;
