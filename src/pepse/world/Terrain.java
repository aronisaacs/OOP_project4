package pepse.world;

import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.util.NoiseGenerator;
import pepse.world.infiniteworld.Scrollable;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import static pepse.PepseGameManager.GAME_BLOCK_SIZE;

/**
 * Manages the terrain in the game world, including ground height and block creation.
 * The terrain is generated using a noise function to create a natural-looking landscape.
 * The class extends Scrollable to handle dynamic loading and unloading of terrain blocks as the avatar moves.
 * @see Scrollable
 * @see NoiseGenerator
 * @author Aron Isaacs
 */
public class Terrain extends Scrollable<Block> {
    /** The ratio of the window height at which the ground starts. */
    public static final float GROUND_RATIO = 0.7f;

    private final int groundHeightAtX0;
    private static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);
    private static final int BLOCK_DEPTH = 20; // number of blocks below surface
    private final NoiseGenerator noiseGenerator;

    /**
     * Creates a Terrain instance with the specified window dimensions and seed for noise generation.
     * The ground height at x=0 is calculated based on the window height and a predefined ratio.
     * @param windowDimensions The dimensions of the game window.
     * @param seed The seed for the noise generator to ensure consistent terrain generation.
     */
    public Terrain(Vector2 windowDimensions, int seed){
        groundHeightAtX0 = (int) ( windowDimensions.y() * GROUND_RATIO);
        this.noiseGenerator = new NoiseGenerator(seed, groundHeightAtX0);
    }


    /**
     * Creates terrain blocks within the specified chunk range.
     * The ground height is determined using a noise function to create a natural-looking landscape.
     * Blocks are created in columns, extending downwards from the ground height.
     * @see Scrollable#createInRange(int, int)
     * @param minX The left boundary of the chunk range.
     * @param maxX The right boundary of the chunk range.
     * @return A list of Block objects to be added to the game within the specified bounds.
     */
    @Override
    public List<Block> createInRange(int minX, int maxX) {
        List<Block> blocks = new ArrayList<>();

        // Align min and max to block grid
        int alignedMinX = (minX / GAME_BLOCK_SIZE) * GAME_BLOCK_SIZE;
        int alignedMaxX = ((maxX + GAME_BLOCK_SIZE - 1) / GAME_BLOCK_SIZE) * GAME_BLOCK_SIZE;

        for (int x = alignedMinX; x < alignedMaxX; x += GAME_BLOCK_SIZE) {
            float topY = groundHeightAt(x);
            createColumnOfBlocks(x, topY, blocks);
        }

        return blocks;
    }

    /**
     * Creates a vertical column of blocks at the specified x-coordinate, starting from the given top y-coordinate.
     * The column extends downwards for a predefined depth, creating a solid ground structure.
     * Each block is assigned a color that approximates the base ground color.
     * @param x The x-coordinate where the column of blocks will be created.
     * @param topY The y-coordinate of the top block in the column.
     * @param blocks The list to which the created blocks will be added.
     */
    private static void createColumnOfBlocks(int x, float topY, List<Block> blocks) {
        for (int i = 0; i < BLOCK_DEPTH; i++) {
            Vector2 topLeft = new Vector2(x, topY + i * GAME_BLOCK_SIZE);

            Block block = new Block(
                    topLeft,
                    new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR))
            );

            block.setTag("ground");
            blocks.add(block);
        }
    }

    /**
     * Calculates the ground height at a given x-coordinate using a noise function.
     * The height is aligned to the block grid to ensure consistent placement of terrain blocks.
     * @param x The x-coordinate for which to calculate the ground height.
     * @return The y-coordinate of the ground height at the specified x-coordinate, aligned to the block grid.
     */
    public float groundHeightAt(float x) {
        //note the castings to int and then back to float are to ensure the height is aligned to the block
        // grid. The API of the project requires both the parameter and return value to be a float.
        int startOfBlock = (((int) x) / GAME_BLOCK_SIZE) * GAME_BLOCK_SIZE;
        int noise = (int) noiseGenerator.noise(startOfBlock, GAME_BLOCK_SIZE *7);
        return (float) ((groundHeightAtX0 + noise) / GAME_BLOCK_SIZE) * GAME_BLOCK_SIZE;

    }
}
