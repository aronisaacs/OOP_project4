package pepse.world.infiniteworld;

import danogl.GameObject;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * Manages loading and unloading of game objects in chunks based on the avatar's position.
 * @param <T> The type of ChunkLoadable objects being managed.
 * @author Aron Isaacs
 */
public class ChunkManager<T extends ChunkLoadable> {

    /**
     * A functional interface for placing chunks of game objects within specified bounds.
     * Implementations should define how to create and return a list of ChunkLoadable objects
     * that fit within the given left and right bounds.
     * @param <T> The type of ChunkLoadable objects to be placed.
     */
    @FunctionalInterface
    public interface ChunkPlacer<T extends ChunkLoadable> {
        List<T> place(int leftBound, int rightBound);
    }

    // A deque to keep track of loaded chunks and their associated game objects.
    private final Deque<Map.Entry<Integer, List<T>>> loadedChunks = new ArrayDeque<>();
    private final int chunkSize;
    private final int rangeBefore;
    private final int rangeAfter;
    private final ChunkPlacer<T> placer;

    public ChunkManager(int chunkSize, int rangeBefore, int rangeAfter, ChunkPlacer<T> placer) {
        this.chunkSize = chunkSize;
        this.rangeBefore = rangeBefore;
        this.rangeAfter = rangeAfter;
        this.placer = placer;
    }

    /**
     * Updates the loaded chunks based on the avatar's current position.
     * Loads new chunks as the avatar moves forward and unloads chunks that are out of range.
     * @param avatarX The current x-coordinate of the avatar.
     * @param addGameObject A BiConsumer to add game objects to the game.
     * @param destroyGameObject A BiConsumer to remove game objects from the game.
     */
    public void update(float avatarX,
                       BiConsumer<GameObject, Integer> addGameObject,
                       BiConsumer<GameObject, Integer> destroyGameObject) {
        int avatarChunk = Math.round(avatarX / chunkSize);
        int minChunk = avatarChunk - rangeBefore;
        int maxChunk = avatarChunk + rangeAfter;

        removeChunks(destroyGameObject, minChunk, maxChunk);
        addChunks(addGameObject, minChunk, maxChunk);
    }

    /*
        * Adds new chunks of game objects within the specified chunk range.
        * @param addGameObject A BiConsumer to add game objects to the game.
        * @param minChunk The minimum chunk index to load.
        * @param maxChunk The maximum chunk index to load.
     */
    private void addChunks(BiConsumer<GameObject, Integer> addGameObject, int minChunk, int maxChunk) {
        for (int chunk = minChunk; chunk <= maxChunk; chunk++) {
            int finalChunk = chunk;
            boolean exists = loadedChunks.stream().anyMatch(e -> e.getKey() == finalChunk);
            if (!exists) {
                int left = chunk * chunkSize;
                int right = left + chunkSize - 1;
                List<T> placed = placer.place(left, right);
                for (T obj : placed) {
                    obj.addToGame(addGameObject);
                }
                loadedChunks.addLast(new AbstractMap.SimpleEntry<>(chunk, placed));
            }
        }
    }

    /*
        * Removes chunks of game objects that are outside the specified chunk range.
        * @param destroyGameObject A BiConsumer to remove game objects from the game.
        * @param minChunk The minimum chunk index to keep.
        * @param maxChunk The maximum chunk index to keep.
     */
    private void removeChunks(BiConsumer<GameObject, Integer> destroyGameObject, int minChunk, int maxChunk) {
        // Remove old chunks
        Iterator<Map.Entry<Integer, List<T>>> it = loadedChunks.iterator();
        while (it.hasNext()) {
            Map.Entry<Integer, List<T>> entry = it.next();
            int chunkIndex = entry.getKey();
            if (chunkIndex < minChunk || chunkIndex > maxChunk) {
                for (T obj : entry.getValue()) {
                    obj.destroy(destroyGameObject);
                }
                it.remove();
            }
        }
    }
}

