package robogp.matchmanager;

import org.jetbrains.annotations.NotNull;
import robogp.common.Instruction;
import robogp.robodrome.Rotation;

public class MatchInstruction extends Instruction {

    private int priority;

    public MatchInstruction(String name, int stepsToTake, Rotation rotation, int priority) {
        super(name, stepsToTake, rotation);
        this.priority = priority;
    }

    public static MatchInstruction getInstructionByName(String instructionName, int priority) {
        Instruction instr = Instruction.getInstructionByName(instructionName);
        return new MatchInstruction(instr.getName(), instr.getStepsToTake(), instr.getRotation(), priority);
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    @Override
    public String toString() {
        return this.getName()+":"+this.priority;
    }
}
