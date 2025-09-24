package pepse.world.trees;


import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.util.Vector2;
import pepse.world.Block;


import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.function.BiConsumer;


public class Tree {
    private static final int BLOCK_SIZE = Block.SIZE;
    private static final int TRUNK_HEIGHT_MIN = 4;
    private static final int TRUNK_HEIGHT_MAX = 8;
    private static final int FOLIAGE_WIDTH_BLOCKS = 5;
    private static final int FOLIAGE_HEIGHT_BLOCKS = 6;


    private final GameObject trunk;
    private final Set<GameObject> leaves;
    private final Set<Fruit> fruits;


    public Tree(Vector2 position, BiConsumer<GameObject, Integer> addObject) {
        Random random = new Random((long) position.x());


        int trunkWidth = BLOCK_SIZE;
        int trunkHeightBlocks = TRUNK_HEIGHT_MIN + random.nextInt(TRUNK_HEIGHT_MAX - TRUNK_HEIGHT_MIN + 1);
        int trunkHeight = trunkHeightBlocks * BLOCK_SIZE;


        this.trunk = Trunk.create(position.add(new Vector2(0, -trunkHeight)), new Vector2(trunkWidth, trunkHeight));
        addObject.accept(this.trunk, Layer.STATIC_OBJECTS);


        this.leaves = new HashSet<>();
        this.fruits = new HashSet<>();


        int startX = (int) position.x() - (FOLIAGE_WIDTH_BLOCKS / 2) * BLOCK_SIZE;
        int startY = (int) position.y() - trunkHeight - (FOLIAGE_HEIGHT_BLOCKS - 1) * BLOCK_SIZE;


        for (int i = 0; i < FOLIAGE_WIDTH_BLOCKS; i++) {
            for (int j = 0; j < FOLIAGE_HEIGHT_BLOCKS; j++) {
                int choice = random.nextInt(3); // 0: leaf, 1: fruit, 2: nothing
                float x = startX + i * BLOCK_SIZE;
                float y = startY + j * BLOCK_SIZE;
                Vector2 objPos = new Vector2(x, y);


                if (choice == 0) {
                    GameObject leaf = Leaf.create(objPos);
                    this.leaves.add(leaf);
                    addObject.accept(leaf, Layer.BACKGROUND);
                } else if (choice == 1) {
                    Fruit fruit = new Fruit(objPos);
                    this.fruits.add(fruit);
                    addObject.accept(fruit, Layer.STATIC_OBJECTS);
                }
            }
        }
    }
}