package robogp.matchmanager;

import connection.Connection;
import connection.Message;
import connection.MessageObserver;
import connection.PartnerShutDownException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import robogp.robodrome.*;


/**
 *
 * @author claudia
 */
public class Match extends Observable implements MessageObserver{

    public static final int ROBOTSINGAME = 8;
    public static final String MatchJoinRequestMsg = "joinMatchRequest";
    public static final String MatchJoinReplyMsg = "joinMatchReply";
    public static final String MatchStartMsg = "startMatch";
    public static final String MatchCancelMsg = "cancelMatch";
    public static final String MatchErrorMsg = "errorMessage";
    public static final String MatchReadyMsg = "readyMessage";

    public static final String MancheInstructionPoolMsg = "instructionPool";
    public static final String MancheProgrammedRegistriesMsg = "programmedRegistries";
    public static final String MancheDeclarationSubPhaseMsg = "declarationSubPhase";
    public static final String MancheRobotsAnimationsMsg = "robotsMoveAnimations";
    public static final String MancheRobodromeActivationMsg = "robodromeActivationAnimations";
    public static final String MancheLasersAndWeaponsMsg = "lasersAndWeaponsAnimations";

    public enum EndGame {
        First, First3, AllButLast
    };


    public enum State {
        Created, Started, Canceled
    };

    private static final String[] ROBOT_COLORS = {"blue", "red", "yellow", "emerald", "violet", "orange", "turquoise", "green"};
    private static final String[] ROBOT_NAMES = {"robot-blue", "robot-red", "robot-yellow", "robot-emerald", "robot-violet", "robot-orange", "robot-turquoise", "robot-green"};
    private final Robodrome theRobodrome;
    private final MatchRobot[] robots;
    private final EndGame endGameCondition;
    private final int nMaxPlayers;
    private final int nRobotsXPlayer;
    private final boolean initUpgrades;
    private State status;
    private int readyPlayers = 0;
    private int numPlayers;

    private final HashMap<String, Connection> waiting;
    private final HashMap<String, Connection> players;

    private HashMap<String, List<MatchRobot>> ownedRobots;

    /* Gestione pattern singleton */
    private static Match singleInstance;


    /** TODO
     * 	quando un giocatore ha finito di programmare il robot per quella
     * 	manche chiama il metodo setReadyPlayers per aumentare di 1 il counter
     * 	una volta che sono tutti pronti si attiva il thread che eseguirà le istruzioni
     * 	e comunica tutto ai giocatori
     */

    private class MatchHelper implements Runnable {

        @Override
        public void run() {
            while(true){
                getReadyPlayers();
                log("Giocatori Pronti...");

                sendInstructionPools();
                getReadyPlayers();
                log("Tutti i robot sono stati programmati correttamente...");

                // a questo punto tutti i giocatori hanno programmato i propri robot
                // inizio ciclo principale della manche
                //printRobots();
                log("Inizio ciclo principale esecuzione Manche");
                for(int i = 1; i <= 5; i++) {
                    log("Inizio esecuzione registro "+i+"...");
                    //manda lista ordinata in base a priorità di schede istr ai giocatori secondo registro corrente
                    declarationSubPhase(i);
                    log("Tutti i robot sono stati programmati correttamente...");
                    getReadyPlayers();
                    //move subphase
                    moveSubPhase(i);
                    log("Tutte le animazioni della sottofase Mossa sono state inviate...");
                    getReadyPlayers();
                    log("Tutte le animazioni della sottofase Mossa sono terminate...");
                    //esecuzione con robodromo
                    robodromeActivationSubPhase();
                    log("Tutte le animazioni della sottofase Attivazione robodromo sono state inviate...");
                    getReadyPlayers();
                    log("Tutte le animazioni della sottofase Attivazione robodromo sono terminata...");
                    lasersAndWeaponsSubPhase();
                    getReadyPlayers();
                }

            }

        }
    }

    /*
    * metodi del ciclo principale di giocare
    * */

    /**
     * manda i pool di schede instruzione ai giocatori, calcolati in base ai punti vita dei robot
     */
    public void sendInstructionPools() {
        MatchInstructionManager instructionManager = MatchInstructionManager.getInstance();
        for(Map.Entry<String, Connection> player : players.entrySet()) {
            String nickname = player.getKey();
            Connection conn = player.getValue();

            Message msg = new Message(Match.MancheInstructionPoolMsg);
            Object[] param = new Object[1];

            //HashMap<String, ArrayList<String>> robotsPool =  new HashMap<>();
            HashMap<String, String> robotsPool =  new HashMap<>();

            for (MatchRobot robot : this.ownedRobots.get(nickname)) {
                // per ogni robot di un giocatore
                if (robot.getLifePoints() > 1) {
                    //ArrayList<String> stringpool = new ArrayList<>();
                    robotsPool.put(robot.getName(), instructionManager.getRandomInstructionPool(robot.getHitPoints() - 1).toString().replaceAll("[\\[\\]]", ""));
                }
            }

            param[0] = robotsPool;
            msg.setParameters(param);

            try {
                conn.sendMessage(msg);
            } catch (PartnerShutDownException ex) {
                Logger.getLogger(Match.class.getName()).log(Level.SEVERE, "Unable to send robot instruction pool to player: "+nickname, ex);
            }
        }
        instructionManager.resetInstructionPool();
    }

    /**
     * manda le schede istruzione che verranno eseguite nella sottofase per il dato registro
     * @param regNum numero del registro attualmente in eseguzione
     */
    public void declarationSubPhase(int regNum) {
        // messaggio: String strutturato "nomerobot:instrname:priority" separatore ","
        ArrayList<String> orderedInstr = new ArrayList<>();
        for(Map.Entry<String, List<MatchRobot>> robotlist : ownedRobots.entrySet()) {
            for (MatchRobot robot : robotlist.getValue()) {
                // per ogni robot di un giocatore
                Registry rReg = robot.getRegistry(regNum);
                if (!rReg.isLocked() && rReg.getInstruction() != null) {
                    orderedInstr.add(robot.getName()+":"+rReg.getInstruction().getName()+":"+rReg.getInstruction().getPriority());
                } else {
                    orderedInstr.add(robot.getName()+":locked:0");
                }
            }
        }

        orderedInstr.sort((str1, str2) -> -Integer.compare(Integer.parseInt(str1.split(":")[2]), Integer.parseInt(str2.split(":")[2])));
        //str1.split(":")[2].compareTo(str2.split(":")[2])

        //sperando che l'arraylist sia ordinata
        String message = orderedInstr.toString().replaceAll("[\\[\\]\\s]", "");

        broadcastMessage(message, Match.MancheDeclarationSubPhaseMsg);
    }

    public void moveSubPhase(int regNum) {
        // sottofase dove vengono calcolate le animazioni
        // si prendono i robot in ordine per
        ArrayList<MatchRobot> orderedRobotList = new ArrayList<>();
        for(Map.Entry<String, List<MatchRobot>> robotlist : ownedRobots.entrySet()) {
            //orderedRobotList.addAll(robotlist.getValue());
            for (MatchRobot validrobot : robotlist.getValue()) {
                if (!validrobot.getRegistry(regNum).isLocked() && validrobot.getRegistry(regNum).getInstruction() != null) orderedRobotList.add(validrobot);
            }
        }

        orderedRobotList.sort((mr1, mr2) -> -Integer.compare(mr1.getRegistry(regNum).getInstruction().getPriority(), mr2.getRegistry(regNum).getInstruction().getPriority()));

        // dalla lista ordinata di robot in base alla priorità dell'istruzione del registro regNum si fanno le animazioni di quel registro
        log("Verranno calcolate animazioni per "+orderedRobotList.size()+" robot.");
        //String[] animationInstr = new String[orderedRobotList.size()];
        ArrayList<String> animations = new ArrayList<>();
        int i = 0;
        for (MatchRobot robot : orderedRobotList) {
            Position robotPos = robot.getPosition();
            //System.out.println("initial posX="+robotPos.getPosX()+", posY="+robotPos.getPosY());
            MatchInstruction instrToExecute = robot.getRegistry(regNum).getInstruction();
            int steps;
            int stepstaken = 0;
            Direction chosendir = robotPos.getDirection();
            Rotation instrRot = instrToExecute.getRotation();

            try {
                for (steps = instrToExecute.getStepsToTake(); steps > 0; steps--) {
                    // controllo che non ci siano muri
                    if (this.theRobodrome.pathHasWall(robotPos.getPosX(), robotPos.getPosY(), chosendir)) {
                        break;
                    }
                    // il percorso per la prossima cella è libero, continuo a muovermi
                    robotPos.changePosition(1, instrRot);
                    // se la nuova prosizione è save point -> salvo posizione
                    BoardCell tempbcell = theRobodrome.getCell(robotPos.getPosX(), robotPos.getPosY());
                    if (tempbcell instanceof FloorCell) {
                        FloorCell tempfcell = (FloorCell) tempbcell;
                        if (tempfcell.isRepair())
                            robot.setLastCheckpointPosition(robotPos.clone());
                    }
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
                //animationInstr[i] = robot.getName() + ":" + stepstaken + ":" + chosendir + ":" + instrRot;
                animations.add(robot.getName() + ":" + stepstaken + ":" + chosendir + ":" + instrRot);
            } catch (ArrayIndexOutOfBoundsException e) {
                Position checkpointPos = robot.getLastCheckpointPosition();
                animations.add(robot.getName() + ":" + stepstaken + ":" + chosendir + ":" + instrRot);
                animations.add(robot.getName()+":"+checkpointPos.getPosX()+":"+checkpointPos.getPosY()+":"+checkpointPos.getDirection()+":outofrobodrome");
                robot.setPosition(checkpointPos.clone());
                continue;
            }
            i++;
        }

        log("Sono state calcolate "+i+" animazioni.");

        //String message = Arrays.toString(animationInstr).replaceAll("[\\[\\]\\s]", "");
        String message = animations.toString().replaceAll("[\\[\\]\\s]", "");

        broadcastMessage(message, MancheRobotsAnimationsMsg);
    }

    public void robodromeActivationSubPhase() {
        // fase attivazione robodromo, controlla la posizione di ogni robot
        // se un robot è finito su una cella attiva allora si aggiungono le animazioni per quel robot
        ArrayList<MatchRobot> robotsToAnimate = new ArrayList<>();
        for(Map.Entry<String, List<MatchRobot>> robotlist : ownedRobots.entrySet()) {
            for (MatchRobot robot : robotlist.getValue()) {
                if (this.theRobodrome.isCellActive(robot.getPosition().getPosX(), robot.getPosition().getPosY()))
                    robotsToAnimate.add(robot);
            }
        }

        log("Robodrome activation: "+robotsToAnimate.size()+" robots to animate.");

        ArrayList<String> animations = new ArrayList<>();

        for (MatchRobot robot : robotsToAnimate) {
            Position robotPos = robot.getPosition();
            Rotation rotation = Rotation.NO;
            String robotName = robot.getName();

            int movement = 0;
            BoardCell currentcell = this.theRobodrome.getCell(robotPos.getPosX(), robotPos.getPosY());

            if (currentcell instanceof BeltCell) {
                BeltCell bcell = (BeltCell) currentcell;
                Direction output = bcell.getOutputDirection();
                try {
                    if (this.theRobodrome.pathHasWall(robotPos.getPosX(), robotPos.getPosY(), output)) {
                        continue;
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
                        animations.add(robotName+":"+movement + ":" + bcell.getOutputDirection() + ":" + rotation);
                        //System.out.println("-> 1:robot posx="+robotPos.getPosX()+", posy="+robotPos.getPosY());
                    } else {
                        //nastro è curva
                        turn = true;
                        Rotation trot = BeltCell.getTurnRotation(bcell);
                        //System.out.println("-> 1:turn belt rot= "+trot);
                        robotPos.changePosition(0, trot);
                        animations.add(robotName+":0:" + robotPos.getDirection() + ":" + trot);
                    }

                    BoardCell nextcell = theRobodrome.getCell(robotPos.getPosX(), robotPos.getPosY());

                    if ((nextcell instanceof BeltCell && bcell.getType() == 'E') || turn) { //continua il viaggio
                        bcell = (BeltCell) nextcell;
                        turn = false;
                        if (this.theRobodrome.pathHasWall(robotPos.getPosX(), robotPos.getPosY(), bcell.getOutputDirection())) {
                            continue;
                        }
                        if (movement == 1 && !bcell.hasInputDirection(Direction.getOppositeDirection(bcell.getOutputDirection()))) { // nastro trasportatore è curva
                            turn = true;
                            Rotation trot = BeltCell.getTurnRotation(bcell);
                            robotPos.changePosition(0, trot);
                            animations.add(robotName+":0:" + robotPos.getDirection() + ":" + trot);
                        }

                        if (!turn && movement == 0 && bcell.getType() == 'E') {
                            //movimento nastro doppio senza curve partendo da curva
                            movement = 2;
                            robotPos.changePosition(movement, bcell.getOutputDirection(), rotation);
                            animations.add(robotName+":"+movement + ":" + bcell.getOutputDirection() + ":" + rotation);
                        } else {
                            robotPos.changePosition(movement, bcell.getOutputDirection(), rotation);
                            animations.add(robotName+":"+movement + ":" + bcell.getOutputDirection() + ":" + rotation);
                        }
                    }
                    currentcell = bcell;
                } catch (ArrayIndexOutOfBoundsException e) { // quando belt cell porta fuori da robodromo
                    Position checkpointPos = robot.getLastCheckpointPosition();
                    animations.add(robotName+":1:"+output+":"+Rotation.NO);
                    animations.add(robotName+":"+checkpointPos.getPosX()+":"+checkpointPos.getPosY()+":"+checkpointPos.getDirection()+":outofrobodrome");
                    robot.setPosition(checkpointPos.clone());
                    continue;
                }
            } else if (currentcell instanceof PitCell) { // quando cela è buco nero
                // ripristina pos robot a ultima salvata e fa animazione muovi robot a quella
                // robot perde una vita, e se ha ancora vite allora resetto posizione, altrimenti viene tolto da lista owned robots

                if (damageRobot(robot, 0, 1)) {
                    Position checkpointPos = robot.getLastCheckpointPosition();
                    animations.add(robotName+":"+checkpointPos.getPosX()+":"+checkpointPos.getPosY()+":"+checkpointPos.getDirection()+":pitfall");
                    robot.setPosition(checkpointPos.clone());
                } else {
                    animations.add(robotName+":-1:-1:NO:death");
                }

            } else if (currentcell instanceof FloorCell) {
                FloorCell fcell = (FloorCell) currentcell;
                if (fcell.isCheckpoint()) {
                    // robot.checkPointTouched();
                } else if (fcell.isRepair()) {
                    robot.setLastCheckpointPosition(robotPos.clone());
                } else if (fcell.isLeftRotator()) {
                    animations.add(robotName+":0:"+robotPos.getDirection()+":"+Rotation.CCW90);
                    robotPos.changePosition(0, Rotation.CCW90);
                } else if (fcell.isRightRotator()) {
                    animations.add(robotName+":0:"+robotPos.getDirection()+":"+Rotation.CW90);
                    robotPos.changePosition(0, Rotation.CW90);
                }
            }

            if (currentcell.hasHorizontalLaser() || currentcell.hasVerticalLaser()) {
                System.out.println("H or V laser cell");
                animations.add(robotName+":"+Direction.N+":"+"laserhit");
                if (!damageRobot(robot, 1, 0)) {
                    animations.add(robotName+":-1:-1:NO:death");
                }
            }
        }

        log("Robodrome activation end: "+animations.size()+" animations created.");

        String message = animations.toString().replaceAll("[\\[\\]\\s]", "");

        broadcastMessage(message, Match.MancheRobodromeActivationMsg);

    }

    private void lasersAndWeaponsSubPhase() {
        // la vista deve fare rv.addLaserFire(robots[0], Direction.E, 3, 15, false, false);
        // per ogni robot guardo se ci sono altri robot nella via del laser, se si colpisco, aggiungo anim e continue
        // se no, trovo il primo muro del robodromo dove far fermare il laser
        ArrayList<MatchRobot> allRobots = new ArrayList<>();
        for(Map.Entry<String, List<MatchRobot>> robotlist : ownedRobots.entrySet()) {
            allRobots.addAll(robotlist.getValue());
        }
        ArrayList<String> animations = new ArrayList<>();
        // se trova un robt nella direzione di sparo
        for (MatchRobot robot : allRobots) {
            int hitRobotX = -1;
            int hitRobotY = -1;
            String enemyDestroyed = "";
            Position robotPos = robot.getPosition();
            for (MatchRobot subRobot : allRobots) {
                boolean hit = false;
                if (subRobot != robot) { // tutti gli altri robot
                    int signX = robotPos.getPosX() - subRobot.getPosition().getPosX() > 0? -1:1;
                    int signY = robotPos.getPosY() - subRobot.getPosition().getPosY() > 0? -1:1;
                    if ((subRobot.getPosition().getPosX() == robotPos.getPosX() && Direction.getDirectionAxis(robotPos.getDirection()) == signY && Direction.isHorizontal(robotPos.getDirection())) || (
                            subRobot.getPosition().getPosY() == robotPos.getPosY() && Direction.getDirectionAxis(robotPos.getDirection()) == signX && !Direction.isHorizontal(robotPos.getDirection()))) {
                        // subot sulla linea di tiro
                        hitRobotX = subRobot.getPosition().getPosX();
                        hitRobotY = subRobot.getPosition().getPosY();
                        // aggiorno statistiche robot
                        if (!damageRobot(subRobot, 1, 0))
                            enemyDestroyed = subRobot.getName()+":death";
                        hit = true;
                    }
                }
                if (hit) break;
            }
            if (hitRobotX >= 0 && hitRobotY >= 0) { // calcolo animazione robot colpito
                //int startColumn = hitRobotX == robotPos.getPosX()? Math.abs(hitRobotY - robotPos.getPosY()) : Math.abs(hitRobotX - robotPos.getPosX());
                int startpoint = 0;
                int endpoint = 0;
                if (Direction.isHorizontal(robotPos.getDirection())) {
                    startpoint = robotPos.getPosX();
                    endpoint = hitRobotX;
                } else {
                    startpoint = robotPos.getPosY();
                    endpoint = hitRobotY;
                }
                animations.add(robot.getName()+":"+robotPos.getDirection()+":"+startpoint+":"+endpoint+":true:false");
                if (enemyDestroyed.length() > 0) animations.add(enemyDestroyed);
            } else {
                // nessun robot colpito, controllo se laser colpisce qualche muro
                if (!Direction.isHorizontal(robotPos.getDirection())) {
                    try {
                        for (int i = robotPos.getPosX(); i < theRobodrome.getColumnCount() && i >= 0; i = i + (Direction.getDirectionAxis(robotPos.getDirection()))) {
                            if (theRobodrome.pathHasWall(i, robotPos.getPosY(), robotPos.getDirection())) {
                                animations.add(robot.getName() + ":" + robotPos.getDirection() + ":" + robotPos.getPosX() + ":" + i + ":false:true");
                                break;
                            }
                        }
                    } catch (NullPointerException e) {
                        // no wall hit, out of robodrome
                        animations.add(robot.getName()+":"+robotPos.getDirection()+":"+robotPos.getPosX()+":"+theRobodrome.getColumnCount()+":false:false");
                    }
                } else {
                    try {
                        for (int i = robotPos.getPosY(); i < theRobodrome.getRowCount() && i >= 0; i = i + (Direction.getDirectionAxis(robotPos.getDirection()))) {
                            if (theRobodrome.pathHasWall(robotPos.getPosX(), i, robotPos.getDirection())) {
                                animations.add(robot.getName() + ":" + robotPos.getDirection() + ":" + robotPos.getPosY() + ":" + i + ":false:true");
                                break;
                            }
                        }
                    } catch (NullPointerException e) {
                        // no wall hit, out of robodrome
                        animations.add(robot.getName()+":"+robotPos.getDirection()+":"+robotPos.getPosY()+":"+theRobodrome.getRowCount()+":false:false");
                    }
                }
            }
        }
        log("Laser and weapons subphase end: "+animations.size()+" animations created.");

        String message = animations.toString().replaceAll("[\\[\\]\\s]", "");

        broadcastMessage(message, Match.MancheLasersAndWeaponsMsg);
    }

    private void touchAndSaveSubPhase() {
        // in questa sottofase vengono

        ArrayList<MatchRobot> robots = new ArrayList<>();
        for(Map.Entry<String, List<MatchRobot>> robotlist : ownedRobots.entrySet()) {
            robots.addAll(robotlist.getValue());
        }

        for (MatchRobot robot : robots) {
            Position robotPos = robot.getPosition();
            BoardCell tempbcell = theRobodrome.getCell(robotPos.getPosX(), robotPos.getPosY());
            if (tempbcell instanceof FloorCell) {
                FloorCell tempfcell = (FloorCell) tempbcell;
                if (tempfcell.isRepair())
                    robot.setLastCheckpointPosition(robotPos.clone());
            }
        }

    }

    /**
     * modifica i dati di robot in base ai danni presi, se alla fine del calcolo dei danni il robot è distrutto allora ritorna false
     * @param HPDamage danni subiti agli hit points
     * @param LPDamage danni subiti alle vite
     * @return true se dopo il calcolo dei danni il robot è ancora vivo, false altrimenti
     */
    private boolean damageRobot(MatchRobot robot, int HPDamage, int LPDamage) {
        int currentHP = robot.getHitPoints();
        int currentLP = robot.getLifePoints();

        currentHP -= HPDamage;

        if (HPDamage <= 0) { currentLP--; currentHP = 0; }

        currentLP -= LPDamage;

        if (currentLP > 0) {
            robot.setHitPoints(currentHP);
            robot.setLifePoints(currentLP);
            return true;
        } else {
            // rimuove riferimento di robot da owned robots
            for(Map.Entry<String, List<MatchRobot>> robotlist : ownedRobots.entrySet()) {
                robotlist.getValue().remove(robot);
            }
            return false;
        }
    }

    /**
     * manda in messaggio dato a tutti i giocatori
     * @param message messaggio da inviare
     * @param messageType tipo di messaggio da inviare
     */
    private void broadcastMessage(String message, String messageType) {
        int i = 0;
        for(Map.Entry<String, Connection> player : players.entrySet()) {
            String nickname = player.getKey();
            Connection conn = player.getValue();

            Message msg = new Message(messageType);
            Object[] param = new Object[1];

            param[0] = message;
            msg.setParameters(param);

            try {
                conn.sendMessage(msg);
            } catch (PartnerShutDownException ex) {
                Logger.getLogger(Match.class.getName()).log(Level.SEVERE, "Unable to send broadcast message "+messageType+" to player: "+nickname, ex);
            }
            i++;
        }

        log("Broadcast messages sent: "+i+" of type: "+messageType);
    }


    private void log(String message) {
        setChanged();
        notifyObservers(message);
    }

    private synchronized  void  setReadyPlayers() {
        while(readyPlayers == numPlayers){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        readyPlayers++;
        notifyAll();
    }

    private  synchronized void getReadyPlayers() {
        while(readyPlayers != numPlayers){
            try{
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        readyPlayers = 0;
        notifyAll();
    }

    private Match(String rbdName, int nMaxPlayers, int nRobotsXPlayer, EndGame endGameCond, boolean initUpg) {
        this.nMaxPlayers = nMaxPlayers;
        this.numPlayers = nMaxPlayers;
        this.nRobotsXPlayer = nRobotsXPlayer;
        this.endGameCondition = endGameCond;
        this.initUpgrades = initUpg;
        String rbdFileName = "robodromes/" + rbdName + ".txt";
        this.robots = new MatchRobot[Match.ROBOT_NAMES.length];
        this.theRobodrome = new Robodrome(rbdFileName);
        for (int i = 0; i < Match.ROBOT_NAMES.length; i++) {
            this.robots[i] = new MatchRobot(Match.ROBOT_NAMES[i], Match.ROBOT_COLORS[i]);
        }

        this.waiting = new HashMap<>();
        this.players = new HashMap<>();
        this.status = State.Created;

        this.ownedRobots = new HashMap<>();

        MatchHelper matchThread = new MatchHelper();
        (new Thread(matchThread)).start();

    }

    public static Match getInstance(String rbdName, int nMaxPlayers,
            int nRobotsXPlayer, EndGame endGameCond, boolean initUpg) {
        if (Match.singleInstance == null || Match.singleInstance.status == Match.State.Canceled) {
            Match.singleInstance = new Match(rbdName, nMaxPlayers, nRobotsXPlayer, endGameCond, initUpg);
        }
        return Match.singleInstance;
    }

    public static Match getInstance() {
        if (Match.singleInstance == null || Match.singleInstance.status == Match.State.Canceled) {
            return null;
        }
        return Match.singleInstance;
    }

    @Override
    public void notifyMessageReceived(Message msg) {
        switch (msg.getName()) {
            case Match.MatchJoinRequestMsg:
                String nickName = (String) msg.getParameter(0);
                boolean isCorrect = true;
                if (msg.getParameter(1) != null) {
                    char[] psswd = MatchManagerApp.getAppInstance().getIniziarePartitaController().getServerAccessKey().toCharArray();
                    isCorrect = Arrays.equals(psswd, (char[]) msg.getParameter(1));
                    System.out.println("\tpassword->  " + (isCorrect ? "Correct" : "Incorrect"));
                    if (!isCorrect) {
                        Message reply = new Message(Match.MatchErrorMsg);
                        Object[] parameters = new Object[1];
                        parameters[0] = "Incorrect Password";
                        reply.setParameters(parameters);
                        try {
                            msg.getSenderConnection().sendMessage(reply);
                        } catch (PartnerShutDownException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (isCorrect) {
                    this.waiting.put(nickName, msg.getSenderConnection());
                    MatchManagerApp.getAppInstance().getIniziarePartitaController().matchJoinRequestArrived(msg);
                }
                break;

            case Match.MatchReadyMsg:
                // messaggio da un giocatore, indica che è pronto per iniziare il match
                setReadyPlayers();
                break;

            case Match.MancheProgrammedRegistriesMsg:
                // messaggio da un giocatore contenente i registri dei suoi robot programmati, arg0 = nome giocatore
                try {
                    setProgrammedRegistries((String) msg.getParameter(0), (HashMap<String, String>) msg.getParameter(1));
                } catch (Exception e) {
                    Logger.getLogger(Match.class.getName()).log(Level.SEVERE, "MancheProgrammedRegistriesMsg: unable to cast message arguments", e);
                }
                setReadyPlayers();
                break;
        }
    }

    /**
     * metodo che aggiorna i registri dei robot del giocatore dato con i registri dati
     * @param nickname
     * @param registries
     */
    private void setProgrammedRegistries(String nickname, HashMap<String, String> registries) {
        // formato: hashmap<robot_name, string registri> registri è nel formato "numeroregistro:nomeistruzione:priorità" separato da ","
        // se un registro è bloccato ci sarà la stringa "--"
        for (MatchRobot robot : ownedRobots.get(nickname)) {
            for (String registry : registries.get(robot.getName()).split(",")) {
                if (registry.equals("--")) continue;
                String[] regData = registry.split(":");
                if (regData.length != 3) continue;

                int regNumber = Integer.parseInt(regData[0]);
                String instrName = regData[1];
                int regPriority = Integer.parseInt(regData[2]);

                robot.setRegistry(regNumber, MatchInstruction.getInstructionByName(instrName, regPriority));
            }
        }
    }

    public State getStatus() {
        return this.status;
    }

    public void cancel() {
        this.status = State.Canceled;

        Message msg = new Message(Match.MatchCancelMsg);
        for (String nickname : waiting.keySet()) {
            this.refusePlayer(nickname);
        }

        players.values().stream().forEach((conn) -> {
            try {
                conn.sendMessage(msg);
            } catch (PartnerShutDownException ex) {
                Logger.getLogger(Match.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public void start() {
        this.status = State.Started;
        numPlayers = getPlayerCount();
        Message msg = new Message(Match.MatchStartMsg);
        Object[]param = new Object[2];
        param[0] = theRobodrome.getName();

        ArrayList<MatchRobot> rOnBord = new ArrayList<>();
        ownedRobots.forEach((k,v) ->rOnBord.addAll(v));

        param[1] =rOnBord;

        msg.setParameters(param);

        players.values().stream().forEach((conn) -> {
            try {
                conn.sendMessage(msg);
            } catch (PartnerShutDownException ex) {
                Logger.getLogger(Match.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public void stop() {
        // PROBABILMENTE NON IMPLEMENTATO NEL CORSO DI QUESTO PROGETTO
    }

    public ArrayList<MatchRobot> getAvailableRobots() {
        ArrayList<MatchRobot> ret = new ArrayList<>();
        for (MatchRobot m : this.robots) {
            if (!m.isAssigned()) {
                ret.add(m);
            }
        }
        return ret;
    }

    public ArrayList<MatchRobot> getAllRobots() {
        ArrayList<MatchRobot> ret = new ArrayList<>();
        ret.addAll(Arrays.asList(this.robots));
        return ret;
    }

    public int getRobotsPerPlayer() {
        return this.nRobotsXPlayer;
    }

    public void refusePlayer(String nickname) {
        try {

            Connection conn = this.waiting.get(nickname);

            Message reply = new Message(Match.MatchJoinReplyMsg);
            Object[] parameters = new Object[1];
            parameters[0] = new Boolean(false);
            reply.setParameters(parameters);

            conn.sendMessage(reply);
        } catch (PartnerShutDownException ex) {
            Logger.getLogger(Match.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            waiting.remove(nickname);
        }
    }

    public boolean addPlayer(String nickname, List<MatchRobot> selection) {
        boolean added = false;
        try {
            for (MatchRobot rob : selection) {
                int dock = this.getFreeDock();
                rob.assign(nickname, dock);
            }

            ownedRobots.put(nickname, selection);

            Connection conn = this.waiting.get(nickname);

            Message reply = new Message(Match.MatchJoinReplyMsg);
            Object[] parameters = new Object[3];
            parameters[0] = new Boolean(true);
            parameters[1] = selection.toArray(new MatchRobot[selection.size()]);
            parameters[2] = theRobodrome.getName();
            reply.setParameters(parameters);
            conn.sendMessage(reply);

            for (MatchRobot rob : selection) {
                rob.setPosition(theRobodrome.getDockPosition(rob.getDock()));
            }
            this.players.put(nickname, conn);
            added = true;
        } catch (PartnerShutDownException ex) {
            Logger.getLogger(Match.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            waiting.remove(nickname);
        }
        return added;

    }

    private int getFreeDock() {
        boolean[] docks = new boolean[this.theRobodrome.getDocksCount()];
        for (MatchRobot rob : this.robots) {
            if (rob.isAssigned()) {
                docks[rob.getDock() - 1] = true;
            }
        }
        int count = 0;
        while (docks[count]) {
            count++;
        }
        if (count < docks.length) {
            return count + 1;
        }
        return -1;
    }

    public int getPlayerCount() {
        return this.players.size();
    }

    public int getMaxPlayers() {
        return this.nMaxPlayers;
    }

    public void printRobots() {
        for(MatchRobot rb : this.robots) {
            System.out.println(rb.toString());
        }
    }
}
