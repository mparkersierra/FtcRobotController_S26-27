package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.software.TransferData;

public class RobotHardware {

    // Alliance enum
    public enum Alliance {
        RED,
        BLUE,
        EMPTY
    }
    // Current Alliance
    public Alliance alliance = Alliance.RED;

    private HardwareMap hw;

    // Motors
    public DcMotor lf, rf, lb, rb, intake;
    public DcMotorEx launcher;

    public CRServo feedI, feedL;

    // Sensors
    public GoBildaPinpointDriver odo;
    public HuskyLens huskyLensTracker;

    public RobotHardware(HardwareMap hw) {
        this.hw = hw;

        alliance = convertToAlliance(TransferData.getAlliance(hw));

        initMotors();
        initSensors();
        initServos();
    }

    private void initMotors() {
        lf = hw.get(DcMotor.class, "lf");
        rf = hw.get(DcMotor.class, "rf");
        lb = hw.get(DcMotor.class, "lb");
        rb = hw.get(DcMotor.class, "rb");

        lf.setDirection(DcMotor.Direction.REVERSE);
        rf.setDirection(DcMotor.Direction.FORWARD);
        lb.setDirection(DcMotor.Direction.FORWARD);
        rb.setDirection(DcMotor.Direction.REVERSE);

        lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        intake = hw.get(DcMotor.class, "intake");
        intake.setDirection(DcMotorSimple.Direction.REVERSE);

        launcher = hw.get(DcMotorEx.class, "launcher");
    }

    private void initServos() {
        feedI = hw.get(CRServo.class, "feedI");
        feedI.setDirection(CRServo.Direction.REVERSE);
        feedL = hw.get(CRServo.class, "feedL");
    }

    private void initSensors() {
        odo = hw.get(GoBildaPinpointDriver.class, "odo");
        odo.setOffsets(-1.05680886216051,-2.6479120329609067, DistanceUnit.INCH);
        odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.REVERSED, GoBildaPinpointDriver.EncoderDirection.FORWARD);

        huskyLensTracker = hw.get(HuskyLens.class, "huskylensTracker");
        huskyLensTracker.selectAlgorithm(HuskyLens.Algorithm.COLOR_RECOGNITION);
    }



    public Alliance convertToAlliance(int rawA) {
        if (rawA == 0) return Alliance.RED;
        if (rawA == 1) return Alliance.BLUE;
        return Alliance.EMPTY;
    }
    public boolean isRedAlliance() {
        return alliance.ordinal() == RobotHardware.Alliance.RED.ordinal();
    }
}
