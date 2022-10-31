package byog.Core.pojos;

import java.io.Serializable;

public class Position implements Serializable {
    private int X;
    private int Y;

    public Position(int X, int Y) {
        this.X = X;
        this.Y = Y;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    @Override
    public String toString() {
        return "Position{" +
                "posX=" + X +
                ", posY=" + Y +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (X != position.X) return false;
        return Y == position.Y;
    }

    @Override
    public int hashCode() {
        int result = X;
        result = 31 * result + Y;
        return result;
    }
}
