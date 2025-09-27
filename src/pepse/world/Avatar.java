package pepse.world;

import danogl.GameObject;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import java.awt.*;
import java.awt.event.KeyEvent;

/**
 * Represents the player's avatar in the game, capable of moving, jumping, and managing energy levels.
 * The avatar's state changes based on user input and its current velocity, affecting its animation and energy consumption.
 * @author Aron Isaacs
 */
public class Avatar extends GameObject {

    private static final float VELOCITY_X = 400;
    private static final float VELOCITY_Y = -650;
    private static final float GRAVITY = 600;
    public static final float AVATAR_SIZE = 50f;

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
    private final UserInputListener inputListener;
    private final ImageReader imageReader;
    private final AnimationRenderable idleRenderable;
    private final AnimationRenderable runRenderable;
    private final AnimationRenderable jumpRenderable;
    private State currentState = State.IDLE;

    /**
     * Constructs an Avatar object at the specified position with the given input listener and image reader.
     * Initializes the avatar's physics, animations, and rendering.
     * @param pos The initial position of the avatar in the game world.
     * @param inputListener The listener for user input to control the avatar.
     * @param imageReader The image reader to load avatar animations.
     */
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

    /* Loads an array of Renderable frames from the specified file names.
     * @param files The array of file names for the animation frames.
     * @return An array of Renderable objects corresponding to the loaded frames.
     */
    private Renderable[] loadFrames(String[] files) {
        Renderable[] frames = new Renderable[files.length];
        for (int i = 0; i < files.length; i++) {
            frames[i] = imageReader.readImage(files[i], true);
        }
        return frames;
    }

    /* Enum representing the different states of the avatar.
     */
    private enum State { IDLE, RUNNING_LEFT, RUNNING_RIGHT, JUMPING, FALLING }

    /* Determines the avatar's state based on user input and energy levels.
     * @param left Whether the left movement key is pressed.
     * @param right Whether the right movement key is pressed.
     * @param space Whether the jump key is pressed.
     * @return The determined State of the avatar.
     */
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

    /**
     * Updates the avatar's state, velocity, and animation based on user input and elapsed time.
     * This method is called once per frame to ensure smooth movement and energy management.
     * @param deltaTime The time elapsed since the last update call, in seconds.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        State newState = getState();
        determineVelocity(deltaTime, newState);
        chooseRenderable(newState);
    }

    /* Chooses and sets the appropriate renderable animation based on the avatar's current state.
     * @param newState The new state of the avatar to determine the correct animation.
     */
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

    /* Determines and sets the avatar's horizontal velocity based on its state and energy levels.
     * Also manages energy consumption and regeneration.
     * @param deltaTime The time elapsed since the last update call, in seconds.
     * @param state The current state of the avatar to determine movement behavior.
     */
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
            gainEnergy(deltaTime * ENERGY_GAIN_RATE);
        }
    }

    /* Determines the avatar's state based on user input and manages jumping behavior and energy consumption.
     * @return The determined State of the avatar.
     */
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

    /** Increases the avatar's energy by the specified amount, up to the maximum energy limit.
     * @param amount The amount of energy to gain.
     */
    public void gainEnergy(float amount) {
        energy = Math.min(MAX_ENERGY, energy + amount);
    }

    /* Decreases the avatar's energy by the specified amount, ensuring it does not go below zero.
     * @param amount The amount of energy to lose. currently a private method, but could be made public if
     * needed.
     */
    private void loseEnergy(float amount) {
        energy = Math.max(0, energy - amount);
    }

    /** Returns the current energy level of the avatar.
     * @return The current energy level.
     */
    public float getEnergy() {
        return energy;
    }
}

