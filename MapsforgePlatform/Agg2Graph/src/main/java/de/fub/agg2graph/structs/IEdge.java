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

/**
 * Common interface for all edges, support for from and to objects and computing
 * the edge's length.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public interface IEdge<T extends ILocation> {
	public IEdge<T> setFrom(T from);

	public IEdge<T> setTo(T to);

	public void setID(String id);

	public T getFrom();

	public T getTo();

	public String getID();

	public String toDebugString();

	public double getLength();
}
