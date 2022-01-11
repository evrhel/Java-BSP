package com.evrhel.bsp;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Polygon polyA = new Polygon("A", new Vector2(0, 0), new Vector2(0, 1));
        Polygon polyB = new Polygon("B", new Vector2(0, 0), new Vector2(1, 0));
        Polygon polyC = new Polygon("C", new Vector2(-1, -1), new Vector2(1, 1));

        List<Polygon> polys = Arrays.asList(polyA, polyB, polyC);

        BSPTree tree = new BSPTree(polys);
        tree.forEach(System.out::println);
    }
}
