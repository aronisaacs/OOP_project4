package pepse.world.infiniteworld;


/**
 * A functional interface representing a method to determine ground height at a given x-coordinate.
 * Implementations should provide the logic to calculate and return the ground height as a float value.
 */
@FunctionalInterface
public interface GroundHeightAt {
	/**
	 * Accepts an x-coordinate and returns the corresponding ground height.
	 *
	 * @param value The x-coordinate for which to determine the ground height.
	 * @return The ground height at the specified x-coordinate.
	 */
	float accept(float value);
}
