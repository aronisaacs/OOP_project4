package pepse;

import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.util.Vector2;
import pepse.world.*;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.infiniteworld.GroundHeightAt;
import pepse.world.infiniteworld.Scrollable;
import pepse.world.trees.Flora;
import pepse.world.trees.Tree;

import java.util.ArrayList;
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
    // all times are in seconds
    public final static int GAME_BLOCK_SIZE = 30;// Top-left corner, adjust as needed

    private static final int SEED = new Random().nextInt();
    private ImageReader imageReader;
    private UserInputListener inputListener;
    private WindowController windowController;
    private Avatar avatar;
    private final List<Scrollable<?>> scrollables = new ArrayList<>();
    GroundHeightAt groundHeightAt;

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
        makeGameObjects(windowController);
    }

    /*
     * Creates and adds the main game objects to the game.
     * This includes the terrain, background objects, avatar, and energy bar.
     * @param windowController used to get the dimensions of the game window.
     */
    private void makeGameObjects(WindowController windowController) {
        makeBackgroundObjects();
        Terrain terrain = new Terrain(windowController.getWindowDimensions(), SEED);
        groundHeightAt = terrain::groundHeightAt;
        scrollables.add(terrain);
        scrollables.add(new Flora(SEED, groundHeightAt));
        updateScrollables(0);
        this.avatar = makeAvatar();
        makeEnergyBar(avatar::getEnergy);
        setCamera(new Camera(avatar, Vector2.ZERO, windowController.getWindowDimensions(),
                windowController.getWindowDimensions()));
    }

    /*
     * Creates and adds background objects to the game.
     * The background objects include the sky, sun, sun halo, and night overlay.
     * These objects create a dynamic day-night cycle in the game.
     */
    private void makeBackgroundObjects() {
        GameObject sky = Sky.create(windowController.getWindowDimensions());
        gameObjects().addGameObject(sky, Layer.BACKGROUND);
        GameObject sun = Sun.create(windowController.getWindowDimensions(), Sun.CYCLE_LENGTH_OF_DAY);
        GameObject sunHalo = SunHalo.create(sun);
        gameObjects().addGameObject(sunHalo, Layer.BACKGROUND);
        gameObjects().addGameObject(sun, Layer.BACKGROUND);
        GameObject night = Night.create(windowController.getWindowDimensions(), Sun.CYCLE_LENGTH_OF_DAY);
        gameObjects().addGameObject(night, Layer.FOREGROUND);
    }



    /*
     * Updates all scrollable objects based on the avatar's current x-coordinate.
     * This method ensures that only the relevant parts of the terrain and flora are loaded around the avatar.
     * @param coordinateX the current x-coordinate of the avatar.
     */
    private void updateScrollables(float coordinateX) {
        for (Scrollable<?> scrollable : scrollables) {
            scrollable.updateAroundAvatar(coordinateX, this::addGameObject, this::removeGameObject);
        }
    }

    /*
     * Creates and adds the avatar to the game.
     * The avatar is the main character controlled by the player.
     * @return the created avatar.
     */
    private Avatar makeAvatar() {
        //todo decide on initial position of avatar
        float startingY = groundHeightAt.accept(0f) - Avatar.AVATAR_SIZE;
        Vector2 initialPosition = new Vector2(0, startingY);
        var avatar = new Avatar(initialPosition, inputListener, imageReader);
        gameObjects().addGameObject(avatar, Layer.DEFAULT);
        return avatar;
    }

    /*
     * Creates and adds an energy bar to the game.
     * The energy bar displays the avatar's current energy level.
     * @param energySupplier a supplier that provides the current energy level of the avatar.
     */
    private void makeEnergyBar(Supplier<Float> energySupplier) {
        EnergyBar energyBar = new EnergyBar(EnergyBar.ENERGY_BAR_POSITION, Avatar.MAX_ENERGY,
                energySupplier);
        gameObjects().addGameObject(energyBar, Layer.UI);
    }

    /**
     * Adds a game object to the game at the specified layer.
     * @param obj the game object to add.
     * @param layer the layer to add the game object to.
     */
    public void addGameObject(GameObject obj, int layer) {
        gameObjects().addGameObject(obj, layer);
    }

    /**
     * Removes a game object from the game at the specified layer.
     * @param obj the game object to remove.
     * @param layer the layer to remove the game object from.
     */
    public void removeGameObject(GameObject obj, int layer) {
        gameObjects().removeGameObject(obj, layer);
    }


    /**
     * Updates the game state.
     * This method is called once per frame by the danogl library.
     * It updates the scrollable objects based on the avatar's position.
     * @param deltaTime The time, in seconds, that passed since the last invocation
     * of this method (i.e., since the last frame).
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        updateScrollables(avatar.getCenter().x());
    }
}
