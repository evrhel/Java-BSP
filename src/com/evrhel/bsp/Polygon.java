package com.evrhel.bsp;

import static com.evrhel.bsp.Vector2.*;

public class Polygon {

    private final String name;
    private final Vector2 start, end;
    private final Vector2 normal;

    public Polygon(String name, Vector2 start, Vector2 end) {
        this.name = name;
        this.start = new Vector2(start);
        this.end = new Vector2(end);
        this.normal = this.end  .sub(this.start)
                                .normalize()
                                .rotate(90);
    }

    public String getName() {
        return this.name;
    }

    public Vector2 getStart() {
        return new Vector2(this.start);
    }

    public Vector2 getEnd() {
        return new Vector2(this.end);
    }

    public Vector2 getNormal() {
        return new Vector2(this.normal);
    }

    /**
     * Calculates the intersection point between this polygon and a plane.
     *
     * @param plane The plane to intersect this polygon with.
     * @return The intersection point, null if the plane and polygon do not intersect,
     * and Vector2.NaN() if the polygon is contained in the plane.
     */
    public Vector2 intersection(Plane plane) {
        // represent polygon as
        // l_0 + ld for l_0 = start, l = direction, and d [0, 1]

        Vector2 dir = this.end.sub(this.start);
        Vector2 planeNormal = plane.getNormal();
        Vector2 planeOrigin = plane.getOrigin();

        // intersection along this polygon as if it were an infinitely long line
        float denominator = dot(dir, planeNormal);
        if (denominator == 0) return null; // parallel

        float numerator = dot(planeOrigin.sub(this.start), planeNormal);
        if (numerator == 0) return Vector2.NaN(); // contained

        float d = numerator / denominator;

        // intersection must be within the polygon
        if (d < 0 || d > 1)
            return null;

        return dir.mul(d).add(this.start);
    }

    public boolean split(Plane plane, Polygon[] dest) {
        if (dest == null || dest.length != 2) return false;
        Vector2 intersection = intersection(plane);
        if (intersection == null) return false;

        Polygon first = new Polygon(name + "_1", start, intersection);
        Polygon second = new Polygon(name + "_2", intersection, end);

        if (RelativePosition.positionOf(plane, first) == RelativePosition.FRONT) {
            dest[RelativePosition.BEHIND] = second;
            dest[RelativePosition.FRONT] = first;
        } else {
            dest[RelativePosition.BEHIND] = first;
            dest[RelativePosition.FRONT] = second;
        }

        return true;
    }

    @Override
    public String toString() {
        return "Polygon[name=\"" + this.name + "\", " + this.start + " -> " + this.end + ")";
    }
}
