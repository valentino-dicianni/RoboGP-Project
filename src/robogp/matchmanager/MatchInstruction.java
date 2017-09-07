package robogp.matchmanager;

import robogp.common.Instruction;
import robogp.robodrome.Rotation;

public class MatchInstruction extends Instruction {

    private int priority;

    public MatchInstruction(String name, int stepsToTake, Rotation rotation, int priority) {
        super(name, stepsToTake, rotation);
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
