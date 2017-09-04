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

                    //System.out.println("TtrainingThread: inst executed");
                }
                else{
                    setChanged();
                    notifyObservers("endInstructions");
                    //System.out.println("Fine dei giochi");
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
        //System.out.println("initial posX="+robotPos.getPosX()+", posY="+robotPos.getPosY());
        Instruction instrToExecute = this.robot.getCurrentInstruction();
        int steps;
        int stepstaken = 0;
        Direction chosendir = robotPos.getDirection();
        Rotation instrRot = instrToExecute.getRotation();
        String[] animationInstr = new String[2];
        for (steps = instrToExecute.getStepsToTake(); steps > 0; steps--) {
            // controllo che non ci siano muri
            if (this.theRobodrome.pathHasWall(robotPos.getPosX(), robotPos.getPosY(), chosendir)) {
                break;
            }
            // il percorso per la prossima cella è libero, continuo a muovermi
            robotPos.changePosition(1, instrRot);
            stepstaken++;
        }
        // se instruzione backup
        if (steps == -1 && !this.theRobodrome.pathHasWall(robotPos.getPosX(), robotPos.getPosY(), Direction.getOppositeDirection(chosendir))) {
            stepstaken = Math.abs(steps);
            robotPos.changePosition(steps, instrRot);
            chosendir = Direction.getOppositeDirection(chosendir);
        }
        if (instrToExecute.getStepsToTake() == 0) { // se istruzione rotazione
            robotPos.changePosition(0, instrRot);
        }

        // animazione codificata del movimento fatto con scheda istruzione
        animationInstr[0] = stepstaken+":"+chosendir+":"+instrRot;

        //System.out.println("updtrobotpos: "+robot.getPosition().toString());

        //TODO String[] robodromeAnimation = robodromeActivation() -->attacca alla stringa di animazioni anche quelle di attivazione del robodromo
        String[] robodromeanim = robodromeActivation();
        int aLen = animationInstr.length;
        int bLen = robodromeanim.length;
        String[] finalAnimList = new String[aLen+bLen];
        System.arraycopy(animationInstr, 0, finalAnimList, 0, aLen);
        System.arraycopy(robodromeanim, 0, finalAnimList, aLen, bLen);

        setChanged();
        notifyObservers(finalAnimList);

        this.robot.goToNextInstruction();
    }

    /**
     * attivazione robodromo, guarda la pos corrente del robot, se NON è su un elem attivo return null
     * se è su elemnto attivo crea animazione in base ad elemento:
     *  nastro singolo: muove di 1 nella direzione direzione in cui è puntato il nastro
     *  nastro doppio: muove di due
     *  pitt cell: il robot viene rimesso alla posizione salvata dall'ultimo checkpoint
     *  floor cell checkpoint: salva posizione check point robot
     *  floor cell rotator: fa animazione rotazione
     */
    private String[] robodromeActivation() {
        //posizione iniziale del robot (nel caso cada in buco nero)
        String[] animation = new String[3]; // max 3 animazioni: doppio nastro + curva
        Position robotPos = this.robot.getPosition();
        // rotazione da fare
        Rotation rotation = Rotation.NO;
        Direction dir = robotPos.getDirection();
        int movement = 0;
        BoardCell currentcell = this.theRobodrome.getCell(robotPos.getPosX(), robotPos.getPosY());
        // TODO: while robot is on active cell?
        if (currentcell instanceof BeltCell) {
            BeltCell bcell = (BeltCell) currentcell;
            Direction output = bcell.getOutputDirection();
            if (this.theRobodrome.pathHasWall(robotPos.getPosX(), robotPos.getPosY(), output)) {
                return animation;
            }

            // quando il nastro trasportatore ha input direction != output direction allora è un nastro curva
            // se nastro è curva il robot oltre a spostarsi ruota
            boolean turn = false;
            //System.out.println("-> cell output dir: "+bcell.getOutputDirection());
            if (bcell.hasInputDirection(Direction.getOppositeDirection(bcell.getOutputDirection()))) { // primo nastro è pezzo dritto
                // guarda se il nastro trasportatore ha un input nella data direzione, se si = nastro dritto, se no = curva
                //System.out.println("-> 1:straight belt odir= "+bcell.getOutputDirection());
                robotPos.changePosition(1, bcell.getOutputDirection(), rotation);
                movement = 1;
                animation[0] = movement+":"+bcell.getOutputDirection()+":"+rotation;
                //System.out.println("-> 1:robot posx="+robotPos.getPosX()+", posy="+robotPos.getPosY());
            } else {
                //nastro è curva
                turn = true;
                Rotation trot = BeltCell.getTurnRotation(bcell);
                //System.out.println("-> 1:turn belt rot= "+trot);
                robotPos.changePosition(0, trot);
                animation[0] = "0:"+robotPos.getDirection()+":"+trot;
            }

            BoardCell nextcell = theRobodrome.getCell(robotPos.getPosX(), robotPos.getPosY());
            if ((nextcell instanceof BeltCell && bcell.getType() == 'E') || turn) { //continua il viaggio
                bcell = (BeltCell) nextcell;
                turn = false;
                if (movement == 1 && !bcell.hasInputDirection(Direction.getOppositeDirection(bcell.getOutputDirection()))) { // nastro trasportatore è curva
                    turn = true;
                    // mette animation di rotazione in animation 1
                    Rotation trot = BeltCell.getTurnRotation(bcell);
                    //System.out.println("-> 2:turn belt rot= "+trot);
                    robotPos.changePosition(0, trot);
                    animation[1] = "0:"+robotPos.getDirection()+":"+trot;
                    dir = robotPos.getDirection();
                }

                if (!turn && movement == 0 && bcell.getType() == 'E') {
                    //movimento nastro doppio senza curve partendo da curva
                    movement = 2;
                    robotPos.changePosition(movement, bcell.getOutputDirection(), rotation);
                    animation[1] = movement+":"+bcell.getOutputDirection()+":"+rotation;
                } else {
                    robotPos.changePosition(movement, bcell.getOutputDirection(), rotation);
                    animation[turn ? 2 : 1] = movement + ":" + bcell.getOutputDirection() + ":" + rotation;
                }
            }
        } else if (currentcell instanceof PitCell) {
            // ripristina pos robot a ultima salvata e fa animazione muovi robot a quella
            // posx:posy:direction:pitfall
            Position checkpointPos = robot.getLastCheckpointPosition();
            animation[0] = checkpointPos.getPosX()+":"+checkpointPos.getPosY()+":"+checkpointPos.getDirection()+":pitfall";
            robot.setPosition(checkpointPos.clone());
            //System.out.println("after fall posX="+robot.getPosition().getPosX()+", posY="+robot.getPosition().getPosY());
        } else if (currentcell instanceof FloorCell) {
            FloorCell fcell = (FloorCell) currentcell;
            if (fcell.isCheckpoint()) {
                this.robot.setLastCheckpointPosition(robotPos.clone());
            } else if (fcell.isLeftRotator()) {
                // TODO: animazione gira a sinistra
            } else if (fcell.isRightRotator()) {
                // TODO: animazione gira a destra
            }
        }
        return animation;
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

    public void setRobodrome(Robodrome robodrome) {
        this.theRobodrome = robodrome;
    }

    public Robodrome getRobodrome() {
        return theRobodrome;
    }


}
