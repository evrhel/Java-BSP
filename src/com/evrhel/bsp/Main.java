package com.evrhel.bsp;

import java.io.FileNotFoundException;

public class Main {

    public static void main(String[] args) {
        Polygon poly = new Polygon("A", new Vector2(0, 0), new Vector2(1, -1));
        Plane plane = new Plane(new Vector2(0, -1), new Vector2(0, -1));

        int position = RelativePosition.positionOf(plane, poly);
        if (position == RelativePosition.BEHIND) {
            System.out.println("Polygon is behind plane");
        } else if (position == RelativePosition.FRONT) {
            System.out.println("Polygon is in front of plane");
        } else {
            Vector2 intersection = poly.intersection(plane);
            System.out.println("Polygon intersects plane at " + intersection);
            Polygon[] split = new Polygon[2];
            poly.split(plane, split);

            System.out.println("Polygon split into: behind = " +
                    split[RelativePosition.BEHIND] + " and front = " + split[RelativePosition.FRONT]);
        }
    }
}
