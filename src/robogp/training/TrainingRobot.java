package robogp.training;

import robogp.common.Instruction;
import robogp.matchmanager.RobotMarker;
import robogp.robodrome.Position;

import java.util.ArrayList;

public class TrainingRobot extends RobotMarker{
    //private String robotName;
    private Program program;
    private ArrayList<Status> statesList;

    public TrainingRobot(String name, String color, Program program) {
        super(name, color);
        this.program = program;
        this.statesList = new ArrayList<>();
    }

    public Program getProgram() {
        return program;
    }

    public void runProgram() {
        this.program.setRunning();
    }

    public void setProgram(Program program) {
        this.program = program;
    }

    public void addState(Position newPosition, Instruction currentInstruction) {
        statesList.add(new Status(currentInstruction,newPosition));
    }

    public Instruction getCurrentInstruction() {
        return this.program.getCurrentInstruction();
    }

    public void goToNextInstruction() {
        this.program.goToNextInstruction();
    }

}
