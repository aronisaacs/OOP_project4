package pepse.world.infiniteworld;

import danogl.GameObject;
import java.util.List;
import java.util.function.BiConsumer;
import static pepse.PepseGameManager.GAME_BLOCK_SIZE;

public abstract class Scrollable<T extends ChunkLoadable> {

    private static final int CHUNK_SIZE = GAME_BLOCK_SIZE * 8;
    private static final int RANGE_BEFORE = 6;
    private static final int RANGE_AFTER = 6;

    private final ChunkManager<T> chunkManager;

    public Scrollable() {
        this.chunkManager = new ChunkManager<>(
                CHUNK_SIZE,
                RANGE_BEFORE,
                RANGE_AFTER,
                this::createInRange
        );
    }

    public final void updateAroundAvatar(float avatarX,
                                         BiConsumer<GameObject, Integer> addGameObject,
                                         BiConsumer<GameObject, Integer> destroyGameObject) {
        chunkManager.update(avatarX, addGameObject, destroyGameObject);
    }

    protected abstract List<T> createInRange(int leftBound, int rightBound);
}


