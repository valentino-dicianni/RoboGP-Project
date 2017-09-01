package robogp.training;

import robogp.common.Instruction;
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
     * @param robodromename: file name of the chosen robodrome
     * @return dock positions in the chosen robodrome
     */
    public ArrayList<Position> setRobodrome(String robodromename) {
        //System.out.println(robodromename);
        training.setRobodrome(new Robodrome("robodromes/"+robodromename+".txt"));
        return training.getRobodrome().getDockPos();
    }

    /**
     * start training given a list of instructions in the form of Strings
     */
    public void start(Object[] arr) {
        Program tmpprog = new Program();
        for(Object instrname : arr)
            tmpprog.loadInstruction(Instruction.getInstructionByName(instrname.toString()));
        training.setRobot(tmpprog);
        training.executeProgram();
        //System.out.println(tmpprog.toString());
    }
}
