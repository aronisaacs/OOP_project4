package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * A class representing a sun halo effect in the game world.
 * The sun halo is a semi-transparent circular glow that follows the sun's position.
 *
 * @author Aron Isaacs
 */
public class SunHalo {
	private static final Color HALO_COLOR = new Color(255, 255, 0, 20); // Semi-transparent yellow
	private static final float HALO_SIZE_FACTOR = 1.5f;
	private static final String SUN_HALO_TAG = "sunHalo";

	/**
	 * Creates a sun halo GameObject that follows the sun's center.
	 *
	 * @param sun The sun GameObject to follow.
	 * @return The sun halo GameObject.
	 */
	public static GameObject create(GameObject sun) {
		float haloDiameter = sun.getDimensions().x() * HALO_SIZE_FACTOR;
		Renderable haloRenderable = new OvalRenderable(HALO_COLOR);
		GameObject halo = new GameObject(Vector2.ZERO,
				new Vector2(haloDiameter, haloDiameter),
				haloRenderable);
		halo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		halo.setTag(SUN_HALO_TAG);

		// Set initial position
		halo.setCenter(sun.getCenter());

		// Add component to follow the sun's center
		halo.addComponent((deltaTime) -> halo.setCenter(sun.getCenter()));

		return halo;
	}
}
