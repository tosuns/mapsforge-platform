package de.fub.agg2graph.agg.strategy;

import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.agg.IMergeHandler;
import de.fub.agg2graph.agg.ITraceDistance;
import de.fub.agg2graph.structs.ClassObjectEditor;
import de.fub.agg2graph.structs.GPSCalc;
import de.fub.agg2graph.structs.GPSEdge;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.ILocation;
import de.fub.agg2graph.structs.frechet.IAggregatedMap;
import de.fub.agg2graph.structs.frechet.ITrace;
import de.fub.agg2graph.structs.frechet.Pair;
import de.fub.agg2graph.structs.frechet.PartialFrechetDistance;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

public class ConformalPathDistance implements ITraceDistance {

    public double maxDistance = 7.5;

    public static AggContainer aggContainer;
    public IAggregatedMap map;
    public ITrace trace;
    public ILocation start;

    @Override
    public Object[] getPathDifference(List<AggNode> aggPath,
            List<GPSPoint> tracePoints, int startIndex, IMergeHandler dmh) {
//		double bestValue = 0;
//		double bestValueLength = 0;
        int segmentLength;

        //1. Input: Converting List<GPSPoint> to List<GPSEdge>
        List<GPSEdge> path1 = new ArrayList<GPSEdge>();
        for (int i = 0; i < aggPath.size() - 1; i++) {
            path1.add(new GPSEdge(aggPath.get(i), aggPath.get(i + 1)));
        }

        //2. Input: Converting List<GPSPoint> to List<GPSEdge>
        List<GPSEdge> path2 = new ArrayList<GPSEdge>();
        for (int i = startIndex; i < tracePoints.size() - 1; i++) {
            path2.add(new GPSEdge(tracePoints.get(i), tracePoints.get(i + 1)));
        }
        segmentLength = tracePoints.size();
        tracePoints = tracePoints.subList(startIndex, tracePoints.size());

        //Partial Frechet Distance
        if (path1.isEmpty() || path2.isEmpty()) {
            return new Object[]{0, new ArrayList<List<AggNode>>(), new ArrayList<List<GPSPoint>>()};
        }
        PartialFrechetDistance pfd = new PartialFrechetDistance(path1, path2, maxDistance / 92500);
        List<Pair<Point, Point>> ret = pfd.getPathList();
        List<List<AggNode>> aggResults = extractAgg(ret, aggPath, tracePoints);
        List<List<GPSPoint>> traceResults = extractTrace(ret, aggPath, tracePoints);
        return new Object[]{segmentLength, aggResults, traceResults};
    }

    private List<List<AggNode>> extractAgg(List<Pair<Point, Point>> ret,
            List<AggNode> aggPath, List<GPSPoint> tracePoints) {
        List<List<AggNode>> aggResults = new ArrayList<List<AggNode>>();
        for (Pair<Point, Point> r : ret) {
            List<AggNode> agg = new ArrayList<AggNode>();
            int start = r.a.x;
            int end = r.b.x;
            for (int i = start; i <= Math.min(end, aggPath.size()); i++) {
                agg.add(aggPath.get(i));
            }
            //TODO: Still discrete
            int gpsEnd = r.b.y;
            if (end + 1 < aggPath.size()) {
                if (GPSCalc.getDistanceTwoPointsMeter(aggPath.get(end + 1), tracePoints.get(gpsEnd)) <= maxDistance) {
                    agg.add(aggPath.get(end + 1));
                } else if (gpsEnd + 1 < tracePoints.size()) {
                    if (GPSCalc.getDistanceTwoPointsMeter(aggPath.get(end + 1), tracePoints.get(gpsEnd + 1)) <= maxDistance) {
                        agg.add(aggPath.get(end + 1));
                    }
                }
            }

            aggResults.add(agg);
        }
        return aggResults;
    }

    private List<List<GPSPoint>> extractTrace(List<Pair<Point, Point>> ret,
            List<AggNode> aggPath, List<GPSPoint> tracePoints) {
        List<List<GPSPoint>> traceResults = new ArrayList<List<GPSPoint>>();
        for (Pair<Point, Point> r : ret) {
            List<GPSPoint> trace = new ArrayList<GPSPoint>();
            int start = r.a.y;
            int end = r.b.y;
            for (int i = start; i <= Math.min(end, tracePoints.size()); i++) {
                trace.add(tracePoints.get(i));
            }
            int aggEnd = r.b.x;
            if (end + 1 < tracePoints.size()) {
                if (GPSCalc.getDistanceTwoPointsMeter(tracePoints.get(end + 1), aggPath.get(aggEnd)) <= maxDistance) {
                    trace.add(tracePoints.get(end + 1));
                } else if (aggEnd + 1 < aggPath.size()) {
                    if (GPSCalc.getDistanceTwoPointsMeter(tracePoints.get(end + 1), aggPath.get(aggEnd + 1)) <= maxDistance) {
                        trace.add(tracePoints.get(end + 1));
                    }
                }
            }

            traceResults.add(trace);
        }
        return traceResults;
    }

    @Override
    public List<ClassObjectEditor> getSettings() {
        List<ClassObjectEditor> result = new ArrayList<ClassObjectEditor>();
        result.add(new ClassObjectEditor(this));
        return result;
    }

//	/**
//	 * This class force @FrechetDistance to generate FreeSpaceDiagram.
//	 * For the optimization of calculation, there will be no real image.
//	 * @param f
//	 */
//	private void drawImaginaryFreeSpace(FrechetDistance f) {
//		f.updateFreeSpaceW();
//
//		int tileSize = 40;
//		int startx = 0;
//		int starty = 0;
//		int endx = f.getSizeQ() - 1;
//		int endy = f.getSizeP() - 1;
//		BufferedImage tile = new BufferedImage(tileSize, tileSize, BufferedImage.TYPE_INT_RGB);
//
//		//For Every Cell
//		for(int y =  starty ; y <= endy; ++y) {
//			for(int x = startx; x <= endx; ++x) {
//				Cell cell = f.getCell((f.getSizeP() - y - 1 ), x);
//				if(cell == null)
//					continue;
//
//				cell.getFreeSpace(tile, tileSize);
//				cell.getParameterMarks(tile, tileSize);
//				tile = cell.getReachableMarks(tile, tileSize);
//
//			}
//		}
//
//		//Update the bottom left
//	}
}
