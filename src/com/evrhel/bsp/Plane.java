package com.evrhel.bsp;

/**
 * Defines a 2D plane.
 */
public class Plane {

    private final Vector2 origin, normal;

    /**
     * Constructs a <code>Plane</code> such that all points contained in
     * <code>poly</code> are in the new <code>Plane</code>.
     *
     * @param poly The <code>Polygon</code> to construct the <code>Plane</code>
     *             from.
     */
    public Plane(Polygon poly) {
        this(poly.getStart(), poly.getNormal());
    }

    /**
     * Constructs a <code>Plane</code> using an origin point and a normal
     * vector. This <code>Plane</code> will contain the set of points <code>p</code>
     * such that <code>dot(p - origin, normal) = 0</code>.
     *
     * @param origin The origin of the <code>Plane</code>.
     * @param normal The normal vector of the <code>Plane</code>.
     */
    public Plane(Vector2 origin, Vector2 normal) {
        this.origin = new Vector2(origin);
        this.normal = normal.normalize();
    }

    /**
     * Returns the origin of the <code>Plane</code>.
     *
     * @return The origin.
     */
    public Vector2 getOrigin() {
        return new Vector2(this.origin);
    }

    /**
     * Returns the normal vector of the <code>Plane</code>.
     *
     * @return The normal.
     */
    public Vector2 getNormal() {
        return new Vector2(this.normal);
    }

    @Override
    public String toString() {
        return "Plane[origin=" + this.origin + ",normal=" + this.normal + "]";
    }
}
