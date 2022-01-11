package com.evrhel.bsp;

import java.util.*;

public class BSPTree implements Iterable<Polygon> {

    private BSPNode root;
    private int polyCount;

    public BSPTree(List<Polygon> polys) {
        this.root = genTree(polys);
    }

    public int getPolygonCount() {
        return this.polyCount;
    }

    @Override
    public Iterator<Polygon> iterator() {
        return new BSPIterator();
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
            case RelativePosition.ON:
                return null; // discard
            }
        }

        this.polyCount++;

        node.behind = genTree(behind);
        if (node.behind != null)
            node.behind.parent = node;

        node.front = genTree(front);
        if (node.front != null)
            node.front.parent = node;

        return node;
    }

    private class BSPNode {
        Polygon poly;
        BSPNode parent;
        BSPNode behind, front;

        BSPNode(Polygon poly) {
            this.poly = poly;
        }
    }

    class BSPIterator implements Iterator<Polygon> {

        BSPNode next;

        BSPIterator() {
            this.next = BSPTree.this.root;
            if (this.next != null) {
                while (this.next.behind != null)
                    this.next = this.next.behind;
            }
        }

        @Override
        public boolean hasNext() {
            return this.next != null;
        }

        @Override
        public Polygon next() {
            if (this.next == null)
                throw new NoSuchElementException();

            Polygon poly = this.next.poly;

            if (this.next.front != null) {
                this.next = this.next.front;
                while (this.next.behind != null)
                    this.next = this.next.behind;
            } else {
                do {
                    if (this.next.parent == null) {
                        this.next = null;
                        break;
                    }

                    if (this.next.parent.behind == this.next) {
                        this.next = this.next.parent;
                        break;
                    }

                    this.next = this.next.parent;
                } while (true);
            }

            return poly;
        }
    }
}
