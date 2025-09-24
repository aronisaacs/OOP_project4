package pepse.world.trees;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import java.awt.*;

public class Trunk {
    private static final Color TRUNK_COLOR = new Color(102, 51, 0);

    public static GameObject create(Vector2 position, Vector2 dimensions) {
        GameObject trunk = new GameObject(position, dimensions, new RectangleRenderable(TRUNK_COLOR));
        trunk.physics().preventIntersectionsFromDirection(Vector2.ZERO);
        trunk.physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
        trunk.setTag("trunk");
        return trunk;
    }
}
