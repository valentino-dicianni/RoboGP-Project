package robogp.training;

import robogp.common.Instruction;
import robogp.robodrome.BoardCell;
import robogp.robodrome.Position;
import robogp.robodrome.Robodrome;
import java.util.Observable;

public class Training extends Observable {

    private class TrainingHelper implements Runnable {

        @Override
        public void run() {
            /** guarda currentInstruction del programma, guarda la posizione attuale del robot nel robodromo
            *  in base a ciò calcola tutte il susseguirsi di animazioni che andrà a fare il robodromo:
            *   prima guarda lo spostamento da fare secondo la scheda istruzione,
            *   una volta calcolata la nuova posizione si esegue l'animazione (play)
            *   a questo punto se il robot è finito su una casella attiva del robodromo
            *   si calcola la nuova posizione e le animazioni da fare dopo l'attivazione di quella casella
            *   se anche la casella successiva è una casella attiva si ripete questo processo
            *   fino a quando il robot finisce su una casella non attiva
            *   alla fine si fa play delle animazioni messe in coda della fase attivazione robodomo
            */

            executeNextInstruction();
            /* si mette in sleep, quando viene svegliato fa avanzare il programa di robot
             all'istruzione successiva e fa executenextinstr */
            System.out.println("inst executed");
        }
    }

    private static Training singleInstance;
    private boolean paused;
    private TrainingRobot robot;
    private Robodrome theRobodrome;
    private TrainingHelper trainingThread;

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

    public void setRobot(Program initialProgram, Position robotInitialPos) {
        this.robot = new TrainingRobot("robot-red","red", initialProgram);
        this.robot.setPosition(robotInitialPos);
    }

    public TrainingRobot getRobot() {
        return robot;
    }

    /**
     * fa partire il thread di training helper che farà i metodi che calcolano le animazioni
     * e aggiornano la posizione del robot
     */
    public void executeProgram() {
        this.robot.executeProgram();
        this.trainingThread = new TrainingHelper();
        this.trainingThread.run();
    }

    /**
     * metodo che prende la currentInstruction del robot e calcola le animazioni da fare, aggiorna la posizione robot
     * e fa notify con argomento le animazioni che robodrome view dovrà fare.
     * una volta terminata l'esecuzione
     */
    private void executeNextInstruction() {
        Position initialRobotPos = this.robot.getPosition();
        //rv.addRobotMove(robots[2], 3, Direction.E, Rotation.NO);
        Instruction instrToExecute = this.robot.getCurrentInstruction();
        //BoardCell nextcell = this.theRobodrome.getCell(initialRobotPos.getPosX(),initialRobotPos.getPosY());
        //System.out.println(initialRobotPos.toString());
        setChanged();
        notifyObservers(instrToExecute.getStepsToTake()+":"+initialRobotPos.getRotation()+":"+instrToExecute.getRotation());
        // aggiorno pos robot

        robodromeActivation();
    }

    /**
     * fase di attivatione del robodromo, crea lista di animazioni di robodrome view
     * e le manda a observers con notify
     */
    private void robodromeActivation() {
        //posizione iniziale del robot (nel caso cada in buco nero)
        Position initialRobotPos = this.robot.getPosition();
    }

    public void setRobodrome(Robodrome robodrome) {
        this.theRobodrome = robodrome;
    }

    public Robodrome getRobodrome() {
        return theRobodrome;
    }


}
