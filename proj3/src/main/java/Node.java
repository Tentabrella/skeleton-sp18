import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

public class Node implements Comparable<Node>{
    private final long id;
    private final Node parent;
    private final double distance;
    private final double estimateTotalDistance;
    private final long tgtId;
    private final GraphDB g;

    public Node(long id, Node parent, double distance, long tgtId, GraphDB g) {
        this.id = id;
        this.parent = parent;
        this.distance = distance;
        this.tgtId = tgtId;
        this.g = g;
        this.estimateTotalDistance = distance + estimateDistance(tgtId);
    }

    public Node(long id, Node parent, double distance) {
        this.id = id;
        this.parent = parent;
        this.distance = distance;
        this.tgtId = parent.tgtId;
        this.g = parent.g;
        this.estimateTotalDistance = distance + estimateDistance(tgtId);
    }

    private double estimateDistance(long tgtId) {
        return g.distance(id, tgtId);
    }


    public long getId() {
        return id;
    }

    public Node getParent() {
        return parent;
    }

    public Iterable<Node> children() {
        List<Node> children = new LinkedList<>();
        Iterable<Long> adjacent = g.adjacent(id);
        for (Long neibor : adjacent) {
            if (parent == null || neibor != parent.getId()) {
                children.add(new Node(neibor, this, distance + g.distance(id, neibor)));
            }
        }
        return children;
    }

    public Iterable<Node> strictChildren(HashSet<Long> marked) {
        List<Node> children = new LinkedList<>();
        Iterable<Long> adjacent = g.adjacent(id);
        for (Long neibor : adjacent) {
            if (!marked.contains(neibor)) {
                children.add(new Node(neibor, this, distance + g.distance(id, neibor)));
            }
        }
        return children;
    }

    @Override
    public int compareTo(Node node) {
        return Double.compare(estimateTotalDistance, node.estimateTotalDistance);
    }
}
