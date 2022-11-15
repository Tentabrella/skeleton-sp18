import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    /**
     * Return a List of longs representing the shortest path from the node
     * closest to a start location and the node closest to the destination
     * location.
     * @param g The graph to use.
     * @param stlon The longitude of the start location.
     * @param stlat The latitude of the start location.
     * @param destlon The longitude of the destination location.
     * @param destlat The latitude of the destination location.
     * @return A list of node id's in the order visited on the shortest path.
     */
    public static List<Long> shortestPath(GraphDB g, double stlon, double stlat,
                                          double destlon, double destlat) {
        long svrtx = g.closest(stlon, stlat);
        long evrtx = g.closest(destlon, destlat);
        List<Long> path = Astar(g, svrtx, evrtx);
        return path;
    }

    private static List<Long> Astar(GraphDB g, long svrtx, long evrtx) {
        PriorityQueue<Node> minPQ = new PriorityQueue<>();
        Node currNode = new Node(svrtx, null, 0.0, evrtx, g);
        HashSet<Long> marked = new HashSet<>();

        while (currNode.getId() != evrtx) {
            marked.add(currNode.getId());
            for (Node child : currNode.strictChildren(marked)) {
                minPQ.offer(child);
            }
            currNode = minPQ.remove();
        }

        List<Long> route = readPath(currNode);
        return route;
    }

    private static List<Long> readPath(Node currNode) {
        Stack<Long> stack = new Stack<>();
        while (currNode != null) {
            stack.push(currNode.getId());
            currNode = currNode.getParent();
        }
        List<Long> route = new LinkedList<>();
        while (!stack.isEmpty()) {
            route.add(stack.pop());
        }
        return route;
    }

    /**
     * Create the list of directions corresponding to a route on the graph.
     * @param g The graph to use.
     * @param route The route to translate into directions. Each element
     *              corresponds to a node from the graph in the route.
     * @return A list of NavigatiionDirection objects corresponding to the input
     * route.
     */
    public static List<NavigationDirection> routeDirections(GraphDB g, List<Long> route) {
        List<Move> moves = initMoves(g, route);
        List<NavigationDirection> result = new LinkedList<>();
        int movePointer = 1;
        NavigationDirection startDirection = getStartDirection(moves);
        result.add(startDirection);
        NavigationDirection lastDirection = startDirection;
        Move lastMove = moves.get(0);
        while (movePointer < moves.size()) {
            Move currMove = moves.get(movePointer);
            NavigationDirection currentDirection = calcDirection(lastMove, currMove);
            if (ifCompressPath(lastDirection, currentDirection)) {
                lastDirection.distance += currentDirection.distance;
            } else {
                result.add(currentDirection);
                lastDirection = currentDirection;
            }
            lastMove = currMove;
            movePointer++;
        }
        return result;
    }

    private static void printInConsole(List<NavigationDirection> result) {
        StringBuilder sb = new StringBuilder();
        int step = 1;
        for (NavigationDirection d: result) {
            sb.append(String.format("%d. %s \n", step, d));
            step += 1;
        }
        System.out.println(sb.toString());
    }

    private static boolean ifCompressPath(NavigationDirection lastDirection, NavigationDirection currentDirection) {
        return lastDirection.way.equals(currentDirection.way);
    }

    private static NavigationDirection getStartDirection(List<Move> moves) {
        NavigationDirection startDirection = new NavigationDirection();
        startDirection.direction = NavigationDirection.START;
        startDirection.distance = moves.get(0).calcDistance();
        startDirection.way = parseWayName(moves.get(0));
        return startDirection;
    }

    private static List<Move> initMoves(GraphDB g, List<Long> route) {
        int slow = 0;
        int fast = 1;
        List<Move> moves = new LinkedList<>();
        while (fast < route.size()) {
            Move move = new Move(route.get(slow), route.get(fast), g);
            moves.add(move);
            slow = fast;
            fast++;
        }
        return moves;
    }

    private static NavigationDirection calcDirection(Move lastMove, Move currMove) {
        double lastAngle = lastMove.calcAngle();
        double currAngle = currMove.calcAngle();
        double angleDiff = currAngle - lastAngle;
        NavigationDirection direction = new NavigationDirection();
        angleDiff = formatAngle(angleDiff);
        if (angleDiff >= -15 && angleDiff < 15) {
            direction.direction = NavigationDirection.STRAIGHT;
        } else if (angleDiff >= -30 && angleDiff < -15) {
            direction.direction = NavigationDirection.SLIGHT_LEFT;
        } else if (angleDiff >= 15 && angleDiff < 30) {
            direction.direction = NavigationDirection.SLIGHT_RIGHT;
        } else if (angleDiff >= -100 && angleDiff < -30) {
            direction.direction = NavigationDirection.LEFT;
        } else if (angleDiff >= 30 && angleDiff < 100) {
            direction.direction = NavigationDirection.RIGHT;
        } else if (angleDiff < -100) {
            direction.direction = NavigationDirection.SHARP_LEFT;
        } else if (angleDiff > 100) {
            direction.direction = NavigationDirection.SHARP_RIGHT;
        }
        direction.distance = currMove.calcDistance();
        direction.way = parseWayName(currMove);
        return direction;
    }

    private static double formatAngle(double angleDiff) {
        if (Math.abs(angleDiff) < 180) {
            return angleDiff;
        } else if (angleDiff < 0) {
            return 360 + angleDiff;
        } else if (angleDiff > 0) {
            return angleDiff - 360;
        }
        return angleDiff;
    }

    private static String parseWayName(Move move) {
        String wayName = move.getWayName();
        return wayName == null ? "" : wayName;
    }


    /**
     * Class to represent a navigation direction, which consists of 3 attributes:
     * a direction to go, a way, and the distance to travel for.
     */
    public static class NavigationDirection {

        /** Integer constants representing directions. */
        public static final int START = 0;
        public static final int STRAIGHT = 1;
        public static final int SLIGHT_LEFT = 2;
        public static final int SLIGHT_RIGHT = 3;
        public static final int RIGHT = 4;
        public static final int LEFT = 5;
        public static final int SHARP_LEFT = 6;
        public static final int SHARP_RIGHT = 7;

        /** Number of directions supported. */
        public static final int NUM_DIRECTIONS = 8;

        /** A mapping of integer values to directions.*/
        public static final String[] DIRECTIONS = new String[NUM_DIRECTIONS];

        /** Default name for an unknown way. */
        public static final String UNKNOWN_ROAD = "unknown road";
        
        /** Static initializer. */
        static {
            DIRECTIONS[START] = "Start";
            DIRECTIONS[STRAIGHT] = "Go straight";
            DIRECTIONS[SLIGHT_LEFT] = "Slight left";
            DIRECTIONS[SLIGHT_RIGHT] = "Slight right";
            DIRECTIONS[LEFT] = "Turn left";
            DIRECTIONS[RIGHT] = "Turn right";
            DIRECTIONS[SHARP_LEFT] = "Sharp left";
            DIRECTIONS[SHARP_RIGHT] = "Sharp right";
        }

        /** The direction a given NavigationDirection represents.*/
        int direction;
        /** The name of the way I represent. */
        String way;
        /** The distance along this way I represent. */
        double distance;

        /**
         * Create a default, anonymous NavigationDirection.
         */
        public NavigationDirection() {
            this.direction = STRAIGHT;
            this.way = UNKNOWN_ROAD;
            this.distance = 0.0;
        }

        public String toString() {
            return String.format("%s on %s and continue for %.3f miles.",
                    DIRECTIONS[direction], way, distance);
        }

        /**
         * Takes the string representation of a navigation direction and converts it into
         * a Navigation Direction object.
         * @param dirAsString The string representation of the NavigationDirection.
         * @return A NavigationDirection object representing the input string.
         */
        public static NavigationDirection fromString(String dirAsString) {
            String regex = "([a-zA-Z\\s]+) on ([\\w\\s]*) and continue for ([0-9\\.]+) miles\\.";
            Pattern p = Pattern.compile(regex);
            Matcher m = p.matcher(dirAsString);
            NavigationDirection nd = new NavigationDirection();
            if (m.matches()) {
                String direction = m.group(1);
                if (direction.equals("Start")) {
                    nd.direction = NavigationDirection.START;
                } else if (direction.equals("Go straight")) {
                    nd.direction = NavigationDirection.STRAIGHT;
                } else if (direction.equals("Slight left")) {
                    nd.direction = NavigationDirection.SLIGHT_LEFT;
                } else if (direction.equals("Slight right")) {
                    nd.direction = NavigationDirection.SLIGHT_RIGHT;
                } else if (direction.equals("Turn right")) {
                    nd.direction = NavigationDirection.RIGHT;
                } else if (direction.equals("Turn left")) {
                    nd.direction = NavigationDirection.LEFT;
                } else if (direction.equals("Sharp left")) {
                    nd.direction = NavigationDirection.SHARP_LEFT;
                } else if (direction.equals("Sharp right")) {
                    nd.direction = NavigationDirection.SHARP_RIGHT;
                } else {
                    return null;
                }

                nd.way = m.group(2);
                try {
                    nd.distance = Double.parseDouble(m.group(3));
                } catch (NumberFormatException e) {
                    return null;
                }
                return nd;
            } else {
                // not a valid nd
                return null;
            }
        }

        @Override
        public boolean equals(Object o) {
            if (o instanceof NavigationDirection) {
                return direction == ((NavigationDirection) o).direction
                    && way.equals(((NavigationDirection) o).way)
                    && distance == ((NavigationDirection) o).distance;
            }
            return false;
        }

        @Override
        public int hashCode() {
            return Objects.hash(direction, way, distance);
        }
    }

    public static class Move {
        final long start;
        final long end;
        final GraphDB g;

        public Move(long start, long end, GraphDB g) {
            this.start = start;
            this.end = end;
            this.g = g;
        }

        public String getWayName() {
            Set<Long> startWays = g.getVertices().get(start).getWayIds();
            Set<Long> endWays = g.getVertices().get(end).getWayIds();
            Set<Long> startWaysCopy = new HashSet<>(startWays);
            startWaysCopy.retainAll(endWays);
            long commonWayId = (Long) startWaysCopy.toArray()[0];
            return g.getWays().get(commonWayId).getName();
        }

        public double calcAngle() {
            return g.bearing(start, end);
        }

        public double calcDistance() {
            return g.distance(start, end);
        }
    }
}
