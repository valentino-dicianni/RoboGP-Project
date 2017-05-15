/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robogp.robodrome.animation;

import robogp.robodrome.Direction;
import robogp.robodrome.Rotation;

/**
 *
 * @author claudia
 */
public class RobotMoveAnimation extends Animation {
  private int movement;
  private Direction dir;
  private Rotation rotation;
  
  private int startX;
  private int startY;

  private String[] pushRobots;
  
  private RobotMoveAnimation() {
    this.type = Animation.Type.ROBOT_MOVE;
  }
  
  public RobotMoveAnimation(String robotName, int move, Direction dir, Rotation rot) {
      this();
      this.which = robotName;
      this.movement = move;
      this.dir = dir;
      this.rotation = rot;
      this.duration = (movement > 0? Animation.TIMEUNIT * movement : 
              (rot.getDegrees() > 0 ? (int)(Animation.TIMEUNIT*(rot.getDegrees()/180)): 0));
      this.pushRobots = new String[0];
  }
  
    public RobotMoveAnimation(String robotName, int move, Direction dir, Rotation rot, String[] push) {
      this(robotName, move, dir, rot);
      this.pushRobots = push;
  }

    public void setStartPosition(int posX, int posY) {
        this.startX = posX;
        this.startY = posY;
    }

    /**
     * @return the movement
     */
    public int getMovement() {
        return movement;
    }

    /**
     * @return the dir
     */
    public Direction getDirection() {
        return dir;
    }

    /**
     * @return the rotation
     */
    public Rotation getRotation() {
        return rotation;
    }

    /**
     * @return the pushRobots
     */
    public String[] getPushRobots() {
        return pushRobots;
    }

    /**
     * @return the startX
     */
    public int getStartX() {
        return startX;
    }

    /**
     * @return the startY
     */
    public int getStartY() {
        return startY;
    }
}
