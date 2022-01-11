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

    public World(List<Polygon> polys) {
        this.polys = new ArrayList<>(polys);
    }

    public List<Polygon> segments() {
        return Collections.unmodifiableList(polys);
    }
}
