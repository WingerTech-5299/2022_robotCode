// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.ctre.phoenix.motorcontrol.can.WPI_VictorSPX;

import edu.wpi.first.wpilibj.TimedRobot;
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
  WPI_TalonSRX cont_winch = new WPI_TalonSRX(16);

  //drivetrain
  DifferentialDrive drive = new DifferentialDrive(cont_driveL, cont_driveR);

  //Inputs
  XboxController xbox_drive = new XboxController(0);
  XboxController xbox_util = new XboxController(1);

  //buttons
  Double btn_driveFB;
  Double btn_driveSpin;
  Boolean btn_driveTurbo;
  int btn_winch;

  Double btn_shoulders;
  Boolean btn_ElbowLOut;
  Boolean btn_ElbowROut;
  Double btn_Elbows;
  Double btn_EblowLIn;
  Double btn_ElbowRIn;

  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {}

  /**
   * This function is called every robot packet, no matter the mode. Use this for items like
   * diagnostics that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {}

  @Override
  public void autonomousInit() {}

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {}

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {

    //double turboModifier = 0.5;

    // if (btn_driveTurbo){
    //   turboModifier = 1.0;
    // } else {
    //   turboModifier = 0.5;
    // }
 
    
    btn_driveFB = xbox_drive.getRawAxis(1);
    btn_driveSpin = xbox_drive.getRawAxis(4);

    btn_EblowLIn = xbox_util.getRawAxis(2);
    btn_ElbowRIn = xbox_util.getRawAxis(3);

    btn_ElbowLOut = xbox_util.getRawButton(5);
    btn_ElbowROut = xbox_util.getRawButton(6);

    btn_Elbows = xbox_util.getRawAxis(1);

    btn_shoulders = xbox_util.getRawAxis(1);

    cont_shoulder.set(-0.5*btn_shoulders);

    if (btn_ElbowLOut) {
      cont_elbowL.set(-0.3);
    } else {
      cont_elbowL.set(0);
    }

    if (btn_ElbowROut) {
      cont_elbowR.set(-0.3);
    } else {
      cont_elbowR.set(0);
    }

    if (btn_ElbowRIn > 0.05){
      cont_elbowR.set(btn_ElbowRIn);

    }
    if (btn_EblowLIn > 0.05){
      cont_elbowL.set(btn_EblowLIn);
    }

    // if (btn_EblowLIn == 0 && btn_ElbowRIn == 0 && !btn_ElbowLOut && !btn_ElbowROut){
      
    //   cont_elbowL.set(btn_Elbows*0.5);
    //   cont_elbowR.set(btn_Elbows*-0.5);

    // }
    
    drive.arcadeDrive(0.8*btn_driveFB, 0.8*btn_driveSpin);



  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}
}
