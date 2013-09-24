package de.fub.agg2graph.structs.frechet;

import java.util.LinkedList;
import java.util.ListIterator;

public class Sequence<E> extends LinkedList<E> {

	private static final long serialVersionUID = 7236239462121135643L;

	public void replace(E agg, Sequence<E> splittedSeq) {
		ListIterator<E> it = listIterator(indexOf(agg));
		E tmp = it.next();
		assert(tmp == agg);
		it.set(splittedSeq.getFirst());
		it.add(splittedSeq.getLast());
	}
	
}
	