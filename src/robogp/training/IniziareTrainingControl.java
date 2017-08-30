package robogp.training;

public class IniziareTrainingControl {
    private static IniziareTrainingControl singleInstance;


    public static IniziareTrainingControl getInstance(){
        if(IniziareTrainingControl.singleInstance == null)
            IniziareTrainingControl.singleInstance = new IniziareTrainingControl();
        return IniziareTrainingControl.singleInstance;
    }

    public void setRobodrome() { }
}
