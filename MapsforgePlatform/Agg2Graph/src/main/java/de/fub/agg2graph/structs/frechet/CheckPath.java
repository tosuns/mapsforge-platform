package de.fub.agg2graph.structs.frechet;

import de.fub.agg2graph.structs.frechet.FrechetDistance.Cell;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to determined the monotone (and later conformal path) in
 * the free space diagram. All possible path are computed with dynamic
 * programming. From Algorithm 1 Alt'95
 *
 * @author Martinus
 *
 */
public class CheckPath {

    // Free space cells
    public Cell[][] cells;

    // Parameter that secure the monotone requirements
    int i; // The highest i
    int j; // The highest j
    double maxDistance;

    // Head
    Point start;
    Point head;
    List<Point> heads;

    /**
     * Constructor from the @FrechetDistance i = agg ; j = trace
     *
     * @param fd = used FrechetDistance
     */
    public CheckPath(FrechetDistance fd) {
        i = fd.getSizeP();
        j = fd.getSizeQ();
        cells = new Cell[i][j];
        for (Cell c : fd.cells) {
            cells[c.i][c.j] = c;
        }
        maxDistance = fd.getEpsilon();
        start = new Point();
        head = new Point();
        heads = new ArrayList<Point>();
    }

    /**
     * Algorithm 1 from Alt's paper
     */
    public void algorithm1() {
        // Start Point should be (0,0) else TODO
        algorithm1(0, 0);
        start.setLocation(0, 0);
        System.out.println(head);
        // TODO: for debug-test only
        for (int i = 0; i < this.i; i++) {
            for (int j = 0; j < this.j; j++) {
                System.out.println("i = " + i + " : j = " + j);
                System.out.println("leftR   = " + !cells[i][j].getLeftR().isEmpty());
                System.out.println("bottomR = "
                        + !cells[i][j].getBottomR().isEmpty());
            }
        }
    }

    /**
     *
     * @param i vertical
     * @param j horizontal
     */
    private void algorithm1(int i, int j) {
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
                    updateHead(it, jt);
                }
            }
        }
    }

    private void construct(int i, int j) {
        if (i < (this.i - 1)) {
            calculateBottom(i, j);
        }
        if (j < (this.j - 1)) {
            calculateLeft(i, j);
        }
        // only for check
        if (i == (this.i - 1) && j == (this.j - 1)) {
            Cell current = cells[i][j];
            if (!current.getBottomR().isEmpty()
                    && !current.getLeftR().isEmpty()) {
                System.out.println("MATCHED");
            }
        }
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
            // start = top.getBottomF().start;
            // end = top.getBottomF().end;
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
     * Update head, if the point is further than head
     *
     * @param current
     */
    private void updateHead(int i, int j) {
        if ((i + j) > (head.x + head.y)) {
            head.setLocation(i, j);
        }
    }

    /**
     * TODO: Check correctness Check connectivity from head to start
     */
    public boolean checkConnectivity() {
        Cell current = cells[head.x][head.y];
        boolean startFound = false;
        if (i == start.x
                && j == start.y
                && (!current.getBottomR().isEmpty() || !current.getLeftR()
                .isEmpty())) {
            startFound = true;
        } else {
            if (i > start.x && !current.getBottomR().isEmpty()) {
                if (checkConnectivity(head.x - 1, head.y)) {
                    startFound = true;
                }
            }
            if (j > start.y && !current.getLeftR().isEmpty()) {
                if (checkConnectivity(head.x, head.y - 1)) {
                    startFound = true;
                }
            }
        }

        return startFound;
    }

    private boolean checkConnectivity(int i, int j) {
        Cell current = cells[i][j];
        boolean startFound = false;

        if (i == start.x
                && j == start.y
                && (!current.getBottomR().isEmpty() || !current.getLeftR()
                .isEmpty())) {
            startFound = true;
        } else {
            if (i > start.x && !current.getBottomR().isEmpty()) {
                if (checkConnectivity(i - 1, j)) {
                    startFound = true;
                }
            }
            if (j > start.y && !current.getLeftR().isEmpty()) {
                if (checkConnectivity(i, j - 1)) {
                    startFound = true;
                }
            }
        }

        return startFound;
    }
}
