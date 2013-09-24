package de.fub.agg2graph.structs.frechet;

public class Interval {

    public double start;
    public double end;

    public Interval(double start, double end) {
        this.start = start;
        this.end = end;
    }

    public Interval() {
        start = Double.MAX_VALUE;
        end = Double.MIN_VALUE;
    }

    public boolean isEmpty() {
        return start > end;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "()";
        } else {
            return "(" + start + "..." + end + ")";
        }
    }
}
