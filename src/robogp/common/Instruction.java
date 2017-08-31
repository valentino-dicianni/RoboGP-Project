package robogp.common;

import robogp.robodrome.Rotation;

public class Instruction {
    private int stepsToTake;
    private Rotation rotation;

    public Instruction(int stepsToTake, Rotation rotation) {
        this.stepsToTake = stepsToTake;
        this.rotation = rotation;
    }

    public int getStepsToTake() {
        return this.stepsToTake;
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    public static Instruction getInstructionByName(String instructionName) {
        switch (instructionName) {
            case "move1":
                return new Instruction(1, Rotation.NO);
            case "move2":
                return new Instruction(2, Rotation.NO);
            case "move3":
                return new Instruction(3, Rotation.NO);
            case "turnL":
                return new Instruction(0, Rotation.CCW90);
            case "turnR":
                return new Instruction(0, Rotation.CW90);
            case "uturn":
                return new Instruction(0, Rotation.CW180);
            case "backup":
                return new Instruction(-1, Rotation.NO);
        }
        return null;
    }

    @Override
    public String toString() {
        return "Steps: "+stepsToTake+", Angle: "+rotation.toString();
    }
}
