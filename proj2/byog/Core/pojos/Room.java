package byog.Core.pojos;

public class Room {
    private int width;
    private int height;
    private Position position;

    public Room(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public Room(int width, int height, Position position) {
        this.width = width;
        this.height = height;
        this.position = position;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Room{" +
                "width=" + width +
                ", height=" + height +
                ", position=" + position +
                '}';
    }
}
