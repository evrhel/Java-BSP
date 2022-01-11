package com.evrhel.bsp.render;

import com.evrhel.bsp.BSPTree;
import com.evrhel.bsp.Polygon;
import com.evrhel.bsp.Vector2;
import com.evrhel.bsp.World;

import javax.swing.text.View;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.text.AttributedCharacterIterator;

public class ViewWindow {

    private World world;
    private InternalWindow window;

    public ViewWindow(World world) {
        this.world = world;
        this.window = new InternalWindow();
    }

    public void show() {
        this.window.setVisible(true);
    }

    class InternalWindow extends Frame implements WindowListener {

        InternalWindow() {
            add(new RenderComponent());
            addWindowListener(this);
            setTitle("BSP");
            pack();
            setLocationRelativeTo(null);
            setVisible(true);
        }

        @Override
        public void windowOpened(WindowEvent e) { }

        @Override
        public void windowClosing(WindowEvent e) {
            System.exit(0);
        }

        @Override
        public void windowClosed(WindowEvent e) { }

        @Override
        public void windowIconified(WindowEvent e) { }

        @Override
        public void windowDeiconified(WindowEvent e) { }

        @Override
        public void windowActivated(WindowEvent e) { }

        @Override
        public void windowDeactivated(WindowEvent e) { }

        class RenderComponent extends Component {

            BufferedImage backBuffer;
            int width, height;
            Graphics graphics;
            Vector2 min, max;

            RenderComponent() {
                this.min = ViewWindow.this.world.getMin();
                this.max = ViewWindow.this.world.getMax();
                setPreferredSize(new Dimension(500, 500));
            }

            void setupBuffers() {
                if (this.graphics != null)
                    this.graphics.dispose();

                if (this.backBuffer != null)
                    this.backBuffer.flush();

                Dimension dim = getSize();
                this.width = dim.width;
                this.height = dim.height;
                this.backBuffer = new BufferedImage(this.width, this.height, BufferedImage.TYPE_INT_ARGB);
                this.graphics = backBuffer.createGraphics();
            }

            @Override
            public void paint(Graphics g) {
                if (this.backBuffer != null && this.graphics != null) {
                    this.graphics.setColor(Color.WHITE);
                    this.graphics.fillRect(0, 0, this.width, this.height);
                    drawWorld();
                    g.drawImage(this.backBuffer, 0, 0, this);
                }
            }

            @Override
            public void invalidate() {
                super.invalidate();

                this.width = -1;
                this.height = -1;

                if (this.graphics != null)
                    this.graphics.dispose();
                this.graphics = null;

                if (this.backBuffer != null)
                    this.backBuffer.flush();
                this.backBuffer = null;
            }

            @Override
            public void revalidate() {
                super.revalidate();
            }

            @Override
            public void validate() {
                super.validate();
                setupBuffers();
            }

            void drawWorld() {
                World world = ViewWindow.this.world;
                BSPTree tree = world.getBspTree();
                tree.forEach(this::drawPoly);

                this.graphics.setColor(Color.BLACK);
                this.graphics.drawString(String.format("World Polys: %d Partitioned Polys: %d",
                        world.getPolys().size(), tree.getPolygonCount()), 0, 10);
            }

            void drawPoly(Polygon poly) {
                Point start = toPoint(poly.getStart());
                Point end = toPoint(poly.getEnd());

                this.graphics.setColor(Color.RED);
                this.graphics.drawLine(start.x, start.y, end.x, end.y);

                Vector2 mid = poly.getStart().add(poly.getEnd()).div(2);

                Point normStart = toPoint(mid);
                Point normEnd = toPoint(mid.add(poly.getNormal()));

                Vector2 normal = new Vector2(normEnd.x - normStart.x, normEnd.y - normStart.y);
                normal = normal.normalize().mul(50);

                this.graphics.setColor(Color.GREEN);
                this.graphics.drawLine(normStart.x, normStart.y, (int)(normStart.x + normal.x), (int)(normStart.y + normal.y));

                this.graphics.setColor(Color.BLACK);
                this.graphics.fillOval(start.x - 3, start.y - 3, 6, 6);
                this.graphics.fillOval(end.x - 3, end.y - 3, 6, 6);
            }

            Point toPoint(Vector2 point) {
                float x = this.width / (this.max.x - this.min.y);
                float y = this.height / (this.max.y - this.min.y);

                Vector2 toOrigin = point.sub(this.min);
                Vector2 scaled = toOrigin.mul(new Vector2(x, y));

                return new Point((int)scaled.x, (int)scaled.y);
            }
        }
    }
}
