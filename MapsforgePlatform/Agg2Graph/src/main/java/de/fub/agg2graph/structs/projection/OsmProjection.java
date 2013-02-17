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
package de.fub.agg2graph.structs.projection;

import de.fub.agg2graph.structs.GPSPoint;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.OsmMercator;

public class OsmProjection extends OsmMercator {
	public final static int ZOOM_LEVEL = JMapViewer.MAX_ZOOM;

	public static double[] gpsToCartesian(GPSPoint p) {
		return new double[] { OsmMercator.LonToX(p.getLon(), ZOOM_LEVEL),
				OsmMercator.LatToY(p.getLat(), ZOOM_LEVEL) };
	}

	public static GPSPoint cartesianToGps(double x, double y) {
		return new GPSPoint(OsmMercator.XToLon((int) x, ZOOM_LEVEL),
				OsmMercator.YToLat((int) y, ZOOM_LEVEL));
	}
}
