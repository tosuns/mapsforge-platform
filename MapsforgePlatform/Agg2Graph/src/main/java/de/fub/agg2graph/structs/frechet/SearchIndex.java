package de.fub.agg2graph.structs.frechet;

import java.util.List;

import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.structs.GPSRegion;

interface SearchIndex<E extends AggNode> {
	
	public E searchNN(E point);
	public List<E> searchKnn(E point, int k);
	public List<E> searchRegion(GPSRegion region);
	
}