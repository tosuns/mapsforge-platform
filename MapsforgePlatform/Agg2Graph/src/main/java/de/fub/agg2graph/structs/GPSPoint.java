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
package de.fub.agg2graph.structs;

import org.openstreetmap.gui.jmapviewer.Coordinate;

/**
 * A gps point with some useful constructors additionally to the basic
 * functionality inherited from {@link AbstractLocation}.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class GPSPoint extends AbstractLocation {
	public GPSPoint() {
		super();
	}

	public GPSPoint(double lat, double lon) {
		super(lat, lon);
	}

	public GPSPoint(String ID, double lat, double lon) {
		super(ID, lat, lon);
	}

	public GPSPoint(ILocation location) {
		super(location);
	}

	public GPSPoint(Coordinate position) {
		super(position);
	}
}
