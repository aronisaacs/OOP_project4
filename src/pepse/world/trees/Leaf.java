

package pepse.world.trees;

import danogl.GameObject;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import pepse.world.Block;

import java.awt.*;
import java.util.Random;

public class Leaf {
    private static final Vector2 LEAF_SIZE = new Vector2(Block.SIZE, Block.SIZE);
    private static final Color LEAF_COLOR = new Color(34, 139, 34);
    private static final float SWAY_ANGLE = 5f; // degrees
    private static final float SCALE_VARIATION = 0.2f;
    private static final float SWAY_DURATION = 1.5f; // seconds

    public static GameObject create(Vector2 position) {
        GameObject leaf = new GameObject(position, LEAF_SIZE, new RectangleRenderable(LEAF_COLOR));
        leaf.setTag("leaf");

        addSwayingAnimation(leaf);

        return leaf;
    }


    public static void addSwayingAnimation(GameObject leaf) {
        Random rand = new Random(); // Not seeded, so each call gets a different pattern

        // Randomize sway frequency slightly
        float swayFrequency = SWAY_DURATION + rand.nextFloat(); // Frequency between 1.5 and 2.5 seconds

        // Sway rotation in degrees
        float swayAngle = SWAY_ANGLE;

        // Animate angle
        new Transition<>(
                leaf,
                leaf.renderer()::setRenderableAngle,
                -swayAngle,
                swayAngle,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                swayFrequency,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );

        // Animate scale slightly (breathing effect)
        float scaleFactor =  1 - SCALE_VARIATION + (rand.nextFloat() * 2 * SCALE_VARIATION); // between 1.05
        // and 1.10
        new Transition<Float>(
                leaf,
                f -> leaf.setDimensions(new Vector2(Block.SIZE, Block.SIZE).mult(f)),
                1f,
                scaleFactor,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                swayFrequency,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );
    }


}


