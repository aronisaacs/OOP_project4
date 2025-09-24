package pepse.world;

import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;



public class Terrain {
    public static final float GROUND_RATIO = 0.7f;
    public int groundHeightAtX0;
    private final Vector2 windowDimensions;
    private final int seed;
    public static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int BLOCK_DEPTH = 20; // number of blocks below surface
    private NoiseGenerator noiseGenerator;

    public Terrain(Vector2 windowDimensions, int seed){
        groundHeightAtX0 = (int) ( windowDimensions.y() * GROUND_RATIO);
        this.windowDimensions = windowDimensions;
        this.seed = seed;
        this.noiseGenerator = new NoiseGenerator(seed, groundHeightAtX0);
    }

    public float groundHeightAt(float x) {
        float startOfBlock = (x / Block.SIZE) * Block.SIZE;
        int noise = (int) noiseGenerator.noise(startOfBlock, Block.SIZE *7);
        return  ((groundHeightAtX0 + noise) / Block.SIZE) * Block.SIZE;

    }

    public List<Block> createInRange(int minX, int maxX) {
        List<Block> blocks = new ArrayList<>();

        // Align min and max to block grid
        int alignedMinX = (minX / Block.SIZE) * Block.SIZE;
        int alignedMaxX = ((maxX + Block.SIZE - 1) / Block.SIZE) * Block.SIZE;

        for (int x = alignedMinX; x < alignedMaxX; x += Block.SIZE) {
            float topY = groundHeightAt(x);
            createColumnOfBlocks(x, topY, blocks);
        }

        return blocks;
    }

    private static void createColumnOfBlocks(int x, float topY, List<Block> blocks) {
        for (int i = 0; i < BLOCK_DEPTH; i++) {
            Vector2 topLeft = new Vector2(x, topY + i * Block.SIZE);

            Block block = new Block(
                    topLeft,
                    new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR))
            );

            block.setTag("ground");
            blocks.add(block);
        }
    }


}
