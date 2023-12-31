package frc.robot.commands.DriveTrain.Auto.trajectory;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import edu.wpi.first.math.controller.PIDController;
import edu.wpi.first.math.controller.RamseteController;
import edu.wpi.first.math.controller.SimpleMotorFeedforward;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.trajectory.Trajectory;
import edu.wpi.first.math.trajectory.TrajectoryConfig;
import edu.wpi.first.math.trajectory.TrajectoryGenerator;
import edu.wpi.first.math.trajectory.TrajectoryUtil;
import edu.wpi.first.math.trajectory.constraint.DifferentialDriveVoltageConstraint;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Filesystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import edu.wpi.first.wpilibj2.command.RamseteCommand;
import frc.robot.Constants;
import frc.robot.subsystems.DriveTrain;


public class FollowPath extends CommandBase {
  
  private DifferentialDriveVoltageConstraint autoVoltageConstraint;
  private DriveTrain driveTrain; 
  private TrajectoryConfig config;
  private Trajectory exampleTrajectory;
  private RamseteCommand ramseteCommand;
  
  //private String trajectoryJSON = "paths/game.wpilib.json";

    /** Creates a new FollowPath. */
  public FollowPath(DriveTrain driveTrain) {
    this.driveTrain = driveTrain;

       // Use addRequirements() here to declare subsystem dependencies.
    addRequirements(driveTrain);
  }

  // Called when the command is initially scheduled.
  @Override
  public void initialize() {
    
    autoVoltageConstraint = new DifferentialDriveVoltageConstraint(
      new SimpleMotorFeedforward(
        Constants.TrajectoryConstants.ksVolts, Constants.TrajectoryConstants.kvVoltSecondsPerMeter, Constants.TrajectoryConstants.kaVoltSecondsSquaredPerMeter),
        driveTrain.driveKinematics,  12);
  
    config = new TrajectoryConfig(
      Constants.TrajectoryConstants.kMaxSpeedMetersPerSecond, 
      Constants.TrajectoryConstants.kMaxAccelerationMetersPerSecondSquared)
      .setKinematics(driveTrain.driveKinematics).addConstraint(autoVoltageConstraint);

    exampleTrajectory = TrajectoryGenerator.generateTrajectory(
      new Pose2d(0,0, new Rotation2d()), 
      List.of(new Translation2d(1, 1), new Translation2d(2, -1), new Translation2d(2.7, 0)),
      new Pose2d(3, 0, new Rotation2d(0)), 
      config); 

      String trajectoryJSON = "New Path";
      try {
        Path trajectoryPath = Filesystem.getDeployDirectory().toPath().resolve(trajectoryJSON);
        exampleTrajectory = TrajectoryUtil.fromPathweaverJson(trajectoryPath);
     } catch (IOException ex) {
        DriverStation.reportError("Unable to open trajectory: " + trajectoryJSON, ex.getStackTrace());
     }
     
    driveTrain.resetOdometry(exampleTrajectory.getInitialPose());

     ramseteCommand = new RamseteCommand(
      exampleTrajectory, driveTrain::getPose, 
      new RamseteController(Constants.TrajectoryConstants.kRamseteB, Constants.TrajectoryConstants.kRamseteZeta),
       new SimpleMotorFeedforward(Constants.TrajectoryConstants.ksVolts, 
        Constants.TrajectoryConstants.kvVoltSecondsPerMeter, 
        Constants.TrajectoryConstants.kaVoltSecondsSquaredPerMeter), 
       driveTrain.driveKinematics, 
       driveTrain::getWheelSpeeds, 
       new PIDController(Constants.TrajectoryConstants.kPDriveVel, 0, 0), 
       new PIDController(Constants.TrajectoryConstants.kPDriveVel, 0, 0), 
       driveTrain::tankDriveVolts, driveTrain);
    
    driveTrain.resetOdometry(exampleTrajectory.getInitialPose());
    ramseteCommand.initialize();
 
  }

  // Called every time the scheduler runs while the command is scheduled.
  @Override
  public void execute() {
    ramseteCommand.execute();
    //return ramseteCommand.andThen(() -> driveTrain.tankDriveVolts(0, 0));
  }

  // Called once the command ends or is interrupted.
  @Override
  public void end(boolean interrupted) {
    ramseteCommand.end(interrupted);
    driveTrain.tankDriveVolts(0, 0);
  }

  // Returns true when the command should end.
  @Override
  public boolean isFinished() {
    return ramseteCommand.isFinished();
//    return false;
  }
}