package org.concord.swing;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Vector;

/**
 * Replacement for org.concord.swing.QuickHull. Computes the 2D convex hull
 * via Andrew's monotone chain algorithm (O(n log n)); the hull of a point
 * set is unique, so this is behaviourally equivalent to the original for
 * the API surface used (constructor + getHullPointsAsVector).
 * Points are returned in counter-clockwise order.
 */
public class QuickHull {

    private final Vector<Point> hull = new Vector<Point>();

    public QuickHull(Point[] points) {
        if (points == null || points.length == 0) {
            return;
        }
        java.util.List<Point> nn = new java.util.ArrayList<Point>();
        for (Point q : points) if (q != null) nn.add(q);
        if (nn.isEmpty()) return;
        Point[] pts = nn.toArray(new Point[0]);
        Arrays.sort(pts, new Comparator<Point>() {
            public int compare(Point a, Point b) {
                if (a.x != b.x) return Integer.compare(a.x, b.x);
                return Integer.compare(a.y, b.y);
            }
        });
        int n = pts.length;
        if (n < 3) {
            for (Point p : pts) hull.add(p);
            return;
        }
        Point[] h = new Point[2 * n];
        int k = 0;
        // lower hull
        for (int i = 0; i < n; i++) {
            while (k >= 2 && cross(h[k - 2], h[k - 1], pts[i]) <= 0) k--;
            h[k++] = pts[i];
        }
        // upper hull
        for (int i = n - 2, t = k + 1; i >= 0; i--) {
            while (k >= t && cross(h[k - 2], h[k - 1], pts[i]) <= 0) k--;
            h[k++] = pts[i];
        }
        for (int i = 0; i < k - 1; i++) {
            hull.add(h[i]);
        }
    }

    private static long cross(Point o, Point a, Point b) {
        return (long) (a.x - o.x) * (b.y - o.y) - (long) (a.y - o.y) * (b.x - o.x);
    }

    public Vector<Point> getHullPointsAsVector() {
        return hull;
    }

    public Point[] getHullPoints() {
        return hull.toArray(new Point[0]);
    }
}
