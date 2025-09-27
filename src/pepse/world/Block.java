package pepse.world;

import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.infiniteworld.ChunkLoadable;

import java.util.function.BiConsumer;

import static pepse.PepseGameManager.GAME_BLOCK_SIZE;

public class Block extends GameObject implements ChunkLoadable {

    private static final int SIZE = GAME_BLOCK_SIZE;

    public Block(Vector2 topLeftCorner, Renderable renderable) {
        super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
    }

    @Override
    public void addToGame(BiConsumer<GameObject, Integer> addGameObject) {
        // Add this block to the game using addGameObject
        addGameObject.accept(this, Layer.STATIC_OBJECTS);
    }

    @Override
    public void destroy(BiConsumer<GameObject, Integer> destroyGameObject) {
        destroyGameObject.accept(this, Layer.STATIC_OBJECTS);
    }


}
