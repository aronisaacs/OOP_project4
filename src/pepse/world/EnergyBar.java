package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import java.awt.Color;
import java.util.function.Supplier;

/**
 * A class representing an energy bar in the game.
 * The energy bar displays the player's current energy as a percentage of the maximum energy.
 * It updates dynamically based on the player's energy level.
 * @author Aron Isaacs
 */
public class EnergyBar extends GameObject {
    public static final Vector2 ENERGY_BAR_POSITION = new Vector2(20f, 20f);
    private static final Vector2 BAR_SIZE = new Vector2(60, 20);
    private final Supplier<Float> energySupplier;
    private final float maxEnergy;
    private final TextRenderable textRenderable;

    /**
     * Constructs an EnergyBar object at the specified position.
     * @param topLeftCorner The top-left corner position of the energy bar.
     * @param maxEnergy The maximum energy value for the player.
     * @param energySupplier A supplier function that provides the current energy value.
     */
    public EnergyBar(Vector2 topLeftCorner, float maxEnergy, Supplier<Float> energySupplier) {
        super(topLeftCorner, BAR_SIZE, null);
        this.maxEnergy = maxEnergy;
        this.energySupplier = energySupplier;
        this.textRenderable = new TextRenderable("");
        this.textRenderable.setColor(Color.GREEN);
        this.renderer().setRenderable(textRenderable);
        this.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
    }

    /**
     * Updates the energy bar display based on the current energy level.
     * This method is called once per frame.
     * @param deltaTime The time elapsed since the last update call.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float energy = Math.max(0, Math.min(maxEnergy, energySupplier.get()));
        int percent = Math.round((energy / maxEnergy) * 100);
        textRenderable.setString(percent + "%");
    }
}