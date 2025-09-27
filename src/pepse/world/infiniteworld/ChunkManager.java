package pepse.world.infiniteworld;

import danogl.GameObject;

import java.util.*;
import java.util.function.BiConsumer;

public class ChunkManager<T extends ChunkLoadable> {

    @FunctionalInterface
    public interface ChunkPlacer<T extends ChunkLoadable> {
        List<T> place(int leftBound, int rightBound);
    }

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

    public void update(float avatarX,
                       BiConsumer<GameObject, Integer> addGameObject,
                       BiConsumer<GameObject, Integer> destroyGameObject) {
        int avatarChunk = Math.round(avatarX / chunkSize);
        int minChunk = avatarChunk - rangeBefore;
        int maxChunk = avatarChunk + rangeAfter;

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

        // Add new chunks
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
}

