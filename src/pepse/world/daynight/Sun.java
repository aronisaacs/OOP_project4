package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import pepse.world.Terrain;

import java.awt.Color;

public class Sun {
    private static final float SUN_SIZE_RATIO = 0.1f; // Sun diameter is 10% of window width
    private static final float SUN_PATH_RADIUS_RATIO = 0.4f; // Orbit radius is 40% of window width
    private static final Color SUN_COLOR = Color.YELLOW;

    /**
     * Creates a sun GameObject that moves in a circular path to simulate a day-night cycle.
     * @param windowDimensions The dimensions of the window.
     * @param cycleLength The duration (in seconds) of a full sun cycle.
     * @return The sun GameObject.
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        float sunDiameter = windowDimensions.x() * SUN_SIZE_RATIO;
        Renderable sunRenderable = new OvalRenderable(SUN_COLOR);
        GameObject sun = new GameObject(Vector2.ZERO, new Vector2(sunDiameter, sunDiameter), sunRenderable);
        sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);

        // Center of the sun's circular path
        Vector2 cycleCenter = new Vector2(windowDimensions.x() * 0.5f,
                windowDimensions.y() * Terrain.GROUND_RATIO);
        float orbitRadius = windowDimensions.x() * SUN_PATH_RADIUS_RATIO;
        Vector2 initialSunCenter = cycleCenter.add(new Vector2(0, -orbitRadius));
        sun.setCenter(initialSunCenter);

        // Animate the sun's position along a circular path
        new Transition<Float>(
                sun,
                angle -> {
                    sun.setCenter
                            (initialSunCenter.subtract(cycleCenter)
                                    .rotated(angle)
                                    .add(cycleCenter));
                },
                0f,
                360f,
                Transition.LINEAR_INTERPOLATOR_FLOAT,
                cycleLength,
                Transition.TransitionType.TRANSITION_LOOP,
                null
        );

        sun.setTag("sun");
        return sun;
    }
}