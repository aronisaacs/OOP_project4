package pepse.world;

import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;
import java.awt.event.KeyEvent;

public class Avatar extends GameObject {

    private static final float VELOCITY_X = 400;
    private static final float VELOCITY_Y = -650;
    private static final float GRAVITY = 600;
    private static final float AVATAR_SIZE = 50f;

    public static final float MAX_ENERGY = 100f;
    private static final float ENERGY_GAIN_RATE = 3f; // per second
    private static final float ENERGY_LOSS_MOVE = 5f;  // per second
    private static final float ENERGY_LOSS_JUMP = 10f;



    // Animation file name arrays
    private static final String[] IDLE_FRAMES = {
            "idle_0.png", "idle_1.png", "idle_2.png", "idle_3.png"
    };
    private static final String[] RUN_FRAMES = {
            "run_0.png", "run_1.png", "run_2.png", "run_3.png", "run_4.png", "run_5.png"
    };
    private static final String[] JUMP_FRAMES = {
            "jump_0.png", "jump_1.png", "jump_2.png", "jump_3.png"
    };

    private float energy = MAX_ENERGY;
    private State currentState = State.IDLE;
    private final UserInputListener inputListener;
    private final ImageReader imageReader;
    private final AnimationRenderable idleRenderable;
    private final AnimationRenderable runRenderable;
    private final AnimationRenderable jumpRenderable;

    public Avatar(Vector2 pos, UserInputListener inputListener, ImageReader imageReader) {
        super(pos, Vector2.ONES.mult(AVATAR_SIZE), imageReader.readImage(IDLE_FRAMES[0], true));
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);

        this.imageReader = imageReader;
        this.inputListener = inputListener;
        idleRenderable = new AnimationRenderable(loadFrames(IDLE_FRAMES), 0.3f);
        runRenderable = new AnimationRenderable(loadFrames(RUN_FRAMES), 0.1f);
        jumpRenderable = new AnimationRenderable(loadFrames(JUMP_FRAMES), 0.2f);

        renderer().setRenderable(idleRenderable);
    }

    // Helper to load frames from file names
    private Renderable[] loadFrames(String[] files) {
        Renderable[] frames = new Renderable[files.length];
        for (int i = 0; i < files.length; i++) {
            frames[i] = imageReader.readImage(files[i], true);
        }
        return frames;
    }

    // In Avatar.java

    private enum State { IDLE, RUNNING_LEFT, RUNNING_RIGHT, JUMPING, FALLING }

    private State chooseState(boolean left, boolean right, boolean space) {
        if (left && !right && energy >= ENERGY_LOSS_MOVE) {
            return State.RUNNING_LEFT;
        }
        if (right && !left && energy >= ENERGY_LOSS_MOVE) {
            return State.RUNNING_RIGHT;
        }
        if (getVelocity().y() < 0) {
            return State.JUMPING;
        }
        if (getVelocity().y() > 0) {
            return State.FALLING;
        }
        return State.IDLE;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        State newState = getState();
        determineVelocity(deltaTime, newState);
        chooseRenderable(newState);
    }

    private void chooseRenderable(State newState) {
        if (newState == currentState) return;
        switch (newState) {
            case JUMPING:
                renderer().setRenderable(jumpRenderable);
                renderer().setIsFlippedHorizontally(false);
                break;
            case FALLING:
            case IDLE:
                renderer().setRenderable(idleRenderable);
                renderer().setIsFlippedHorizontally(false);
                break;
            case RUNNING_LEFT:
                renderer().setRenderable(runRenderable);
                renderer().setIsFlippedHorizontally(true);
                break;
            case RUNNING_RIGHT:
                renderer().setRenderable(runRenderable);
                renderer().setIsFlippedHorizontally(false);
                break;
        }
        currentState = newState;
    }

    private void determineVelocity(float deltaTime, State state) {
        float xVel = 0;
        switch (state) {
            case RUNNING_LEFT:
                xVel = -VELOCITY_X;
                loseEnergy(ENERGY_LOSS_MOVE * deltaTime);
                break;
            case RUNNING_RIGHT:
                xVel = VELOCITY_X;
                loseEnergy(ENERGY_LOSS_MOVE * deltaTime);
                break;
            default:
                xVel = 0;
        }
        transform().setVelocityX(xVel);

        if (state == State.IDLE) {
            gainEnergy(deltaTime);
        }
    }

    private State getState() {
        boolean left = inputListener.isKeyPressed(KeyEvent.VK_LEFT);
        boolean right = inputListener.isKeyPressed(KeyEvent.VK_RIGHT);
        boolean space = inputListener.isKeyPressed(KeyEvent.VK_SPACE);

        if (space && getVelocity().y() == 0 && energy >= ENERGY_LOSS_JUMP) {
            transform().setVelocityY(VELOCITY_Y);
            loseEnergy(ENERGY_LOSS_JUMP);
        }

        State state = chooseState(left, right, space);
        return state;
    }


    public void gainEnergy(float amount) {
        energy = Math.min(MAX_ENERGY, energy + amount);
    }

    private void loseEnergy(float amount) {
        energy = Math.max(0, energy - amount);
    }

    public float getEnergy() {
        return energy;
    }

    public void changeEnergy(float amount) {
        energy = Math.max(0, Math.min(MAX_ENERGY, energy + amount));
    }
}

