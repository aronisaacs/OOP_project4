package pepse.world;

import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.infiniteworld.ChunkLoadable;
import java.util.function.BiConsumer;

import static pepse.PepseGameManager.GAME_BLOCK_SIZE;

/**
 * A class representing a block in the game world.
 * The block is a rectangular game object with specific physical properties.
 * It implements the ChunkLoadable interface to allow for dynamic loading and unloading in chunks.
 * @author Aron Isaacs
 */
public class Block extends GameObject implements ChunkLoadable {


    /**
     * Constructs a Block object at the specified position with the given renderable.
     * The block is immovable and prevents intersections from all directions.
     * @param topLeftCorner The position to place the block in the game world.
     * @param renderable The renderable to use for the block's appearance.
     */
    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(GAME_BLOCK_SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }

    /**
     * Adds this block to the game using the provided BiConsumer.
     * @param addGameObject A BiConsumer to add game objects to the game.
     */
    @Override
    public void addToGame(BiConsumer<GameObject, Integer> addGameObject) {
        // Add this block to the game using addGameObject
        addGameObject.accept(this, Layer.STATIC_OBJECTS);
    }

    /**
     * Destroys this block using the provided BiConsumer.
     * @param destroyGameObject A BiConsumer to remove game objects from the game.
     */
    @Override
    public void destroy(BiConsumer<GameObject, Integer> destroyGameObject) {
        destroyGameObject.accept(this, Layer.STATIC_OBJECTS);
    }
}
