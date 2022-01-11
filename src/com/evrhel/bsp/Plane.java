package com.evrhel.bsp;

public class Plane {

    private final Vector2 origin, normal;

    public Plane(Polygon poly) {
        this(poly.getStart(), poly.getNormal());
    }

    public Plane(Vector2 origin, Vector2 normal) {
        this.origin = new Vector2(origin);
        this.normal = normal.normalize();
    }

    public Vector2 getOrigin() {
        return new Vector2(this.origin);
    }

    public Vector2 getNormal() {
        return new Vector2(this.normal);
    }

    @Override
    public String toString() {
        return "Plane[origin=" + this.origin + ",normal=" + this.normal + "]";
    }
}
