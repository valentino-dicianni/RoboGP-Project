package robogp.training;

import robogp.matchmanager.RobotMarker;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class TrainingRobot extends RobotMarker{
    //private String robotName;
    private Program program;
    private ArrayList<Status> statesList;

    public TrainingRobot(String name, String color, Program program) {
        super(name, color);
        this.program = program;
        this.statesList = new ArrayList<>();
    }

    public void executeProgram() {
        this.program.setRunning();
    }

    public void setProgram(Program program) {
        this.program = program;
    }

}
