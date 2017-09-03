package robogp.training;

import com.sun.org.apache.xerces.internal.util.SynchronizedSymbolTable;
import robogp.common.Instruction;
import robogp.robodrome.*;
import robogp.robodrome.view.RobodromeAnimationObserver;

import java.util.Observable;

public class Training extends Observable {

    private class TrainingHelper implements Runnable, RobodromeAnimationObserver {

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
            //System.out.println("TtrainingThread: inst executed");
        }

        @Override
        public void animationStarted() {

        }

        @Override
        public void animationFinished() {

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

    private synchronized boolean isAnimating() {
        return false;
    }

    public static Training getInstance() {
        if(Training.singleInstance == null)
            Training.singleInstance = new Training();
        return Training.singleInstance;
    }

    public TrainingHelper getTrainingHelper() {
        return trainingThread;
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
        setChanged();
        notifyObservers(robot.getProgram().getInstructions());
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
        Position robotPos = this.robot.getPosition();
        Position newRobotPos = robotPos.clone();
        //rv.addRobotMove(robots[2], 3, Direction.E, Rotation.NO);
        Instruction instrToExecute = this.robot.getCurrentInstruction();
        // TODO: se si sta facendo un movimento bisogna prima guardare se le celle in cui si vuole muovere sono libere (niente muri  buchi)
        // se sono tutte libere si procede con l'esecuzione, se una cella ha un muro nella direzione in cui si sta andando e si devono ancora fare passi
        // i passi successivi non vengono fatti, se invece una delle celle è un buco nero, si crea l'animazione del robot che va fino a quella cella
        // e poi ritorna nella posizione di partenza
        int steps;
        int stepstaken = 0;
        Direction chosendir = newRobotPos.getDirection();
        String[] animationInstr = new String[2];
        for (steps = instrToExecute.getStepsToTake(); steps > 0; steps--) {
            // muovo di 1 il robot
            BoardCell landingCell = this.theRobodrome.getCell(newRobotPos.getPosX(), newRobotPos.getPosY());

            // la cella attuale ha un muro nell direzione in cui voglio andare, lo stostamento finisce qui
            if (landingCell instanceof FloorCell) {
                FloorCell fcell = (FloorCell)landingCell;
                System.out.println("board cell has wall "+newRobotPos.getDirection()+"? "+fcell.hasWall(newRobotPos.getDirection()));
                if (fcell.hasWall(newRobotPos.getDirection())) {
                    newRobotPos.changePosition(stepstaken, instrToExecute.getRotation());
                    break;
                }
            }
            /*if (landingCell.hasWall(newRobotPos.getDirection())) {
                newRobotPos.changePosition(stepstaken, instrToExecute.getRotation());
                break;
            }*/
            // non si è su una cella con un muro, continuo a muovermi
            newRobotPos.changePosition(1, instrToExecute.getRotation());
            landingCell = this.theRobodrome.getCell(newRobotPos.getPosX(), newRobotPos.getPosY());
            if (landingCell instanceof PitCell) {
                // il robot è finito su un buco nero?
                // faccio animazione inversa
                animationInstr[1] = stepstaken+":"+Direction.getOppositeDirection(chosendir)+":"+instrToExecute.getRotation();
                stepstaken = 0;
                newRobotPos = robotPos; // ripristino posizione iniziale
                break;
            }
            stepstaken++;
        }
        if (steps == -1) {
            stepstaken = 1;
            chosendir = Direction.getOppositeDirection(chosendir);
        }
        animationInstr[0] = stepstaken+":"+chosendir+":"+instrToExecute.getRotation();
        if (stepstaken == instrToExecute.getStepsToTake()) // tutti i passi che si dovevano fare sono stati fatti
            newRobotPos.changePosition(instrToExecute.getStepsToTake(), instrToExecute.getRotation());
        this.robot.setPosition(newRobotPos);

        System.out.println("updtrobotpos: "+robot.getPosition().toString());
        setChanged();
        notifyObservers(animationInstr);

        this.robot.goToNextInstruction();


        //robodromeActivation();
    }

    /**
     * fase di attivatione del robodromo, crea lista di animazioni di robodrome view
     * e le manda a observers con notify
     */
    private void robodromeActivation() {
        //posizione iniziale del robot (nel caso cada in buco nero)
        Position robotPos = this.robot.getPosition();
        // copia posizione corrente e modifica quella, nel caso che
        Position newRobotPos = robotPos.clone();

    }

    public void setRobodrome(Robodrome robodrome) {
        this.theRobodrome = robodrome;
    }

    public Robodrome getRobodrome() {
        return theRobodrome;
    }


}
