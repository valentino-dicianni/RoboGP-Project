package robogp.matchmanager;

public class Registry {
    private boolean locked;
    private MatchInstruction instruction;

    public Registry() {
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
