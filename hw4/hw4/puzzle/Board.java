package hw4.puzzle;


import edu.princeton.cs.algs4.Queue;

import java.util.Arrays;

public class Board implements WorldState {
    private final int BLANK = 0;
    private final int[][] tiles;
    private final int size;

    /***
     * Constructs a board from an N-by-N array of tiles where
     * tiles[i][j] = tile at row i, column j
     * @param tiles
     */
    public Board(int[][] tiles) {
        this.tiles = Arrays.stream(tiles).map(el -> el.clone()).toArray(int[][]::new);
        size = tiles.length;
    }

    /**
     * Returns value of tile at row i, column j (or 0 if blank)
     * @param i
     * @param j
     * @return
     */
    public int tileAt(int i, int j) {
        if (valid(i) && valid(j)) {
            return tiles[i][j];
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    private boolean valid(int i) {
        return i >= 0 && i < size();
    }

    /**
     * Returns the board size N
     * @return
     */
    public int size() {
        return size;
    }

    /**
     * Returns the neighbors of the current board
     * @return
     * @source http://joshh.ug/neighbors.html
     */
    @Override
    public Iterable<WorldState> neighbors() {
        Queue<WorldState> neighbors = new Queue<>();
        int hug = size();
        int bug = -1;
        int zug = -1;
        for (int rug = 0; rug < hug; rug++) {
            for (int tug = 0; tug < hug; tug++) {
                if (tileAt(rug, tug) == BLANK) {
                    bug = rug;
                    zug = tug;
                }
            }
        }
        int[][] ili1li1 = new int[hug][hug];
        for (int pug = 0; pug < hug; pug++) {
            for (int yug = 0; yug < hug; yug++) {
                ili1li1[pug][yug] = tileAt(pug, yug);
            }
        }
        for (int l11il = 0; l11il < hug; l11il++) {
            for (int lil1il1 = 0; lil1il1 < hug; lil1il1++) {
                if (Math.abs(-bug + l11il) + Math.abs(lil1il1 - zug) - 1 == 0) {
                    ili1li1[bug][zug] = ili1li1[l11il][lil1il1];
                    ili1li1[l11il][lil1il1] = BLANK;
                    Board neighbor = new Board(ili1li1);
                    neighbors.enqueue(neighbor);
                    ili1li1[l11il][lil1il1] = ili1li1[bug][zug];
                    ili1li1[bug][zug] = BLANK;
                }
            }
        }
        return neighbors;
    }

    private int[] pos(int index) {
        int x = Math.floorDiv(index, size);
        int y = index % size;
        return new int[]{x, y};
    }
    
    private int[] expectPos(int number) {
        number--;
        int x = Math.floorDiv(number, size);
        int y = number % size;
        return new int[]{x, y};
    }

    /**
     * Hamming estimate described below
     * @return
     */
    public int hamming() {
        int hdiff = 0;
        for (int i = 0; i < size * size - 1; i++) {
            int[] pos = pos(i);
            if (tileAt(pos[0], pos[1]) != i + 1) {
                hdiff++;
            }
        }
        return hdiff;
    }

    /**
     * Manhattan estimate described below
     * @return
     */
    public int manhattan() {
        int mdiff = 0;
        for (int i = 0; i < size * size; i++) {
            int[] actPos = pos(i);
            int num = tileAt(actPos[0], actPos[1]);
            if (num != 0) {
                int[] expPos = expectPos(tileAt(actPos[0], actPos[1]));
                mdiff += Math.abs(expPos[0] - actPos[0]) + Math.abs(expPos[1] - actPos[1]);
            }
        }
        return mdiff;
    }

    /**
     * Estimated distance to goal. This method should
     * simply return the results of manhattan() when submitted to
     * Gradescope.
     * @return
     */
    @Override
    public int estimatedDistanceToGoal() {
        return manhattan();
    }

    /**
     * Returns true if this board's tile values are the same
     * position as y's
     * @param y
     * @return
     */
    public boolean equals(Object y) {
        if (y == this) {
            return true;
        }
        if (y == null || y.getClass() != this.getClass()) {
            return false;
        }
        Board boardy = (Board) y;
        if (boardy.size() != size) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                if (tileAt(i, j) != boardy.tileAt(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Returns the string representation of the board.*/
    public String toString() {
        StringBuilder s = new StringBuilder();
        int N = size();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%2d ", tileAt(i, j)));
            }
            s.append("\n");
        }
        s.append("\n");
        return s.toString();
    }

    @Override
    public int hashCode() {
        int result = Arrays.deepHashCode(tiles);
        result = 31 * result + size;
        return result;
    }
}
