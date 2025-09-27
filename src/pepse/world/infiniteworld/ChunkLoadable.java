package pepse.world.infiniteworld;

import danogl.GameObject;

import java.util.function.BiConsumer;

public interface ChunkLoadable {
    void addToGame(BiConsumer<GameObject, Integer> addGameObject);
    void destroy(BiConsumer<GameObject, Integer> destroyGameObject);
}

