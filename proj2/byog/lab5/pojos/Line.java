package byog.lab5.pojos;

import byog.TileEngine.TETile;

public class Line {
    private final Position position;
    private final int size;
    private final TETile teTile;

    public Line(Position position, int size, TETile teTile) {
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
}
