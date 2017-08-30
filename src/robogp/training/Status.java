package robogp.training;

import robogp.robodrome.Position;

public class Status {
    private TrainingInstruction instruction;
    private Position position;

    public Status(TrainingInstruction instruction, Position position) {
        this.instruction = instruction;
        this.position = position;
    }

    public Position getPosition() {
        return position;
    }

    public TrainingInstruction getInstruction() {
        return instruction;
    }
}
