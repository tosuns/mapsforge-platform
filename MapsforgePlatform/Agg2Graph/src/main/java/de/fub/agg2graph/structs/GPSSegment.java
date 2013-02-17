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

import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

/**
 * A gps segment as found in gpx files. It is a list of {@link GPSPoint}s.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class GPSSegment extends LinkedList<GPSPoint> {
	private static final long serialVersionUID = -124779073052348850L;

	public GPSSegment() {

	}

	public GPSSegment(List<? extends ILocation> list) {
		this();
		GPSPoint point;
		for (ILocation loc : list) {
			point = new GPSPoint(loc);
			if (loc.getID() == null) {
				point.setID(UUID.randomUUID().toString());
				System.out.println(point);
			} else {
				point.setID(loc.getID());
			}
			add(point);
		}
	}

	public void addIDs(String prefix) {
		addIDs(prefix, 1, true);
	}

	public void addIDs(String prefix, int start) {
		addIDs(prefix, start, false);
	}

	public void addIDs(String prefix, int start, boolean keepIDs) {
		for (int i = 0; i < size(); i++) {
			if (keepIDs && get(i).getID() != null) {
				get(i).setID(String.format("%s-%s", prefix, get(i).getID()));
			} else {
				get(i).setID(String.format("%s-%d", prefix, start + i));
			}
		}
	}
}
