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

/**
 * Represents a fruit in the game that can be collected by the avatar to gain energy.
 * The fruit respawns after a certain period once collected.
 * @author Aron Isaacs
 */
public class Fruit extends GameObject {
    private static final Vector2 FRUIT_SIZE = new Vector2(GAME_BLOCK_SIZE, GAME_BLOCK_SIZE);
    private static final Color FRUIT_COLOR = new Color(255, 69, 0);
    private static final float RESPAWN_TIME_SECONDS = 15f;
    private static final int ENERGY_VALUE = 10;
    private static final Renderable FRUIT_RENDERABLE = new OvalRenderable(FRUIT_COLOR);

    /**
     * Constructs a Fruit object at the specified position.
     * @param position The position to place the fruit in the game world.
     */
    public Fruit(Vector2 position) {
        super(position, FRUIT_SIZE, FRUIT_RENDERABLE);
        setTag("fruit");
    }

    /**
     * Handles collision events with other game objects.
     * If the colliding object is an Avatar, the avatar gains energy and the fruit disappears,
     * then respawns after a set time.
     * @param other The other game object involved in the collision.
     * @param collision Information about the collision event.
     */
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        if (renderer(). getRenderable() == null) return;

        if (other instanceof Avatar avatar) {
            avatar.gainEnergy(ENERGY_VALUE);
            renderer(). setRenderable(null);
            // Schedule respawn
            new ScheduledTask(this, RESPAWN_TIME_SECONDS, false,
                    () -> renderer().setRenderable(FRUIT_RENDERABLE));
        }
    }
}


