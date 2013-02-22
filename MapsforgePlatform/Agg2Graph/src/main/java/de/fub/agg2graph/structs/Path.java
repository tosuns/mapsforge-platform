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
