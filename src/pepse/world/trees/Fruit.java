package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Avatar;

import java.awt.*;

import static pepse.PepseGameManager.GAME_BLOCK_SIZE;

public class Fruit extends GameObject {
    private static final Vector2 FRUIT_SIZE = new Vector2(GAME_BLOCK_SIZE, GAME_BLOCK_SIZE);
    private static final Color FRUIT_COLOR = new Color(255, 69, 0);
    private static final float RESPAWN_TIME_SECONDS = 15f;
    private static final int ENERGY_VALUE = 10;
    private static final Renderable FRUIT_RENDERABLE = new OvalRenderable(FRUIT_COLOR);

    public Fruit(Vector2 position) {
        super(position, FRUIT_SIZE, FRUIT_RENDERABLE);
        setTag("fruit");
    }

    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        if (renderer(). getRenderable() == null) return;

        if (other instanceof Avatar avatar) {
            avatar.gainEnergy(ENERGY_VALUE);
            renderer(). setRenderable(null);
            new ScheduledTask(this, RESPAWN_TIME_SECONDS, false,
                    () -> renderer().setRenderable(FRUIT_RENDERABLE));
        }
    }
}


