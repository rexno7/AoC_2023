package day18;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Day18 {
    
    public static void main(String[] args) throws Exception {
        Scanner scanner = new Scanner(new File("src/day18/resources/input.txt"));
        Map<Coord, String> trench = new HashMap<Coord, String>();

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = 0;
        int maxY = 0;
        Coord diggerLocation = new Coord(0, 0);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] params = line.split(" ");
            Coord vector = null;
            int distance = Integer.parseInt(params[1]);
            String color = params[2].substring(1, params[2].length()-1);
            switch (params[0]) {
                case "R":
                    vector = new Coord(1, 0);
                    break;
                case "L":
                    vector = new Coord(-1, 0);
                    break;
                case "U":
                    vector = new Coord(0, -1);
                    break;
                case "D":
                    vector = new Coord(0, 1);
                    break;
                default:
                    throw new Exception("Bad direction: " + params[0]);
            }
            for (int i=0; i < distance; i++) {
                trench.put(diggerLocation, color);
                diggerLocation = diggerLocation.add(vector);
                minX = minX > diggerLocation.x ? diggerLocation.x : minX;
                minY = minY > diggerLocation.y ? diggerLocation.y : minY;
                maxX = maxX < diggerLocation.x ? diggerLocation.x : maxX;
                maxY = maxY < diggerLocation.y ? diggerLocation.y : maxY;
            }
        }
        scanner.close();

        int dugOutLagoonCubicMeters = 0;
        for (int y=minY; y <= maxY; y++) {
            boolean inTrench = false;
            int checkY = 0;
            for (int x=minX; x <= maxX; x++) {
                Coord pointer = new Coord(x, y);
                boolean pointerIsOnTrench = trench.containsKey(pointer);
                boolean prevPointerIsOnTrench = trench.containsKey(pointer.add(-1, 0));
                boolean trenchEdgeContinuesBack = trench.containsKey(pointer.add(0, checkY));
                if (pointerIsOnTrench && prevPointerIsOnTrench && trenchEdgeContinuesBack) { // Last edge
                    inTrench = !inTrench;
                    checkY = 0;
                } else if (pointerIsOnTrench && !prevPointerIsOnTrench) { // First edge
                    inTrench = !inTrench;
                    boolean hasNorthTrench = trench.containsKey(pointer.add(0, -1));
                    boolean hasSouthTrench = trench.containsKey(pointer.add(0, 1));
                    if (hasNorthTrench && !hasSouthTrench) {
                        // check north for trench at end of edge to swap back
                        checkY = -1;
                    } else if (!hasNorthTrench && hasSouthTrench) {
                        // check south for trench at end of edge to swap back
                        checkY = 1;
                    }
                }
                if (pointerIsOnTrench || inTrench) {
                    dugOutLagoonCubicMeters += 1;
                }
            }
        }
        System.out.println(dugOutLagoonCubicMeters);
    }
}

class Coord {
    int x, y;

    public Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coord add(int x, int y) {
        return new Coord(this.x + x, this.y + y);
    }

    public Coord add(Coord c) {
        return add(c.x, c.y);
    }

    public static int getCoordHash(int x, int y) {
        return (x + y + "").hashCode();
    }

    @Override
    public boolean equals(Object o) {
        Coord other = (Coord) o;
        return this.x == other.x && this.y == other.y;
    }

    @Override
    public int hashCode() {
        return Coord.getCoordHash(x, y);
    }

    @Override
    public String toString() {
        return "(" + x + "," + y + ")";
    }
}
