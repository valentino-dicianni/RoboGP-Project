package robogp.training;

import robogp.common.Instruction;
import robogp.robodrome.Position;

public class Status {
    private Instruction instruction;
    private Position position;

    public Status(Instruction instruction, Position position) {
        this.instruction = instruction;
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public Instruction getInstruction() {
        return instruction;
    }
}
