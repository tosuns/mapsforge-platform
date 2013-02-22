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
