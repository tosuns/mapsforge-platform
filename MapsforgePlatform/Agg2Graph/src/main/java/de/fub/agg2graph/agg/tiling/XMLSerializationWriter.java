/*******************************************************************************
   Copyright 2013 Johannes Mitlmeier

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
******************************************************************************/
package de.fub.agg2graph.agg.tiling;

import java.io.File;
import java.util.Set;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import de.fub.agg2graph.agg.AggConnection;
import de.fub.agg2graph.agg.AggNode;

/**
 * Class for writing tiles to XML data format.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class XMLSerializationWriter {
	private static Logger logger = Logger.getLogger("agg2graph.tilecache.xml",
			null);
	private File path;
	private Document doc;
	private Element tileElement;

	public XMLSerializationWriter(File path)
			throws ParserConfigurationException {
		logger.info("Opened XML writer @" + path.getAbsolutePath());
		this.path = path;

		DocumentBuilderFactory docFactory = DocumentBuilderFactory
				.newInstance();
		DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

		// root elements
		doc = docBuilder.newDocument();
		tileElement = doc.createElement("t"); // tile...
		doc.appendChild(tileElement);
	}

	public void writeNode(AggNode node) {
		logger.info("Writing node to XML " + node);
		// node's properties first
		Element nodeElem = doc.createElement("n"); // node...
		tileElement.appendChild(nodeElem);
		nodeElem.setAttribute("id", node.getID());
		nodeElem.setAttribute("lat", String.valueOf(node.getLat()));
		nodeElem.setAttribute("lon", String.valueOf(node.getLon()));
		// outgoing connections deep
		Set<AggConnection> out = node.getOut();
		Element outElem = doc.createElement("o"); // outConnections...
		nodeElem.appendChild(outElem);
		for (AggConnection outConn : out) {
			writeConnection(outConn, outElem, true);
		}
		// incoming connections shallow
		Set<AggConnection> in = node.getIn();
		Element inElem = doc.createElement("i"); // outConnections...
		nodeElem.appendChild(inElem);
		for (AggConnection inConn : in) {
			writeConnection(inConn, inElem, false);
		}
	}

	public void writeConnection(AggConnection conn, Element parent, boolean deep) {
		logger.info("Writing connection to XML " + conn);
		Element connElem = doc.createElement("c");
		parent.appendChild(connElem);
		if (deep) {
			// outging conn
			connElem.setAttribute("to", conn.getTo().getInternalID());
			if (conn.getWeight() != 1) {
				connElem.setAttribute("w", String.valueOf(conn.getWeight()));
				connElem.setAttribute("d", String.valueOf(conn.getAvgDist()));
			}
		} else {
			// incoming conn
			connElem.setAttribute("from", conn.getFrom().getInternalID());
		}
	}

	public void close() throws TransformerException {
		logger.info("Writing XML file.");
		TransformerFactory transformerFactory = TransformerFactory
				.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);

		StreamResult result;
		if (logger.getLevel() != null
				&& logger.getLevel().getName().equals("FINEST")) {
			result = new StreamResult(System.out);
			transformer.transform(source, result);
		}

		result = new StreamResult(path);
		transformer.transform(source, result);
	}
}
