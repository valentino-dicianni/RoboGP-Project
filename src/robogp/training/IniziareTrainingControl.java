package robogp.training;

import robogp.robodrome.Position;

import java.util.ArrayList;

public class IniziareTrainingControl {
    private static IniziareTrainingControl singleInstance;


    public static IniziareTrainingControl getInstance(){
        if(IniziareTrainingControl.singleInstance == null)
            IniziareTrainingControl.singleInstance = new IniziareTrainingControl();
        return IniziareTrainingControl.singleInstance;
    }

    public ArrayList<Position> setRobodrome() {
        return null;
    }
}
