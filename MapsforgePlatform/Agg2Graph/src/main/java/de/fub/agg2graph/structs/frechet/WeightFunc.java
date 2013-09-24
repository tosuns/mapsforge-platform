package de.fub.agg2graph.structs.frechet;

import de.fub.agg2graph.structs.AbstractEdge;

// You may implement a weight function for control the edge iterators path finding algorithm.
public interface WeightFunc {
	double weight(AbstractEdge<?> edge);
	
	/** May be used to stop the iterator.
	 * If true the iterator stops by return false on hasNext calls.
	 * @return
	 */
	boolean isFinish();
}