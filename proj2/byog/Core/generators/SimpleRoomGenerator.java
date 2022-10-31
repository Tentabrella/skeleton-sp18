package byog.Core.generators;

import byog.Core.Utils.RandomUtils;
import byog.Core.pojos.Position;
import byog.Core.pojos.Room;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;

import java.util.Random;

public class SimpleRoomGenerator {
    private Random random;

    public SimpleRoomGenerator(Random random) {
        this.random = random;
    }

    /**
     * Generate room for given map
     * Basic idea is generating non-overlapping rooms. And delete rooms to required room number;
     * @param world The map to be processed
     * @param roomNum Required number of rooms to be generated
     * @param ratio The ratio which to generate more rooms than required
     * @return Generated rooms
     */
    public Room[] makeRooms(TETile[][] world, int roomNum, double ratio) {
        int makeRoomNum = (int) (roomNum*ratio);
        int curRoomNum = 0;
        Room[] existRooms = new Room[makeRoomNum];
        //make rooms to fill map
        while (curRoomNum < makeRoomNum) {
            Room room = randomRoom(world, curRoomNum);
            if (checkBoundary(world, room) && checkNoOverlap(room, existRooms, curRoomNum)) {
                existRooms[curRoomNum] = room;
                curRoomNum++;
            }
        }
        //delete room to roomNum
        Room[] selectRooms = selectRooms(existRooms, ratio, roomNum);
        for (Room room : selectRooms) {
            drawRoom(world, room);
        }
        return selectRooms;
    }

    private Room[] selectRooms(Room[] existRooms, double ratio, int roomNum) {
        Room[] selectRooms = new Room[roomNum];
        int count = 0;
        for (Room room : existRooms) {
            if (count == 0 || RandomUtils.uniform(random) < (1/ratio + 0.05)) {
                selectRooms[count] = room;
                count++;
            }
            if (count == roomNum) {
                break;
            }
        }
        return selectRooms;
    }

    private void drawRoom(TETile[][] world, Room room) {
        Position position = room.getPosition();
        int posX = position.getX();
        int posY = position.getY();
        for (int i = posX; i < posX + room.getWidth(); i++) {
            for (int j = posY; j < posY + room.getHeight(); j++) {
                world[i][j] = Tileset.FLOOR;
            }
        }
        for (int i = posX; i < posX + room.getWidth(); i++) {
            world[i][posY] = Tileset.WALL;
            world[i][posY + room.getHeight() - 1] = Tileset.WALL;
        }
        for (int i = posY; i < posY + room.getHeight(); i++) {
            world[posX][i] = Tileset.WALL;
            world[posX + room.getWidth() - 1][i] = Tileset.WALL;
        }
    }

    private boolean checkNoOverlap(Room room, Room[] existRooms, int roomNumber) {
        for (int i = 0; i < roomNumber; i++) {
            if (!checkNoOverlapHelper(room, existRooms[i])) {
                return false;
            }
        }
        return true;
    }

    private boolean checkNoOverlapHelper(Room room, Room existRoom) {
        Position pos1 = room.getPosition();
        Position pos2 = existRoom.getPosition();
        if (pos1.getX() > (pos2.getX() + existRoom.getWidth()) || pos2.getX() > (pos1.getX() + room.getWidth())) {
            return true;
        }
        if (pos1.getY() > (pos2.getY() + existRoom.getHeight()) || pos2.getY() > (pos1.getY() + room.getHeight())) {
            return true;
        }
        return false;
    }

    private boolean checkBoundary(TETile[][] world, Room room) {
        Position position = room.getPosition();
        int posX = position.getX();
        int posY = position.getY();
        if (posX < 0 || posX + room.getWidth() > world.length || posY < 0 || posY + room.getHeight() > world[0].length) {
            return false;
        }
        return true;
    }

    private Room randomRoom(TETile[][] world, int currentRoom) {
        int width = RandomUtils.uniform(random, 9) + 8;
        int height = RandomUtils.uniform(random, 6) + 8;
        if (RandomUtils.uniform(random) < 0.4) {
            width = width ^ height;
            height = width ^ height;
            width = width ^ height;
        }
        int posX = RandomUtils.uniform(random, world.length);
        int posY = RandomUtils.uniform(random, world[0].length);
        if (currentRoom == 0) {
            width*=2.5;
            height*=2.5;
        }
        Position pos = new Position(posX, posY);
        Room room = new Room(width, height, pos);
        return room;
    }


}
