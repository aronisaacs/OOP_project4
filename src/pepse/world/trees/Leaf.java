
package pepse.world.trees;

import danogl.GameObject;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.awt.*;

public class Leaf {
    private static final Vector2 LEAF_SIZE = new Vector2(Block.SIZE, Block.SIZE);
    private static final Color LEAF_COLOR = new Color(34, 139, 34);

    public static GameObject create(Vector2 position) {
        GameObject leaf = new GameObject(position, LEAF_SIZE, new RectangleRenderable(LEAF_COLOR));
        leaf.setTag("leaf");
        return leaf;
    }
}

