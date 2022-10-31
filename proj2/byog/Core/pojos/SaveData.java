package byog.Core.pojos;

import byog.TileEngine.TETile;

import java.io.Serializable;
import java.util.Random;

public class SaveData implements Serializable {
    Random random;
    int level;
    TETile[][] world;
    Position character;

    public SaveData(Random random, int level, TETile[][] world, Position character) {
        this.random = random;
        this.level = level;
        this.world = world;
        this.character = character;
    }

    public Random getRandom() {
        return random;
    }

    public int getLevel() {
        return level;
    }

    public TETile[][] getWorld() {
        return world;
    }

    public Position getCharacter() {
        return character;
    }
}
