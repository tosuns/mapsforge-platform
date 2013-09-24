package de.fub.agg2graph.structs.frechet;

import java.util.ArrayList;

import de.fub.agg2graph.agg.AggConnection;
import de.fub.agg2graph.structs.GPSEdge;

/**
 * In both directions extendible frechet distance decision algorithm. One
 * can append and prepend edges to the curves.
 * At the Moment reverseFd is deactivated
 * 
 */
public class BidirectionalFrechetDistance {
	
	public FrechetDistance fd;
	public FrechetDistance reverseFd;
	
	public void setEpsilon(double epsilon) {
		fd.setEpsilon(epsilon);
		reverseFd.setEpsilon(epsilon);
	}
	
	public BidirectionalFrechetDistance(double maxDistance) {
		fd = new FrechetDistance(maxDistance);
		fd.P = new ArrayList<>();//P
		fd.Q = new ArrayList<>();//Q
		reverseFd = new FrechetDistance(maxDistance);
		reverseFd.P = new ArrayList<>();
		reverseFd.Q = new ArrayList<>();
	}
	
	public void appendToP(AggConnection conn) {
		fd.P.add(conn);
//				fd.P.add(edge);
		if(reverseFd.P.isEmpty()) {
			prependToP(conn);
		}
		fd.resizeCells();

		fd.updateFreeSpace();
	}
//	
	public void removeLastOfP() {
		assert(!fd.P.isEmpty());
		fd.P.remove(fd.P.size() - 1);
		// remove the overlapped cells.
		if(fd.P.isEmpty() && reverseFd.P.size() <= 2) {
			reverseFd.P.remove(0);
		}

		fd.resizeCells();
		fd.updateFreeSpace();
	}
//	
	public void prependToP(AggConnection conn) {
		reverseFd.P.add(reversed(conn));
		if(fd.P.isEmpty()) {
			appendToP(conn);
		}
		reverseFd.resizeCells();
		reverseFd.updateFreeSpace();
	}
//
	public void removeFirstOfP() {
		assert(!reverseFd.P.isEmpty());
		reverseFd.P.remove(reverseFd.P.size() - 1);
		if(reverseFd.P.isEmpty() && fd.P.size() <= 2) {
			fd.P.remove(0);
		}

		reverseFd.resizeCells();
		reverseFd.updateFreeSpace();
	}
//
	public void appendToQ(GPSEdge edge) {
		fd.Q.add(edge);
		if(reverseFd.Q.isEmpty()) {
			prependToQ(edge);
		}
		fd.resizeCells();
		fd.updateFreeSpace();
	}
//	
	public void removeLastOfQ() {
		assert(!fd.Q.isEmpty());
		fd.Q.remove(fd.Q.size() - 1);
		if(fd.Q.isEmpty() && reverseFd.Q.size() <= 2) {
			reverseFd.Q.remove(0);
		}

		fd.resizeCells();
		fd.updateFreeSpace();
	}
//	
	public void prependToQ(GPSEdge edge) {
		reverseFd.Q.add(reversed(edge));
		if(fd.Q.isEmpty()) {
			appendToQ(edge);
		}
		reverseFd.resizeCells();
		reverseFd.updateFreeSpace();
	}
//	
	public void removeFirstOfQ() {
		assert(!reverseFd.Q.isEmpty());
		reverseFd.Q.remove(reverseFd.Q.size() - 1);
		if(reverseFd.Q.isEmpty() && fd.Q.size() <= 2) {
			fd.Q.remove(0);
		}

		reverseFd.resizeCells();
		reverseFd.updateFreeSpace();
	}
//	
	public boolean isInDistance() {
//		return fd.isInDistance();
		return fd.isInDistance() && reverseFd.isInDistance();
	}
//	
	public double approximateDistance() {
//		if(fd.P.size() < 1 || fd.Q.size() < 1)
		if(fd.P.size() < 1 || fd.Q.size() < 1 || reverseFd.P.size() < 1 || reverseFd.Q.size() < 1)
			return Double.POSITIVE_INFINITY;
		
		double e1 = fd.computeEpsilon();
		double e2 = reverseFd.computeEpsilon();

//		return Math.max(e1, e2); TODO: Original
		return Math.min(e1, e2);
	}
//	
	
	private AggConnection reversed(AggConnection conn) {
		return new AggConnection(conn.getTo(), conn.getFrom(), conn.getDistance());
	}
//	
	private GPSEdge reversed(GPSEdge edge) {
		return new GPSEdge(edge.getTo(), edge.getFrom(), edge.getDistance());
	}
}
