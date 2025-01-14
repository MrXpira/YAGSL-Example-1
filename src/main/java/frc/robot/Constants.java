// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;


import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.geometry.Translation3d;
import edu.wpi.first.math.util.Units;
import swervelib.math.Matter;
import swervelib.parser.PIDFConfig;
import webblib.util.HolonomicPose2d;
import webblib.util.RectanglePoseArea;
import webblib.util.chargedup.ScoringArea;

/**
 * The Constants class provides a convenient place for teams to hold robot-wide numerical or boolean constants. This
 * class should not be used for any other purpose. All constants should be declared globally (i.e. public static). Do
 * not put anything functional in this class.
 *
 * <p>It is advised to statically import this class (or one of its inner classes) wherever the
 * constants are needed, to reduce verbosity.
 */
public final class Constants
{
  public static final double ROBOT_MASS = 34;
  public static final Matter CHASSIS    = new Matter(new Translation3d(0, 0, Units.inchesToMeters(8)), ROBOT_MASS);
  public static final double LOOP_TIME  = 0.13; //s, 20ms + 110ms sprk max velocity lag
  public static final String CANBUS = "Canivore";

  public static final class ArmConstants {
    public static final int ARM_MAIN_MOTOR = 13;
    public static final int ARM_FOLLOWER_MOTOR = 14;

    public static final int kTimeoutMs = 0;
    public static final int kPIDLoopIdx = 0;

    /* Sysid values divided by 12 to convert from voltage */
    public static final double armkG = (.45);
    public static final double armkS = (0);
    public static final double openLoopRamp = 0.25;
    public static final double motionCruiseVelocity = 4000;
    public static final double motionAcceleration = 3000;
    public static final double kA = 0;
    public static final double kV = 0;
    public static final double armkP = .15; //.5
    public static final double armkI = 0;
    public static final double armkD = 9.44; //5
    public static final double armkF = 0;

    public static final double kArmEncoderDistPerPulse = 2.0 * Math.PI / 2048;

    public static final double shooterArmPeakCurrentDuration = 1;
    public static final double shooterArmPeakCurrentLimit = 55;
    public static final double shooterArmContinuousCurrentLimit = 40;
    public static final boolean shooterArmEnableCurrentLimit = true;
    


    public static final double MidPosition = 0;
    public static final double restPosition = 0;
    public static final double lowPosition = 0;
    public static final double highPosition = 0;
    public static final double intakePosition = 19500 ;
    public static final double cannonPosition = 7000;
    public static final int ArmGearRatio = 32;
    
  }
  public static final class ShooterConstants {
    public static final int SHOOTER_TOP_MOTOR = 15;
    public static final int SHOOTER_BOTTOM_MOTOR = 16;

    public static final double highGoalVelocityTopMotor = .27;
    public static final double highGoalVelocityBottomMotor = .31;

    public static final double midGoalVelocityBottomMotor = .18;
    public static final double midGoalVelocityTopMotor = .18;

    public static final double bottomGoalVelocityTopMotor = .14;
    public static final double bottomGoalVelocityBottomMotor = .05;
    
    public static final double cannonGoalVelocityTopMotor = .55;
    public static final double cannonGoalVelocityBottomMotor = .45;

    public static final double intakeVelocity = .3;
    public static final int currentThreshold = 25;
    public static final double shootWaitTime = 1;
    

  }
  public static final class Auton
  {  
    /* Balance PID Values */
    public static final PIDController balancePID = new PIDController(.048, 0.0001, 0.01);

    /* Pathplanner */
    public static final PIDFConfig xAutoPID     = new PIDFConfig(0.7, 0, 0);
    public static final PIDFConfig yAutoPID     = new PIDFConfig(0.7, 0, 0);
    public static final PIDFConfig angleAutoPID = new PIDFConfig(0.4, 0, 0.01);

    public static final double MAX_SPEED        = 4;
    public static final double MAX_ACCELERATION = 2;

    public static final double lineUpMid = 1.73;
    public static final List<ScoringArea> scoreAreaList =
        new ArrayList<ScoringArea>() {
          {
            add(
                new ScoringArea(
                    new RectanglePoseArea(
                        new Translation2d(1.23, 3.53), new Translation2d(2.86, 5.33)),
                    // diagonal y's should not overlap
                    new HolonomicPose2d(new Pose2d(lineUpMid, 4.95, new Rotation2d(Math.PI)), new Rotation2d()),
                    new HolonomicPose2d(new Pose2d(lineUpMid, 4.40, new Rotation2d(Math.PI)), new Rotation2d()),
                    new HolonomicPose2d(
                        new Pose2d(lineUpMid, 3.84, new Rotation2d(Math.PI)), new Rotation2d())));
            add(
                new ScoringArea(
                    new RectanglePoseArea(
                        new Translation2d(1.23, 1.90), new Translation2d(2.92, 3.52)),
                    new HolonomicPose2d(new Pose2d(lineUpMid, 3.30, new Rotation2d(Math.PI)), new Rotation2d()),
                    new HolonomicPose2d(new Pose2d(lineUpMid, 2.72, new Rotation2d(Math.PI)), new Rotation2d()),
                    new HolonomicPose2d(
                        new Pose2d(lineUpMid, 2.19, new Rotation2d(Math.PI)), new Rotation2d())));
            add(
                new ScoringArea(
                    new RectanglePoseArea(
                        new Translation2d(1.23, 0.0), new Translation2d(2.89, 1.89)),
                    new HolonomicPose2d(new Pose2d(lineUpMid, 1.61, new Rotation2d(Math.PI)), new Rotation2d()),
                    new HolonomicPose2d(new Pose2d(lineUpMid, 1.03, new Rotation2d(Math.PI)), new Rotation2d()),
                    new HolonomicPose2d(
                        new Pose2d(lineUpMid, 0.55, new Rotation2d(Math.PI)), new Rotation2d())));
          }
        };

    public static final double maxSpeedMPS = 5;

    public static final double maxAccelerationMPS = 3;

	  public static final double moveOntoChargeStationSpeed = 3;
  }

  public static final class Drivebase
  {

    // Hold time on motor brakes when disabled
    public static final double WHEEL_LOCK_TIME = 10; // seconds
  }

  public static class OperatorConstants
  {

    // Joystick Deadband
    public static final double LEFT_X_DEADBAND = 0.01;
    public static final double LEFT_Y_DEADBAND = 0.01;
  }

  public static class LEDConstants {

    public static final int PWMPORT = 0;
    public static final int LENGTH = 0;

  }

}
