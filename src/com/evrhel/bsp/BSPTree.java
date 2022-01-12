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

    /**
     * Returns an iterator which returns <code>Polygon</code>s back-to-front
     * relative to the give position.
     *
     * @param position The position to determine the order of the <code>Iterator</code>.
     * @return A new <code>Iterator</code> over the <code>Polygon</code>s.
     */
    public Iterator<Polygon> iterator(Vector2 position) {
        Queue<Polygon> polys = new LinkedList<>();
        genPolyList(polys, position, this.root);
        return polys.iterator();
    }

    /**
     * Returns an <code>Iterator</code> over this tree. The <code>Iterator</code> returns
     * <code>Polygon</code>s in an arbitrary order.
     *
     * @return A new <code>Iterator</code>.
     */
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

        BSPNode node = new BSPNode(rootPlane);
        node.add(rootPoly);
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
                break;
            }
        }

        node.behind = genTree(behind);
        if (node.behind != null)
            node.behind.parent = node;

        node.front = genTree(front);
        if (node.front != null)
            node.front.parent = node;

        return node;
    }

    private void genPolyList(Queue<Polygon> polyQueue, Vector2 position, BSPNode node) {
        if (node == null) return;

        int relativePosition = RelativePosition.positionOf(node.plane, position);
        switch (relativePosition) {
        case RelativePosition.FRONT:
            genPolyList(polyQueue, position, node.behind);
            polyQueue.addAll(node.polys);
            genPolyList(polyQueue, position, node.front);
            break;
        case RelativePosition.BEHIND:
            genPolyList(polyQueue, position, node.front);
            polyQueue.addAll(node.polys);
            genPolyList(polyQueue, position, node.behind);
            break;
        case RelativePosition.ON:
            genPolyList(polyQueue, position, node.front);
            genPolyList(polyQueue, position, node.behind);
            break;
        }
    }

    private class BSPNode {
        List<Polygon> polys;
        Plane plane;
        BSPNode parent;
        BSPNode behind, front;

        BSPNode(Plane plane) {
            this.plane = plane;
            this.polys = new ArrayList<>();
        }

        void add(Polygon poly) {
            this.polys.add(poly);
            BSPTree.this.polyCount++;
        }
    }

    private class BSPIterator implements Iterator<Polygon> {

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
