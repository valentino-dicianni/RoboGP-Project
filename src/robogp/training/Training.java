package robogp.training;

import java.util.Observable;

public class Training extends Observable {
    private static Training singleInstance;
    private boolean paused;
    private TrainingRobot robot;

    private Training() {
        this.paused = false;
    }


    public static Training getInstance() {
        if(Training.singleInstance == null)
            Training.singleInstance = new Training();
        return Training.singleInstance;
    }


    public void setPaused(boolean paused) {
        this.paused = paused;
        setChanged();
        notifyObservers(this.paused);
    }

    public boolean isPaused() {
        return paused;
    }

    public void setRobot(String robotName) {
        this.robot = new TrainingRobot(robotName);
        this.addObserver(this.robot);
    }
}
