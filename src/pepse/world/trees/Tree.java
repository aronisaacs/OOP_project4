package pepse.world.trees;


import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.util.Vector2;
import pepse.world.infiniteworld.ChunkLoadable;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;

import static pepse.PepseGameManager.GAME_BLOCK_SIZE;

/**
 * Represents a tree in the game world, consisting of a trunk, leaves, and fruits.
 * The tree is generated with a random height and foliage distribution.
 * Implements ChunkLoadable to allow for dynamic loading and unloading in an infinite world.
 * @author Aron Isaacs
 */
public class Tree implements ChunkLoadable {
    private static final int TREE_WIDTH = GAME_BLOCK_SIZE;
    private static final int TRUNK_HEIGHT_MIN = 4;
    private static final int TRUNK_HEIGHT_MAX = 8;
    private static final int FOLIAGE_WIDTH_BLOCKS = 5;
    private static final int FOLIAGE_HEIGHT_BLOCKS = 6;
    private static final float LEAF_RATIO = 0.7f;
    private static final float FRUIT_RATIO = 0.2f;


    private final GameObject trunk;
    private final Set<GameObject> leaves;
    private final Set<Fruit> fruits;
    private final Random random;


    /**
     * Constructs a Tree object at the specified position.
     * The tree consists of a trunk, leaves, and fruits, all generated with some randomness.
     * @param position The position to place the base of the tree in the game world.
     */
    public Tree(Vector2 position) {

        random = new Random((long) position.x() + (long) position.y() * 31);
        int trunkHeightBlocks = TRUNK_HEIGHT_MIN + random.nextInt(TRUNK_HEIGHT_MAX - TRUNK_HEIGHT_MIN + 1);
        int trunkHeight = trunkHeightBlocks * TREE_WIDTH;
        this.trunk = Trunk.create(position.add(new Vector2(0, -trunkHeight)), new Vector2(TREE_WIDTH, trunkHeight));
        this.leaves = new HashSet<>();
        this.fruits = new HashSet<>();
        int startX = (int) position.x() - (FOLIAGE_WIDTH_BLOCKS / 2) * TREE_WIDTH;
        int startY = (int) position.y() - trunkHeight - (FOLIAGE_HEIGHT_BLOCKS - 1) * TREE_WIDTH;
        // Generate tree foliage
        for (int i = 0; i < FOLIAGE_WIDTH_BLOCKS; i++) {
            for (int j = 0; j < FOLIAGE_HEIGHT_BLOCKS; j++) {
                makeTreeFoliage(startX + i * TREE_WIDTH, startY + j * TREE_WIDTH);
            }
        }
    }

    /* Generates foliage (leaves or fruits) at the specified coordinates based on predefined ratios.
     * @param x The x-coordinate for the foliage placement.
     * @param y The y-coordinate for the foliage placement.
     */
    private void makeTreeFoliage(float x, float y) {
        float choice = random.nextFloat(1); // 0: leaf, 1: fruit, 2: nothing
        Vector2 objPos = new Vector2(x, y);

        if (choice < LEAF_RATIO) {
            GameObject leaf = Leaf.create(objPos);
            this.leaves.add(leaf);
        } else if (choice < LEAF_RATIO + FRUIT_RATIO) {
            Fruit fruit = new Fruit(objPos);
            this.fruits.add(fruit);
        }
    }

    /**
     * Adds the tree's components (trunk, leaves, fruits) to the game using the provided BiConsumer.
     * @param addObject A BiConsumer that accepts a GameObject and a layer integer to add objects to the game.
     */
    @Override
    public void addToGame(BiConsumer<GameObject, Integer> addObject) {
        addObject.accept(trunk, Layer.STATIC_OBJECTS);
        for (GameObject leaf : leaves) {
            addObject.accept(leaf, Layer.BACKGROUND);
        }
        for (Fruit fruit : fruits) {
            addObject.accept(fruit, Layer.STATIC_OBJECTS);
        }
    }

    /**
     * Removes the tree's components (trunk, leaves, fruits) from the game using the provided BiConsumer.
     * @param removeObject A BiConsumer that accepts a GameObject and a layer integer to remove objects from the game.
     */
    @Override
    public void destroy(BiConsumer<GameObject, Integer> removeObject) {
        removeObject.accept(trunk, Layer.STATIC_OBJECTS);
        for (GameObject leaf : leaves) {
            removeObject.accept(leaf, Layer.BACKGROUND);
        }
        for (Fruit fruit : fruits) {
            removeObject.accept(fruit, Layer.STATIC_OBJECTS);
        }
    }
}