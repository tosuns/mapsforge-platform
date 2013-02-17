package de.fub.agg2graph.structs;

/**
 * Minimal interface for marking hideable objects.
 * 
 * @author Johannes Mitlmeier
 * 
 */
public interface Hideable {
	public void setVisible(boolean visibility);

	public boolean isVisible();
}
