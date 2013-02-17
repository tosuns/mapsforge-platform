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
package de.fub.agg2graph.agg;

import de.fub.agg2graph.structs.ILocation;
import java.util.Set;

/**
 * Shallow version of an {@link AggNode}. A ShallowAggNode cannot have any
 * {@link AggConnection}s attached.
 * 
 * @author Johannes Mitlmeier
 */
public class ShallowAggNode extends AggNode {
	private AggContainer aggContainer;

	public ShallowAggNode(ILocation location, AggContainer aggContainer) {
		super(location, aggContainer);
	}

	public ShallowAggNode(String ID, double lat, double lon,
			AggContainer aggContainer) {
		super(ID, lat, lon, aggContainer);
	}

	public ShallowAggNode(ShallowAggNode node) {
		this(node, node.aggContainer);
	}

	@Override
	public boolean isLoaded() {
		return false;
	}

	@Override
	public void setLoaded(boolean loaded) {
	}

	@Override
	public Set<AggConnection> getIn() {
		return null;
	}

	@Override
	public Set<AggConnection> getOut() {
		return null;
	}

	@Override
	public String toString() {
		if (ID != null) {
			return "{" + ID + ", shallow}";
		}
		return "ShallowAggNode [lat=" + getLat() + ", lon=" + getLon() + "]";
	}

	@Override
	public String toDebugString() {
		return "ShallowAggNode [ID=" + ID + ", lat=" + getLat() + ", lon="
				+ getLon() + "]";
	}

	@Override
	public boolean isShallow() {
		return true;
	}

}
