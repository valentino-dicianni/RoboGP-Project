package robogp.training;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class TrainingRobot implements Runnable, Observer {
    //private String robotName;
    private Program program;
    private ArrayList<Status> statesList;

    public TrainingRobot(/*String robotName*/) {
        //this.robotName = robotName;
        this.program = new Program();
        this.statesList = new ArrayList<>();
    }

    public void executeProgram() {
        this.program.setRunning();
    }

    @Override
    public void run() {

    }

    @Override
    public void update(Observable o, Object arg) {

    }

    /*@Override
    public void update(boolean paused) {
        try {
            if (paused) {
                this.wait();
            } else {
                this.notify();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }*/
}
