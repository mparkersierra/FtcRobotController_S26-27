package org.firstinspires.ftc.teamcode.software;

import com.bylazar.configurables.annotations.Configurable;

@Configurable 
public class Physics {

    // Fixed launcher geometry. Set these to the robot's measured values.
    public static final double LAUNCH_HEIGHT = 10.0; // inches
    public static final double TARGET_HEIGHT = 30.0; // inches
    public static final double LAUNCH_ANGLE = Math.toRadians(77.0); // above horizontal

    public static final double GRAVITY = 386.0886; // inches / second squared

    /**
     * Calculates launch speed for a horizontal distance to the goal using the
     * fixed launch angle and heights above. Assumes no air resistance.
     *
     * @param distanceToTarget horizontal distance from the launcher to the goal,
     *                         in inches (not the line-of-sight distance)
     * @return projectile launch speed in inches per second, or -1 if the distance
     *         is invalid or no finite launch speed can reach the goal
     */
    public static double calculateLaunchSpeed(double distanceToTarget) {
        if (Double.isNaN(distanceToTarget) || Double.isInfinite(distanceToTarget)
                || distanceToTarget <= 0.0) {
            return -1;
        }

        double heightDifference = TARGET_HEIGHT - LAUNCH_HEIGHT;
        double gravityDrop = distanceToTarget * Math.tan(LAUNCH_ANGLE) - heightDifference;

        // Gravity can only bend the trajectory below the straight launch line.
        if (gravityDrop <= 0.0) {
            return -1;
        }

        // From deltaH = d*tan(angle) - g*d^2 / (2*v^2*cos(angle)^2):
        // v = d/cos(angle) * sqrt(g / (2*(d*tan(angle) - deltaH))).
        double speed = distanceToTarget / Math.cos(LAUNCH_ANGLE)
                * Math.sqrt(GRAVITY / (2.0 * gravityDrop));

        if (Double.isNaN(speed) || Double.isInfinite(speed) || speed <= 0.0) {
            return -1;
        }

        return speed;
    }
}
