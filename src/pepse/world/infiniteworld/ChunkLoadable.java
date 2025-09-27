package pepse.world.infiniteworld;

import danogl.GameObject;

import java.util.function.BiConsumer;


/**
 * An interface for objects that can be loaded and unloaded in chunks within the game world.
 * Implementing classes must define how to add and destroy game objects in the context of chunk loading.
 */
public interface ChunkLoadable {
    /**
     * Adds the game object to the game using the provided BiConsumer.
     * @param addGameObject A BiConsumer that takes a GameObject and an Integer (chunk index) to add the object to the game.
     */
    void addToGame(BiConsumer<GameObject, Integer> addGameObject);
    /**
     * Destroys the game object using the provided BiConsumer.
     * @param destroyGameObject A BiConsumer that takes a GameObject and an Integer (chunk index) to remove the object from the game.
     */
    void destroy(BiConsumer<GameObject, Integer> destroyGameObject);
}

