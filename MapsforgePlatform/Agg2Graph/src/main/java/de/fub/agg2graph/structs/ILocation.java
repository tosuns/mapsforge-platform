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
