// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

/**
 * Example program that shows a variety of command based and programming
 * "best practices."
 * 
 * Demonstration output is on five sets of ten LEDs to show the program is
 * operating; operator input is Xbox controller. The sixth demonstration
 * output is the console "prints."
 * 
 * 1. Target Vision Acquired subsystem output Top LEDView subsystem (runs disabled, too)
 *     default blue;
 *     autonomous mode command dark green (no requirement for Target Vision Acquired);
 *     target vision acquired orange (simulate target acquired by pressing "A" button);
 *     slowly around the color wheel (initiated by pressing "X" button) (no requirement
 *       for Target Vision Acquired)
 * 2. Game Piece Intake Acquired subsystem output Main LEDView subsystem
 *     default cyan;
 *     autonomous mode command light green (no requirement for Game Piece Intake Acquired);
 *     intake game piece acquired magenta fast blink (simulate game piece intake
 *       acquired by pressing "B" button)
 * 3. EnableDisable LEDView subsystem
 *     enabled mode green slow blink
 *     disabled mode red slow blink
 * 4. HistoryFSM subsystem HistoryDemo LEDView subsystem
 *     random colors that don't repeat for awhile (history) (initiated by pressing "Y"
 *       button then self perpetuating) (runs in enabled mode)
 * 5. AchieveHueGoal subsystem output AchieveHueGoal LEDView subsystem
 *     Subsystem based controller runs continuously and responds to a goal setting
 *       subsystem. Colors on color wheel position showing PID controller subsystem
 *       converging on a color selected by Xbox right trigger axis (press to start)
 * 6. Disjoint Sequential Group Demo console output initiated by teleop enable mode
 *     Show subsystem default command doesn't run within a group command unless the
 *     command with the subsystem requirement is disjointed from the group by using
 *     a Proxy structure. (runs in teleop mode)
 * 
 * All commands are interruptible.
 * Some button presses are debounced.
 */

/**
 * Example program demonstrating:
 * 
 * Splitting a common resource into two separately used resources (LEDs)
 * Configure button trigger
 * Triggers
 * Use of command parameters set at command creation time
 * Use of command parameters set at changable at runtime (Suppliers)
 * Use of method reference
 * Inject TimedRobot.addPeriodic() into other classes
 * Some commentary on composite commands and mode changes
 * Command logging
 * Configuring an autonomous commnad
 * Use of Xbox controller to produce fake events
 * Use of Xbox controller to trigger an event
 * Use of public command factories in subsystems
 * Use of private non-Command methods to prevent other classes from forgetting to lock a subsystem
 * Change TimeRobot loop speed
 * Change LED update rate different from the TimedRobot loop speed
 * Overloading method parameter types
 * No commands with the word Command in the name
 * No triggers with the word Trigger in the name
 * Supplier of dynamic LED pattern
 * Static LED pattern
 * Restrict Subsystem Default Command to none until set once at any time and then unchangeable
 * Goal setting subsystem for a resource
 * Triggers available for other systems to use
 * Default commands can either run or not run within a sequential group depending on how the group is defined
 * 
 * This code anticipates extensions to the WPILib addressable LED class which are included here.
 * This example program runs in real or simulated mode of the 2024 WPILib.
 * 
 * This is a refactor and extension of code donated by ChiefDelphi @illinar. It is intended
 * to demonstrate good programming based on @Oblarg's rules.
 * Any errors or confusions are the fault and responsibility of ChiefDelphi @SLAB-Mr.Thomas; github tom131313.
 * 
 */
package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;

public class Robot extends TimedRobot {

  private RobotContainer m_robotContainer;
  private Command m_autonomousCommand;

  @Override
  public void robotInit() {

    m_robotContainer = new RobotContainer();
  }

  @Override
  public void robotPeriodic() {

    m_robotContainer.beforeCommands();    // get a consistent set of all inputs
    CommandScheduler.getInstance().run(); // check all the triggers and run all the scheduled commands
    m_robotContainer.afterCommands();     // write outputs such as logging, dashboards and indicators
  }

  @Override
  public void disabledInit() {} // commands running from another mode haven't been cancelled

  @Override
  public void disabledPeriodic() {}

  @Override
  public void disabledExit() {}

  @Override
  public void autonomousInit() {

    // commands running from another mode haven't been cancelled directly but may be interrupted by this command
    m_autonomousCommand = m_robotContainer.getAutonomousCommand();

    if (m_autonomousCommand != null) {
      m_autonomousCommand.schedule();
    }
  }

  @Override
  public void autonomousPeriodic() {}

  @Override
  public void autonomousExit() {

       if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
  }

  public static int teleopIterationCounter;

  @Override
  public void teleopInit() { // commands running from another mode haven't been cancelled directly except the one below

    if (m_autonomousCommand != null) {
      m_autonomousCommand.cancel();
    }
    teleopIterationCounter = 0;
    m_robotContainer.disjointedSequenceTestJob.schedule();
  }

  @Override
  public void teleopPeriodic() {
    teleopIterationCounter++;
  }

  @Override
  public void teleopExit() {}

  @Override
  public void testInit() {

    CommandScheduler.getInstance().cancelAll();
  }

  @Override
  public void testPeriodic() {}

  @Override
  public void testExit() {}
}
