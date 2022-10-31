package byog.Core.generators;

import byog.Core.Utils.MapUtils;
import byog.Core.pojos.Position;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.util.Random;

public class CanvasGenerator {
    private static final int TILESIZE = 16;

    public TETile[][] generateMap(int width, int height) {
        TETile[][] world = new TETile[width][height];
        for (int x = 0; x < width; x += 1) {
            for (int y = 0; y < height; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        return world;
    }
}
