package com.evrhel.bsp;

import com.evrhel.bsp.render.ViewWindow;

import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Polygon polyA = new Polygon("A", new Vector2(1f, 0f), new Vector2(-1f, 0f));
        Polygon polyB = new Polygon("B", new Vector2(-1f, -.5f), new Vector2(-1f, .5f));
        Polygon polyC = new Polygon("C", new Vector2(1f, -1f), new Vector2(2f, .5f));
        Polygon polyD = new Polygon("D", new Vector2(-2f, 1f), new Vector2(-.5f, -2f));

        List<Polygon> polys = Arrays.asList(polyA, polyB, polyC, polyD);

       World world = new World(polys);

        ViewWindow win = new ViewWindow(world);
        win.show();

        /*BSPTree tree = new BSPTree(polys);
        tree.forEach(System.out::println);*/
    }
}
