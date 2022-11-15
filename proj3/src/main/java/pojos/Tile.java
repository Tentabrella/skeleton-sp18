package pojos;

import utils.Tiles;

import java.util.Objects;

public class Tile {
    private int dimension;
    private int x;
    private int y;
    private double lonLen;
    private double latLen;

    public Tile(int dimension, int x, int y) {
        this.dimension = dimension;
        this.x = x;
        this.y = y;
        lonLen = Tiles.D0LON/Math.pow(2, dimension);
        latLen = Tiles.D0LAT/Math.pow(2, dimension);
    }

    public String tilePath() {
        return tilePath("");
    }

    public String tilePath(String prefix) {
        String path = String.format(prefix + "d%d_x%d_y%d.png", dimension, x, y);
        return path;
    }

    public int getDimension() {
        return dimension;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double ulLon() {
        return Tiles.D0ULLON + x * lonLen;
    }

    public double lrLon() {
        return ulLon() + lonLen;
    }

    public double ulLat() {
        return Tiles.D0ULLAT + y * latLen;
    }

    public double lrLat() {
        return ulLat() + latLen;
    }

    public double getLonLen() {
        return lonLen;
    }

    public double getLatLen() {
        return latLen;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return dimension == tile.dimension && x == tile.x && y == tile.y;
    }

    @Override
    public int hashCode() {
        return Objects.hash(dimension, x, y);
    }

    @Override
    public String toString() {
        return "Tile{" +
                "dimension=" + dimension +
                ", x=" + x +
                ", y=" + y +
                '}';
    }
}
