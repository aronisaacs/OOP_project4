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

public class PepseGameManager extends GameManager {
    static final int SEED = 100;
    static final float CYCLE_LENGTH_OF_DAY = 15f;
    private Terrain terrain;
    private ImageReader imageReader;
    private SoundReader soundReader;
    private UserInputListener inputListener;
    private WindowController windowController;


    public static void main(String[] args) {
        new PepseGameManager().run();
    }

    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader, UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
        this.imageReader = imageReader;
        this.soundReader = soundReader;
        this.inputListener = inputListener;
        this.windowController = windowController;
        makeTerrain();
        makeBackgroundObjects();
        Avatar avatar = makeAvatar();
        makeEnergyBar(avatar);
        placeTreeAt(60);

    }

    private void makeEnergyBar(Avatar avatar) {
        Vector2 energyBarPosition = new Vector2(20f, 20f); // Top-left corner, adjust as needed
        EnergyBar energyBar = new EnergyBar(energyBarPosition, 100f, avatar::getEnergy);
        gameObjects().addGameObject(energyBar, Layer.UI);
    }

    private Avatar makeAvatar() {
        //todo decide on initial position of avatar
        float startingX = windowController.getWindowDimensions().x() / 2f;
        float startingY = windowController.getWindowDimensions().y() / 2f;
        Vector2 initialPosition = new Vector2(startingX, startingY);
        var avatar = new Avatar(initialPosition, inputListener, imageReader);
        gameObjects().addGameObject(avatar, Layer.DEFAULT);
        return avatar;
    }

    private void makeTerrain() {
        this.terrain = new Terrain(windowController.getWindowDimensions(), SEED);
        List<Block> blocks = terrain.createInRange(0, (int) windowController.getWindowDimensions().x());
        for (Block block : blocks) {
            //todo for optimization, maybe switch some blocks to the background
            gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
        }
    }

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

    private void placeTreeAt(float locationX) {
        float blockSize = Block.SIZE;
        // Align x to block size
        float x = Math.round(locationX / blockSize) * blockSize;
        // Example: place tree at ground level (adjust as needed)
        float y = terrain.groundHeightAt(x);
        Vector2 position = new Vector2(x, y);

        new Tree(position,
                (gameObject, layer) -> gameObjects().addGameObject(gameObject, layer)
        );
    }

}
