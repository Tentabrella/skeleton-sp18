package hw4.puzzle;

import edu.princeton.cs.algs4.MinPQ;
import edu.princeton.cs.algs4.Stack;

public class Solver {
    private MinPQ<WorldStateNode> candidatePQ;
    private WorldStateNode node;

    /***
     * Constructor which solves the puzzle, computing
     * everything necessary for moves() and solution() to
     * not have to solve the problem again. Solves the
     * puzzle using the A* algorithm. Assumes a solution exists.
     * @param initial
     */
    public Solver(WorldState initial) {
        candidatePQ = new MinPQ<>();
        node = new WorldStateNode(initial, 0, null);

        while (!node.isGoal()) {
            for (WorldStateNode child : node.children()) {
                candidatePQ.insert(child);
            }
            node = candidatePQ.delMin();
        }
    }

    /***
     * Returns the minimum number of moves to solve the puzzle starting
     * at the initial WorldState.
     * @return
     */
    public int moves() {
        return node.getMove();
    }

    /***
     * Returns a sequence of WorldStates from the initial WorldState
     * to the solution.
     * @return
     */
    public Iterable<WorldState> solution() {
        Stack<WorldState> stack = new Stack<>();
        WorldStateNode pointer = node;
        while (pointer != null) {
            stack.push(pointer.getWorldState());
            pointer = pointer.getParent();
        }
        return stack;
    }
}
