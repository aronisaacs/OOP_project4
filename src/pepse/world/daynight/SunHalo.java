package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.Component;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.Color;

public class SunHalo {
    private static final Color HALO_COLOR = new Color(255, 255, 0, 20); // Semi-transparent yellow

    /**
     * Creates a sun halo GameObject that follows the sun's center.
     * @param sun The sun GameObject to follow.
     * @return The sun halo GameObject.
     */
    public static GameObject create(GameObject sun) {
        float haloDiameter = sun.getDimensions().x() * 1.5f;
        Renderable haloRenderable = new OvalRenderable(HALO_COLOR);
        GameObject halo = new GameObject(Vector2.ZERO, new Vector2(haloDiameter, haloDiameter), haloRenderable);
        halo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        halo.setTag("sunHalo");

        // Set initial position
        halo.setCenter(sun.getCenter());

        // Add component to follow the sun's center
        halo.addComponent((deltaTime) -> halo.setCenter(sun.getCenter()));

        return halo;
    }
}
