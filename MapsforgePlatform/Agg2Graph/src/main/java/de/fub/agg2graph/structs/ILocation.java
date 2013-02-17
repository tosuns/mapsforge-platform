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


/**
 * Common interface for all locations, support for latitude and longitude values
 * as well as a projected location.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public interface ILocation {
	public void setLat(double lat);

	public void setLon(double lon);

	public void setLatLon(double lat, double lon);

	public void setLatLon(double[] latlon);

	public void setX(double x);

	public void setY(double y);

	public void setXY(double x, double y);

	public void setXY(double[] xy);

	public void setID(String ID);

	public double getLat();

	public double getLon();

	public double[] getLatLon();

	public double getX();

	public double getY();

	public double[] getXY();

	public String getID();

	public double getWeight();

	public String toDebugString();
}
