package com.evrhel.bsp;

/**
 * 2-component floating-point vector
 */
public class Vector2 {

    public static Vector2 NaN() {
        return new Vector2(Float.NaN);
    }

    public static Vector2 Inf() {
        return new Vector2(Float.POSITIVE_INFINITY);
    }

    public static Vector2 NInf() {
        return new Vector2(Float.NEGATIVE_INFINITY);
    }

    public static float dot(Vector2 first, Vector2 second) {
        return first.x * second.x + first.y * second.y;
    }

    public float x, y;

    public Vector2() {
        this(0.0f);
    }

    public Vector2(float scalar) {
        this(scalar, scalar);
    }

    public Vector2(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector2(Vector2 other) {
        this(other.x, other.y);
    }

    public Vector2 add(Vector2 other) {
        return new Vector2(this.x + other.x, this.y + other.y);
    }

    public Vector2 sub(Vector2 other) {
        return new Vector2(this.x - other.x, this.y - other.y);
    }

    public Vector2 mul(Vector2 other) {
        return new Vector2(this.x * other.x, this.y * other.y);
    }

    public Vector2 mul(float scalar) {
        return new Vector2(this.x * scalar, this.y * scalar);
    }

    public Vector2 div(Vector2 other) {
        return new Vector2(this.x / other.x, this.y / other.y);
    }

    public Vector2 div(float scalar) {
        return new Vector2(this.x / scalar, this.y / scalar);
    }

    public float length() {
        return (float)Math.sqrt(this.x * this.x + this.y * this.y);
    }

    public Vector2 normalize() {
        return mul(1.0f / length());
    }

    public Vector2 rotate(float degrees) {
        double radians = Math.toRadians(degrees);
        return new Vector2(
                (float)Math.cos(radians) * this.x - (float)Math.sin(radians) * this.y,
                (float)Math.sin(radians) * this.x - (float)Math.cos(radians) * this.y
        );
    }

    @Override
    public String toString() {
        return String.format("(%.2f,%.2f)", this.x, this.y);
    }
}
