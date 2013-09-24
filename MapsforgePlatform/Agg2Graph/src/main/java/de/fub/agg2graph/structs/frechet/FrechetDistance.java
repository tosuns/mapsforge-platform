package de.fub.agg2graph.structs.frechet;

import de.fub.agg2graph.agg.AggConnection;
import de.fub.agg2graph.agg.AggContainer;
import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.structs.GPSCalc;
import de.fub.agg2graph.structs.GPSEdge;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.ILocation;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;
import java.util.logging.Logger;

/**
 *
 * @author Martinus
 *
 */
public class FrechetDistance {

    @SuppressWarnings("unused")
    private static final Logger logger = Logger
            .getLogger("agg2graph.agg.frechet.dist");

    public double maxDistance = 1;

    public List<AggConnection> P; // AGG
    public List<GPSEdge> Q; // TRA
    public Cell[] cells = null; // List of Container
    AggContainer aggContainer;

    public FrechetDistance(double maxDistance) {
        P = null;
        Q = null;
        this.maxDistance = maxDistance;
    }

    public int getSizeP() {
        return (P != null) ? P.size() : 0;
    }

    public int getSizeQ() {
        return (Q != null) ? Q.size() : 0;
    }

    public FrechetDistance(List<GPSEdge> a, List<GPSEdge> t, double epsilon) {
        this.P = new ArrayList<AggConnection>();
        for (GPSEdge agg : a) {
            P.add(new AggConnection(agg.getFrom(), agg.getTo(), aggContainer));
        }
        this.Q = t;
        this.maxDistance = epsilon;
        calculateReachableSpace();
    }

    void updateFreeSpace() {
        for (int i = 0; i < P.size(); ++i) {
            for (int j = 0; j < Q.size(); ++j) {
                getCell(i, j).updateCell();
            }
        }
        calculateReachableSpace();
    }

    public double getEpsilon() {
        return maxDistance;
    }

    public void setEpsilon(double epsilon) {
        this.maxDistance = epsilon;
        updateFreeSpace();
    }

    public class Cell {

        public int i;
        public int j;
        private double a, b, c, d;
        public GPSEdge q;
        public AggConnection p;
        public int width;
        boolean isRelevant;
        public Point from = new Point(-1, -1);
        public Point to = new Point(-1, -1);

        // Extension
        List<Point> convexHull = new ArrayList<Point>(); // Convex Hull of white
        // region
        Integer convexTracker = Integer.MIN_VALUE; // A help variable to
        // determine the hull

        // intervals of the reachable space
        Interval leftF;
        Interval bottomF;

        Interval leftR;
        Interval bottomR;

        public Interval getLeftF() {
            return leftF;
        }

        public void setLeftF(Interval leftF) {
            this.leftF = leftF;
        }

        public Interval getLeftR() {
            return leftR;
        }

        public void setLeftR(Interval leftR) {
            this.leftR = leftR;
        }

        public Interval getBottomF() {
            return bottomF;
        }

        public void setBottomF(Interval bottomF) {
            this.bottomF = bottomF;
        }

        public Interval getBottomR() {
            return bottomR;
        }

        public void setBottomR(Interval bottomR) {
            this.bottomR = bottomR;
        }

        public Cell(int i, int j) {
            this.i = i;
            this.j = j;
            p = P.get(i);
            q = Q.get(j);
            isRelevant = p.getFrom().isRelevant();
            updateCell();
        }

        void updateCell() {
            leftF = GPSCalc.getSegmentCircleIntersection2(p.getFrom().getLon(),
                    p.getFrom().getLat(), p.getTo().getLon(), p.getTo()
                    .getLat(), q.getFrom().getLon(), q.getFrom()
                    .getLat(), maxDistance);

            a = leftF.start;
            b = leftF.end;

            bottomF = GPSCalc.getSegmentCircleIntersection2(q.getFrom()
                    .getLon(), q.getFrom().getLat(), q.getTo().getLon(), q
                    .getTo().getLat(), p.getFrom().getLon(), p.getFrom()
                    .getLat(), maxDistance);

            c = bottomF.start;
            d = bottomF.end;
        }

        /**
         * Draw the free space region of the cell with given epsilon.
         *
         * @param img
         * @param width
         * @return
         */
        public BufferedImage getFreeSpace(BufferedImage img, int width) {
            this.width = width;
            BufferedImage buffer = img;
            if (buffer == null) {
                buffer = new BufferedImage(width, width,
                        BufferedImage.TYPE_INT_RGB);
            }
            double stepsize = 1.0 / width;

            /**
             * Mark white-black region and convexhull of the white region TODO:
             * Optimization
             */
            // AGG
            for (int s = 0; s < width; ++s) {
                double sStep = s * stepsize;
                ILocation pAtt = p.at(sStep);
                convexTracker = Integer.MIN_VALUE;
                List<Point> current = new ArrayList<Point>(2);

                // TRA
                for (int t = 0; t < width; ++t) {
                    double tStep = t * stepsize;
                    ILocation qAtt = q.at(tStep);

                    double distance = GPSCalc.getDistanceTwoPointsDouble(pAtt,
                            qAtt);
                    if (distance < maxDistance && isRelevant) {
                        buffer.setRGB(t, width - 1 - s, Color.WHITE.getRGB());
                        if (convexTracker == Integer.MIN_VALUE) {
                            current.add(new Point(s, t));
                            convexTracker = s;
                        } else if (convexTracker == s) {
                            if (current.size() == 2) {
                                current.remove(1);
                            }
                            current.add(new Point(s, t));
                        }
                    } else {
                        buffer.setRGB(t, width - 1 - s, Color.BLACK.getRGB());
                    }
                }
                convexHull.addAll(current);
            }

            // TRA
            for (int t = 0; t < width; ++t) {
                double tStep = t * stepsize;
                ILocation qAtt = q.at(tStep);
                convexTracker = Integer.MIN_VALUE;
                List<Point> current = new ArrayList<Point>(2);

                // AGG
                for (int s = 0; s < width; ++s) {
                    double sStep = s * stepsize;
                    ILocation pAtt = p.at(sStep);

                    double distance = GPSCalc.getDistanceTwoPointsDouble(qAtt,
                            pAtt);

                    if (distance < maxDistance && isRelevant) {
                        if (convexTracker == Integer.MIN_VALUE) {
                            current.add(new Point(s, t));
                            convexTracker = t;
                        } else if (convexTracker == t) {
                            if (current.size() == 2) {
                                current.remove(1);
                            }
                            current.add(new Point(s, t));
                        }
                    }
                }
                convexHull.addAll(current);
            }

            // Sorting All-List
            Collections.sort(convexHull, new Comparator<Point>() {

                @Override
                public int compare(Point p1, Point p2) {
                    if (p1.y != p2.y) {
                        return p1.y - p2.y;
                    } else {
                        if (p1.x < p2.x) {
                            return -1;
                        } else if (p1.x > p2.x) {
                            return 1;
                        }
                        return 0;
                    }
                }
            });

            // Remove duplicate elements
            for (int i = 0; i < convexHull.size() - 1; i++) {
                if (convexHull.get(i).equals(convexHull.get(i + 1))) {
                    convexHull.remove(i--);
                }
            }

            // Draw the path if possible
            if (from.x != -1 && from.y != -1 && to.x != -1 && to.y != -1) {
                Graphics2D g2 = (Graphics2D) buffer.getGraphics();
                g2.setColor(Color.BLUE);
                g2.setStroke(new BasicStroke(5));
                g2.drawLine(from.y, width - 1 - from.x, to.y, width - 1 - to.x);
            }

            return buffer;
        }

        /**
         * Draw the lines marking the cell boundary intervals.
         */
        public BufferedImage getParameterMarks(BufferedImage img, int width) {
            BufferedImage buffer = img;
            if (buffer == null) {
                buffer = new BufferedImage(width, width,
                        BufferedImage.TYPE_INT_RGB);
            }
            Graphics2D g2 = (Graphics2D) buffer.getGraphics();
            g2.clearRect(0, 0, width, width);
            if (a < Double.MAX_VALUE && b > Double.MIN_VALUE) {
                g2.draw(new Line2D.Double(0., width - a * width, 0., width - b
                        * width));
            }

            if (c < Double.MAX_VALUE && d > Double.MIN_VALUE) {
                g2.draw(new Line2D.Double(c * width, width - 1, d * width,
                        width - 1));
            }
            return buffer;
        }

        /**
         * Draws the reachable space markers.
         */
        public BufferedImage getReachableMarks(BufferedImage img, int width) {
            BufferedImage buffer = img;
            if (buffer == null) {
                buffer = new BufferedImage(width, width,
                        BufferedImage.TYPE_INT_RGB);
            }
            Graphics2D g2 = (Graphics2D) buffer.getGraphics();
            g2.clearRect(0, 0, width, width);

            g2.setColor(Color.red);
            g2.setStroke(new BasicStroke(2.f));
            if (!leftF.isEmpty()) {
                g2.draw(new Line2D.Double(4., width - leftF.start * width, 4.,
                        width - leftF.end * width));
            }
            if (!bottomF.isEmpty()) {
                g2.draw(new Line2D.Double(bottomF.start * width, width - 5.,
                        bottomF.end * width, width - 5.));
            }

            g2.setComposite(AlphaComposite.SrcAtop.derive(0.4f));
            if (!leftF.isEmpty()) {
                g2.fill(new Rectangle2D.Double(4., width - leftF.end * width,
                        width - 8, (leftF.end - leftF.start) * width));
            }

            if (!bottomF.isEmpty()) {
                g2.fill(new Rectangle2D.Double(bottomF.start * width, 5.,
                        (bottomF.end - bottomF.start) * width, width - 10));
            }
            return buffer;
        }

        /**
         * This grid may be used to create a height plot of the distance field.
         */
        public double[] getDistanceGrid(double[] grid, int size) {
            double[] buffer = grid;
            if (buffer == null) {
                buffer = new double[size];
            }
            double stepsize = 1.0 / size;

            for (int s = 0; s < size; ++s) {
                double sStep = s * stepsize;
                ILocation traceAt = q.at(sStep);

                for (int t = 0; t < size; ++t) {
                    double tStep = t * stepsize;
                    ILocation aggAt = p.at(tStep);

                    buffer[(s * size + t)] = GPSCalc
                            .getDistanceTwoPointsDouble(traceAt, aggAt);
                }
            }
            return buffer;
        }

        @Override
        public String toString() {
            return "(" + i + ", " + j + ") left=" + leftF + "  bottom="
                    + bottomF;
        }

        /**
         * Check if there is white region
         *
         * @return
         */
        public boolean isWhiteEmpty() {
            return convexHull.isEmpty() ? true : false;
        }

        /**
         * Get path with given direction
         *
         * @param direction = true ? agg : trace;
         */
        public void getPath(boolean direction) {
            int mXindex = -1;
            int mYindex = -1;
            Point to = new Point(-1, -1);

            // get the most right x and lowest from this x -> direction trace
            if (direction) {
                for (Point p : convexHull) {
                    if (mYindex < p.y && p.y >= from.y) {
                        mYindex = p.y;
                    }
                }
                for (Point p : convexHull) {
                    if ((p.y == mYindex && p.x >= from.x)
                            && (mXindex == -1 || mXindex > p.x)) {
                        mXindex = p.x;
                        to = p;
                    }
                }
            } // get the most top y and most left from this y -> direction agg
            else {
                for (Point p : convexHull) {
                    if (mXindex < p.x && p.x >= from.x) {
                        mXindex = p.x;
                    }
                }
                for (Point p : convexHull) {
                    if ((p.x == mXindex && p.y >= from.y)
                            && (mYindex == -1 || mYindex > p.y)) {
                        mYindex = p.y;
                        to = p;
                    }
                }
            }

            this.to = to;
            if (to.x == -1 || to.y == -1) {
                // System.out.println("i = " + this.i + " : j = " + this.j);
                // System.out.println("STIMMT nicht");
            }
        }

        /**
         * Get the from point. from shall be very left and bottom
         *
         * @param limit
         * @return from point
         */
        public Point setLowestFrom(int limit) {
            int y = Integer.MAX_VALUE, bestI = -1;
            int best = Integer.MAX_VALUE, current;
            for (int k = 0; k < convexHull.size(); k++) {
                current = convexHull.get(k).x + convexHull.get(k).y;
                if (best > current && y > convexHull.get(k).y
                        && convexHull.get(k).y >= limit) {
                    best = current;
                    y = convexHull.get(k).y;
                    bestI = k;
                }
            }
            if (bestI == -1) {
                return new Point(-1, -1);
            }
            return convexHull.get(bestI);
        }

        /**
         * Get the longest path of a cell
         *
         * @return the y-Coord
         */
        public int getLongestPath() {
            int best = -1, current;
            Point bestTo = null, currentTo;

            for (int i = 0; i < convexHull.size(); i++) {
                currentTo = convexHull.get(i);
                if (from.x < currentTo.x && from.y < currentTo.y) {
                    current = (currentTo.x - from.x) + (currentTo.y - from.y);
                    if (best < current) {
                        bestTo = currentTo;
                        best = current;
                    }
                }
            }
            this.to = bestTo;
            return this.to.y;
        }
    }

    /**
     * Checks the condition for the decision problem. The cells must be update
     * with the actual epsilon beforehand.
     *
     * @return
     */
    public boolean isInDistance() {
        if (P.size() < 1 || Q.size() < 1) {
            return false;
        }

        Cell lastCell = getCell(P.size() - 1, Q.size() - 1);

        // Calculate a b for and imaginary cell p,q+1;
        Interval gate = new Interval();
        AggConnection lastSegOfP = P.get(P.size() - 1);
        ILocation lastPointOfQ = Q.get(Q.size() - 1).getTo();
        gate = GPSCalc.getSegmentCircleIntersection2(lastSegOfP.getFrom()
                .getLon(), lastSegOfP.getFrom().getLat(), lastSegOfP.getTo()
                .getLon(), lastSegOfP.getTo().getLat(), lastPointOfQ.getLon(),
                lastPointOfQ.getLat(), maxDistance);

        if (lastCell.leftF.isEmpty() && lastCell.bottomF.isEmpty()) {
            return false; // the gate would be empty.
        } else if (GPSCalc.compareDouble(gate.end, 1.) == 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Simple newton approximation for the epsilon. The algorithm starts with
     * the maximum distance of the path end points. With that start given
     * maxSteps of newton steps will be made to refine epsilon.
     *
     * @param maxSteps
     * @return
     */
    public double approximate(int maxSteps) {
        double startValue = Math.max(GPSCalc.getDistanceTwoPointsDouble(P
                .get(0).getFrom(), Q.get(0).getFrom()), GPSCalc
                .getDistanceTwoPointsDouble(P.get(P.size() - 1).getTo(),
                        Q.get(Q.size() - 1).getTo()));

        setEpsilon(startValue);

        if (isInDistance()) {
            return startValue;
        }

        double lastWorkingEpsilon = Double.MAX_VALUE;
        double stepsize = startValue;
        int steps = 0;
        double epsilonToTest = 0.;
        while (++steps <= maxSteps) {
            if (epsilonToTest == (maxDistance + stepsize)) {
                break;
            }

            epsilonToTest = maxDistance + stepsize;
            setEpsilon(epsilonToTest);
            if (isInDistance()) {
                lastWorkingEpsilon = maxDistance;
                stepsize = -(stepsize / 2.);
            } else {
                stepsize = 2. * stepsize;
            }
        }

        setEpsilon(lastWorkingEpsilon);
        return maxDistance;
    }

    /**
     * Pair.first is point in P Pair.second is point in Q
     */
    public HashMap<Pair<ILocation, ILocation>, Double> distanceMatrix = new HashMap<>();

    /**
     * The location indices, also corresponding index of the edges in P or Q
     * matched to the points on the other curve
     */
    public HashMap<Integer, TreeSet<AggNode>> fromP = new HashMap<>();
    public HashMap<Integer, TreeSet<GPSPoint>> fromQ = new HashMap<>();
    boolean needsRecalculation = true;

    private void addP(int cellIndexi, ILocation trace, ILocation other) {
        distanceMatrix.put(new Pair<>(trace, other),
                GPSCalc.getDistanceTwoPointsDouble(trace, other));
        if (cellIndexi != -1) {
            if (!fromP.containsKey(cellIndexi) || fromP.get(cellIndexi) == null) {
                fromP.put(cellIndexi, new TreeSet<AggNode>());
            }
            fromP.get(cellIndexi).add(new AggNode(other, aggContainer));
        }
    }

    private void addQ(int cellIndexj, ILocation agg, ILocation other) {
        distanceMatrix.put(new Pair<>(other, agg),
                GPSCalc.getDistanceTwoPointsDouble(other, agg));
        if (cellIndexj != -1) {
            if (!fromQ.containsKey(cellIndexj) || fromQ.get(cellIndexj) == null) {
                fromQ.put(cellIndexj, new TreeSet<GPSPoint>());
            }
            fromQ.get(cellIndexj).add(new GPSPoint(other));
        }
    }

    /**
     * Compute the critical edges. These are used by the FrechetBasedMerge merge
     * algorithm.
     */
    public void computeMetaData() {
        distanceMatrix.clear();
        fromP.clear();
        fromQ.clear();

        // case a)
        addP(0, P.get(0).getFrom(), Q.get(0).getFrom());
        addQ(0, Q.get(0).getFrom(), P.get(0).getFrom());

        addP(P.size(), P.get(P.size() - 1).getTo(), Q.get(Q.size() - 1).getTo());
        addQ(Q.size(), Q.get(Q.size() - 1).getTo(), P.get(P.size() - 1).getTo());
		// addP(0, P.firstElement().getFrom(), Q.firstElement().getFrom());
        // addQ(0, Q.firstElement().getFrom(), P.firstElement().getFrom());
        //
        // addP(P.size(), P.lastElement().getTo(), Q.lastElement().getTo());
        // addQ(Q.size(), Q.lastElement().getTo(), P.lastElement().getTo());

        // Case b)
        for (int i = 0; i < P.size(); ++i) {
            for (int j = 0; j < Q.size(); ++j) {
                AggConnection p = P.get(i);
                GPSEdge q = Q.get(j);

                ILocation intersectionPerpQThroughPWithQ = GPSCalc
                        .intersectionWithPerpendicularThrough(q.getFrom(),
                                q.getTo(), p.getFrom());
                ILocation intersectionPerpPThroughQWithP = GPSCalc
                        .intersectionWithPerpendicularThrough(p.getFrom(),
                                p.getTo(), q.getFrom());

                if (intersectionPerpPThroughQWithP != null
                        && GPSCalc.PntOnLine(p.getFrom(), p.getTo(),
                                intersectionPerpPThroughQWithP)) {
                    addP(-1, intersectionPerpPThroughQWithP, q.getFrom());
                    addQ(j, q.getFrom(), intersectionPerpPThroughQWithP);
                }

                if (intersectionPerpQThroughPWithQ != null
                        && GPSCalc.PntOnLine(q.getFrom(), q.getTo(),
                                intersectionPerpQThroughPWithQ)) {
                    addP(i, p.getFrom(), intersectionPerpQThroughPWithQ);
                    addQ(-1, intersectionPerpQThroughPWithQ, p.getFrom());
                }
            }
        }

        // Case c)
        // The path of p has to be converted into a vertex list.
        ArrayList<GPSPoint> a = new ArrayList<>();
        for (AggConnection p : P) {
            a.add(p.getFrom());
        }
        a.add(P.get(P.size() - 1).getTo());

        for (int i = 0; i < a.size() - 1; ++i) {
            for (int k = i + 1; k < a.size(); ++k) {
                GPSEdge ik = new GPSEdge((GPSPoint) a.get(i),
                        (GPSPoint) a.get(k));
                ILocation mid = ik.at(0.5);
                for (int j = 0; j < Q.size(); ++j) {
                    GPSEdge q = Q.get(j);

                    // Intersection test of the bisector of P(i), P(k) with Q(j)
                    ILocation intersection = GPSCalc
                            .IntersectionOfPerpendicularWithLine(ik.getFrom(),
                                    ik.getTo(), mid, q.getFrom(), q.getTo());
                    if (intersection != null
                            && GPSCalc.PntOnLine(q.getFrom(), q.getTo(),
                                    intersection)) {
                        addP(i, a.get(i), intersection);
                        addQ(-1, intersection, a.get(i));

                        addP(k, a.get(k), intersection);
                        addQ(-1, intersection, a.get(k));
                    }
                }
            }
        }

        a.clear();

        for (GPSEdge q : Q) {
            a.add(q.getFrom());
        }
        a.add(Q.get(Q.size() - 1).getTo());

        for (int i = 0; i < a.size() - 1; ++i) {
            for (int k = i + 1; k < a.size(); ++k) {
                GPSEdge ik = new GPSEdge((GPSPoint) a.get(i),
                        (GPSPoint) a.get(k));
                ILocation mid = ik.at(0.5);
                for (int j = 0; j < P.size(); ++j) {
                    AggConnection p = P.get(j);

                    // Intersection test of the bisector of Q(i), Q(k) with P(j)
                    ILocation intersection = GPSCalc
                            .IntersectionOfPerpendicularWithLine(ik.getFrom(),
                                    ik.getTo(), mid, p.getFrom(), p.getTo());
                    if (intersection != null
                            && GPSCalc.PntOnLine(p.getFrom(), p.getTo(),
                                    intersection)) {

                        addP(-1, intersection, a.get(i));
                        addQ(i, a.get(i), intersection);

                        addP(-1, intersection, a.get(k));
                        addQ(k, a.get(k), intersection);
                    }
                }
            }
        }
    }

    public List<Double> criticalValues = new ArrayList<Double>();

    /**
     * Compute epsilon by first calculate all critical points of type a, b and c
     * described in the paper Alt and Godau 95. Then do a binary search to find
     * the smallest of the critical values that passes the inDistance test.
     *
     * @return
     */
    public double computeEpsilon() {
        criticalValues.clear();
        // case a)
        criticalValues.add(GPSCalc.getDistanceTwoPointsDouble(P.get(0)
                .getFrom(), Q.get(0).getFrom()));
        criticalValues.add(GPSCalc.getDistanceTwoPointsDouble(
                P.get(P.size() - 1).getTo(), Q.get(Q.size() - 1).getTo()));
		// criticalValues.add(locationToLocationDistance.getDistance(P.firstElement().getFrom(),
        // Q.firstElement().getFrom()));
        // criticalValues.add(locationToLocationDistance.getDistance(P.lastElement().getTo(),
        // Q.lastElement().getTo()) );

        // Case b)
        for (int i = 0; i < P.size(); ++i) {
            for (int j = 0; j < Q.size(); ++j) {
                // L^F
                AggConnection p = P.get(i);
                GPSEdge q = Q.get(j);

                criticalValues.add(GPSCalc.getDistancePointToEdgeDouble(
                        p.getFrom(), q.getFrom(), q.getTo()));
                criticalValues.add(GPSCalc.getDistancePointToEdgeDouble(
                        q.getFrom(), p.getFrom(), p.getTo()));
                // criticalValues.add(locationToEdgeDistance.getDistance(p.getFrom(),
                // q));
                // criticalValues.add(locationToEdgeDistance.getDistance(q.getFrom(),
                // p));
            }
        }
        // The missing last elements.
        {
            AggConnection p = P.get(P.size() - 1);
            // P.get(P.size() - 1);
            for (int j = 0; j < Q.size(); ++j) {
                criticalValues.add(GPSCalc.getDistancePointToEdgeDouble(
                        p.getTo(), Q.get(j).getFrom(), Q.get(j).getTo()));
                // criticalValues.add(locationToEdgeDistance.getDistance(p.getTo(),
                // Q.get(j)));
            }
            GPSEdge q = Q.get(Q.size() - 1);
            for (int i = 0; i < P.size(); ++i) {
                criticalValues.add(GPSCalc.getDistancePointToEdgeDouble(
                        q.getTo(), P.get(i).getFrom(), P.get(i).getTo()));

                // criticalValues.add(locationToEdgeDistance.getDistance(q.getTo(),
                // P.get(i)));
            }
        }

        // Case c)
        // The path of p has to be converted into a vertex list.
        ArrayList<ILocation> a = new ArrayList<ILocation>();
        // for (int i = 0; i < Q.size(); i++) {
        // a.add(Q.get(i));
        // }
        for (AggConnection p : P) {
            a.add(p.getFrom());
        }
        a.add(P.get(P.size() - 1).getTo());

        for (int i = 0; i < a.size(); ++i) {
            for (int k = 0; k < a.size(); ++k) {
                if (i == k) {
                    continue;
                }
                GPSEdge ik = new GPSEdge((GPSPoint) a.get(i),
                        (GPSPoint) a.get(k));
                ILocation mid = ik.at(0.5);
                for (int j = 0; j < Q.size(); ++j) {
                    GPSEdge q = Q.get(j);

                    // Intersection test of the bisector of P(i), P(k) with Q(j)
                    ILocation intersection = GPSCalc
                            .IntersectionOfPerpendicularWithLine(ik.getFrom(),
                                    ik.getTo(), mid, q.getFrom(), q.getTo());
                    if (intersection != null // && Algorithms.PntOnLine(q.getFrom(), q.getTo(),
                            // intersection)
                            ) {
                        criticalValues.add(GPSCalc.getDistanceTwoPointsDouble(
                                a.get(i), intersection));
                        criticalValues.add(GPSCalc.getDistanceTwoPointsDouble(
                                a.get(k), intersection));
                        // criticalValues.add(a.get(i).getDistanceTo(intersection));
                        // criticalValues.add(a.get(k).getDistanceTo(intersection));
                    }
                }
            }
        }

        a.clear();
        // for (int i = 0; i < P.size(); i++) {
        // a.add(P.get(i));
        // }
        for (GPSEdge q : Q) {
            a.add(q.getFrom());
        }
        a.add(Q.get(Q.size() - 1).getTo());

        for (int i = 0; i < a.size(); ++i) {
            for (int k = 0; k < a.size(); ++k) {
                if (i == k) {
                    continue;
                }
                GPSEdge ik = new GPSEdge((GPSPoint) a.get(i),
                        (GPSPoint) a.get(k));
                ILocation mid = ik.at(0.5);
                for (int j = 0; j < P.size(); ++j) {
                    AggConnection p = P.get(j);

                    // Intersection test of the bisector of Q(i), Q(k) with P(j)
                    ILocation intersection = GPSCalc
                            .IntersectionOfPerpendicularWithLine(ik.getFrom(),
                                    ik.getTo(), mid, p.getFrom(), p.getTo());
                    if (intersection != null // && Algorithms.PntOnLine(p.getFrom(), p.getTo(),
                            // intersection)
                            ) {
                        criticalValues.add(GPSCalc.getDistanceTwoPointsDouble(
                                a.get(i), intersection));
                        criticalValues.add(GPSCalc.getDistanceTwoPointsDouble(
                                a.get(k), intersection));
                        // criticalValues.add(a.get(i).getDistanceTo(intersection));
                        // criticalValues.add(a.get(k).getDistanceTo(intersection));
                    }
                }
            }
        }

        // Finally binsearch for the real value of epsilon.
        Collections.sort(criticalValues);
        final double epsilon2 = binSearch(criticalValues);

        return epsilon2;
    }

    private double binSearch(List<Double> criticalValues) {
        int low = 0;
        int high = criticalValues.size() - 1;
        int median = 0;
        int goodMedian = -1;
        double goodDistance = Double.POSITIVE_INFINITY;

        while (low <= high) {
            median = (low + high) / 2;

            double test = criticalValues.get(median);
            setEpsilon(test);

            if (isInDistance()) {
                high = median - 1;
                if (test < goodDistance) {
                    goodDistance = test;
                    goodMedian = median;
                }
            } else {
                low = median + 1;
            }
        }

        if (goodMedian < 0) {
            double check = criticalValues.get(criticalValues.size() - 1);
            // Seems rounding errors lead to not detect good values for distance
            // right at the ends of the lines.
            check += 0.0000001;
            setEpsilon(check);
            if (isInDistance()) {
                goodDistance = check;
            }
        }

        setEpsilon(goodDistance);

        return goodDistance;
    }

    void calculateReachableSpace() {
        calculateReachableSpace(0, 0, false);
    }

    // The reachable space is calculated by dynamic programming.
    void calculateReachableSpace(int iOfPivot, int jOfPivot, boolean skipInit) {
        if (P.size() < 1 || Q.size() < 1) {
            return;
        }

        if (!skipInit) {
            Cell nullnull = getCell(iOfPivot, jOfPivot);
            if (nullnull == null) {
                return;
            }

            if (nullnull.leftF.start > 0 && nullnull.bottomF.start > 0) {
                nullnull.leftF = new Interval();
                nullnull.bottomF = new Interval();
            }
        }

        // Calculate Rv_i0
        for (int i = iOfPivot + 1; i < P.size(); ++i) {
            Cell cell = getCell(i, jOfPivot);
            cell.leftF = new Interval();
            if (cell.bottomF.isEmpty()) {
                continue;
            }

            Cell bottomCell = getCell(i - 1, jOfPivot);
            if (bottomCell.bottomF.isEmpty()
                    || bottomCell.bottomF.start > cell.bottomF.end) {
                cell.bottomF = new Interval(); // Empty.
            }
        }

        // Calculate Rh_0j
        for (int j = jOfPivot + 1; j < Q.size(); ++j) {
            Cell cell = getCell(iOfPivot, j);
            cell.bottomF = new Interval();
            if (cell.leftF.isEmpty()) {
                continue; // All following cells will be made empty.
            }

            Cell leftCell = getCell(iOfPivot, j - 1);
            if (leftCell.leftF.isEmpty()
                    || leftCell.leftF.start > cell.leftF.end) {
                cell.leftF = new Interval(); // Empty Interval.
            }
        }

        for (int i = iOfPivot; i < P.size(); ++i) {
            for (int j = jOfPivot; j < Q.size(); ++j) {
                Cell cell = getCell(i, j);
                Cell rightCell = (j < Q.size() - 1) ? getCell(i, j + 1) : null;
                Cell topCell = (i < P.size() - 1) ? getCell(i + 1, j) : null;
                // Cell rightCell = (j < Q.size() - 1) ? getCell(i, j + 1) :
                // null;
                // Cell topCell = (i < P.size() - 1) ? getCell(i + 1, j) : null;

                if (rightCell != null) {
                    if ((!cell.bottomF.isEmpty())
                            || cell.leftF.start < rightCell.leftF.start) {
                        rightCell.leftF = rightCell.leftF;
                    } else if (cell.leftF.isEmpty()
                            || cell.leftF.start > rightCell.leftF.end) {
                        rightCell.leftF = new Interval(); // Empty.
                    } else {
                        rightCell.leftF = new Interval(cell.leftF.start,
                                rightCell.leftF.end);
                    }
                }

                if (topCell != null) {
                    if ((!cell.leftF.isEmpty())
                            || cell.bottomF.start < topCell.bottomF.start) {
                        topCell.bottomF = topCell.bottomF;
                    } else if (cell.bottomF.isEmpty()
                            || cell.bottomF.start > topCell.bottomF.end) {
                        topCell.bottomF = new Interval();
                    } else {
                        topCell.bottomF = new Interval(cell.bottomF.start,
                                topCell.bottomF.end);
                    }
                }
            }
        }
    }

    void resizeCells() {
        int newSize = Q.size() * P.size();
        if (cells == null) {
            cells = new Cell[newSize];
        } else {
            // if(cells.length > newSize) {
            // return;
            // } else {
            // cells = new Cell[newSize + 10*10];
            // needsRecalculation = true;
            // }
            cells = new Cell[newSize];
        }
    }

    //
    public Cell getCell(int i, int j) {
        assert (0 <= i && i < P.size());
        assert (0 <= j && j < Q.size());

        if (!(0 <= i && i < P.size() && 0 <= j && j < Q.size())) {
            return null;
        }

        if (cells == null) {
            cells = new Cell[Q.size() * P.size()];
        }

        if (cells[j * P.size() + i] == null) {
            cells[j * P.size() + i] = new Cell(i, j);
        }

        return cells[j * P.size() + i];
    }

    public double getDistance(List<AggConnection> a, List<GPSEdge> b) {
        if (a == null || b == null || a.size() < 1 || b.size() < 1) {
            return Double.POSITIVE_INFINITY;
        }

        this.P = new ArrayList<AggConnection>(a);
        this.Q = new ArrayList<GPSEdge>(b);

        resizeCells();

        double result = computeEpsilon();

        this.P = null;
        this.Q = null;

        return result;
    }

    //
    public boolean isInDistance(List<GPSEdge> a, List<GPSEdge> b, double epsilon) {
        if (a == null || b == null || a.size() < 1 || b.size() < 1) {
            return false;
        }

        this.P = new ArrayList<AggConnection>();
        for (GPSEdge agg : a) {
            P.add(new AggConnection(agg.getFrom(), agg.getTo(), aggContainer));
            this.Q = new ArrayList<GPSEdge>(b);
        }

        resizeCells();
        setEpsilon(epsilon);

        boolean result = isInDistance();

        this.P = null;
        this.Q = null;

        return result;
    }
}
