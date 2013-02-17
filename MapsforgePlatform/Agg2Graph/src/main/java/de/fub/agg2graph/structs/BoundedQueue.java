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

import java.util.AbstractSequentialList;
import java.util.LinkedList;
import java.util.ListIterator;

/**
 * Queue with maximum size, oldest values are automatically discarded.
 * 
 * @author Johannes Mitlmeier
 * 
 * @param <E>
 */
public class BoundedQueue<E> extends AbstractSequentialList<E> {

	private final int maxSize;
	private final LinkedList<E> innerList;

	public BoundedQueue(int maxSize) {
		this.maxSize = maxSize;
		innerList = new LinkedList<E>();
	}

	public boolean offer(E e) {
		if (size() == maxSize) {
			remove(0);
		}
		add(e);
		return true;
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return innerList.listIterator(index);
	}

	@Override
	public int size() {
		return innerList.size();
	}

}
