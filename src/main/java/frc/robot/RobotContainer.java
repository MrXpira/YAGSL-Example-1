// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.RepeatCommand;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.AutoMap;
import frc.robot.commands.ShootingArmCommands;
import frc.robot.commands.swervedrive.auto.GoToScoring;
import frc.robot.commands.swervedrive.auto.PathBuilder;
import frc.robot.commands.swervedrive.auto.GoToScoring.POSITION;
import frc.robot.commands.swervedrive.drivebase.TeleopDrive;
import frc.robot.subsystems.Arm;
import frc.robot.subsystems.CANdleSubsystem;
import frc.robot.subsystems.Shooter;
import frc.robot.subsystems.Arm.ArmPosition;
import frc.robot.subsystems.Shooter.ShootSpeed;
import frc.robot.subsystems.swervedrive.SwerveSubsystem;
import swervelib.SwerveDrive;

import java.io.File;

import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.sensors.AbsoluteSensorRange;
import com.pathplanner.lib.PathConstraints;
import com.pathplanner.lib.PathPlanner;

/**
 * This class is where the bulk of the robot should be declared. Since Command-based is a "declarative" paradigm, very
 * little robot logic should actually be handled in the {@link Robot} periodic methods (other than the scheduler calls).
 * Instead, the structure of the robot (including subsystems, commands, and trigger mappings) should be declared here.
 */
public class RobotContainer
{
  private SendableChooser<Command> chooser = new SendableChooser<>();

  // The robot's subsystems and commands are defined here...
  private final SwerveSubsystem drivebase = new SwerveSubsystem(new File(Filesystem.getDeployDirectory(),
                                                                         "swerve/falcon"));
  private final Shooter shooter = new Shooter();
  private final Arm arm = new Arm();
  private final CANdleSubsystem candleSubsystem = new CANdleSubsystem();
  // CommandJoystick rotationController = new CommandJoystick(1);
  // Replace with CommandPS4Controller or CommandJoystick if needed
  //CommandJoystick driverController = new CommandJoystick(1);

  // CommandJoystick driverController   = new CommandJoystick(3);//(OperatorConstants.DRIVER_CONTROLLER_PORT);
  private static final CommandXboxController driverXbox = new CommandXboxController(0);
  private static final CommandXboxController operatorXbox = new CommandXboxController(1);


  private final ShootingArmCommands shootingArmCommands = new ShootingArmCommands(shooter, arm);
  private final AutoMap autoMap = new AutoMap(shootingArmCommands, drivebase);
  private final PathBuilder builder = new PathBuilder(drivebase, autoMap.getEventMap());
  

  private final int translationAxis = XboxController.Axis.kLeftY.value;
  private final int strafeAxis = XboxController.Axis.kLeftX.value;
  private final int rotationAxis = XboxController.Axis.kRightX.value;
  private final DigitalInput resetArmSwitch = new DigitalInput(0); // Limit switch on DIO 0
  private final DigitalInput unlockArmSwitch = new DigitalInput(1); // Limit switch on DIO 1


  private final TeleopDrive closedFieldRel =
  new TeleopDrive(
      drivebase,
      () -> driverXbox.getRawAxis(translationAxis), 
      () -> driverXbox.getRawAxis(strafeAxis), 
      () -> driverXbox.getRawAxis(rotationAxis), 
      () -> driverXbox.leftBumper().getAsBoolean(),
      false,
      false
      );
  /**
   * The container for the robot. Contains subsystems, OI devices, and commands.
   */
  public RobotContainer()
  {
    // Configure the trigger bindings
    configureBindings();
    initializeChooser();

    drivebase.setDefaultCommand(closedFieldRel);

    arm.setDefaultCommand(shootingArmCommands.Rest());
    shooter.setDefaultCommand(shooter.stop());
  }

  private void initializeChooser() {

    chooser.addOption(
        "Default Test",
        builder.getSwerveCommand(
            PathPlanner.loadPathGroup(
                "Test Path", new PathConstraints(Constants.Auton.MAX_SPEED, Constants.Auton.MAX_ACCELERATION))));

    chooser.addOption(
      "Calibration 2 Meter",
      builder.getSwerveCommand(
          PathPlanner.loadPathGroup(
              "Calibration Test", new PathConstraints(Constants.Auton.MAX_SPEED, Constants.Auton.MAX_ACCELERATION))));

    chooser.setDefaultOption(
        "Test - 2", 
        builder.getSwerveCommand(
            PathPlanner.loadPathGroup(
                "SamplePath", new PathConstraints(Constants.Auton.MAX_SPEED, Constants.Auton.MAX_ACCELERATION))));

    chooser.addOption(
      "2 Cube Auto", 
      builder.getSwerveCommand(
          PathPlanner.loadPathGroup(
              "2 Cube Auto", new PathConstraints(Constants.Auton.MAX_SPEED, Constants.Auton.MAX_ACCELERATION))));

    chooser.addOption(
          "Shoot High And Balance - No PathPlanner",
          drivebase.moveRevOntoChargeStation()
          .andThen(() -> drivebase.balanceRobot()));

    SmartDashboard.putData("Auto", chooser);
  }

  /**
   * Use this method to define your trigger->command mappings. Triggers can be created via the
   * {@link Trigger#Trigger(java.util.function.BooleanSupplier)} constructor with an arbitrary predicate, or via the
   * named factories in {@link edu.wpi.first.wpilibj2.command.button.CommandGenericHID}'s subclasses for
   * {@link CommandXboxController Xbox}/{@link edu.wpi.first.wpilibj2.command.button.CommandPS4Controller PS4}
   * controllers or {@link edu.wpi.first.wpilibj2.command.button.CommandJoystick Flight joysticks}.
   */
  private void configureBindings()
  {
    Trigger resetArm = new Trigger(resetArmSwitch::get);
    Trigger unlockArm = new Trigger(unlockArmSwitch::get);
    if (DriverStation.isDisabled()) {
      resetArm.onTrue(arm.resetArm().ignoringDisable(true));
      unlockArm.onTrue(new InstantCommand(() -> arm.setBrake(NeutralMode.Coast)).ignoringDisable(true))
               .onFalse(new InstantCommand(() -> arm.setBrake(NeutralMode.Brake)).ignoringDisable(true));  
    }
    
    driverXbox.a().whileTrue(new RepeatCommand(new InstantCommand(drivebase::lock, drivebase)));
    driverXbox.y().onTrue((new InstantCommand(drivebase::zeroGyro)));
    // driverXbox.povRight().whileTrue(new GoToScoring(drivebase, POSITION.LEFT).getCommand());
    // driverXbox.povDown().whileTrue(new GoToScoring(drivebase, POSITION.MIDDLE).getCommand());
    // driverXbox.povLeft().whileTrue(new GoToScoring(drivebase, POSITION.RIGHT).getCommand());
    // driverXbox.b().whileTrue(new TeleopDriveWithAutoAlign(drivebase,
    // () -> -driverXbox.getRawAxis(translationAxis), 
    // () -> -driverXbox.getRawAxis(strafeAxis), 
    // 10,
    // () -> driverXbox.leftBumper().getAsBoolean(),
    // false));

    // operatorXbox.y().whileTrue(shooter.shoot(ShootSpeed.High));
    // operatorXbox.b().whileTrue(shooter.shoot(ShootSpeed.Mid));
    // operatorXbox.a().whileTrue(shooter.shoot(ShootSpeed.Low));
    // operatorXbox.x().whileTrue(shooter.intake());
    //operatorXbox.start().whileTrue(shooter.shoot(1, 1));

    operatorXbox.x().whileTrue(shootingArmCommands.Intake());
    operatorXbox.rightBumper().whileTrue(arm.moveArmToPosition(ArmPosition.Cannon));
    // operatorXbox.b().whileTrue(arm.moveArmToPosition(ArmPosition.Mid));
    // operatorXbox.y().whileTrue(arm.moveArmToPosition(ArmPosition.High));
    // operatorXbox.a().whileTrue(arm.moveArmToPosition(ArmPosition.Low));
    // operatorXbox.x().whileTrue(arm.moveArmToPosition(ArmPosition.Cannon));
    operatorXbox.b().onTrue(shooter.shootMid());
    operatorXbox.y().onTrue(shooter.shootHigh());
    operatorXbox.a().onTrue(shooter.shootLow());
    operatorXbox.leftBumper().onTrue(shooter.shootCannon());

    
    // operatorXbox.x().whileTrue(arm.moveArmToPosition(ArmPosition.Cannon));
    operatorXbox.back().onTrue(shootingArmCommands.Cannon());

    /* Manual Arm Override */
    operatorXbox.start().whileTrue(arm.moveArm(() -> operatorXbox.getRawAxis(3)-operatorXbox.getRawAxis(2)));
  }   

  /**
   * Use this to pass the autonomous command to the main {@link Robot} class.
   *
   * @return the command to run in autonomous
   */  
  public Command getAutonomousCommand()
  {
    return chooser.getSelected();
  }

  public void setDriveMode()
  {
    //drivebase.setDefaultCommand();
  }

  public void setMotorBrake(boolean brake)
  {
    drivebase.setMotorBrake(brake);
  }
}
