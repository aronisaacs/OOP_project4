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


    public Tree(Vector2 position) {

        random = new Random((long) position.x() + (long) position.y() * 31);


        int trunkHeightBlocks = TRUNK_HEIGHT_MIN + random.nextInt(TRUNK_HEIGHT_MAX - TRUNK_HEIGHT_MIN + 1);
        int trunkHeight = trunkHeightBlocks * TREE_WIDTH;


        this.trunk = Trunk.create(position.add(new Vector2(0, -trunkHeight)), new Vector2(TREE_WIDTH, trunkHeight));



        this.leaves = new HashSet<>();
        this.fruits = new HashSet<>();


        int startX = (int) position.x() - (FOLIAGE_WIDTH_BLOCKS / 2) * TREE_WIDTH;
        int startY = (int) position.y() - trunkHeight - (FOLIAGE_HEIGHT_BLOCKS - 1) * TREE_WIDTH;


        for (int i = 0; i < FOLIAGE_WIDTH_BLOCKS; i++) {
            for (int j = 0; j < FOLIAGE_HEIGHT_BLOCKS; j++) {
                makeFoliageBlock(startX + i * TREE_WIDTH, startY + j * TREE_WIDTH);
            }
        }
    }

    private void makeFoliageBlock( float x, float y) {
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

    public void addToGame(BiConsumer<GameObject, Integer> addObject) {
        addObject.accept(trunk, Layer.STATIC_OBJECTS);
        for (GameObject leaf : leaves) {
            addObject.accept(leaf, Layer.BACKGROUND);
        }
        for (Fruit fruit : fruits) {
            addObject.accept(fruit, Layer.STATIC_OBJECTS);
        }
    }

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