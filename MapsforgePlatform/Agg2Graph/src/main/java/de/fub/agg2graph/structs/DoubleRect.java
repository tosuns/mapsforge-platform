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
