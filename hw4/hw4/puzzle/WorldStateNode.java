package hw4.puzzle;

import java.util.HashSet;
import java.util.Set;

public class WorldStateNode implements Comparable<WorldStateNode> {
    private final WorldState worldState;
    private final int move;
    private final WorldStateNode parent;
    private final int estimatedTotalDistance;

    public WorldStateNode(WorldState worldState, int move, WorldStateNode parent) {
        this.worldState = worldState;
        this.move = move;
        this.parent = parent;
        estimatedTotalDistance = this.move + this.worldState.estimatedDistanceToGoal();
    }

    private int estimateTotalDistance() {
        return estimatedTotalDistance;
    }

    public boolean isGoal() {
        return worldState.isGoal();
    }

    public Iterable<WorldStateNode> children() {
        Set<WorldStateNode> neighbs = new HashSet<>();
        for (WorldState neighbor : worldState.neighbors()) {
            if (this.getParent() == null || !neighbor.equals(this.getParent().getWorldState())) {
                neighbs.add(new WorldStateNode(neighbor, move + 1, this));
            }
        }
        return neighbs;
    }

    public WorldStateNode getParent() {
        return parent;
    }

    public WorldState getWorldState() {
        return worldState;
    }

    public int getMove() {
        return move;
    }

    @Override
    public int compareTo(WorldStateNode node) {
        return Integer.compare(estimatedTotalDistance, node.estimateTotalDistance());
    }
}
