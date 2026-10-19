package org.firstinspires.ftc.teamcode.opMode.autonomous;

import com.pedropathing.api.Paths;
import com.pedropathing.api.PoseFactory;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import org.firstinspires.ftc.teamcode.hardware.RobotHardware;
import org.firstinspires.ftc.teamcode.pedro.Constants;

@Autonomous(name = "Straight Line")
public class firstPedro extends OpMode {
    private RobotHardware robot;
    private Follower follower;

    private final PoseFactory poseFactory = PoseFactory.degrees();
    private final Pose startPose = poseFactory.of(0, 0, 0);

    private final Pose finalPose = poseFactory.of(10, 0, 0);
    private final Path move = Paths.line(startPose, finalPose).linear(startPose, finalPose);

    @Override
    public void init() {
        follower = Constants.create(hardwareMap);
        follower.setPose(startPose);
        follower.update();
    }
    @Override
    public void start() {
        follower.follow(move);
    }

    @Override
    public void loop() {
        follower.update();
        if (!follower.isBusy()) {
            requestOpModeStop();
        }
    }
}
