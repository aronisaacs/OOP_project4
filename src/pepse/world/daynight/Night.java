package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import java.awt.Color;

/**
 * A class representing the night overlay in the game, which simulates the transition between day and night.
 * The night overlay is a black rectangle that covers the entire game window and changes its opacity
 * to create a day-night cycle effect.
 * @author Aron Isaacs
 */
public class Night {
    // The maximum opacity level for the night overlay, representing the darkest point of the night.
    private static final Float MAX_OPAQUENESS = 0.5f;

    /**
     * Creates a night overlay GameObject that covers the entire game window and transitions its opacity
     * to simulate a day-night cycle.
     * @param windowDimensions The dimensions of the game window.
     * @param cycleLength The total duration of one complete day-night cycle in seconds.
     * @return A GameObject representing the night overlay with a day-night cycle effect.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        GameObject night = new GameObject(
            Vector2.ZERO,
            windowDimensions,
            new RectangleRenderable(Color.BLACK)
        );
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag("night");

        // Create a transition to change the opacity of the night overlay over time
        new Transition<>(night,
            night.renderer()::setOpaqueness,
            0f, MAX_OPAQUENESS,
            Transition.CUBIC_INTERPOLATOR_FLOAT,
            cycleLength / 2,
            Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
            null
        );

        return night;
    }
}