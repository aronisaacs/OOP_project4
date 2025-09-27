package pepse.world.trees;

import danogl.util.Vector2;
import pepse.world.infiniteworld.GroundHeightAt;
import pepse.world.infiniteworld.Scrollable;


import java.util.*;

import static pepse.PepseGameManager.GAME_BLOCK_SIZE;

public class Flora extends Scrollable<Tree> {

    static final float CHANCE_FOR_TREE = 0.15f;
    static final int CHUNK_SIZE = GAME_BLOCK_SIZE * 8;

    private final int seed;
    private final GroundHeightAt groundHeightAt;




    public Flora(int seed, GroundHeightAt groundHeightAt) {
        this.seed = seed;
        this.groundHeightAt = groundHeightAt;
    }

    public List<Tree> createInRange(int minX, int maxX) {
        List<Tree> newTrees = new ArrayList<>();
        int start = (minX / GAME_BLOCK_SIZE) * GAME_BLOCK_SIZE;
        int end = (maxX / GAME_BLOCK_SIZE) * GAME_BLOCK_SIZE;

        for (int x = start; x <= end; x += GAME_BLOCK_SIZE) {
            float y = pseudoRandomFloatAt(x);
            if (y < CHANCE_FOR_TREE) {
                newTrees.add(placeTreeAt((float) x));
            }
        }
        return newTrees;
    }


    /*
     * Places a tree at the specified x-coordinate.
     * The tree is aligned to the block grid and positioned at ground level.
     * @param locationX the x-coordinate where the tree should be placed.
     */
    private Tree placeTreeAt(float locationX) {
        // Align x to block size
        float x = Math.round(locationX / GAME_BLOCK_SIZE) * GAME_BLOCK_SIZE;
        // Example: place tree at ground level (adjust as needed)
        float y = groundHeightAt.accept(x);
        Vector2 position = new Vector2(x, y);
        return new Tree(position);
    }

    /*
     * Generates a pseudo-random float in the range [0, 1) based on the input x-coordinate and the SEED.
     * This function uses bitwise operations and multiplications to create a hash-like effect.
     * @param x the input x-coordinate used to generate the pseudo-random float.
     * @return a pseudo-random float in the range [0, 1).
     */
    public float pseudoRandomFloatAt(int x) {
        //todo should this be moved to a utility class? or a lambda function?
        int z = x ^ seed;
        z = (z ^ (z >>> 16)) * 0x45d9f3b;
        z = (z ^ (z >>> 16)) * 0x45d9f3b;
        z = z ^ (z >>> 16);
        return (z & 0x7FFFFFFF) / (float) Integer.MAX_VALUE;
    }
}
