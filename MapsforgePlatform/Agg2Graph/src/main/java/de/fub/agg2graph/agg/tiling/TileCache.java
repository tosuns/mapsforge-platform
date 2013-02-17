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
import de.fub.agg2graph.input.FileHandler;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Logger;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;

/**
 * Cache management for tiles.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class TileCache {
	private static Logger logger = Logger
			.getLogger("agg2graph.tilecache", null);
	private Set<Tile<AggNode>> activeTiles;
	// private int cacheSize;
	private DefaultCachingStrategy dcs;
	private AggContainer agg;

	// private boolean inMemory = true;

	public TileCache(DefaultCachingStrategy dcs, AggContainer agg, int cacheSize) {
		this.dcs = dcs;
		this.agg = agg;
		// this.cacheSize = cacheSize;
		activeTiles = new HashSet<Tile<AggNode>>();
	}

	public AggNode loadNode(String ID) {
		try {
			return dcs.tm.getNodeByFullID(ID);
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean isTileLoaded(Tile<AggNode> tile) {
		return activeTiles.contains(tile);
	}

	/**
	 * Load a tile from storage to this cache making it available in memory.
	 * 
	 * @param tile
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void loadTile(Tile<AggNode> tile, boolean force)
			throws ParserConfigurationException, SAXException, IOException {
		if (!force
				&& (isTileLoaded(tile) || dcs.getTc().isInMemory() || dcs
						.getTm().isEmpty())) {
			tile.setLoaded(true);
			activeTiles.add(tile);
			return;
		}
		// drill down
		Queue<Tile<AggNode>> tileQueue = new LinkedList<Tile<AggNode>>();
		tileQueue.add(tile);
		Tile<AggNode> currentTile;
		File file;
		while (!tileQueue.isEmpty()) {
			currentTile = tileQueue.poll();
			if (!force && isTileLoaded(currentTile)) {
				continue;
			}
			// check if this is the saved tile size
			file = new File(agg.getDataSource().getAbsoluteFile()
					+ File.separator
					+ String.format("%s.xml", currentTile.getID()));
			if (!file.exists()) {
				currentTile.split();
				tileQueue.addAll(currentTile.children);
			} else {
				/*
				 * We can read that file. Order is important here to prevent
				 * from infinite recursion // upon adding nodes while reading
				 * the XML representation.
				 */
				activeTiles.add(currentTile);
				XMLSerializationReader.loadNodes(file, agg);
			}
		}
	}

	public void loadTile(Tile<AggNode> tile)
			throws ParserConfigurationException, SAXException, IOException {
		loadTile(tile, false);
	}

	/**
	 * Remove all nodes of one tile from memory.
	 * 
	 * @param tile
	 */
	public void unloadTile(Tile<AggNode> tile) {
		ArrayList<AggNode> nodeListCopy = new ArrayList<AggNode>(
				tile.elements.size());
		nodeListCopy.addAll(tile.elements);
		for (AggNode node : nodeListCopy) {
			for (AggConnection inConn : node.getIn()) {
				inConn.unloadFrom();
			}
			for (AggConnection outConn : node.getOut()) {
				outConn.unloadTo();
			}
			dcs.getTm().addConnectionCounter(-node.getOut().size());
			agg.removeNodeSilently(node);
		}
		activeTiles.remove(tile);
	}

	/**
	 * Remove the data structures in memory as well as deleting all files
	 * attached to this TileCache, thus somewhat a hard reset.
	 * 
	 * @return
	 */
	public boolean remove() {
		// clear memory
		List<Tile<AggNode>> tileCopy = new ArrayList<Tile<AggNode>>(
				activeTiles.size());
		tileCopy.addAll(activeTiles);
		for (Tile<AggNode> tile : tileCopy) {
			unloadTile(tile);
		}
		// remove the files
		return !agg.getDataSource().getAbsoluteFile().exists()
				|| FileHandler.removeDirectory(agg.getDataSource()
						.getAbsoluteFile());
	}

	/**
	 * Save a tile to disk.
	 * 
	 * @param tile
	 * @throws ParserConfigurationException
	 * @throws TransformerException
	 */
	public void saveTile(Tile<AggNode> tile)
			throws ParserConfigurationException, TransformerException {
		agg.getDataSource().mkdirs();
		if (tile.isLeaf) {
			File outFile = new File(agg.getDataSource().getAbsoluteFile()
					+ File.separator + String.format("%s.xml", tile.getID()));
			logger.info("saving tile " + tile + " to "
					+ outFile.getAbsolutePath());
			XMLSerializationWriter xmlWriter = new XMLSerializationWriter(
					outFile);
			for (AggNode node : tile.elements) {
				logger.info("serializing " + node);
				xmlWriter.writeNode(node);
			}
			xmlWriter.close();

			unloadTile(tile);
		} else {
			for (Tile<AggNode> subTile : tile.children) {
				saveTile(subTile);
			}
		}
	}

	/**
	 * Make sure an area is loaded by loading all tiles contained (even partly)
	 * in that area.
	 * 
	 * @param areaRect
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	public void loadArea(Rectangle2D.Double areaRect)
			throws ParserConfigurationException, SAXException, IOException {
		// drill down
		Queue<Tile<AggNode>> tileQueue = new LinkedList<Tile<AggNode>>();
		tileQueue.add(dcs.getTm().getRoot());
		Tile<AggNode> currentTile;
		Rectangle2D.Double overlap;
		while (!tileQueue.isEmpty()) {
			currentTile = tileQueue.poll();
			// check overlap
			overlap = (Double) currentTile.size.createIntersection(areaRect);
			if (overlap.width == 0) {
				continue;
			} else if (overlap.equals(currentTile.getSize())) {
				loadTile(currentTile);
			} else {
				tileQueue.addAll(currentTile.children);
			}
		}
	}

	public void setAggContainer(AggContainer agg) {
		this.agg = agg;
	}

	public boolean isInMemory() {
		return agg == null || agg.getDataSource() == null
				|| !agg.getDataSource().exists()
				|| !agg.getDataSource().isDirectory()
				|| agg.getDataSource().list().length == 0;
	}

	public void clear() {
		activeTiles.clear();
	}

	public Set<Tile<AggNode>> getActiveTiles() {
		return activeTiles;
	}
}
