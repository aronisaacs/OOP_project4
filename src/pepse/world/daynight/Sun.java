package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Terrain;

import java.awt.*;

/**
 * A class representing the sun in the game world.
 * The sun moves in a circular path to simulate a day-night cycle.
 *
 * @author Aron Isaacs
 */
public class Sun {

	private static final float SUN_SIZE_RATIO = 0.1f; // Sun diameter is 10% of window width
	private static final float SUN_PATH_RADIUS_RATIO = 0.4f; // Orbit radius is 40% of window width
	private static final Color SUN_COLOR = Color.YELLOW;
	private static final float CYCLE_SIZE_FACTOR = 0.5f;
	private static final String SUN_TAG = "sun";
	private static final Float FULL_CIRCLE = 360f;

	/**
	 * Creates a sun GameObject that moves in a circular path to simulate a day-night cycle.
	 *
	 * @param windowDimensions The dimensions of the window.
	 * @param cycleLength      The duration (in seconds) of a full sun cycle.
	 * @return The sun GameObject.
	 */
	public static GameObject create(Vector2 windowDimensions, float cycleLength) {
		float sunDiameter = windowDimensions.x() * SUN_SIZE_RATIO;
		Renderable sunRenderable = new OvalRenderable(SUN_COLOR);
		GameObject sun = new GameObject(Vector2.ZERO, new Vector2(sunDiameter, sunDiameter), sunRenderable);
		sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);

		// Center of the sun's circular path
		Vector2 cycleCenter = new Vector2(windowDimensions.x() * CYCLE_SIZE_FACTOR,
				windowDimensions.y() * Terrain.GROUND_RATIO);
		float orbitRadius = windowDimensions.x() * SUN_PATH_RADIUS_RATIO;
		Vector2 initialSunCenter = cycleCenter.add(new Vector2(0, -orbitRadius));
		sun.setCenter(initialSunCenter);

		addSunTransition(sun, initialSunCenter, cycleCenter, cycleLength);

		sun.setTag(SUN_TAG);
		return sun;
	}

	/**
	 * Adds a transition to the sun GameObject to move it in a circular path.
	 *
	 * @param sun              The sun GameObject to animate.
	 * @param initialSunCenter The initial center position of the sun.
	 * @param cycleCenter      The center of the circular path.
	 */
	private static void addSunTransition(GameObject sun, Vector2 initialSunCenter, Vector2 cycleCenter,
										 float cycleLength) {
		new Transition<>(
				sun,
				angle ->
						sun.setCenter(initialSunCenter.subtract(cycleCenter).rotated(angle).add(cycleCenter)),
				0f,
				FULL_CIRCLE,
				Transition.LINEAR_INTERPOLATOR_FLOAT,
				cycleLength,
				Transition.TransitionType.TRANSITION_LOOP,
				null
		);
	}
}