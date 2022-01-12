package com.evrhel.bsp;

import java.util.*;

/**
 * Defines a BSP tree.
 */
public class BSPTree implements Iterable<Polygon> {

    private final BSPNode root;
    private int polyCount;

    /**
     * Creates a BSP tree from a <code>List</code> of <code>Polygon</code>s.
     *
     * @param polys The <code>Polygon</code>s to generate the BSP tree from.
     */
    public BSPTree(List<Polygon> polys) {
        this.root = genTree(new LinkedList<>(polys));
    }

    /**
     * Returns the number of <code>Polygon</code>s this BSP tree has generated.
     *
     * @return The <code>Polygon</code> count.
     */
    public int getPolygonCount() {
        return this.polyCount;
    }

    @Override
    public Iterator<Polygon> iterator() {
        return new BSPIterator();
    }

    private BSPNode genTree(Queue<Polygon> polys) {
        if (polys.isEmpty()) return null;

        Polygon[] split = new Polygon[2];
        Queue<Polygon> behind = new LinkedList<>(), front = new LinkedList<>();

        Polygon rootPoly = polys.remove();
        Plane rootPlane = new Plane(rootPoly);

        BSPNode node = new BSPNode(rootPoly);
        while (!polys.isEmpty()) {
            Polygon child = polys.remove();

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
                node.add(child);
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

    private static class BSPNode {
        List<Polygon> polys;
        BSPNode parent;
        BSPNode behind, front;

        BSPNode(Polygon poly) {
            this.polys = new ArrayList<>();
            this.polys.add(poly);
        }

        void add(Polygon poly) {
            this.polys.add(poly);
        }
    }

    class BSPIterator implements Iterator<Polygon> {

        BSPNode next;
        Iterator<Polygon> currentIterator;

        BSPIterator() {
            this.next = BSPTree.this.root;
            if (this.next != null) {
                while (this.next.behind != null)
                    this.next = this.next.behind;
                this.currentIterator = this.next.polys.iterator();
            }
        }

        @Override
        public boolean hasNext() {
            return this.currentIterator != null;
        }

        @Override
        public Polygon next() {
            if (!hasNext())
                throw new NoSuchElementException();

            Polygon result = this.currentIterator.next();
            if (!this.currentIterator.hasNext()) {
                this.currentIterator = null;
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

                if (this.next != null)
                    this.currentIterator = this.next.polys.iterator();
            }
            return result;
        }
    }
}
