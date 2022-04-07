// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.InvertType;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;


/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {

  //Motor Controllers 
  WPI_VictorSPX cont_driveL = new WPI_VictorSPX(11);
  WPI_VictorSPX cont_driveR = new WPI_VictorSPX(12);
  WPI_TalonSRX cont_shoulder = new WPI_TalonSRX(13);
  WPI_TalonSRX cont_elbowL = new WPI_TalonSRX(14);
  WPI_TalonSRX cont_elbowR = new WPI_TalonSRX(15);

  //drivetrain
  DifferentialDrive drive = new DifferentialDrive(cont_driveL, cont_driveR);

  //Inputs
  XboxController xbox_drive = new XboxController(0);
  XboxController xbox_util = new XboxController(1);

  //buttons
  Double btn_driveFB;
  Double btn_driveSpin;
  Boolean btn_driveTurbo;

  Double btn_shoulders;
  Double btn_Elbows;

  Boolean btn_encoderSetZero;
  Boolean btn_climbMode;

  //Auto timer
  Timer timer = new Timer();

  //Misc varaiables
  Boolean sensorResetComplete;
  Boolean climbMode;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {

    climbMode = false;
    sensorResetComplete = false;
    
    //Elbow lock
    cont_elbowL.setNeutralMode(NeutralMode.Brake);
    cont_elbowR.setNeutralMode(NeutralMode.Brake);
    cont_shoulder.setNeutralMode(NeutralMode.Brake);

    cont_driveL.setNeutralMode(NeutralMode.Brake);
    cont_driveR.setNeutralMode(NeutralMode.Brake);
    cont_driveR.setInverted(true);
  }

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {
    
    /*
    //Automatic encoder reset
    if (!sensorResetComplete){
      while (cont_elbowL.getSelectedSensorVelocity(0) > 5){
        cont_elbowL.set(TalonSRXControlMode.PercentOutput, -0.3);
      }
      while (cont_elbowR.getSelectedSensorVelocity(0) > 5){
        cont_elbowR.set(TalonSRXControlMode.PercentOutput, -0.3);
      }
      while (cont_shoulder.getSelectedSensorVelocity(0) > 5){
        cont_shoulder.set(TalonSRXControlMode.PercentOutput, 0.3);
      }

      if (cont_shoulder.getSelectedSensorVelocity(0) < 5 && cont_elbowL.getSelectedSensorVelocity(0) < 0 && cont_elbowR.getSelectedSensorVelocity(0) < 5){
        cont_shoulder.setSelectedSensorPosition(0, 0, 100);
        cont_elbowL.setSelectedSensorPosition(0, 0, 100);
        cont_elbowR.setSelectedSensorPosition(0, 0, 100);
        sensorResetComplete = true;
      }
    }
    */
  }

  @Override
  public void autonomousInit() {
    cont_elbowR.follow(cont_elbowL);
    cont_elbowR.setInverted(InvertType.OpposeMaster);
    timer.reset();
    timer.start();
  }
 
  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    if (timer.get() < 1){
      drive.arcadeDrive(-0.8, 0);
    } else if (timer.get() > 1 && timer.get() < 1.5){
      drive.stopMotor();
    } else if (timer.get() > 1.5 && timer.get() < 2.5){
      drive.arcadeDrive(0.8, 0);
    } else if (timer.get() > 2.5 && timer.get() < 3){
      drive.stopMotor();
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {
    cont_elbowR.follow(cont_elbowL);
    cont_elbowR.setInverted(InvertType.OpposeMaster);
  }

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {

    //Button mapping
    btn_driveFB = xbox_drive.getRawAxis(1);
    btn_driveSpin = xbox_drive.getRawAxis(4);

    btn_Elbows = xbox_util.getRawAxis(5);
    btn_shoulders = xbox_util.getRawAxis(1);
    btn_encoderSetZero = xbox_util.getRawButton(1);

    //Calculations for arm restrictions
    Double maxExtentionDistance = 30.0;

    Double shoulderEncoderPosition = cont_shoulder.getSelectedSensorPosition(0);
    Double elbowEncoderPosition = cont_elbowL.getSelectedSensorPosition(0);
    Double correctedShoulderEncoderPostion = shoulderEncoderPosition - 0;
    Double correctedElbowEncoderPosition = elbowEncoderPosition - 0;
    
    Double shoulderLength = 13.5;
    Double elbowLength = 20.75;

    Double angleShoulder = (22.0 / 60) * correctedShoulderEncoderPostion * (2 * Math.PI);
    Double angleElbow = (correctedElbowEncoderPosition * (2 * Math.PI)) - ((Math.PI / 2) - angleShoulder);
    Double distanceToElbow = shoulderLength * Math.cos(angleShoulder);
    Double distanceToHook = elbowLength * Math.sin(angleElbow);

    Double totalReach = distanceToElbow + distanceToHook;

    Double elbowCorrectionAngle = Math.asin(-(((shoulderLength * Math.cos(angleShoulder)) - maxExtentionDistance) / elbowLength) + angleShoulder +(Math.PI / 2));

    //Arm restrictions
    if (totalReach > maxExtentionDistance){
      cont_elbowL.set(TalonSRXControlMode.Position, elbowCorrectionAngle * (4096 / (2 * Math.PI)));
    }

    //Shoulder section control
    cont_shoulder.set(TalonSRXControlMode.PercentOutput, -btn_shoulders);;
    
    //Elbow section control
    if (totalReach < 31.5){
      cont_elbowL.set(-btn_Elbows);
    }
    cont_elbowR.follow(cont_elbowL);

    cont_elbowL.set(TalonSRXControlMode.PercentOutput, btn_Elbows);
    cont_shoulder.set(TalonSRXControlMode.PercentOutput, -btn_shoulders);
    
    //drivetrain
    drive.arcadeDrive(-0.8*btn_driveFB, btn_driveSpin);
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {
    timer.reset();
    timer.start();
  }

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {
    cont_shoulder.set(TalonSRXControlMode.Velocity, 300);

    if (timer.get() > 2){
      cont_shoulder.stopMotor();
    }

  }
}