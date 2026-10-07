package org.firstinspires.ftc.teamcode.tests;

import org.firstinspires.ftc.teamcode.hardware.RobotHardware;
import org.firstinspires.ftc.teamcode.software.MecanumDrive;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name = "Test Intake", group = "Examples")
public class TestIntake extends LinearOpMode {

    @Override
    public void runOpMode() {
        // Initialize hardware and mecanum drive
        RobotHardware robot = new RobotHardware(hardwareMap);
        MecanumDrive drive = new MecanumDrive(robot);

        double intakePower = 0.5;
        boolean intakeOn = false;

        boolean lastA = false;
        boolean lastDpadUp = false;
        boolean lastDpadDown = false;

        waitForStart();

        while (opModeIsActive()) {

            // Read gamepad inputs
            double leftStickX = gamepad1.left_stick_x;
            double leftStickY = gamepad1.left_stick_y;
            double rightStickX = gamepad1.right_stick_x;
            double rightStickY = gamepad1.right_stick_y;

            // Toggle intake ON/OFF with A
            if (gamepad1.a && !lastA) {
                intakeOn = !intakeOn;
            }
            lastA = gamepad1.a;

            // Increase power with D-pad Up
            if (gamepad1.dpad_up && !lastDpadUp) {
                intakePower += 0.1;
            }
            lastDpadUp = gamepad1.dpad_up;

            // Decrease power with D-pad Down
            if (gamepad1.dpad_down && !lastDpadDown) {
                intakePower -= 0.1;
            }
            lastDpadDown = gamepad1.dpad_down;

            // Keep power between -1 and 1
            intakePower = Math.max(-1.0, Math.min(1.0, intakePower));

            // Set intake power
            robot.intake.setPower(intakeOn ? intakePower : 0);

            telemetry.addData("Intake On", intakeOn);
            telemetry.addData("Intake Power", "%.1f", intakePower);
            telemetry.addData("Actual Power", "%.1f", robot.intake.getPower());

            telemetry.addData("leftx", leftStickX);
            telemetry.addData("lefty", leftStickY);
            telemetry.addData("rightx", rightStickX);
            telemetry.addData("righty", rightStickY);

            telemetry.update();

            // Update mecanum drive
            drive.update(leftStickX, leftStickY, rightStickX, rightStickY);
        }
    }
}