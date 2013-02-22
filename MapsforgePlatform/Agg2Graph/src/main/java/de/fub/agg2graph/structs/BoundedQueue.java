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
