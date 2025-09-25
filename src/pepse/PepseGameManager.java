package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Vector2;
import pepse.world.*;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.trees.Tree;

import java.util.List;
import java.util.Random;
import java.util.function.Supplier;

/**
 * The main class of the game.
 * It is responsible for initializing the game and creating the game objects.
 * It extends the GameManager class from the danogl library.
 * The game is a side-scrolling platformer where the player controls an avatar that can move and jump.
 * The game features a procedurally generated terrain, trees, and a day-night cycle.
 * The game also features an energy bar that depletes over time and can be replenished by collecting fruits from trees.
 * note that this is more of a simulation than a game, as there are no win or lose conditions.
 * @see GameManager
 * @see Terrain
 * @see Tree
 * @see Avatar
 * @author Aron Isaacs
 */
public class PepseGameManager extends GameManager {
    /*
    The seed for the pseudo-random number generator (used throughout the game).
    generated randomly at the start of each game, and then kept constant to ensure object permanence.
     */
    private static final int SEED = new Random().nextInt();
    // all times are in seconds
    static final float CYCLE_LENGTH_OF_DAY = 15f;
    static final float CHANCE_FOR_TREE = 0.3f;
    static final Vector2 ENERGY_BAR_POSITION = new Vector2(20f, 20f);
    public final static int GAME_BLOCK_SIZE = 30;// Top-left corner, adjust as needed

    private Terrain terrain;
    private ImageReader imageReader;
    private UserInputListener inputListener;
    private WindowController windowController;

    /**
     * The entry point of the game.
     * runs the game by creating an instance of PepseGameManager and calling its run method.
     * @param args command line arguments (not used).
     */
    public static void main(String[] args) {

        new PepseGameManager().run();
    }

    /**
     * Initializes the game by creating the terrain, background objects, avatar, and energy bar.
     * This method is called by the danogl library when the game starts.
     * @param imageReader   used to read images from files.
     * @param soundReader   used to read sounds from files.
     * @param inputListener used to listen for user input (keyboard and mouse).
     * @param windowController used to control the game window (e.g., get its dimensions).
     */
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.imageReader = imageReader;
        this.inputListener = inputListener;
        this.windowController = windowController;
        makeTerrain();
        makeBackgroundObjects();
        makeTrees();
        var avatar = makeAvatar();
        makeEnergyBar(avatar::getEnergy);

    }

    private void makeTrees() {
        placeTreesInRange(0, windowController.getWindowDimensions().x());
    }

    /*
     * Creates and adds an energy bar to the game.
     * The energy bar displays the avatar's current energy level.
     * @param avatar the avatar whose energy level is displayed by the energy bar.
     */
    private void makeEnergyBar(Supplier<Float> energySupplier) {
        EnergyBar energyBar = new EnergyBar(ENERGY_BAR_POSITION, Avatar.MAX_ENERGY, energySupplier);
        gameObjects().addGameObject(energyBar, Layer.UI);
    }

    /*
     * Creates and adds the avatar to the game.
     * The avatar is positioned at the center of the window.
     * @return the created avatar.
     */
    private Avatar makeAvatar() {
        //todo decide on initial position of avatar
        float startingX = windowController.getWindowDimensions().x() / 2f;
        float startingY = windowController.getWindowDimensions().y() / 2f;
        Vector2 initialPosition = new Vector2(startingX, startingY);
        var avatar = new Avatar(initialPosition, inputListener, imageReader);
        gameObjects().addGameObject(avatar, Layer.DEFAULT);
        return avatar;
    }

    /*
     * Creates and adds the terrain to the game.
     * The terrain is generated using a pseudo-random algorithm based on the SEED.
     * The terrain consists of blocks that form the ground and other static objects.
     */
    private void makeTerrain() {
        this.terrain = new Terrain(windowController.getWindowDimensions(), SEED);
        List<Block> blocks = terrain.createInRange(0, (int) windowController.getWindowDimensions().x());
        for (Block block : blocks) {
            //todo for optimization, maybe switch some blocks to the background
            gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
        }
    }

    /*
     * Creates and adds background objects to the game.
     * The background objects include the sky, sun, sun halo, and night overlay.
     * These objects create a dynamic day-night cycle in the game.
     */
    private void makeBackgroundObjects() {
        GameObject sky = Sky.create(windowController.getWindowDimensions());
        gameObjects().addGameObject(sky, Layer.BACKGROUND);
        GameObject sun = Sun.create(windowController.getWindowDimensions(), CYCLE_LENGTH_OF_DAY);
        GameObject sunHalo = SunHalo.create(sun);
        gameObjects().addGameObject(sunHalo, Layer.BACKGROUND);
        gameObjects().addGameObject(sun, Layer.BACKGROUND);
        GameObject night = Night.create(windowController.getWindowDimensions(), CYCLE_LENGTH_OF_DAY);
        gameObjects().addGameObject(night, Layer.FOREGROUND);
    }

    /*
     * Places trees in the specified horizontal range based on a pseudo-random algorithm.
     * The probability of placing a tree at each block is determined by CHANCE_FOR_TREE.
     * @param minX the minimum x-coordinate of the range (inclusive).
     * @param maxX the maximum x-coordinate of the range (inclusive).
     */
    private void placeTreesInRange(float minX, float maxX) {
        int start = Math.round(minX / PepseGameManager.GAME_BLOCK_SIZE) * PepseGameManager.GAME_BLOCK_SIZE;
        int end = Math.round(maxX / PepseGameManager.GAME_BLOCK_SIZE) * PepseGameManager.GAME_BLOCK_SIZE;

        for (int x = start; x <= end; x += PepseGameManager.GAME_BLOCK_SIZE) {
            float y = pseudoRandomFloatAt(x);
            if (y < CHANCE_FOR_TREE) {
                placeTreeAt((float) x);
            }
        }
    }

    /*
     * Generates a pseudo-random float in the range [0, 1) based on the input x-coordinate and the SEED.
     * This function uses bitwise operations and multiplications to create a hash-like effect.
     * @param x the input x-coordinate used to generate the pseudo-random float.
     * @return a pseudo-random float in the range [0, 1).
     */
    public  float pseudoRandomFloatAt(int x) {
        //todo should this be moved to a utility class? or a lambda function?
        int z = x ^ SEED;
        z = (z ^ (z >>> 16)) * 0x45d9f3b;
        z = (z ^ (z >>> 16)) * 0x45d9f3b;
        z = z ^ (z >>> 16);
        return (z & 0x7FFFFFFF) / (float) Integer.MAX_VALUE;
    }

    /*
     * Places a tree at the specified x-coordinate.
     * The tree is aligned to the block grid and positioned at ground level.
     * @param locationX the x-coordinate where the tree should be placed.
     */
    private void placeTreeAt(float locationX) {
        // Align x to block size
        float x = Math.round(locationX / GAME_BLOCK_SIZE) * GAME_BLOCK_SIZE;
        // Example: place tree at ground level (adjust as needed)
        float y = terrain.groundHeightAt(x);
        Vector2 position = new Vector2(x, y);

        new Tree(position,
                (gameObject, layer) -> gameObjects().addGameObject(gameObject, layer),
                ( gameObject, layer) -> gameObjects().removeGameObject(gameObject)
        );
    }

}
