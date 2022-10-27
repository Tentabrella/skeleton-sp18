package byog.lab5.pojos;

import byog.TileEngine.TETile;

public class Hexgon {
    private final Position position;
    private final int size;
    private final TETile teTile;

    public Hexgon(Position position, int size, TETile teTile) {
        this.position = position;
        this.size = size;
        this.teTile = teTile;
    }

    public Position getPosition() {
        return position;
    }

    public int getSize() {
        return size;
    }

    public TETile getTeTile() {
        return teTile;
    }

    @Override
    public String toString() {
        return "Hexgon{" +
                "position=" + position +
                ", size=" + size +
                ", teTile=" + teTile.description() +
                '}';
    }
}
