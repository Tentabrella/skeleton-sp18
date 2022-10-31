package byog.Core;

import byog.Core.Utils.MapUtils;
import byog.Core.Utils.RandomUtils;
import byog.Core.generators.*;
import byog.Core.pojos.Position;
import byog.Core.pojos.Room;
import byog.Core.pojos.SaveData;
import byog.TileEngine.TERenderer;
import byog.TileEngine.TETile;
import byog.TileEngine.Tileset;
import edu.princeton.cs.introcs.StdDraw;

import java.awt.*;
import java.io.*;
import java.util.Random;

public class Game implements Serializable {
    private final TERenderer ter = new TERenderer();
    private final CanvasGenerator canvasGenerator = new CanvasGenerator();
    public static final int WIDTH = 80;
    public static final int HEIGHT = 80;
    public static final int TILESIZE = 16;
    private boolean isGameClear = false;
    private boolean isFloorClear = false;
    private TETile lastTile = Tileset.FLOOR;
    private TETile[][] world;
    private int level = 1;
    private boolean hitWall;
    private Random random;
    private Position character;
    private boolean quitMode;


    /**
     * Method used for playing a fresh game. The game should start from the main menu.
     */
    public void playWithKeyboard() {
        ter.initialize(WIDTH, HEIGHT + 3);
        Boolean freshStart = welcomeScreen(WIDTH, HEIGHT);
        if (!freshStart) {
            ter.renderFrame(world);
            clearFloor();
            isFloorClear = false;
        }
        while (!isGameClear) {
            world = renderDungeon(canvasGenerator, random);
            character = initCharacter(random);
            initDownStair(random);
            ter.renderFrame(world);
            clearFloor();
            isFloorClear = false;
        }
    }

    /**
     * Method used for autograding and testing the game code. The input string will be a series
     * of characters (for example, "n123sswwdasdassadwas", "n123sss:q", "lwww". The game should
     * behave exactly as if the user typed these characters into the game after playing
     * playWithKeyboard. If the string ends in ":q", the same world should be returned as if the
     * string did not end with q. For example "n123sss" and "n123sss:q" should return the same
     * world. However, the behavior is slightly different. After playing with "n123sss:q", the game
     * should save, and thus if we then called playWithInputString with the string "l", we'd expect
     * to get the exact same world back again, since this corresponds to loading the saved game.
     *
     * @param input the input string to feed to your program
     * @return the 2D TETile[][] representing the state of the world
     */
    public TETile[][] playWithInputString(String input) {
        // TODO: Fill out this method to run the game using the input passed in,
        // and return a 2D tile representation of the world that would have been
        // drawn if the same inputs had been given to playWithKeyboard().

        input = input.toLowerCase();
        int separator = input.indexOf("s");
        int seed = input.substring(1, separator).hashCode();
        System.out.println(seed);
        String command = input.substring(separator + 1);
        System.out.println(command);
        random = new Random(seed);
        do {
            world = renderDungeon(canvasGenerator, random);
            character = initCharacter(random);
            initDownStair(random);
            command = clearFloorWithoutUI(command);
            isFloorClear = false;
        } while (!isGameClear && (command.length() != 0));
        return world;
    }

    private void clearFloor() {
        while (!isFloorClear) {
            String s = solicitNCharsInput(1);
            if (moveCharacter(s)) {
                System.exit(0);
            }
            ter.renderFrame(world);
            showHelperBar();
            isFloorClear = checkDownStair();
        }
    }

    private String clearFloorWithoutUI(String command) {
        int index = 0;
        while (!isFloorClear && (index < command.length())) {
            String s = command.substring(index, index + 1);
            if (moveCharacter(s)) {
                return "";
            }
            isFloorClear = checkDownStair();
            index++;
        }
        return command.substring(index);
    }

    private void showHelperBar() {
        Font font = new Font("Monaco", Font.BOLD, 20);
        StdDraw.setPenColor(Color.white);
        StdDraw.setFont(font);
        StdDraw.textLeft(2, HEIGHT + 1, "Level " + level);
        if (hitWall) {
            StdDraw.text(WIDTH / 2, HEIGHT + 1, "Wall in front!");
        } else {
            StdDraw.text(WIDTH / 2, HEIGHT + 1, lastTile.description());
        }

        StdDraw.show();
    }

    private boolean checkDownStair() {
        if (lastTile.equals(Tileset.TREASURE)) {
            lastTile = Tileset.FLOOR;
            level++;
            return true;
        }
        return false;
    }

    /**
     * move character, return true if what to quit
     * @param s user input command
     * @return return true if encounter the quit command
     */
    private boolean moveCharacter(String s) {
        Position lastPosition = character;
        switch (s) {
            case "w":
                character = new Position(character.getX(), character.getY() + 1);
                quitMode = false;
                break;
            case "a":
                character = new Position(character.getX() - 1, character.getY());
                quitMode = false;
                break;
            case "s":
                character = new Position(character.getX(), character.getY() - 1);
                quitMode = false;
                break;
            case "d":
                character = new Position(character.getX() + 1, character.getY());
                quitMode = false;
                break;
            case "q":
                if (quitMode) {
                    try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("./save"))) {
                        SaveData saveData = new SaveData(this.random, this.level, this.world, this.character);
                        outputStream.writeObject(saveData);
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                }
                quitMode = false;
                return true;
            case ":":
                quitMode = true;
                break;
        }
        if (checkWall()) {
            character = lastPosition;
            hitWall = true;
        } else {
            hitWall = false;
            world[lastPosition.getX()][lastPosition.getY()] = lastTile;
            lastTile = world[character.getX()][character.getY()];
            world[character.getX()][character.getY()] = Tileset.PLAYER;
        }
        return false;
    }

    private boolean checkWall() {
        TETile tile = world[character.getX()][character.getY()];
        return tile.equals(Tileset.WALL)  || tile.equals(Tileset.WALL2);
    }

    private TETile[][] renderDungeon(CanvasGenerator canvasGenerator, Random random) {
        world = canvasGenerator.generateMap(Game.WIDTH, Game.HEIGHT);
        SimpleRoomGenerator roomGenerator = new SimpleRoomGenerator(random);
        Room[] rooms = roomGenerator.makeRooms(world, 13, 1);
        SimplePathGenerator simplePathGenerator = new SimplePathGenerator(random);
        simplePathGenerator.makePath(world, rooms);
        return world;
    }

    private void initDownStair(Random random) {
        int x;
        int y;
        do {
            x = RandomUtils.uniform(random, world.length);
            y = RandomUtils.uniform(random, world[0].length);
        } while (world[x][y] != Tileset.FLOOR);
        world[x][y] = Tileset.TREASURE;
    }

    private Position initCharacter(Random random) {
        int x;
        int y;
        do {
            x = RandomUtils.uniform(random, world.length);
            y = RandomUtils.uniform(random, world[0].length);
        } while (world[x][y] != Tileset.FLOOR);
        world[x][y] = Tileset.PLAYER;
        return new Position(x, y);
    }

    //TODO: show input below
    private String solicitNCharsInput(int n) {
        String input = "";
        while (input.length() < n) {
            if (!StdDraw.hasNextKeyTyped()) {
                continue;
            }
            char key = StdDraw.nextKeyTyped();
            input += String.valueOf(key);
        }
        return input;
    }

    public void reloadGame(SaveData saveData) {
        this.world =  saveData.getWorld();
        this.level =  saveData.getLevel();
        this.random = saveData.getRandom();
        this.character = saveData.getCharacter();
    }

    public Boolean welcomeScreen(int width, int height) {
        StdDraw.setPenColor(Color.white);
        Position centerOfCanvas = MapUtils.centerOfCanvas(width, height);
        Font font = new Font("Monaco", Font.BOLD, TILESIZE * 3 - 2);
        StdDraw.setFont(font);
        StdDraw.text(centerOfCanvas.getX(), centerOfCanvas.getY() * 1.5, "CS61B: KUSO GAME MO");
        font = new Font("Monaco", Font.BOLD, TILESIZE * 2 - 2);
        StdDraw.setFont(font);
        StdDraw.text(centerOfCanvas.getX(), centerOfCanvas.getY() + 4, "New Game (N)");
        StdDraw.text(centerOfCanvas.getX(), centerOfCanvas.getY() + 2, "Load Game (L)");
        StdDraw.text(centerOfCanvas.getX(), centerOfCanvas.getY(), "Quit (Q)");
        StdDraw.show();
        while (true) {
            String userInput = solicitNCharsInput(1);
            switch (userInput.toLowerCase()) {
                case "n":
                    StdDraw.text(WIDTH / 2, HEIGHT / 2 - 4, "Input your seed, press s to start");
                    StdDraw.show();
                    String seed = "";
                    while (!userInput.equalsIgnoreCase("s")) {
                        userInput = solicitNCharsInput(1);
                        seed += userInput;
                        StdDraw.setPenColor(Color.black);
                        StdDraw.filledRectangle(WIDTH / 2, HEIGHT / 2 - 8, 40, 2);
                        StdDraw.setPenColor(Color.white);
                        StdDraw.text(WIDTH / 2, HEIGHT / 2 - 8, seed);
                        StdDraw.show();
                    }
                    random = new Random(seed.substring(0, seed.length() - 1).hashCode());
                    return true;
                case "l":
                    try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("./save"))) {
                        SaveData saveData = (SaveData) inputStream.readObject();
                        reloadGame(saveData);
                        return false;
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    break;
                case "q":
                    System.exit(0);
                    break;
                default:
                    StdDraw.setPenColor(Color.black);
                    StdDraw.filledRectangle(WIDTH / 2, HEIGHT / 2 - 8, 40, 2);
                    StdDraw.setPenColor(Color.white);
                    StdDraw.text(WIDTH / 2, HEIGHT / 2 - 8, "invalid Input, retry again");
                    StdDraw.show();
            }
        }
    }

}
