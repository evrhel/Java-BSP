package com.evrhel.bsp;

import static com.evrhel.bsp.Vector2.*;

/**
 * Defines a polygon - sort of. This polygon is actually just a line segment but is
 * named this way to indicate the BSP algorithm can be abstracted to higher dimensions.
 */
public class Polygon {

    private final String name;
    private final Vector2 start, end;
    private final Vector2 normal;

    /**
     * Creates a new <code>Polygon</code> with a name and a start and end point.
     *
     * @param name The name of the <code>Polygon</code>, can be <code>null</code>.
     * @param start The start point of the <code>Polygon</code>.
     * @param end The end point of the <code>Polygon</code>.
     */
    public Polygon(String name, Vector2 start, Vector2 end) {
        this.name = name;
        this.start = new Vector2(start);
        this.end = new Vector2(end);
        this.normal = this.end  .sub(this.start)
                                .normalize()
                                .rotate(90);
    }

    /**
     * Returns the name of this <code>Polygon</code>, or <code>null</code> if none
     * exists.
     *
     * @return The name.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Returns the starting point of this <code>Polygon</code>.
     *
     * @return The start.
     */
    public Vector2 getStart() {
        return new Vector2(this.start);
    }

    /**
     * Returns the ending point of this <code>Polygon</code>.
     *
     * @return The end.
     */
    public Vector2 getEnd() {
        return new Vector2(this.end);
    }

    /**
     * Returns this <code>Polygon</code>'s normal vector.
     *
     * @return The normal vector.
     */
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

    /**
     * Splits this <code>Polygon</code> along a plane into 2 <code>Polygon</code>s.
     *
     * @param plane The <code>Plane</code> splitting this <code>Polygon</code>.
     * @param dest A 2-element array to store the 2 <code>Polygon</code>s split by <code>plane</code>.
     *             If no splitting occurred, elements of this array are unchanged. Elements
     *             in the array are organized such that index <code>RelativePosition.BEHIND</code>
     *             stores the resultant <code>Polygon</code> behind the <code>plane</code> and
     *             <code>RelativePosition.FRONT</code> stores the <code>Polygon</code> in front
     *             of <code>plane</code>.
     * @return <code>true</code> if the splitting was successful, or <code>false</code>
     * if <code>dest</code> is <code>null</code>, <code>dest.length != 2</code>, or
     * this <code>Polygon</code> does not intersect <code>plane</code>.
     */
    public boolean split(Plane plane, Polygon[] dest) {
        if (dest == null || dest.length != 2) return false;
        Vector2 intersection = intersection(plane);
        if (intersection == null) return false;

        Polygon first = new Polygon(name + "_1", start, intersection);
        Polygon second = new Polygon(name + "_2", intersection, end);

        // Determine relative locations of the two polygons
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
        return "Polygon[name=\"" + this.name + "\", " + this.start + " -> " + this.end + ", normal=" + this.normal + "]";
    }
}
