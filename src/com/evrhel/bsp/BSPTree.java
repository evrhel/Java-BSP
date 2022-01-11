package com.evrhel.bsp;

import java.util.*;

public class BSPTree {

    private BSPNode root;

    public BSPTree(List<Polygon> polys) {
        this.root = genTree(polys);
    }

    private BSPNode genTree(List<Polygon> polys) {
        Iterator<Polygon> polygonIterator = polys.iterator();
        if (!polygonIterator.hasNext()) return null;

        Polygon[] split = new Polygon[2];
        List<Polygon> behind = new LinkedList<>(), front = new LinkedList<>();

        Polygon rootPoly = polygonIterator.next();
        Plane rootPlane = new Plane(rootPoly);

        BSPNode node = new BSPNode(rootPoly);
        while (polygonIterator.hasNext()) {
            Polygon child = polygonIterator.next();

            int pos = RelativePosition.positionOf(rootPlane, child);
            switch (pos) {
            case RelativePosition.BEHIND:
                behind.add(child);
                break;
            case RelativePosition.FRONT:
                front.add(child);
                break;
            case RelativePosition.INTERSECT:
                if (!child.split(rootPlane, split))
                    throw new IllegalStateException("Polygon couldn't be split when it intersects plane");
                behind.add(split[RelativePosition.BEHIND]);
                front.add(split[RelativePosition.FRONT]);
                break;
            }
        }

        node.behind = genTree(behind);
        node.front = genTree(front);

        return node;
    }

    private class BSPNode {
        Polygon poly;
        BSPNode behind, front;

        BSPNode(Polygon poly) {
            this.poly = poly;
        }
    }
}
