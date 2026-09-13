package org.firstinspires.ftc.teamcode.hardware;

import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.dfrobot.HuskyLens;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

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
    public DcMotor lf, rf, lb, rb;

    // Sensors
    public GoBildaPinpointDriver odo;
    public HuskyLens huskyLensTracker;

    public RobotHardware(HardwareMap hw) {
        this.hw = hw;

        alliance = convertToAlliance(TransferData.getAlliance(hw));

        initMotors();
        initSensors();
    }

    private void initMotors() {
        lf = hw.get(DcMotor.class, "lf");
        rf = hw.get(DcMotor.class, "rf");
        lb = hw.get(DcMotor.class, "lb");
        rb = hw.get(DcMotor.class, "rb");

        lf.setDirection(DcMotor.Direction.FORWARD);
        rf.setDirection(DcMotor.Direction.REVERSE);
        lb.setDirection(DcMotor.Direction.REVERSE);
        rb.setDirection(DcMotor.Direction.FORWARD);

        lf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rf.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        lb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        rb.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    private void initSensors() {
        odo = hw.get(GoBildaPinpointDriver.class, "odo");
        huskyLensTracker = hw.get(HuskyLens.class, "huskylensTracker");
        huskyLensTracker.selectAlgorithm(HuskyLens.Algorithm.OBJECT_TRACKING);
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
