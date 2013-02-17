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

import java.util.ArrayList;
import java.util.List;

public class Path<L extends ILocation> extends ArrayList<IEdge<L>> {
	private static final long serialVersionUID = -535612870965870019L;

	public List<L> getNodes() {
		List<L> result = new ArrayList<L>(size() + 1);
		for (IEdge<L> edge : this) {
			result.add(edge.getFrom());
		}
		if (size() > 0) {
			result.add(get(size() - 1).getTo());
		}
		return result;
	}

	public double getLength() {
		double length = 0;
		for (IEdge<L> edge : this) {
			length += edge.getLength();
		}
		return length;
	}

	public List<ILocation> getClonedNodes() {
		List<ILocation> result = new ArrayList<ILocation>(size() + 1);
		for (L node : getNodes()) {
			result.add(new GPSPoint(node));
		}
		return result;
	}
}
