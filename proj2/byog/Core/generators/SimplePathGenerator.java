package byog.Core.generators;

import byog.Core.Utils.MapUtils;
import byog.Core.pojos.Position;
import byog.Core.pojos.Room;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.*;

public class SimplePathGenerator {
    private Random random;

    public SimplePathGenerator(Random random) {
        this.random = random;
    }


    public void makePath(TETile[][] world, Room[] rooms) {
        Queue<int[]> sortedRooms = findMST(rooms);
        while (!sortedRooms.isEmpty()) {
            int[] edge = sortedRooms.poll();
            Room fromRoom = rooms[edge[0]];
            Room toRoom = rooms[edge[1]];
            drawPath(world, fromRoom, toRoom);
        }
    }

    private void drawPath(TETile[][] world, Room fromRoom, Room toRoom) {
        Position fromPos = MapUtils.centerOfRoom(fromRoom);
        Position toPos = MapUtils.centerOfRoom(toRoom);
        if (drawPathHor(world, fromPos, toPos)) return;
        drawPathVer(world, fromPos, toPos);
    }

    /**
     * draw a path vertically
     * @param world
     * @param fromPos
     * @param toPos
     */
    private void drawPathVer(TETile[][] world, Position fromPos, Position toPos) {
        int meetWallCount = 0;
        if (fromPos.getY() < toPos.getY()) {
            for (int i = toPos.getY(); i >= fromPos.getY(); i--) {
                meetWallCount = drawPathVer(world, toPos, meetWallCount, i);
                if (correctCornerPath(meetWallCount)) return;
            }
        } else {
            for (int i = toPos.getY(); i <= fromPos.getY(); i++) {
                meetWallCount = drawPathVer(world, toPos, meetWallCount, i);
                if (correctCornerPath(meetWallCount)) return;
            }
        }
    }

    /**
     * draw a path horizontally, will return true if found a "corner path"
     * @param world
     * @param fromPos
     * @param toPos
     * @return return true if found a "corner path"
     */
    private boolean drawPathHor(TETile[][] world, Position fromPos, Position toPos) {
        int meetWallCount = 0;
        if (fromPos.getX() < toPos.getX()) {
            for (int i = fromPos.getX(); i <= toPos.getX(); i++) {
                meetWallCount = drawPathHor(world, fromPos, meetWallCount, i);
                correctHitWallPath(i, toPos, world, fromPos);
                if (correctCornerPath(meetWallCount)) return true;
            }
        } else {
            for (int i = fromPos.getX(); i >= toPos.getX(); i--) {
                meetWallCount = drawPathHor(world, fromPos, meetWallCount, i);
                correctHitWallPath(i, toPos, world, fromPos);
                if (correctCornerPath(meetWallCount)) return true;
            }
        }
        return false;
    }

    private boolean correctCornerPath(int meetWallCount) {
        if (meetWallCount == -1) {
            return true;
        }
        return false;
    }

    private void correctHitWallPath(int i, Position toPos, TETile[][] world, Position fromPos) {
        if (i == toPos.getX() && world[i][fromPos.getY()] == Tileset.UNLOCKED_DOOR) {
            world[i][fromPos.getY()] = Tileset.WALL;
        }
    }

    private int drawPathHor(TETile[][] world, Position position, int xWallCount, int i) {
        int y = position.getY();
        if (world[i][y] == Tileset.WALL && (world[i + 1][y] == Tileset.WALL || world[i - 1][y] == Tileset.WALL)) {
            int offsetY = world[i][y - 1] == Tileset.WALL ? 1 : -1;
            int offsetX = world[i - 1][y] == Tileset.WALL ? -1 : 1;
            world[i][y] = Tileset.FLOOR;
            world[i][y + offsetY] = Tileset.WALL2;
            world[i + offsetX][y] = Tileset.UNLOCKED_DOOR;
            world[i + offsetX][y + offsetY] = Tileset.WALL2;
            return -1;
        }
        if (world[i][y] == Tileset.WALL) {
            world[i][y] = Tileset.UNLOCKED_DOOR;
            xWallCount++;
            return xWallCount;
        }
        if (world[i][y] == Tileset.WALL2) {
            world[i][y] = Tileset.FLOOR;
            if (world[i][y - 1] == Tileset.NOTHING) {
                world[i][y - 1] = Tileset.WALL2;
            }
            if (world[i][y + 1] == Tileset.NOTHING) {
                world[i][y + 1] = Tileset.WALL2;
            }
            xWallCount++;
            return xWallCount;
        }
        if (xWallCount % 2 == 1) {
            world[i][y] = Tileset.FLOOR;
            if (world[i][y - 1] == Tileset.NOTHING) {
                world[i][y - 1] = Tileset.WALL2;
            }
            if (world[i][y + 1] == Tileset.NOTHING) {
                world[i][y + 1] = Tileset.WALL2;
            }
        }
        return xWallCount;
    }

    private int drawPathVer(TETile[][] world, Position position, int yWallCount, int i) {
        int x = position.getX();
        if (world[x][i] == Tileset.WALL && (world[x][i + 1] == Tileset.WALL || world[x][i - 1] == Tileset.WALL)) {
            int offsetX = world[x - 1][i] == Tileset.WALL ? 1 : -1;
            int offsetY = world[x][i - 1] == Tileset.WALL ? -1 : 1;
            world[x][i] = Tileset.FLOOR;
            world[x + offsetX][i] = Tileset.WALL2;
            world[x][i + offsetY] = Tileset.UNLOCKED_DOOR;
            world[x + offsetX][i + offsetY] = Tileset.WALL2;
            return -1;
        }
        if (world[x][i] == Tileset.WALL) {
            world[x][i] = Tileset.UNLOCKED_DOOR;
            yWallCount++;
            return yWallCount;
        }
        if (yWallCount % 2 == 1) {
            world[x][i] = Tileset.FLOOR;
            if (world[x - 1][i] == Tileset.NOTHING) {
                world[x - 1][i] = Tileset.WALL2;
            }
            if (world[x + 1][i] == Tileset.NOTHING) {
                world[x + 1][i] = Tileset.WALL2;
            }
        }
        return yWallCount;
    }

    /**
     * using prim to find the MST of Rooms
     * return the Queue which room should be connected
     * @param rooms
     * @return
     */
    private Queue<int[]> findMST(Room[] rooms) {
        int n = rooms.length;
        Queue<int[]> res = new ArrayDeque<>();
        int[][] graph = calcAdjMatrix(rooms);

        Boolean[] candidateSet = initCandidateSet(n);
        int[] costs = initCosts(n);
        int[] fromVertex = initFromVertex(n);

        while (existUnusedVertex(candidateSet)) {
            int minCost = Integer.MAX_VALUE;
            int selectVertex = -1;
            for (int i = 0; i < n; i++) {
                if (candidateSet[i] && costs[i] < minCost) {
                    minCost = costs[i];
                    selectVertex = i;
                }
            }
            candidateSet[selectVertex] = false;
            res.add(new int[]{fromVertex[selectVertex], selectVertex});
            renewPrim(selectVertex, costs, fromVertex, candidateSet, graph);
        }
        return res;
    }

    /**
     * Helper function to renew the costs candidateSet and fromVertexSet
     * @param selectVertex
     * @param costs
     * @param fromVertex
     * @param candidateSet
     * @param graph
     */
    private void renewPrim(int selectVertex, int[] costs, int[] fromVertex, Boolean[] candidateSet, int[][] graph) {
        int n = costs.length;
        for (int i = 0; i < n; i++) {
            if (candidateSet[i] && graph[selectVertex][i] < costs[i]) {
                costs[i] = graph[selectVertex][i];
                fromVertex[i] = selectVertex;
            }
        }
    }

    private int[] initFromVertex(int n) {
        int[] vertexes = new int[n];
        for (int i = 0; i < n; i++) {
            vertexes[i] = -1;
        }
        vertexes[0] = 0;
        return vertexes;
    }

    private boolean existUnusedVertex(Boolean[] candidateSet) {
        for (Boolean b : candidateSet) {
            if (b) {
                return true;
            }
        }
        return false;
    }

    private int[] initCosts(int n) {
        int[] costs = new int[n];
        for (int i = 0; i < n; i++) {
            costs[i] = Integer.MAX_VALUE;
        }
        costs[0] = 0;
        return costs;
    }

    private Boolean[] initCandidateSet(int n) {
        Boolean[] mstSet = new Boolean[n];
        for (int i = 0; i < n; i++) {
            mstSet[i] = true;
        }
        return mstSet;
    }


    private int[][] calcAdjMatrix(Room[] rooms) {
        int n = rooms.length;
        int[][] adjMatrix = new int[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = i; j < n; j++) {
                if (i == j) {
                    adjMatrix[i][j] = 0;
                }
                adjMatrix[i][j] = MapUtils.calcDistance(rooms[i].getPosition(), rooms[j].getPosition());
                adjMatrix[j][i] = adjMatrix[i][j];
            }
        }
        return adjMatrix;
    }
}
