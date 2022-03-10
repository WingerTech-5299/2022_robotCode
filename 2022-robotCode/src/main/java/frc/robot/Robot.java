// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import com.ctre.phoenix.motorcontrol.can.WPI_TalonSRX;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

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
  CANSparkMax cont_driveL = new CANSparkMax(11, MotorType.kBrushed);
  CANSparkMax cont_driveR = new CANSparkMax(12, MotorType.kBrushed);
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

  Double btn_shoulderOut;
  Double btn_ShoulderIn;
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

    double turboModifier = 0.5;
    int ifEblowBackwards = 1;

    if (btn_driveTurbo){
      turboModifier = 1.0;
    } else {
      turboModifier = 0.5;
    }

    btn_driveFB = xbox_drive.getRawAxis(5);
    btn_driveSpin = xbox_drive.getRawAxis(1);

    btn_winch = xbox_util.getPOV();
    btn_EblowLIn = xbox_util.getRawAxis(5);
    btn_ElbowRIn = xbox_util.getRawAxis(6);
    btn_ElbowLOut = xbox_util.getRawButton(5);
    btn_ElbowROut = xbox_util.getRawButton(6);
    btn_Elbows = xbox_util.getRawAxis(1);


    while (btn_ElbowLOut) {
      cont_elbowL.set(0.5*ifEblowBackwards);
    } 
      cont_elbowL.set(0);

    while (btn_ElbowROut) {
      cont_elbowR.set(-0.5*ifEblowBackwards);
    }
      cont_elbowR.set(0);

    cont_elbowR.set(btn_ElbowRIn*-0.5*ifEblowBackwards);
    cont_elbowL.set(btn_EblowLIn*0.5*ifEblowBackwards);

    if (btn_EblowLIn == 0 && btn_ElbowRIn == 0 && btn_ElbowLOut == false && btn_ElbowROut == false){

      elbows(btn_Elbows);

    }

    while(btn_winch == 0 || btn_winch == 315 || btn_winch == 45){
      cont_winch.set(0.8);
    }

    while(btn_winch == 180 || btn_winch == 135 || btn_winch == 225){
      cont_winch.set(-0.8);
    }
    cont_winch.set(0);
    
    drive.arcadeDrive(turboModifier*btn_driveFB, 0.8*btn_driveSpin);
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
  
  private void elbows (Double btnInput){

    cont_elbowL.set(btnInput*0.5);
    cont_elbowR.set(btnInput*-0.5);
}
}
