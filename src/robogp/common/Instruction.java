package robogp.common;

public class Instruction {
    private int stepsToTake;
    private int turnAngle;

    public Instruction(int stepsToTake, int turnAngle) {
        this.stepsToTake = stepsToTake;
        this.turnAngle = turnAngle;
    }

    public int getStepsToTake() {
        return this.stepsToTake;
    }

    public int getTurnAngle() {
        return this.turnAngle;
    }
}
