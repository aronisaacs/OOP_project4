package pepse.world.avatar;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

/**
 * Represents the player's avatar in the game, capable of moving, jumping, and managing energy levels.
 * The avatar's state changes based on user input and its current velocity,
 * affecting its animation and energy consumption.
 * @author ron.stein
 */
public class Avatar extends GameObject {

    private static final float VELOCITY_X = 400;
    private static final float VELOCITY_Y = -650;
    private static final float GRAVITY = 600;
    private static final float AVATAR_SIZE = 50f;
    private static final float MAX_ENERGY = 100f;
    private static final float ENERGY_GAIN_RATE = 1f; // per Speed Factor
    private static final float ENERGY_LOSS_MOVE = 2f;  // per Speed Factor
    private static final float ENERGY_LOAD_SPEED_FACTOR = 5f;
    private static final float ENERGY_LOSS_JUMP = 20f;
    private static final float ENERGY_DOUBLE_LOSS_JUMP = 50f;
    // Animation file name arrays
    private static final String[] IDLE_FRAMES = {
            "assets/idle_0.png", "assets/idle_1.png", "assets/idle_2.png", "assets/idle_3.png"
    };
    private static final String[] RUN_FRAMES = {
            "assets/run_0.png", "assets/run_1.png", "assets/run_2.png",
            "assets/run_3.png", "assets/run_4.png", "assets/run_5.png"
    };
    private static final String[] JUMP_FRAMES = {
            "assets/jump_0.png", "assets/jump_1.png", "assets/jump_2.png", "assets/jump_3.png"
    };
    private boolean canDoubleJump = false;
    private float energy = MAX_ENERGY;
    private final UserInputListener inputListener;
    private final ImageReader imageReader;
    private final AnimationRenderable idleRenderable;
    private final AnimationRenderable runRenderable;
    private final AnimationRenderable jumpRenderable;
    private boolean facingLeft;
    private boolean isMovingHorizontally;
    private boolean isCollidedWithGround = false;

    /**
     * Constructs an Avatar object at the specified position with the given input listener and image reader.
     * Initializes the avatar's physics, animations, and rendering.
     * @param topLeftCorner The initial position of the avatar in the game world.
     * @param inputListener The listener for user input to control the avatar.
     * @param imageReader The image reader to load avatar animations.
     */
    public Avatar(Vector2 topLeftCorner, UserInputListener inputListener, ImageReader imageReader) {
        super(topLeftCorner, Vector2.ONES.mult(AVATAR_SIZE), imageReader.readImage(IDLE_FRAMES[0], true));
        physics().preventIntersectionsFromDirection(Vector2.ZERO);
        transform().setAccelerationY(GRAVITY);

        this.imageReader = imageReader;
        this.inputListener = inputListener;
        idleRenderable = new AnimationRenderable(loadFrames(IDLE_FRAMES), 0.3f);
        runRenderable = new AnimationRenderable(loadFrames(RUN_FRAMES), 0.1f);
        jumpRenderable = new AnimationRenderable(loadFrames(JUMP_FRAMES), 0.2f);

        renderer().setRenderable(idleRenderable);
    }
    @Override
    public void onCollisionEnter(GameObject other, Collision collision) {
        super.onCollisionEnter(other, collision);
        if(other.getTag().equals("ground")) {
            isCollidedWithGround = true;
        }
    }
    @Override
    public void onCollisionExit(GameObject other) {
        super.onCollisionExit(other);
        isCollidedWithGround = false;
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
    private enum State { IDLE, RUNNING_LEFT, RUNNING_RIGHT, JUMPING}

    /**
     * Updates the avatar's state, velocity, and animation based on user input and elapsed time.
     * This method is called once per frame to ensure smooth movement and energy management.
     * @param deltaTime The time elapsed since the last update call, in seconds.
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);

        boolean left = inputListener.isKeyPressed(KeyEvent.VK_LEFT);
        boolean right = inputListener.isKeyPressed(KeyEvent.VK_RIGHT);
        boolean space = inputListener.isKeyPressed(KeyEvent.VK_SPACE);
        applyVelocityAndEnergy(deltaTime, left, right, space);

//        boolean spacePressedNow = space && !spacePressed;
//        spacePressed = space;
        State newState = chooseCurrentState(left, right);
        chooseRenderable(newState);
    }

    private State chooseCurrentState(boolean left, boolean right) {
        boolean grounded = getVelocity().y() == 0;
        if (!grounded) {
            return State.JUMPING;
        }
        if (isMovingHorizontally) {
            if (left && !right) {return State.RUNNING_LEFT;}
            if (right && !left) {return State.RUNNING_RIGHT;}
        }
        return State.IDLE;
    }

    private void applyVelocityAndEnergy(float deltaTime, boolean left, boolean right,
                                        boolean spacePressedNow) {
        boolean grounded = getVelocity().y() == 0;

        //reset double jump when grounded
        if(grounded) {
            canDoubleJump = false;
        }
        //horizontal movement
        float xVel = 0f;
        if (left && !right) xVel = -VELOCITY_X;
        if (right && !left) xVel = VELOCITY_X;
        //Energy consumption/gain
        if(grounded){
            if(xVel != 0f) {
                float cost = ENERGY_LOSS_MOVE * deltaTime * ENERGY_LOAD_SPEED_FACTOR;
                if(energy >= cost) {
                    loseEnergy(cost);
                } else {
                    xVel = 0f;
                }
            } else {
                if(isCollidedWithGround) {
                    gainEnergy(ENERGY_GAIN_RATE * deltaTime * ENERGY_LOAD_SPEED_FACTOR);
                }
            }
        }
        //update facing direction booleans
        isMovingHorizontally = xVel != 0f;
        if (xVel < 0) facingLeft = true;
        else if (xVel > 0) facingLeft = false;
        transform().setVelocityX(xVel);

        //jumping
        if(!spacePressedNow) {return;}
        if (grounded && energy >= ENERGY_LOSS_JUMP) {
            transform().setVelocityY(VELOCITY_Y);
            loseEnergy(ENERGY_LOSS_JUMP);
            canDoubleJump = true;
            return;
        }
        if (!grounded && canDoubleJump && getVelocity().y() > 0 && energy >= ENERGY_DOUBLE_LOSS_JUMP) {
            transform().setVelocityY(VELOCITY_Y);
            loseEnergy(ENERGY_DOUBLE_LOSS_JUMP);
            canDoubleJump = false;
        }
        }


    /* Chooses and sets the appropriate renderable animation based on the avatar's current state.
     * @param newState The new state of the avatar to determine the correct animation.
     */
    private void chooseRenderable(State newState) {
        switch (newState) {
            case JUMPING:
                renderer().setRenderable(jumpRenderable);
                renderer().setIsFlippedHorizontally(facingLeft);
                break;
            case IDLE:
                renderer().setRenderable(idleRenderable);
                renderer().setIsFlippedHorizontally(facingLeft);
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