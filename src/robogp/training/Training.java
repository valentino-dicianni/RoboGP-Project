package robogp.training;

import robogp.common.Instruction;
import robogp.robodrome.*;
import robogp.robodrome.view.RobodromeAnimationObserver;
import java.util.Observable;

public class Training extends Observable implements  RobodromeAnimationObserver {

    private class TrainingHelper implements Runnable{

        @Override
        public void run() {
            while(true){
                getReadyAnimation();
                if(robot.getCurrentInstruction() !=null && !isPaused()){
                    executeNextInstruction();

                    System.out.println("TtrainingThread: inst executed");
                }
                else{
                    setChanged();
                    notifyObservers("endInstructions");
                    System.out.println("Fine dei giochi");
                    break;
                }
            }
        }
    }

    private static Training singleInstance;
    private boolean paused;
    private TrainingRobot robot;
    private Robodrome theRobodrome;
    private boolean readyAnimation;

    private Training() {
        this.paused = false;
        this.readyAnimation = true;
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
        this.robot.runProgram();
        TrainingHelper trainingThread = new TrainingHelper();
        Thread t = new Thread(trainingThread);
        t.start();
    }

    @Override
    public void animationStarted() {}

    @Override
    public void animationFinished() {
        setChanged();
        notifyObservers("animationFinished");
        setReadyAnimation();
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
                BoardCell nextlandigcell = this.theRobodrome.getNextCell(newRobotPos.getPosX(), newRobotPos.getPosY(), newRobotPos.getDirection());
                // controlla che la cella successiva non abbia un muro nella direzione opposta
                if (nextlandigcell instanceof  FloorCell) {
                    FloorCell nfcell = (FloorCell)nextlandigcell;
                    System.out.println("Next board cell has wall "+Direction.getOppositeDirection(newRobotPos.getDirection())+"? "+nfcell.hasWall(newRobotPos.getDirection()));
                    System.out.println("Next board cell pos X="+newRobotPos.getPosX()+" Y="+newRobotPos.getPosY());
                    if (nfcell.hasWall(Direction.getOppositeDirection(newRobotPos.getDirection()))) {
                        newRobotPos.changePosition(stepstaken, instrToExecute.getRotation());
                        break;
                    }
                }
                System.out.println("board cell has wall "+newRobotPos.getDirection()+"? "+fcell.hasWall(newRobotPos.getDirection()));
                // controlla anche se la cella successiva ha muro in su direzione opposta
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

        //TODO String[] robodromeAnimation = robodromeActivation() -->attacca alla stringa di animazioni anche quelle di attivazione del robodromo

        setChanged();
        notifyObservers(animationInstr);

        this.robot.goToNextInstruction();
    }
    /**
     * metodi sincronizzati per la fine/inizio animazioni
     */

    private synchronized  void  setReadyAnimation() {
        while(readyAnimation){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        this.readyAnimation = true;
        notify();
    }

    private  synchronized void getReadyAnimation() {
        while(!readyAnimation){
            try{
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        readyAnimation = false;
        notify();
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
