/*******************************************************************************
 * Copyright (c) 2012 Johannes Mitlmeier.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Affero Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/agpl-3.0.html
 * 
 * Contributors:
 *     Johannes Mitlmeier - initial API and implementation
 ******************************************************************************/
package de.fub.agg2graph.agg.tiling;

import de.fub.agg2graph.agg.AggConnection;
import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.agg.ShallowAggNode;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class for reading tiles from XML data format.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class XMLSerializationReader {
	private static Logger logger = Logger.getLogger("agg2graph.xml.read", null);

	public static void loadNodes(File path, AggContainer agg)
			throws ParserConfigurationException, SAXException, IOException {
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		dbFactory.setValidating(false);
		dbFactory.setNamespaceAware(false);
		Document doc = dBuilder.parse(path);
		// doc.getDocumentElement().normalize();
		NodeList nList = doc.getElementsByTagName("n"); // nodes...
		if (logger.getLevel() != null && logger.getLevel().equals(Level.ALL)) {
			try {
				TransformerFactory factory = TransformerFactory.newInstance();
				Transformer transformer;
				transformer = factory.newTransformer();
				StringWriter writer = new StringWriter();
				Result result1 = new StreamResult(writer);
				Source source = new DOMSource(doc);
				transformer.transform(source, result1);
				writer.close();
				logger.warning(writer.toString());
			} catch (TransformerConfigurationException e) {
				e.printStackTrace();
			} catch (TransformerException e) {
				e.printStackTrace();
			}
		}

		AggNode node;
		String ID;
		Map<String, AggNode> strToNode = new java.util.HashMap<String, AggNode>();
		// make node
		double lat, lon;
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if (nNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			Element eElement = (Element) nNode;
			ID = eElement.getAttribute("id");
			lat = Double.valueOf(eElement.getAttribute("lat"));
			lon = Double.valueOf(eElement.getAttribute("lon"));
			node = new AggNode(ID, lat, lon, agg); // set AggContainer later!
			logger.fine("read node (" + i + "): " + node.toDebugString());
			agg.addNode(node);
			strToNode.put(ID, node);
		}

		// make connections
		for (int i = 0; i < nList.getLength(); i++) {
			Node nNode = nList.item(i);
			if (nNode.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			Element eElement = (Element) nNode;
			ID = eElement.getAttribute("id");
			AggNode currentNode = strToNode.get(ID);
			NodeList children = eElement.getChildNodes();
			for (int j = 0; j < children.getLength(); j++) {
				Node childNode = children.item(j);
				if (childNode.getNodeType() != Node.ELEMENT_NODE) {
					continue;
				}
				Element childElement = (Element) childNode;
				NodeList conns = childElement.getChildNodes();
				if (childNode.getNodeName().equals("o")) {
					for (int k = 0; k < conns.getLength(); k++) {
						Node connNode = conns.item(k);
						if (connNode.getNodeType() != Node.ELEMENT_NODE
								|| !connNode.getNodeName().equals("c")) {
							continue;
						}

						Element cElem = (Element) connNode;
						AggConnection conn = null;
						AggNode toNode = strToNode
								.get(cElem.getAttribute("to"));
						if (toNode != null) {
							conn = agg.connect(currentNode, toNode);
						} else {
							String[] parts = cElem.getAttribute("to")
									.split(":");
							ShallowAggNode shallow = new ShallowAggNode(
									parts[2], Double.parseDouble(parts[0]),
									Double.parseDouble(parts[1]), agg);
							conn = agg.connect(currentNode, shallow);
						}
						if (cElem.hasAttribute("w")) {
							conn.setWeight(Float.parseFloat(cElem
									.getAttribute("w")));
						}
						if (cElem.hasAttribute("d")) {
							conn.setAvgDist(Float.parseFloat(cElem
									.getAttribute("d")));
						}
					}
				} else if (childNode.getNodeName().equals("i")) {
					for (int k = 0; k < conns.getLength(); k++) {
						Node connNode = conns.item(k);
						if (connNode.getNodeType() != Node.ELEMENT_NODE
								|| !connNode.getNodeName().equals("c")) {
							continue;
						}
						Element cElem = (Element) connNode;
						AggNode fromNode = strToNode.get(cElem
								.getAttribute("from"));
						if (fromNode != null) {
							agg.connect(fromNode, currentNode);
						} else {
							String[] parts = cElem.getAttribute("from").split(
									":");
							ShallowAggNode shallow = new ShallowAggNode(
									parts[2], Double.parseDouble(parts[0]),
									Double.parseDouble(parts[1]), agg);
							agg.connect(shallow, currentNode);
						}
					}
				}
			}
		}
	}
}
