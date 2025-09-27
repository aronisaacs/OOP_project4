package pepse.world.infiniteworld;

import danogl.GameObject;
import java.util.List;
import java.util.function.BiConsumer;
import static pepse.PepseGameManager.GAME_BLOCK_SIZE;


/**
 * Manages the loading and unloading of game objects in chunks based on the avatar's position.
 * Each chunk contains multiple ChunkLoadable objects that can be added to or removed from the game.
 *
 * @param <T> The type of ChunkLoadable objects being managed.
 * @author Aron Isaacs
 */
public abstract class Scrollable<T extends ChunkLoadable> {

    private static final int CHUNK_SIZE = GAME_BLOCK_SIZE * 8;
    private static final int RANGE_BEFORE = 6;
    private static final int RANGE_AFTER = 6;

    private final ChunkManager<T> chunkManager;

    /**
     * Constructs a Scrollable object with a ChunkManager to handle chunk loading and unloading.
     * The ChunkManager is initialized with predefined chunk size and range parameters.
     * The createInRange method is used to generate the game objects within the specified chunk bounds.
     */
    public Scrollable() {
        this.chunkManager = new ChunkManager<>(
                CHUNK_SIZE,
                RANGE_BEFORE,
                RANGE_AFTER,
                this::createInRange
        );
    }

    /**
     * Updates the loaded chunks based on the avatar's current position.
     * Loads new chunks as the avatar moves forward and unloads chunks that are out of range.
     *
     * @param avatarX The current x-coordinate of the avatar.
     * @param addGameObject A BiConsumer to add game objects to the game.
     * @param destroyGameObject A BiConsumer to remove game objects from the game.
     */
    public final void updateAroundAvatar(float avatarX,
                                         BiConsumer<GameObject, Integer> addGameObject,
                                         BiConsumer<GameObject, Integer> destroyGameObject) {
        chunkManager.update(avatarX, addGameObject, destroyGameObject);
    }

    /**
     * Creates a list of ChunkLoadable objects within the specified left and right bounds.
     * This method must be implemented by subclasses to define how game objects are created
     * and placed within the given range.
     *
     * @param leftBound The left boundary of the chunk range.
     * @param rightBound The right boundary of the chunk range.
     * @return A list of ChunkLoadable objects to be added to the game within the specified bounds.
     */
    protected abstract List<T> createInRange(int leftBound, int rightBound);
}


