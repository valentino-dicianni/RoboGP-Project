package robogp.matchmanager;

import robogp.common.RobotMarker;

import java.util.ArrayList;

public class MatchRobot extends RobotMarker {
    private int hitPoints;
    private int lifePoints;
    private boolean isShutDown;

    private Registry[] registries;

    private ArrayList<MatchInstruction> instructionsPool;

    public MatchRobot(String name, String color) {
        super(name, color);
        this.hitPoints = 5;
        this.lifePoints = 3;
        this.registries = new Registry[5];
        for(int i = 0; i < 5; i++)
            this.registries[i] = new Registry(i + 1);
        this.instructionsPool = new ArrayList<MatchInstruction>();
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public int getLifePoints() {
        return lifePoints;
    }

    public boolean isShutDown() {
        return isShutDown;
    }

    public ArrayList<Integer> getLockedRegs(){
        ArrayList<Integer> locks = new ArrayList<>();
        for(int i=0;i<registries.length;i++){
            if(registries[i].isLocked()) {
                locks.add(registries[i].regNumber);
            }
        }
        return locks;
    }
}
