package robogp.matchmanager;

import robogp.common.Instruction;
import robogp.robodrome.Rotation;

public class MatchInstruction extends Instruction {

    private int priority;

    public MatchInstruction(String name, int stepsToTake, Rotation rotation, int priority) {
        super(name, stepsToTake, rotation);
        this.priority = priority;
    }

    public static MatchInstruction getInstructionByName(String instructionName, int priority) {
        MatchInstruction newinstr = (MatchInstruction)Instruction.getInstructionByName(instructionName);
        //newinstr.priority = priority;
        newinstr.setPriority(priority);
        return newinstr;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
