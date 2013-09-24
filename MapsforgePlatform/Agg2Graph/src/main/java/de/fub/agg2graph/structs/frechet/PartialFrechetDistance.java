package de.fub.agg2graph.structs.frechet;

import de.fub.agg2graph.structs.GPSEdge;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * TODO: More Continuity
 *
 * @author Martinus
 *
 */
public class PartialFrechetDistance extends FrechetDistance {

    // Free space cells
    public Cell[][] cells;

    // Parameter that secure the monotone requirements
    int i; // The highest i
    int j; // The highest j
    double maxDistance;

    // Head
    Point start;
    Point end;
    List<Pair<Point, Point>> pathList;

    public PartialFrechetDistance(double maxDistance) {
        super(maxDistance);
        init();
    }

    public PartialFrechetDistance(List<GPSEdge> a, List<GPSEdge> t,
            double epsilon) {
        super(a, t, epsilon);
        init();
    }

    @Override
    public void updateFreeSpace() {
        for (int i = 0; i < P.size(); ++i) {
            for (int j = 0; j < Q.size(); ++j) {
                if (getCell(i, j).isRelevant) {
                    getCell(i, j).updateCell();
                }
            }
        }
    }

    private void init() {
        i = super.getSizeP();
        j = super.getSizeQ();
        cells = new Cell[i][j];
        for (Cell c : super.cells) {
            cells[c.i][c.j] = c;
        }
        updateFreeSpace();
        maxDistance = super.getEpsilon();
        start = new Point();
        end = new Point();
        pathList = new ArrayList<Pair<Point, Point>>();
    }

    @Override
    public void calculateReachableSpace() {
        int i = 0;
        int j = 0;
        boolean moreWhiteSpace = true;
        Point nextStart = null;
        start.setLocation(i, j);
        while (moreWhiteSpace) {
            calculateReachableSpace(start.x, start.y);
            Pair<Point, Point> newPath = new Pair<Point, Point>(start, end);
            pathList.add(newPath);
            nextStart = checkAnotherWhiteSpaces(end.x, end.y);

            if (nextStart != null) {
                start = new Point(nextStart);
                end = new Point();
            } else {
                moreWhiteSpace = false;
            }
        }
        // TODO: for debug-test only
        // for (int i = 0; i < this.i; i++) {
        // for (int j = 0; j < this.j; j++) {
        // System.out.println("i = " + i + " : j = " + j);
        // System.out.println("leftR   = "
        // + !cells[i][j].getLeftR().isEmpty());
        // System.out.println("bottomR = "
        // + !cells[i][j].getBottomR().isEmpty());
        // }
        // }
    }

    /**
     * This function does almost similar to the @calculateReachableSpace from
     * the super class.
     *
     * @param i vertical
     * @param j horizontal
     */
    private void calculateReachableSpace(int i, int j) {
        for (int it = i; it < this.i; it++) {
            // Calculate LR_i,1
            Cell current = cells[it][j];
            current.setLeftR(current.getLeftF());
        }
        for (int jt = j; jt < this.j; jt++) {
            // Calculate BR_1,j
            Cell current = cells[i][jt];
            current.setBottomR(current.getBottomF());
        }
        for (int it = i; it < this.i; it++) {
            for (int jt = j; jt < this.j; jt++) {
                // Construct the rest
                construct(it, jt);
                Cell current = cells[it][jt];
                if (!current.getBottomR().isEmpty()
                        || !current.getLeftR().isEmpty()) {
                    updateEnd(it, jt);
                }
            }
        }
    }

    /**
     * This function is used to search another monotone path. It determines the
     * next start.
     *
     * @param x
     * @param y
     * @return
     */
    private Point checkAnotherWhiteSpaces(int x, int y) {
        List<Point> candidates = new ArrayList<Point>();
        for (int it = x; it < this.i; it++) {
            for (int jt = y; jt < this.j; jt++) {
                Cell current = cells[it][jt];
                if (!current.getBottomF().isEmpty()
                        || !current.getLeftF().isEmpty()) {
                    if (it > x || jt > y) {
                        candidates.add(new Point(it, jt));
                    }
                }
            }
        }
        Point best = new Point(-1, -1);
        for (Point candidate : candidates) {
            if (best.x + best.y > candidate.x + candidate.y || best.x == -1
                    || best.y == -1) {
                best = candidate;
            }
        }
        return ((best.x == -1 || best.y == -1) ? null : best);
    }

    /**
     * Help function of Algorithm 1 alt'95
     *
     * @param i
     * @param j
     */
    private void construct(int i, int j) {
        if (i < (this.i - 1)) {
            calculateBottom(i, j);
        }
        if (j < (this.j - 1)) {
            calculateLeft(i, j);
        }
        // only for check
        // if (i == (this.i - 1) && j == (this.j - 1)) {
        // Cell current = cells[i][j];
        // if (!current.getBottomR().isEmpty()
        // && !current.getLeftR().isEmpty()) {
        // System.out.println("MATCHED");
        // }
        // }
    }

    /**
     * Calculate BR
     *
     * @param i
     * @param j
     */
    private void calculateBottom(int i, int j) {
        Cell current = cells[i][j];
        Cell top = cells[i + 1][j];

        // if current bottom is empty, then bottomR = bottomF
        if (current.getBottomR().isEmpty() && !current.getLeftR().isEmpty()) {
            Interval bottom = new Interval(top.getBottomF().start,
                    top.getBottomF().end);
            top.setBottomR(bottom);
            return;
        } // if bottom is not empty, then calculate it!
        else if (!current.getBottomR().isEmpty()) {
            Interval bottom = calculate(current.getBottomR(), top.getBottomF());
            top.setBottomR(bottom);
            return;
        }

        // Set BR_i,j+1
        top.setBottomR(new Interval(Double.MAX_VALUE, Double.MIN_VALUE));
    }

    /**
     * Calculate LR
     *
     * @param i
     * @param j
     */
    private void calculateLeft(int i, int j) {
        Cell current = cells[i][j];
        Cell right = cells[i][j + 1];

        // if current left is empty, then leftR = leftF
        if (current.getLeftR().isEmpty() && !current.getBottomR().isEmpty()) {
            // start = right.getLeftF().start;
            // end = right.getLeftF().end;
            Interval left = new Interval(right.getLeftF().start,
                    right.getLeftF().end);
            right.setLeftR(left);
            return;
        } // if left is not empty, then calculate it!
        else if (!current.getLeftR().isEmpty()) {
            Interval left = calculate(current.getLeftR(), right.getLeftF());
            right.setLeftR(left);
            return;
        }

        // Set LR_i+1,j
        right.setLeftR(new Interval(Double.MAX_VALUE, Double.MIN_VALUE));
    }

    /**
     * R-Interval calculation
     *
     * @param before
     * @param after
     * @return
     */
    private Interval calculate(Interval before, Interval after) {
        double start = Double.MAX_VALUE;
        double end = Double.MIN_VALUE;

        // No available F
        if (after.isEmpty()) {
            return new Interval(start, end);
        }

        // Determine Start
        if (before.start <= after.start) {
            start = after.start;
        } else if (before.start > after.start && before.start <= after.end) {
            start = before.start;
        } else {
            return new Interval(start, end);
        }

        // Determine End
        end = after.end;

        return new Interval(start, end);
    }

    /**
     * Update end, if the point is further than head
     *
     * @param current
     */
    private void updateEnd(int i, int j) {
        if ((i + j) > (end.x + end.y)) {
            end.setLocation(i, j);
        }
    }

    public List<Pair<Point, Point>> getPathList() {
        calculateReachableSpace();

        return pathList;
    }

}
