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
package de.fub.agg2graph.agg;

import java.util.Set;

import de.fub.agg2graph.structs.ILocation;

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
