package pepse.world.infiniteworld;

/**
 * A functional interface representing a method to determine ground height at a given x-coordinate.
 * Implementations should provide the logic to calculate and return the ground height as a float value.
 */
@FunctionalInterface
public interface GroundHeightAt {
    float accept(float value);
}
