// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

public final class Constants {
  public static class OperatorConstants {
    public static final int kDriverControllerPort = 0;
    
    public static final int XboxController = 0;
    
  }

  public static class DriveConstants {
    public static final int kLeftMotorMasterPort = 4;
    public static final int kLeftMotorSlavePort = 3;
    public static final int kRightMotorMasterPort = 2;
    public static final int kRightMotorSlavePort = 1;

    public static final float DEAD_ZONE = 0.1f;

    public static final int kEncoderTicksPerRevolution= 4096;
    public static final double kWheelCircunference = 0.471238898;

    //Calculando quantos metros para se equilibrar na charge(obs: tudo em inches)
    public static final double halfChargeStation = (6 + (0.5 / 12)) / 2;
    public static final double halfRobotLength = (29.5275591 / 2) / 12;
    
  }

  public static class AutoConstants {
    public static final double halfChargeStation = (6 + (0.5 / 12)) / 2;
    public static final double halfRobotLength = (29.5275591 / 2) / 12;
    
  }
}