package com.evrhel.bsp;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.*;

public class World {

    public static World worldFromFile(String filename) throws FileNotFoundException {
        List<String> lines = new LinkedList<>();
        Scanner fin = new Scanner(new FileInputStream(filename));
        while (fin.hasNextLine()) {
            String line = fin.nextLine().trim();
            if (line.length() > 0 && line.charAt(0) != '#')
                lines.add(line);
        }
        fin.close();

        List<Polygon> polys = new LinkedList<>();
        lines.forEach(line -> {
            String[] tokens = line.split(" ");
            float x0, y0, x1, y1;
            String name = null;

            if (tokens.length < 4) {
                System.out.printf("Malformed line: \"%s\"\n", line);
                return;
            }

            try {
                x0 = Float.parseFloat(tokens[0]);
                y0 = Float.parseFloat(tokens[1]);
                x1 = Float.parseFloat(tokens[2]);
                y1 = Float.parseFloat(tokens[3]);
            } catch (NumberFormatException e) {
                System.out.printf("Malformed line: \"%s\"\n", line);
                return;
            }

            if (tokens.length > 4)
                name = tokens[4];

            polys.add(new Polygon(name, new Vector2(x0, y0), new Vector2(x1, y1)));
        });

        return new World(polys);
    }

    private final List<Polygon> polys;
    private final BSPTree bspTree;
    private final Vector2 min, max;

    public World(List<Polygon> polys) {
        this.polys = new ArrayList<>(polys);

        this.min = Vector2.Inf();
        this.max = Vector2.NInf();
        polys.forEach(poly -> {
            Vector2 start = poly.getStart();
            Vector2 end = poly.getEnd();

            this.min.x = Math.min(this.min.x, start.x);
            this.min.x = Math.min(this.min.x, end.x);
            this.min.y = Math.min(this.min.y, start.y);
            this.min.y = Math.min(this.min.y, end.y);

            this.max.x = Math.max(this.max.x, start.x);
            this.max.x = Math.max(this.max.x, end.x);
            this.max.y = Math.max(this.max.y, start.y);
            this.max.y = Math.max(this.max.y, end.y);
        });

        this.bspTree = new BSPTree(this.polys);
    }

    public List<Polygon> getPolys() {
        return Collections.unmodifiableList(this.polys);
    }

    public BSPTree getBspTree() {
        return bspTree;
    }

    public Vector2 getMin() {
        return new Vector2(this.min);
    }

    public Vector2 getMax() {
        return new Vector2(this.max);
    }
}
