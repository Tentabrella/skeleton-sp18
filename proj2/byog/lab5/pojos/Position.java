package byog.lab5.pojos;

public class Position {
    private final int posX;
    private final int posY;

    public Position(int posX, int posY) {
        this.posX = posX;
        this.posY = posY;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    @Override
    public String toString() {
        return "Position{" +
                "posX=" + posX +
                ", posY=" + posY +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (posX != position.posX) return false;
        return posY == position.posY;
    }

    @Override
    public int hashCode() {
        int result = posX;
        result = 31 * result + posY;
        return result;
    }
}
