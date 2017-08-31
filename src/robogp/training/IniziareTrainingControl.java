package robogp.training;

import robogp.robodrome.Position;

import java.util.ArrayList;

import robogp.robodrome.Robodrome;

public class IniziareTrainingControl {
    private static IniziareTrainingControl singleInstance;
    private static Training training;

    public static IniziareTrainingControl getInstance(){
        if (IniziareTrainingControl.singleInstance == null) {
            IniziareTrainingControl.singleInstance = new IniziareTrainingControl();
            IniziareTrainingControl.training = Training.getInstance();
        }
        return IniziareTrainingControl.singleInstance;
    }

    /**
     * creates the Training robodrome
     * @param robodromename
     * @return
     */
    public ArrayList<Position> setRobodrome(String robodromename) {
        training.setRobodrome(new Robodrome("robodromes/"+robodromename+".txt"));
        return Training.getInstance().getTheRobodrome().getDockPos();
    }

    /**
     * inizia allenamento, dato un programma del robot
     */
    public void start(String[] arr) {

    }
}
