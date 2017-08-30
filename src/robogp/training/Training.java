package robogp.training;

public class Training {
    private static Training singleInstance;


    public static Training getInstance() {
        if(Training.singleInstance == null)
            Training.singleInstance = new Training();
        return Training.singleInstance;
    }




}
