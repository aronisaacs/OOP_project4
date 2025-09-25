package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.awt.Color;
import java.util.function.Supplier;

public class EnergyBar extends GameObject {
    private static final Vector2 BAR_SIZE = new Vector2(60, 20);
    private final Supplier<Float> energySupplier;
    private final float maxEnergy;
    private final TextRenderable textRenderable;

    public EnergyBar(Vector2 topLeftCorner, float maxEnergy, Supplier<Float> energySupplier) {
        super(topLeftCorner, BAR_SIZE, null);
        this.maxEnergy = maxEnergy;
        this.energySupplier = energySupplier;
        this.textRenderable = new TextRenderable("");
        this.textRenderable.setColor(Color.GREEN);
        this.renderer().setRenderable(textRenderable);
        this.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        float energy = Math.max(0, Math.min(maxEnergy, energySupplier.get()));
        int percent = Math.round((energy / maxEnergy) * 100);
        textRenderable.setString(percent + "%");
    }
}