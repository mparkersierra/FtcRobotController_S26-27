package org.firstinspires.ftc.teamcode.tuning;


import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotor;
import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;

@TeleOp(name = "Test Launcher", group = "Tests")
public class TestLauncher extends OpMode {

    private DcMotorEx launcher;

    double power = 1000;
    final double STEP = 10;
    final double MAX_POWER = 1450;

    boolean lastUp = false;
    boolean lastDown = false;
    @Override
    public void init() {
        launcher = hardwareMap.get(DcMotorEx.class, "launcher");

        // Use power mode so the motor is controlled by power.
        launcher.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        launcher.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }

    @Override
    public void loop() {
        // Increase power
        if (gamepad1.dpad_up && !lastUp) {
            power = Math.min(MAX_POWER, power + STEP);
        }

        // Decrease power
        if (gamepad1.dpad_down && !lastDown) {
            power = Math.max(0.0, power - STEP);
        }

        lastUp = gamepad1.dpad_up;
        lastDown = gamepad1.dpad_down;

        // Apply power
        launcher.setVelocity(power);

        // Read actual motor velocity
        double velocityTicksPerSecond =
                launcher.getVelocity();

        double velocityRPM = launcher.getVelocity(
                AngleUnit.DEGREES
        ) / 6;

        telemetry.addData("Power", "%.2f", power);
        telemetry.addData(
                "Velocity", "%.1f ticks/sec",
                velocityTicksPerSecond
        );
        telemetry.addData(
                "Velocity", "%.1f RPM (motor shaft)",
                velocityRPM
        );
        telemetry.update();
    }
}