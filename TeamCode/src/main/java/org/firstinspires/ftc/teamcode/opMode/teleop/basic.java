package org.firstinspires.ftc.teamcode.opMode.teleop;

import com.bylazar.configurables.annotations.Configurable;
import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.hardware.RobotHardware;
import org.firstinspires.ftc.teamcode.software.MecanumDrive;

@Configurable
@TeleOp(name = "First Comp")
public class basic extends OpMode {

    private RobotHardware robot;
    private MecanumDrive drive;

    private TelemetryManager telemetryM;

    private static double intakePower = 0.5;
    private static double launcherVelocity = 1280;

    private static double feedpower = 0.5;

    private boolean intakeRunning = false;
    private boolean launcherRunning = false;
    private boolean feederRunning = false;

    private boolean crossPressed1 = false;
    private boolean circlePressed1 = false;
    private boolean squarePressed1 = false;

    private boolean fieldCentric = false;

    @Override
    public void init() {
        // Initialize robot hardware
        robot = new RobotHardware(hardwareMap);

        // Get the driving class
        drive = new MecanumDrive(robot);

        telemetryM = PanelsTelemetry.INSTANCE.getTelemetry();

        telemetry.addData("Status", "Initialized - Panels Connected");
    }

    @Override
    public void init_loop() {
        // Toggle field-centric mode
        if (gamepad1.circle && !circlePressed1) {
            fieldCentric = !fieldCentric;
            circlePressed1 = true;
        } else if (!gamepad1.circle) {
            circlePressed1 = false;
        }

        // Toggle alliance (red/blue)
        if (gamepad1.cross && !crossPressed1) {
            robot.alliance = (robot.isRedAlliance())
                    ? RobotHardware.Alliance.BLUE
                    : RobotHardware.Alliance.RED;
            crossPressed1 = true;
        } else if (!gamepad1.b) { crossPressed1 = false; }

        // Toggle two player mode
//        if (gamepad2.a && !aMenuPressed2) {
//            twoPlayers = !twoPlayers;
//            aMenuPressed2 = true;
//        } else if (!gamepad2.a) {
//            aMenuPressed2 = false;
//        }

        telemetry.addData("Menu: Field Centric Mode", fieldCentric ? "ON" : "OFF");
        telemetry.addLine("Press Circle to toggle");

        telemetry.addData("Menu: Alliance", robot.isRedAlliance() ? "RED" : "BLUE");
        telemetry.addLine("Press Cross to toggle");

//        telemetry.addData("Menu: Two Player Mode", twoPlayers ? "YES" : "NO");
//        telemetry.addLine("Gamepad2: Press A to toggle");
        telemetry.update();
    }

    @Override
    public void start() {
        drive.setFieldCentric(fieldCentric);
    }

    @Override
    public void loop() {

        drive.update(
                gamepad1.left_stick_x,
                gamepad1.left_stick_y,
                gamepad1.right_stick_x,
                gamepad1.right_stick_y
        );
        if (gamepad1.dpad_left) drive.resetOdometry();
        if (gamepad1.left_bumper) stopAll();

        if (gamepad1.cross && !crossPressed1) {
            intakeRunning = !intakeRunning;
            updateIntake();
        }

        if (gamepad1.circle && !circlePressed1) {
            launcherRunning = !launcherRunning;
            updateLauncher();
        }

        if (gamepad1.right_bumper) {
            robot.feedI.setPower(feedpower);
            robot.feedL.setPower(feedpower);
        } else {
            robot.feedI.setPower(0);
            robot.feedL.setPower(0);
        }

        circlePressed1 = gamepad1.circle;
        crossPressed1 = gamepad1.cross;
        squarePressed1 = gamepad1.square;

        telemetry.addData("Feeder Running", feederRunning);
        telemetry.addData("feederpow", robot.feedI.getPower());
        telemetry.addData("Intake Running", intakeRunning);
        telemetry.addData("Launcher Running", launcherRunning);
        telemetry.update();
    }

    private void stopAll() {
        launcherRunning = false;
        updateLauncher();
        intakeRunning = false;
        updateIntake();
        feederRunning = false;
        updateFeeder();
    }
    private void updateIntake() {
        if (intakeRunning) robot.intake.setPower(intakePower);
        else robot.intake.setPower(0);
    }
    private void updateLauncher() {
        if (launcherRunning) robot.launcher.setVelocity(launcherVelocity);
        else robot.launcher.setPower(0);
    }
    private void updateFeeder() {



    }
}
