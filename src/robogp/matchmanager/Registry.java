package robogp.matchmanager;

import java.io.Serializable;

public class Registry implements Serializable{
    public int regNumber;
    private boolean locked;
    private MatchInstruction instruction;

    public Registry(int regNumber) {
        this.regNumber = regNumber;
        locked = false;
        instruction = null;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public void setInstruction(MatchInstruction instruction) {
        this.instruction = instruction;
    }

    public MatchInstruction getInstruction() {
        return instruction;
    }
}
