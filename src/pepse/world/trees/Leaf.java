package pepse.world.trees;

import danogl.GameObject;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import java.awt.*;
import java.util.Random;

import static pepse.PepseGameManager.GAME_BLOCK_SIZE;

/**
 * Represents a leaf in the game world with a swaying animation.
 * The leaf gently sways back and forth to simulate natural movement.
 * @author Aron Isaacs
 */
public class Leaf {
    private static final Vector2 LEAF_SIZE = new Vector2(GAME_BLOCK_SIZE, GAME_BLOCK_SIZE);
    private static final Color LEAF_COLOR = new Color(34, 139, 34);
    private static final float SWAY_ANGLE = 5f; // degrees
    private static final float SCALE_VARIATION = 0.2f;
    private static final float SWAY_DURATION = 1.5f; // seconds

    /**
     * Creates a leaf GameObject at the specified position with a swaying animation.
     * @param position The position to place the leaf in the game world.
     * @return The created leaf GameObject.
     */
    public static GameObject create(Vector2 position) {
        GameObject leaf = new GameObject(position, LEAF_SIZE, new RectangleRenderable(LEAF_COLOR));
        leaf.setTag("leaf");
        addSwayingAnimation(leaf);
        return leaf;
    }

    /** Adds a swaying animation to the given leaf GameObject.
     * The animation includes a back-and-forth rotation and a slight scaling effect.
     * @param leaf The leaf GameObject to animate.
     */
    public static void addSwayingAnimation(GameObject leaf) {
        Random rand = new Random(); // Not seeded, so each call gets a different pattern
        // Randomize sway frequency slightly
        float swayFrequency = SWAY_DURATION + rand.nextFloat(); // Frequency between 1.5 and 2.5 seconds
        // Sway rotation in degrees
        float swayAngle = SWAY_ANGLE;
        // Randomize scale factor slightly
        float scaleFactor =  1 - SCALE_VARIATION + (rand.nextFloat() * 2 * SCALE_VARIATION); // between 1.05
        // and 1.10
        addSwayTransition(leaf, swayAngle, swayFrequency);
        addScalingTransition(leaf, scaleFactor, swayFrequency);
    }

    /*
        * Adds a scaling transition to the leaf GameObject to create a gentle pulsing effect.
     */
    private static void addScalingTransition(GameObject leaf, float scaleFactor, float swayFrequency) {
        new Transition<>(
                leaf,
                f -> leaf.setDimensions(new Vector2(GAME_BLOCK_SIZE, GAME_BLOCK_SIZE).mult(f)),
                1f,
                scaleFactor,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                swayFrequency,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null
        );
    }

    /*
        * Adds a swaying rotation transition to the leaf GameObject to simulate natural movement.
     */
    private static void addSwayTransition(GameObject leaf, float swayAngle, float swayFrequency) {
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
    }


}


