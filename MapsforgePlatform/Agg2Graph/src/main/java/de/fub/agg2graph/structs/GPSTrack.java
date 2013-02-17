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

/**
 * A gps track as found in gpx files. It is a list of {@link GPSSegment}s.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class GPSTrack extends LinkedList<GPSSegment> {
	private static final long serialVersionUID = 6139543404998182718L;
}
