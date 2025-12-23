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
 *
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
	private static final float GROUND_EPSILON = 0.05f;
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
	private static final double IDLE_TIME_BETWEEN_FRAMES = 0.3f;
	private static final double RUN_TIME_BETWEEN_FRAMES = 0.1f;
	private static final double JUMP_TIME_BETWEEN_FRAMES = 0.2f;
	private static final String GROUND_TAG = "ground";
	private boolean canDoubleJump = false;
	private float energy = MAX_ENERGY;
	private final UserInputListener inputListener;
	private final ImageReader imageReader;
	private final AnimationRenderable idleRenderable;
	private final AnimationRenderable runRenderable;
	private final AnimationRenderable jumpRenderable;
	private boolean facingLeft;
	private boolean isMovingHorizontally;
	private int groundContacts = 0;
	/**
	 * Constructs an Avatar object at the specified position with the given input listener and image reader.
	 * Initializes the avatar's physics, animations, and rendering.
	 *
	 * @param topLeftCorner The initial position of the avatar in the game world.
	 * @param inputListener The listener for user input to control the avatar.
	 * @param imageReader   The image reader to load avatar animations.
	 */
	public Avatar(Vector2 topLeftCorner, UserInputListener inputListener, ImageReader imageReader) {
		super(topLeftCorner, Vector2.ONES.mult(AVATAR_SIZE), imageReader.readImage(IDLE_FRAMES[0], true));
		physics().preventIntersectionsFromDirection(Vector2.ZERO);
		transform().setAccelerationY(GRAVITY);

		this.imageReader = imageReader;
		this.inputListener = inputListener;
		idleRenderable = new AnimationRenderable(loadFrames(IDLE_FRAMES),
				IDLE_TIME_BETWEEN_FRAMES);
		runRenderable = new AnimationRenderable(loadFrames(RUN_FRAMES),
				RUN_TIME_BETWEEN_FRAMES);
		jumpRenderable = new AnimationRenderable(loadFrames(JUMP_FRAMES),
				JUMP_TIME_BETWEEN_FRAMES);

		renderer().setRenderable(idleRenderable);
	}

	/**
	 * Handles collision events with other game objects.
	 * @param other    The other game object involved in the collision.
	 * @param collision Information about the collision event.
	 */
	@Override
	public void onCollisionEnter(GameObject other, Collision collision) {
		super.onCollisionEnter(other, collision);
		if (other.getTag().equals(GROUND_TAG)) {
			groundContacts++;
		}
	}

	/**
	 * Handles the event when the avatar exits a collision with another game object.
	 * @param other The other game object involved in the collision.
	 */
	@Override
	public void onCollisionExit(GameObject other) {
		super.onCollisionExit(other);
		if (other.getTag().equals(GROUND_TAG)) {
			groundContacts = Math.max(0, groundContacts - 1);
		}
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
	private enum State {IDLE, RUNNING_LEFT, RUNNING_RIGHT, JUMPING}

	/**
	 * Updates the avatar's state, velocity, and animation based on user input and elapsed time.
	 * This method is called once per frame to ensure smooth movement and energy management.
	 *
	 * @param deltaTime The time elapsed since the last update call, in seconds.
	 */
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);

		boolean left = inputListener.isKeyPressed(KeyEvent.VK_LEFT);
		boolean right = inputListener.isKeyPressed(KeyEvent.VK_RIGHT);
		boolean space = inputListener.isKeyPressed(KeyEvent.VK_SPACE);
		applyVelocityEnergy(deltaTime, left, right, space);

		State newState = chooseCurrentState(left, right);
		chooseRenderable(newState);
	}
	/*
	 * Determines the current state of the avatar based on its velocity and user input.
	 */
	private State chooseCurrentState(boolean left, boolean right) {
		boolean grounded = getVelocity().y() == 0;
		if (!grounded) {
			return State.JUMPING;
		}
		if (isMovingHorizontally) {
			if (left && !right) {
				return State.RUNNING_LEFT;
			}
			if (right && !left) {
				return State.RUNNING_RIGHT;
			}
		}
		return State.IDLE;
	}
	/*
	 * Applies velocity changes and manages energy consumption based on user input.
	 */
	/*
	 * Applies velocity changes and manages energy consumption based on user input.
	 */
	private void applyVelocityEnergy(float deltaTime, boolean left, boolean right, boolean spacePressedNow) {
		boolean grounded = Math.abs(getVelocity().y()) < GROUND_EPSILON;
		if (grounded) {
			canDoubleJump = false;
		}

		float xVel = calcHorizontalVelocity(left, right);
		xVel = applyGroundEnergy(deltaTime, grounded, xVel);
		updateHorizontalStateApply(xVel);

		handleJump(spacePressedNow, grounded);
	}
	/* Calculates the horizontal velocity
	 * based on user input for left and right movement.
	 */
	private float calcHorizontalVelocity(boolean left, boolean right) {
		if (left && !right) {
			return -VELOCITY_X;
		}
		if (right && !left) {
			return VELOCITY_X;
		}
		return 0f;
	}
	/* Applies energy changes based on whether the avatar is grounded and its horizontal velocity.
	 */
	private float applyGroundEnergy(float deltaTime, boolean grounded, float xVel) {
		if (!grounded) {
			return xVel; // no energy change mid-air
		}

		if (xVel != 0f) {
			float cost = ENERGY_LOSS_MOVE * deltaTime * ENERGY_LOAD_SPEED_FACTOR;
			if (energy >= cost) {
				loseEnergy(cost);
			} else {
				xVel = 0f; // not enough energy to move
			}
		} else if (groundContacts > 0) {
			gainEnergy(ENERGY_GAIN_RATE * deltaTime * ENERGY_LOAD_SPEED_FACTOR);
		}

		return xVel;
	}
	/* Updates the horizontal movement state and applies the calculated horizontal velocity.
	 */
	private void updateHorizontalStateApply(float xVel) {
		isMovingHorizontally = xVel != 0f;

		if (xVel < 0) {
			facingLeft = true;
		} else if (xVel > 0) {
			facingLeft = false;
		}

		transform().setVelocityX(xVel);
	}
	/* Handles jump logic, including single and double jumps, based on user input and energy levels.
	 */
	private void handleJump(boolean spacePressedNow, boolean grounded) {
		if (!spacePressedNow) {
			return;
		}

		if (grounded && energy >= ENERGY_LOSS_JUMP) {
			transform().setVelocityY(VELOCITY_Y);
			loseEnergy(ENERGY_LOSS_JUMP);
			canDoubleJump = true;
			return;
		}

		boolean falling = getVelocity().y() > 0;
		if (!grounded && canDoubleJump && falling && energy >= ENERGY_DOUBLE_LOSS_JUMP) {
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

	/**
	 * Increases the avatar's energy by the specified amount, up to the maximum energy limit.
	 *
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

	/**
	 * Returns the current energy level of the avatar.
	 *
	 * @return The current energy level.
	 */
	public float getEnergy() {
		return energy;
	}
}