package de.fub.agg2graph.structs.frechet;

import de.fub.agg2graph.agg.AggNode;
import de.fub.agg2graph.structs.GPSPoint;
import de.fub.agg2graph.structs.GPSRegion;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Static KD-Tree with search of nearest neighbor, k-nearest-neighbors and
 * points in axis aligned box.
 *
 * Used basic implementation O(n log^2 n) recursive build with alternating
 * selection of axis.
 *
 *
 * @param <L>
 */
public class KdTreeIndex<L extends AggNode> implements SearchIndex<L> {

    public KdTreeIndex(Collection<L> locations) {
        root = build(new ArrayList<L>(locations), 0);
    }

    @Override
    public L searchNN(L point) {
        SearchState<L> state = new SearchState<L>();
        root.searchNN(point, 0, state);
        return state.bestSoFar;
    }

    @Override
    public List<L> searchKnn(L point, int k) {
        SearchState<L> state = new SearchState<>();
        root.searchKnn(point, 0, state, k);
        List<L> result = new ArrayList<>();
        for (int i = 0; i < k && i < state.bests.size(); ++i) {
            result.add(state.bests.get(i).value);
        }
        return result;
    }

    @Override
    public List<L> searchRegion(GPSRegion region) {
        assert (region.minLocation.getLon() <= region.maxLocation.getLon());
        assert (region.minLocation.getLat() <= region.maxLocation.getLat());

        List<L> result = new ArrayList<L>();
        root.searchRegion(region, 0, result);
        return result;
    }

    public int size() {
        return root.size();
    }

    static class SearchState<L extends GPSPoint> {

        L bestSoFar = null;
        ArrayList<Tuple<Double, L>> bests = new ArrayList<>();
        double minimumDistance = Double.POSITIVE_INFINITY;
    }

    static class Tuple<K extends Comparable<K>, V> implements Comparable<Tuple<K, V>> {

        public K key;
        public V value;

        Tuple(K key, V value) {
            this.key = key;
            this.value = value;
        }

        @Override
        public int compareTo(Tuple<K, V> other) {
            if (other == null) {
                throw new NullPointerException();
            }

            return key.compareTo(other.key);
        }
    }

    private class Node {

        ArrayList<L> locations;
        Node left;
        Node right;

        Node(L location) {
            locations = new ArrayList<L>();
            locations.add(location);
            left = null;
            right = null;
        }

        public Node(ArrayList<L> locations) {
            this.locations = locations;
            left = null;
            right = null;
        }

        void searchNN(L point, int depth, SearchState<L> state) {
            for (L location : locations) {
                double pointDistance = point.getSquaredDistanceTo(location);
                if (pointDistance < state.minimumDistance) {
                    state.bestSoFar = location;
                    state.minimumDistance = pointDistance;
                }
            }

            final int axis = getAxis(depth);

            Node close, far;
            // All attached locations share the axis coordinate so we use location at 0 as a representative.
            double diff = distanceToAxis(axis, locations.get(0), point);
            if (diff <= 0) {
                close = right;
                far = left;
            } else {
                close = left;
                far = right;
            }
            if (close != null) {
                close.searchNN(point, depth + 1, state);
            }

            if (diff * diff < state.minimumDistance && far != null) {
                far.searchNN(point, depth + 1, state);
            }
        }

        public void searchKnn(L point, int depth, SearchState<L> state, int k) {
            Tuple<Double, L> maxTp = (state.bests.isEmpty()) ? null : Collections.max(state.bests);

            for (L location : locations) {
                double pointDistance = point.getSquaredDistanceTo(location);

                if (maxTp == null) {
                    maxTp = new Tuple<Double, L>(pointDistance, location);
                    state.bests.add(maxTp);
                } else if (state.bests.size() < k || pointDistance < maxTp.key) {
                    state.bests.add(new Tuple<Double, L>(pointDistance, location));
                }

                if (state.bests.size() > k) { // could be maximum of 1 diff here.
                    state.bests.remove(maxTp);
                }
            }

            final int axis = getAxis(depth);

            Node close, far;
            // All attached locations share the axis coordinate so we use location at 0 as a representative.
            double diff = distanceToAxis(axis, locations.get(0), point);
            if (diff <= 0) {
                close = right;
                far = left;
            } else {
                close = left;
                far = right;
            }
            if (close != null) {
                close.searchKnn(point, depth + 1, state, k);
            }

            if (far != null && (diff * diff < maxTp.key || state.bests.size() < k)) {
                far.searchKnn(point, depth + 1, state, k);
            }
        }

        public void searchRegion(GPSRegion region, int depth, List<L> result) {
            int axis = getAxis(depth);
            double cmp1 = distanceToAxis(axis, locations.get(0), region.minLocation);
            double cmp2 = distanceToAxis(axis, locations.get(0), region.maxLocation);

            if (cmp1 >= 0 && left != null) {
                left.searchRegion(region, depth + 1, result);
            }

            if (cmp2 <= 0 && right != null) {
                right.searchRegion(region, depth + 1, result);
            }

            if (cmp1 >= 0 && cmp2 <= 0) {
                for (L location : locations) {
                    if (region.contains(location)) {
                        result.add(location);
                    }
                }
            }
        }

        public int size() {
            return locations.size()
                    + ((left != null) ? left.size() : 0)
                    + ((right != null) ? right.size() : 0);
        }
    }

    private Node build(List<L> points, int depth) {
        if (points.isEmpty()) {
            return null;
        }

        if (points.size() == 1) {
            return new Node(points.get(0));
        }

        final int axis = getAxis(depth);
        int median = points.size() / 2;

        sortByAxis(points, axis);

        ArrayList<L> listOfEqualCoord = new ArrayList<L>();
        L medianObject = points.get(median);
        listOfEqualCoord.add(medianObject);

        int higherIndex = median + 1;
        for (int i = higherIndex; i < points.size(); ++i) {
            L current = points.get(i);
            if (comparators[axis].compare(medianObject, current) == 0) {
                listOfEqualCoord.add(current);
                higherIndex = i;
            } else {
                break;
            }
        }

        int lowerIndex = median - 1;
        for (int i = lowerIndex; i > 0; --i) {
            L current = points.get(i);
            if (comparators[axis].compare(medianObject, current) == 0) {
                listOfEqualCoord.add(current);
                lowerIndex = i;
            } else {
                break;
            }
        }

        Node node = new Node(listOfEqualCoord);
        node.left = build(points.subList(0, lowerIndex + 1), depth + 1);
        node.right = build(points.subList(higherIndex, points.size()), depth + 1);
        return node;
    }

    private static int getAxis(int depth) {
        return depth % 2;
    }

    private static double distanceToAxis(int axis, GPSPoint here, GPSPoint point) {
        if (axis == 0) {
            return here.getLon() - point.getLon();
        } else {
            return here.getLat() - point.getLat();
        }
    }

    private void sortByAxis(List<L> points, final int axis) {
        Collections.sort(points, comparators[axis]);
    }

    /**
     * Defined axis comparators. Used in the sort step for sorting based on the
     * axis.
     */
    @SuppressWarnings("unchecked")
    static Comparator<GPSPoint> comparators[] = new Comparator[2];

    { // static initialize the comparators.
        comparators[0] = new LongitudeComparator();
        comparators[1] = new LatitudeComparator();
    }

    private static class LatitudeComparator implements Comparator<GPSPoint> {

        @Override
        public int compare(GPSPoint o1, GPSPoint o2) {
            return Double.compare(o1.getLat(), o2.getLat());
        }
    }

    private static class LongitudeComparator implements Comparator<GPSPoint> {

        @Override
        public int compare(GPSPoint o1, GPSPoint o2) {
            return Double.compare(o1.getLon(), o2.getLon());
        }
    }

    private Node root = null;

}
