package pepse.world.trees;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import java.awt.*;

/**
 * A class representing the trunk of a tree in the game.
 * The trunk is a rectangular game object with a specific color and physical properties.
 * @author Aron Isaacs
 */
public class Trunk {
    private static final Color TRUNK_COLOR = new Color(102, 51, 0);

    /**
     * Creates a trunk GameObject at the specified position with the given dimensions.
     * The trunk is immovable and prevents intersections from all directions.
     * @param position The position to place the trunk in the game world.
     * @param dimensions The dimensions of the trunk.
     * @return The created trunk GameObject.
     */
    public static GameObject create(Vector2 position, Vector2 dimensions) {
        GameObject trunk = new GameObject(position, dimensions, new RectangleRenderable(TRUNK_COLOR));
        trunk.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        trunk.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        trunk.setTag("trunk");
        return trunk;
    }
}
