package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import java.awt.Color;

public class Night {
    private static final Float MAX_OPAQUENESS = 0.5f;

    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        GameObject night = new GameObject(
            Vector2.ZERO,
            windowDimensions,
            new RectangleRenderable(Color.BLACK)
        );
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag("night");

        new Transition<Float>(night,
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