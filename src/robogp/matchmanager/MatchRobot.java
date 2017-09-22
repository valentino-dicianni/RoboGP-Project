package robogp.matchmanager;

import robogp.common.RobotMarker;
import robogp.robodrome.Position;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class MatchRobot extends RobotMarker {
    private int hitPoints;
    private int lifePoints;
    private boolean isShutDown;
    private boolean[] checkpoints;

    private Registry[] registries;

    private ArrayList<MatchInstruction> instructionsPool;

    public MatchRobot(String name, String color, int checkpointsNumber) {
        super(name, color);
        this.hitPoints = 10;
        this.lifePoints = 3;
        this.registries = new Registry[5];
        for(int i = 0; i < 5; i++)
            this.registries[i] = new Registry(i + 1);
        this.instructionsPool = new ArrayList<>();
        this.checkpoints = new boolean[checkpointsNumber];
        Arrays.fill(this.checkpoints, false);
    }

    public void setInstructionsPool(ArrayList<MatchInstruction> instructionsPool) {
        this.instructionsPool = instructionsPool;
    }

    /**
     * il robot ha toccato un checkpoint, se è in ordine con i checkpoint toccati finora mette il checkpoint a true
     * e ritorna true, altrimenti ritorn false
     * @param checkpointNum: posizione del checkpoint toccato
     * @return true se checkpoint toccato è valido, false alrimenti
     */
    public boolean touchCheckpoint(int checkpointNum) {
        int checkpointIndex = checkpointNum - 1;
        for (int i = 0; i < checkpoints.length; i++) {
            if (checkpointIndex == i && !checkpoints[i]) {
                checkpoints[i] = true;
                return true;
            } else if (checkpoints[i]) {
                //continue;
            } else {
                return false;
            }
        }
        return true; // tutti i checkpoint sono stati toccati
    }

    public boolean checkpointsAllTouched() {
        for (int i = 0; i < checkpoints.length; i++) {
            if (!checkpoints[i]) return false;
        }
        return true;
    }

    public void resetCheckpoints() {
        Arrays.fill(this.checkpoints, false);
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    public int getLifePoints() {
        return lifePoints;
    }

    public void setLifePoints(int lifePoints) {
        this.lifePoints = lifePoints;
    }

    public boolean isShutDown() {
        return isShutDown;
    }

    public void setRegistry(int regNumber, MatchInstruction instruction) {
        if (!this.registries[regNumber-1].isLocked())
            this.registries[regNumber-1].setInstruction(instruction);
    }

    public void resetRegistries() {
        for(int i = 0; i < 5; i++) {
            this.registries[i].setInstruction(null);
            this.registries[i].setLocked(false);
        }
    }

    public void setRegistryLock(int regNumber, boolean locked) {
        this.registries[regNumber-1].setLocked(locked);
    }

    public Registry getRegistry(int regNumber) {
        return this.registries[regNumber-1];
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

    @Override
    public String toString() {
        String reg = "";
        String sep = ", ";
        int i = 0;
        for (Registry reG : registries) {
            i++;
            if (reG.isLocked())
                reg += i+": locked"+(i < 5?sep:"");
            else if (reG.getInstruction() == null)
                reg += i+": x"+(i < 5?sep:"");
            else
                reg += i+": "+reG.getInstruction().toString()+(i < 5?sep:"");
        }
        //Position pos = this.getPosition();
        return this.getName()+": reg= "+reg;
    }
}
