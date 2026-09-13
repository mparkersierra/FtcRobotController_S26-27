package org.firstinspires.ftc.teamcode.software;

import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.hardware.RobotHardware;

/**
 * Searches for yellow or alliance-colored balls, centers them, and approaches them.
 *
 * Setup: configure the I2C device as "huskylensTracker", set its protocol to I2C,
 * enable Learn Multiple in COLOR_RECOGNITION mode, and teach yellow as ID 1,
 * red as ID 2, and blue as ID 3 (or change the ID constants below).
 * Mount the camera facing forward and level. Uses RobotHardware's lf/rf/lb/rb
 * motors and its required "odo" sensor.
 *
 * RobotHardware configures the camera's color recognition algorithm.
 * Yellow is always allowed; red/blue must match robot.alliance. If the alliance
 * is EMPTY, only yellow is allowed. Follows the largest allowed detection.
 * Autonomous usage: construct with your existing RobotHardware and MecanumDrive,
 * then call update() each time through your active
 * autonomous loop (about every 50 ms). Each update performs one camera read and
 * drive update; it does not run a loop or sleep. Only this controller should
 * command the drivetrain while following. Call stop() when leaving the follow
 * step and in your OpMode's finally block (or iterative OpMode stop() method).
 * Calling update() again resumes following after stop() or retries after a camera error.
 *
 * Keeps approaching regardless of ball size so the ball can enter the intake
 * beneath the camera. The OpMode decides when collection is complete using
 * hasBall() or the returned state, and calls stop() when finished.
 */
public class HuskyLensBallFollow {
    // Learned color IDs must match the IDs shown on the HuskyLens screen.
    public static final int YELLOW_BALL_ID = 1;
    public static final int RED_BALL_ID = 2;
    public static final int BLUE_BALL_ID = 3;

    // HuskyLens reports positions in a 320 x 240 image.
    public static final double IMAGE_CENTER_X = 160.0;
    public static final double CENTER_TOLERANCE_PIXELS = 10.0;
    public static final double TURN_ONLY_ERROR_PIXELS = 80.0;

    public static final double SEARCH_POWER = 0.18;
    public static final double MAX_FORWARD_POWER = 0.25;
    public static final double MIN_FORWARD_POWER = 0.08;
    public static final double MAX_TURN_POWER = 0.25;
    public static final double MIN_TURN_POWER = 0.08;
    public static final double TURN_KP = 0.003;
    // Change to -1 if a ball on the right makes the robot turn left.
    public static final double TURN_DIRECTION = 1.0;

    public enum State {
        STOPPED, SEARCHING, CENTERING, APPROACHING, CAMERA_ERROR
    }

    private final RobotHardware robot;
    private final MecanumDrive drive;
    private final HuskyLens camera;
    private double searchDirection = 1.0;
    private State state = State.STOPPED;

    public HuskyLensBallFollow(RobotHardware robot, MecanumDrive drive) {
        this.robot = robot;
        this.drive = drive;
        this.camera = robot.huskyLensTracker;
    }

    /**
     * Performs one tracking step. Call only while your autonomous is active.
     * Keeps driving toward an allowed visible ball while centering it. If the ball
     * disappears, returns SEARCHING and spins. The OpMode owns any logic that
     * treats losing sight of the ball as collection and stops following.
     * Each call can move the robot, including the first call after stop().
     */
    public State update() {
        try {
            // A disconnected camera must not be treated as an unseen ball.
            if (!camera.knock()) {
                stop();
                state = State.CAMERA_ERROR;
                return state;
            }

            HuskyLens.Block ball = findBall(camera.blocks());
            double forward = 0.0;
            double turn = 0.0;

            if (ball == null) {
                // Search toward the last side on which the ball was seen.
                turn = searchDirection * SEARCH_POWER * TURN_DIRECTION;
                state = State.SEARCHING;
            } else {
                double error = ball.x - IMAGE_CENTER_X;
                boolean centered = Math.abs(error) <= CENTER_TOLERANCE_PIXELS;
                if (!centered) {
                    searchDirection = Math.signum(error);
                    double magnitude = Range.clip(Math.abs(error) * TURN_KP,
                            MIN_TURN_POWER, MAX_TURN_POWER);
                    turn = Math.signum(error) * magnitude * TURN_DIRECTION;
                }

                if (Math.abs(error) >= TURN_ONLY_ERROR_PIXELS) {
                    state = State.CENTERING;
                } else {
                    // Slow down when off-center, but keep approaching at any ball size.
                    double alignment = 1.0 - Math.abs(error) / TURN_ONLY_ERROR_PIXELS;
                    forward = Range.clip(MAX_FORWARD_POWER * alignment,
                            MIN_FORWARD_POWER, MAX_FORWARD_POWER);
                    state = State.APPROACHING;
                }
            }

            // MecanumDrive expects joystick Y: negative means drive forward.
            drive.update(0.0, -forward, turn, 0.0);
            return state;
        } catch (RuntimeException error) {
            stop();
            state = State.CAMERA_ERROR;
            throw error;
        }
    }

    /** Stops motors immediately. Call update() again only when ready to resume. */
    public void stop() {
        state = State.STOPPED;
        drive.stopMotors();
    }

    public State getState() {
        return state;
    }

    /** Whether the latest update saw an allowed ball. False after stop() or a camera error. */
    public boolean hasBall() {
        return state == State.APPROACHING || state == State.CENTERING;
    }

    private HuskyLens.Block findBall(HuskyLens.Block[] blocks) {
        HuskyLens.Block largest = null;
        for (HuskyLens.Block block : blocks) {
            // Ignore opposing colors, unknown IDs, and incomplete detections.
            if (block == null || !isAllowedColor(block.id) || block.width <= 0 || block.height <= 0) {
                continue;
            }
            if (largest == null || block.width * block.height > largest.width * largest.height) {
                largest = block;
            }
        }
        return largest;
    }

    private boolean isAllowedColor(int id) {
        return id == YELLOW_BALL_ID
                || (id == RED_BALL_ID && robot.alliance == RobotHardware.Alliance.RED)
                || (id == BLUE_BALL_ID && robot.alliance == RobotHardware.Alliance.BLUE);
    }
}
