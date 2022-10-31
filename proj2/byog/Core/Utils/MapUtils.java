package byog.Core.Utils;

import byog.Core.pojos.Position;
import byog.Core.pojos.Room;

public class MapUtils {
    public static Position centerOfCanvas(int width, int height) {
         return new Position((int) (width)/2, (int) (height)/2);
    }

    public static int calcDistance(Position fromPos, Position toPos) {
        double v = Math.pow(fromPos.getX() - toPos.getX(), 2) + Math.pow(fromPos.getY() - toPos.getY(), 2);
        return (int) Math.sqrt(v);
    }

    public static Position centerOfRoom(Room room) {
        Position lb = room.getPosition();
        int x = (int) (lb.getX() + room.getWidth()/2);
        int y = (int) (lb.getY() + room.getHeight()/2);
        return new Position(x, y);
    }
}
