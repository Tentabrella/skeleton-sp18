package hw2;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import org.junit.Assert;

import java.util.HashSet;
import java.util.Set;

public class Percolation {
    private final int N;
    private int[][] grid;
    private WeightedQuickUnionUF quickUnionUF;
    private int openSites = 0;
    private int[] directions = new int[]{-1, 0, 1, 0, -1};
    private Set<Integer> fullParent;

    /***
     * create N-by-N grid, with all sites initially blocked
     * @param N
     */
    public Percolation(int N) {
        if (N <= 0) {
            throw new IllegalArgumentException("the length is below zero");
        }
        grid = new int[N][N];
        this.N = N;
        quickUnionUF = new WeightedQuickUnionUF(N * N);
        fullParent = new HashSet<>();
        for (int i = 0; i < N; i++) {
            fullParent.add(i);
        }
    }

    /***
     * open the site (row, col) if it is not open already
     * @param row
     * @param col
     */
    public void open(int row, int col) {
        if (grid[row][col] != 0) {
            return;
        }
        grid[row][col] = 1;
        boolean isFull = row == 0;
        for (int i = 0; i < 4; i++) {
            int row2 = row + directions[i];
            int col2 = col + directions[i + 1];
            if (boundCheck(row2) && boundCheck(col2) && isOpen(row2, col2)) {
                quickUnionUF.union(simpEncode(row, col), simpEncode(row2, col2));
                if (!isFull && isFull(row2, col2)) {
                    isFull = true;
                }
            }
        }
        int parent = quickUnionUF.find(simpEncode(row, col));
        if (isFull) {
            fullParent.add(parent);
        }
        openSites++;
    }

    private int simpEncode(int row, int col) {
        return row * N + col;
    }

    private int[] simpDecode(int n) {
        int row = Math.floorDiv(n, N);
        int col = n % N;
        return new int[]{row, col};
    }

    /***
     * is the site (row, col) open?
     * @param row
     * @param col
     * @return
     */
    public boolean isOpen(int row, int col) {
        return grid[row][col] != 0;
    }

    private boolean boundCheck(int n) {
        if (n < 0 || n >= N) {
            return false;
        }
        return true;
    }

    /***
     * is the site (row, col) full?
     * @param row
     * @param col
     * @return
     */
    public boolean isFull(int row, int col) {
        if (grid[row][col] == 1) {
            return fullParent.contains(quickUnionUF.find(simpEncode(row, col)));
        }
        return false;
    }

    /***
     * number of open sites
     * @return
     */
    public int numberOfOpenSites() {
        return openSites;
    }

    /***
     * does the system percolate?
     * @return
     */
    public boolean percolates() {
        for (int i = 0; i < N; i++) {
            if (isFull(N - 1, i)) {
                return true;
            }
        }
        return false;
    }

    /***
     * use for unit testing (not required)
     * @param args
     */
    public static void main(String[] args) {
        Percolation small = new Percolation(2);
        Assert.assertFalse(small.percolates());
        small.open(0, 0);
        Assert.assertFalse(small.percolates());
        small.open(0, 1);
        Assert.assertEquals(2, small.numberOfOpenSites());
        Assert.assertFalse(small.percolates());
        small.open(1, 1);
        Assert.assertTrue(small.percolates());
        System.out.println("test pass");
    }
}
