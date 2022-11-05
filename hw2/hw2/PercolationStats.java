package hw2;

import edu.princeton.cs.introcs.StdRandom;
import edu.princeton.cs.introcs.StdStats;

public class PercolationStats {
    private final PercolationFactory factory;
    private int T;
    private int N;
    private double[] record;

    /***
     * perform T independent experiments on an N-by-N grid
     * @param N
     * @param T
     * @param pf
     */
    public PercolationStats(int N, int T, PercolationFactory pf) {
        if (N <= 0 || T <= 0) {
            throw new IllegalArgumentException("N or T is oob");
        }
        this.N = N;
        this.T = T;
        factory = pf;
        record = new double[T];
        helper();
    }

    /***
     * sample mean of percolation threshold
     * @return
     */
    public double mean() {
        return StdStats.mean(record);
    }

    private void helper() {
        for (int i = 0; i < T; i++) {
            Percolation percolation = factory.make(N);
            while (!percolation.percolates()) {
                int row = StdRandom.uniform(N);
                int col = StdRandom.uniform(N);
                percolation.open(row, col);
            }
            record[i] = percolation.numberOfOpenSites() / Math.pow(N, 2);
        }
    }

    /***
     * sample standard deviation of percolation threshold
     * @return
     */
    public double stddev() {
        return StdStats.stddev(record);
    }

    /***
     * low endpoint of 95% confidence interval
     * @return
     */
    public double confidenceLow() {
        return mean() - 1.96 * stddev() / Math.sqrt(T);
    }

    /***
     * high endpoint of 95% confidence interval
     * @return
     */
    public double confidenceHigh() {
        return mean() + 1.96 * stddev() / Math.sqrt(T);
    }
}
