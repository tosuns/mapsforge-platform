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

import java.awt.geom.Rectangle2D;

/**
 * Model for a rectangular region of data.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public class DoubleRect extends Rectangle2D.Double {
	private static final long serialVersionUID = 8741640913960241401L;

	public DoubleRect() {
		super();
	}

	public DoubleRect(double x, double y, double w, double h) {
		super(x, y, w, h);
	}

	public boolean isFresh() {
		return getWidth() == 0 && getHeight() == 0;
	}

	public void reset() {
		setRect(0, 0, 0, 0);
	}

	public void setMaxSize() {
		setRect(0, 0, java.lang.Double.MAX_VALUE, java.lang.Double.MAX_VALUE);
	}

	public void enlarge(double factor) {
		double newWidth = getWidth() * factor;
		double newHeight = getHeight() * factor;
		setRect(getCenterX() - newWidth / 2, getCenterY() - newHeight / 2,
				newWidth, newHeight);
	}

	public void fromMinMax(double minX, double minY, double maxX, double maxY) {
		setRect(minX, minY, maxX - minX, maxY - minY);
	}
}
