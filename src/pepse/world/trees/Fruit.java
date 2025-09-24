
package pepse.world.trees;

import danogl.GameObject;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.awt.*;

public class Fruit extends GameObject {
    private static final Vector2 FRUIT_SIZE = new Vector2(Block.SIZE, Block.SIZE);
    private static final Color FRUIT_COLOR = new Color(255, 69, 0);

    public Fruit(Vector2 position) {
        super(position, FRUIT_SIZE, new OvalRenderable(FRUIT_COLOR));
        setTag("fruit");
    }
}

