package robogp.matchmanager;

import robogp.common.RobotMarker;

import java.util.ArrayList;

public class MatchRobot extends RobotMarker {
    private int hitPoints;
    private int lifePoints;
    private boolean shutDown;

    private Registry[] registries;

    private ArrayList<MatchInstruction> instructionsPool;

    public MatchRobot(String name, String color) {
        super(name, color);
        this.hitPoints = 5;
        this.lifePoints = 3;
        this.registries = new Registry[5];
        this.instructionsPool = new ArrayList<MatchInstruction>();
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public int getLifePoints() {
        return lifePoints;
    }

    public boolean isShutDown() {
        return shutDown;
    }
}
