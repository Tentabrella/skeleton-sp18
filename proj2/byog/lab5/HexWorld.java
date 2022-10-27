package byog.lab5;

import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import byog.lab5.pojos.Hexgon;
import byog.lab5.pojos.Line;
import byog.lab5.pojos.Position;

import java.util.Random;

/**
 * Draws a world consisting of hexagonal regions.
 */
public class HexWorld {
    private static final int WIDTH = 50;
    private static final int HEIGHT = 50;

    private static final long SEED = 2873123;
    private static final Random RANDOM = new Random(SEED);

    public static void main(String[] args) {
        HexWorld hexWorld = new HexWorld();
        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);
        TETile[][] world = hexWorld.initWorld();
        Hexgon hexgon1 = new Hexgon(new Position(41, 42), 4, Tileset.FLOWER);
        hexWorld.addHexagon(world, hexgon1);
        ter.renderFrame(world);
    }


    public void addHexagon(TETile[][] world, Hexgon hexgon) {
        Position position = hexgon.getPosition();
        int posX = position.getPosX();
        int posY = position.getPosY();
        int len = hexgon.getSize();
        TETile teTile = hexgon.getTeTile();
        if (posX < 0 || posX + 3*len - 2 > WIDTH || posY < 0 || posY + 2*len > HEIGHT) {
            System.out.println("Invalid position for " + hexgon);
            return;
        }
        for (int layer = 0; layer < (2*len); layer++) {
            int offset = layer < len ? len - 1 - layer : layer - len;
            int altLen = layer < len ? len + 2*layer : 5*len - 2*layer - 2;
            for (int x = posX + offset; x < posX + offset + altLen; x++) {
                world[x][posY + layer] = teTile;
            }
        }
    }

    private TETile[][] initWorld() {
        TETile[][] world = new TETile[WIDTH][HEIGHT];
        for (int x = 0; x < WIDTH; x += 1) {
            for (int y = 0; y < HEIGHT; y += 1) {
                world[x][y] = Tileset.NOTHING;
            }
        }
        return world;
    }



}
