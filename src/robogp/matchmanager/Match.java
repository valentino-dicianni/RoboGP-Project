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
    public static final String MancheRobotsRepositionsMsg = "robotsRepositionsAnimations";
    public static final String MancheRobodromeActivationMsg = "robodromeActivationAnimations";
    public static final String MancheLasersAndWeaponsMsg = "lasersAndWeaponsAnimations";
    public static final String MancheEndMsg = "mancheEnd";

    public enum EndGame {First, First3, AllButLast}
    public enum State {Created, Started, Canceled}

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

    private ArrayList<String> repositions;

    /* Gestione pattern singleton */
    private static Match singleInstance;


    /**
     * 	Thread MatchHeper:
     *
     * 	questo thread consente di avanzare l'esecusione del gioco.
     * 	All'inizio di ogni manche, non appena tutti i giocatori sono pronti,
     * 	vengono eseguite in sequenza tutte le sottofasi della manche, per ogni registro.
     * 	Alla fine di ogni sottofase viene mandato un messaggio ai giocatori
     * 	con i risultati dell'esecuzione della sottofase in corso.
     *
     *  La sincronizzazione avviene per mezzo dei metodi setReadyPlayer e getReadyPlayer,
     *  che, prendendo il lock sulla variabile readyPlayers, sincronizzano il thread di esecuzione
     *  rispetto alla disponibilità dei vari giocatori.
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
                log("Inizio ciclo principale esecuzione Manche...");
                for(int i = 1; i <= 5; i++) {
                    log("Inizio esecuzione registro "+i+"...");

                    //declaration subfase
                    log("Inizio sottofase Dichiarazione...");
                    declarationSubPhase(i);
                    log("Tutti i robot sono stati programmati correttamente...");
                    getReadyPlayers();

                    //move subphase
                    log("Inizio sottofase Mossa...");
                    String moveAnimations = moveSubPhase(i);
                    broadcastMessage(moveAnimations, Match.MancheRobotsAnimationsMsg);
                    getReadyPlayers();
                    syncRePositions(emptyRepositionsToString());
                    log("Tutte le animazioni della sottofase Mossa sono state inviate...");
                    getReadyPlayers();
                    log("Tutte le animazioni della sottofase Mossa sono terminate...");

                    //robodrome activation subfase
                    log("Inizio sottofase Attivazione Robodromo...");
                    String activAnimations = robodromeActivationSubPhase();
                    broadcastMessage(activAnimations, Match.MancheRobodromeActivationMsg);
                    getReadyPlayers();
                    syncRePositions(emptyRepositionsToString());
                    log("Tutte le animazioni della sottofase Attivazione robodromo sono state inviate...");
                    getReadyPlayers();
                    log("Tutte le animazioni della sottofase Attivazione robodromo sono terminata...");

                    //laser and weapon subfase
                    log("Inizio sottofase Laser & Weapon...");
                    String weapAnimations = lasersAndWeaponsSubPhase();
                    broadcastMessage(weapAnimations, Match.MancheLasersAndWeaponsMsg);
                    getReadyPlayers();
                    syncRePositions(emptyRepositionsToString());
                    log("Tutte le animazioni della sottofase Lasers & Weapons sono state inviate...");
                    getReadyPlayers();
                    log("Tutte le animazioni della sottofase Lasers & Weapons sono terminate...");

                    //touch and save subfase
                    log("Inizio sottofase touch and save...");
                    touchAndSaveSubPhase();
                }

                log("Fine manche, reset per prossima manche...");
                String endMessage = endManche();
                broadcastMessage(endMessage, Match.MancheEndMsg);
            }

        }
    }

    private String emptyRepositionsToString() {
        String rp = repositions.toString().replaceAll("[\\[\\]\\s]", "");
        this.repositions.clear();
        // fare la new?
        return rp;
    }

    /**
     * manda i pool di schede instruzione ai giocatori, calcolati in base ai punti vita dei robot
     */
    private void sendInstructionPools() {
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
                    ArrayList<MatchInstruction> newInstructionsPool = instructionManager.getRandomInstructionPool(robot.getHitPoints() - 1);
                    robotsPool.put(robot.getName(), newInstructionsPool.toString().replaceAll("[\\[\\]\\s]", ""));
                    // setta robot instruction pool
                    robot.setInstructionsPool(newInstructionsPool);
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
    private void declarationSubPhase(int regNum) {
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

    public String moveSubPhase(int regNum) {
        // sottofase dove vengono calcolate le animazioni
        // si prendono i robot in ordine per
        ArrayList<MatchRobot> orderedRobotList = new ArrayList<>();
        for(Map.Entry<String, List<MatchRobot>> robotlist : ownedRobots.entrySet()) {
            //orderedRobotList.addAll(robotlist.getValue());
            for (MatchRobot validrobot : robotlist.getValue()) {
                if (!validrobot.getRegistry(regNum).isLocked() && validrobot.getRegistry(regNum).getInstruction() != null) orderedRobotList.add(validrobot);
            }
        }

        //ordina i robot in base alla priorità della scheda istruzione nel registro da eseguire
        orderedRobotList.sort((mr1, mr2) -> -Integer.compare(mr1.getRegistry(regNum).getInstruction().getPriority(), mr2.getRegistry(regNum).getInstruction().getPriority()));

        // dalla lista ordinata di robot in base alla priorità dell'istruzione del registro regNum si fanno le animazioni di quel registro
        // array di robot che durante l'eseguzione vengono spinti fuori o cadono e quindi non possono eseguire la lora istruzione per quel registro
        ArrayList<MatchRobot> robotsOut = new ArrayList<>();
        log("Verranno calcolate animazioni per "+orderedRobotList.size()+" robot...");
        ArrayList<String> animations = new ArrayList<>();
        //ArrayList<String> repositions = new ArrayList<>();
        int i = 0;
        for (MatchRobot robot : orderedRobotList) {
            if (robotsOut.contains(robot)) continue;
            Position robotPos = robot.getPosition();
            MatchInstruction instrToExecute = robot.getRegistry(regNum).getInstruction();
            int stepstaken = 0;
            Direction chosendir = robotPos.getDirection();
            Rotation instrRot = instrToExecute.getRotation();
            //boolean pitfall = false;

            int stepsToTake = instrToExecute.getStepsToTake();

            if (stepsToTake > 0) {
                // move 1-2-3
                ArrayList<MatchRobot> robotTrain = new ArrayList<>(); // = robot train
                robotTrain.add(robot);
                MatchRobot lastRobot = robot;

                for (stepstaken = 0; stepstaken < stepsToTake && robotTrain.size() > 0; stepstaken++) {
                    // aggiorna lista robot adiacenti all'ultimo robot della fila prima di muovere il treno
                    robotTrain.addAll(getAdiacentRobots(lastRobot, chosendir));
                    lastRobot = robotTrain.get(robotTrain.size() - 1);
                    Position lastRobotPos = lastRobot.getPosition();
                    boolean lastExit = false; //indica se l'utimo robot della cosa è uscito dal robodromo
                    try {
                        if (theRobodrome.pathHasWall(lastRobotPos.getPosX(), lastRobotPos.getPosY(), chosendir)) {
                            // c'è un muro: il train si ferma
                            break;
                        }
                    } catch (ArrayIndexOutOfBoundsException e) {
                        lastExit = true;
                    }
                    // a questo punto il percorso non ha muri
                    // fanno passo tutti i robot della coda
                    StringBuilder strbld = new StringBuilder();
                    for (MatchRobot arb : robotTrain) {
                        arb.getPosition().changePosition(1, chosendir, Rotation.NO);
                        if (arb != robot) // robot che spinge tutti non va nella lista dei robot spinti
                            strbld.append(arb.getName() + "§");
                    }
                    if (strbld.length() > 0)
                        strbld.deleteCharAt(strbld.length() - 1);

                    if (robotTrain.size() > 1) {
                        animations.add(robot.getName() + ":1:" + chosendir + ":" + Rotation.NO + ":" +strbld.toString()); // robot train
                    } else {
                        animations.add(robot.getName() + ":1:" + chosendir + ":" + Rotation.NO); // robot singolo
                    }

                    // controllo se l'ultimo robot è finito in buco nero o è uscito dal robodromo
                    if (lastExit || theRobodrome.isCellPit(lastRobotPos.getPosX(), lastRobotPos.getPosY())) {
                        animations.add(lastRobot.getName() + ":"+ (lastExit?"outofrobodrome":"pitfall"));
                        Position checkpointPos = lastRobot.getLastCheckpointPosition();
                        repositions.add(lastRobot.getName() + ":" + checkpointPos.getPosX() + ":" +
                                checkpointPos.getPosY() + ":" + checkpointPos.getDirection() + ":"+damageRobot(lastRobot, 0, 1));
                        lastRobot.setPosition(checkpointPos.clone());
                        robotTrain.remove(lastRobot);
                        //il robot uscito non deve eseguire la sua istruzione
                        robotsOut.add(lastRobot);
                    }
                }
            } else if (stepsToTake < 0) {
                // backup
                //stepstaken = Math.abs(stepsToTake);
                Direction oppositedir = Direction.getOppositeDirection(chosendir);
                ArrayList<MatchRobot> robotTrain = new ArrayList<>();
                robotTrain.add(robot);
                robotTrain.addAll(getAdiacentRobots(robot, oppositedir));
                MatchRobot lastRobot = robotTrain.get(robotTrain.size() - 1);
                Position lastRobotPos = lastRobot.getPosition();
                boolean lastout = false;
                boolean walled = false;
                try {
                    walled = theRobodrome.pathHasWall(lastRobotPos.getPosX(), lastRobotPos.getPosY(), oppositedir);
                } catch (ArrayIndexOutOfBoundsException e) {
                    lastout = true;
                }
                if (!walled) {

                    StringBuilder strbld = new StringBuilder();
                    for (MatchRobot arb : robotTrain) {
                        arb.getPosition().changePosition(-1, chosendir, Rotation.NO);
                        if (arb != robot)
                            strbld.append(arb.getName() + "§");
                    }
                    if (strbld.length() > 0)
                        strbld.deleteCharAt(strbld.length() - 1);
                    if (robotTrain.size() > 1) {
                        animations.add(robot.getName() + ":1:" + oppositedir + ":" + Rotation.NO + ":" +strbld.toString()); // robot train
                    } else {
                        animations.add(robot.getName() + ":1:" + oppositedir + ":" + Rotation.NO); // robot singolo
                    }
                    if (lastout || theRobodrome.isCellPit(lastRobotPos.getPosX(), lastRobotPos.getPosY())) {
                        // last robot va fuori o cade in pit
                        animations.add(lastRobot.getName() + ":"+ (lastout?"outofrobodrome":"pitfall"));
                        Position checkpointPos = lastRobot.getLastCheckpointPosition();
                        repositions.add(lastRobot.getName() + ":" + checkpointPos.getPosX() + ":" + checkpointPos.getPosY() + ":" + checkpointPos.getDirection() + ":"+damageRobot(lastRobot, 0, 1));
                        lastRobot.setPosition(checkpointPos.clone());
                        //il robot uscito non deve eseguire la sua istruzione
                        robotsOut.add(lastRobot);
                    }
                }
            } else {
                // rotate
                robotPos.changePosition(0, instrRot);
                animations.add(robot.getName() + ":0:" + chosendir + ":" + instrRot);
            }
        }

        log("Sono state calcolate "+i+" animazioni...");

        // ritorna animation e mette le repositions come variabile globale che ogni volta svuoto quando le mando ai client
        // diventano public, ritornano stringa delle animazioni da fare, non manda broadcast msg quello lo manda il thread

        return animations.toString().replaceAll("[\\[\\]\\s]", "");
    }

    public String robodromeActivationSubPhase() {
        // fase attivazione robodromo, controlla la posizione di ogni robot
        // se un robot è finito su una cella attiva allora si aggiungono le animazioni per quel robot
        ArrayList<MatchRobot> robotsToAnimate = new ArrayList<>();
        for(Map.Entry<String, List<MatchRobot>> robotlist : ownedRobots.entrySet()) {
            for (MatchRobot robot : robotlist.getValue()) {
                if (this.theRobodrome.isCellActive(robot.getPosition().getPosX(), robot.getPosition().getPosY()))
                    robotsToAnimate.add(robot);
            }
        }

        log("Robodrome activation: "+robotsToAnimate.size()+" robots to animate...");

        ArrayList<String> animations = new ArrayList<>();
        //ArrayList<String> repositions = new ArrayList<>();

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
                    /*Position checkpointPos = robot.getLastCheckpointPosition();
                    animations.add(robotName+":1:"+output+":"+Rotation.NO);
                    animations.add(robotName+":"+checkpointPos.getPosX()+":"+
                    checkpointPos.getPosY()+":"+checkpointPos.getDirection()+":outofrobodrome");
                    robot.setPosition(checkpointPos.clone());*/

                    Position checkpointPos = robot.getLastCheckpointPosition();
                    animations.add(robotName + ":1:" + output + ":" + Rotation.NO);
                    repositions.add(robotName + ":" + checkpointPos.getPosX() + ":" + checkpointPos.getPosY() + ":"
                            + checkpointPos.getDirection() + ":"+damageRobot(robot, 0, 1));
                    robot.setPosition(checkpointPos.clone());
                    continue;
                }
            } else if (currentcell instanceof PitCell) { // quando cela è buco nero
                // ripristina pos robot a ultima salvata e fa animazione muovi robot a quella
                // robot perde una vita, e se ha ancora vite allora resetto posizione, altrimenti viene tolto da lista owned robots
                animations.add(robotName + ":pitfall");
                Position checkpointPos = robot.getLastCheckpointPosition();
                repositions.add(robotName+":"+checkpointPos.getPosX()+":"+checkpointPos.getPosY()+":"
                        +checkpointPos.getDirection()+":"+damageRobot(robot, 0, 1));
                robot.setPosition(checkpointPos.clone());
            } else if (currentcell instanceof FloorCell) {
                FloorCell fcell = (FloorCell) currentcell;
                if (fcell.isCheckpoint()) {
                    // robot.checkPointTouched();
                    robot.touchCheckpoint(fcell.getCheckpoint());
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
                if (!damageRobot(robot, 1, 0))
                    repositions.add(robotName+":-1:-1:E:false");
            }
        }
        log("Robodrome activation end: "+animations.size()+" animations created.");

        return animations.toString().replaceAll("[\\[\\]\\s]", "");
    }

    public String lasersAndWeaponsSubPhase() {
        // la vista deve fare rv.addLaserFire(robots[0], Direction.E, 3, 15, false, false);
        // per ogni robot guardo se ci sono altri robot nella via del laser, se si colpisco, aggiungo anim e continue
        // se no, trovo il primo muro del robodromo dove far fermare il laser
        ArrayList<MatchRobot> allRobots = new ArrayList<>();
        for(Map.Entry<String, List<MatchRobot>> robotlist : ownedRobots.entrySet()) {
            allRobots.addAll(robotlist.getValue());
        }
        ArrayList<String> animations = new ArrayList<>();
        //ArrayList<String> repositions = new ArrayList<>();
        // se trova un robt nella direzione di sparo
        for (MatchRobot robot : allRobots) {
            // controllo il primo oggetto che il laser colpirebbe se non ci fossero robot, salvo la x/y della cella
            // poi guardo tutti i robot sul robodromo, ogni volta che ne trovo uno
            // che verrebbe colpito prima del bersaglio attuale, aggiorno la pos
            Position robotPos = robot.getPosition();
            int directionAxis = Direction.getDirectionAxis(robotPos.getDirection());
            int offset = -1;
            boolean wallhit = false;
            if (!Direction.isHorizontal(robotPos.getDirection())) { // VERTICAL muovo sulle X
                try {
                    for (int i = robotPos.getPosX(); i < theRobodrome.getRowCount() && i >= 0; i = i + (directionAxis)) {
                        if (theRobodrome.pathHasWall(i, robotPos.getPosY(), robotPos.getDirection())) {
                            offset = i;
                            wallhit = true;
                            break;
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    // no wall hit, out of robodrome
                    offset = Direction.getDirectionAxis(robotPos.getDirection()) > 0? theRobodrome.getRowCount(): 0;
                }
            } else {    // HORIZONTAL muovo sulle Y
                try {
                    for (int i = robotPos.getPosY(); i < theRobodrome.getColumnCount() && i >= 0; i = i + (directionAxis)) {
                        if (theRobodrome.pathHasWall(robotPos.getPosX(), i, robotPos.getDirection())) {
                            offset = i;
                            wallhit = true;
                            break;
                        }
                    }
                } catch (ArrayIndexOutOfBoundsException e) {
                    // no wall hit, out of robodrome
                    offset = Direction.getDirectionAxis(robotPos.getDirection()) > 0? theRobodrome.getColumnCount(): 0;
                }
            }

            MatchRobot hitEnemy = null;

            for (MatchRobot enemyRobot : allRobots) {
                Position enemyPos = enemyRobot.getPosition();
                switch (robotPos.getDirection()) {
                    case W:
                        if (enemyPos.getPosX() == robotPos.getPosX()
                                && enemyPos.getPosY() < robotPos.getPosY() && offset <= enemyPos.getPosY() ) {
                            hitEnemy = enemyRobot;
                            offset = enemyPos.getPosY();
                            wallhit = false;
                        }
                        break;
                    case E:
                        if (enemyPos.getPosX() == robotPos.getPosX()
                                && enemyPos.getPosY() > robotPos.getPosY() && offset >= enemyPos.getPosY() ) {
                            hitEnemy = enemyRobot;
                            offset = enemyPos.getPosY();
                            wallhit = false;
                        }
                        break;
                    case N:
                        if (enemyPos.getPosY() == robotPos.getPosY()
                                && enemyPos.getPosX() < robotPos.getPosX() && offset <= enemyPos.getPosX() ) {
                            hitEnemy = enemyRobot;
                            offset = enemyPos.getPosX();
                            wallhit = false;
                        }
                        break;
                    case S:
                        if (enemyPos.getPosY() == robotPos.getPosY()
                                && enemyPos.getPosX() > robotPos.getPosX() && offset >= enemyPos.getPosX() ) {
                            hitEnemy = enemyRobot;
                            offset = enemyPos.getPosX();
                            wallhit = false;
                        }
                        break;
                }
            }

            if (Direction.isHorizontal(robotPos.getDirection())) // si spara in orizzontale
                animations.add(robot.getName() + ":" + robotPos.getDirection() + ":" + robotPos.getPosY() + ":"
                        + offset + ":"+(hitEnemy != null? hitEnemy.getName(): "false")+":"+wallhit);
            else // si spara in verticale
                animations.add(robot.getName() + ":" + robotPos.getDirection() + ":" + robotPos.getPosX() + ":"
                        + offset + ":"+(hitEnemy != null? hitEnemy.getName(): "false")+":"+wallhit);
            if (hitEnemy != null) {
                int prehitLP = hitEnemy.getLifePoints();
                if (damageRobot(hitEnemy, 1, 0)) {
                    if (prehitLP != hitEnemy.getLifePoints()) {
                        // robot ha perso una vita e deve errere riposizionato in ultimo checkpoint
                        animations.add(hitEnemy.getName() + ":death");
                        Position checkpointPos = hitEnemy.getLastCheckpointPosition();
                        repositions.add(hitEnemy.getName() + ":" + checkpointPos.getPosX() + ":" + checkpointPos.getPosY() + ":" + checkpointPos.getDirection() + ":true");
                        hitEnemy.setPosition(checkpointPos.clone());
                    }
                } else {
                    // robot ha ricevuto colpo fatale e non ha più vite = reposition con death
                    animations.add(hitEnemy.getName() + ":death");
                    repositions.add(hitEnemy.getName() + ":-1:-1:" + Direction.N + ":false");
                }
            }
        }
        log("Laser and weapons subphase end: "+animations.size()+" animations created...");

        return animations.toString().replaceAll("[\\[\\]\\s]", "");
    }

    /**
     * In questa sottofase vengono salvate la posizione dei robot se sono finiti
     * su repair/checkpoint. Quelli finiti su repair guadagnano anche punto vita
     */
    private void touchAndSaveSubPhase() {
        //NON FARE: distribuzione upgrade

        ArrayList<MatchRobot> robots = new ArrayList<>();
        for(Map.Entry<String, List<MatchRobot>> robotlist : ownedRobots.entrySet()) {
            robots.addAll(robotlist.getValue());
        }

        for (MatchRobot robot : robots) {
            Position robotPos = robot.getPosition();
            BoardCell tempbcell = theRobodrome.getCell(robotPos.getPosX(), robotPos.getPosY());
            if (tempbcell instanceof FloorCell) {
                FloorCell tempfcell = (FloorCell) tempbcell;
                if (tempfcell.isRepair()) {
                    robot.setLastCheckpointPosition(robotPos.clone());
                    int rHP = robot.getHitPoints();
                    if (rHP < 10) robot.setHitPoints(rHP+1);
                } else if (tempfcell.isCheckpoint()) {
                    boolean checkpointTouched = robot.touchCheckpoint(tempfcell.getCheckpoint());
                }
            }
        }
    }

    private String endManche() {
        // tolgo schede dai registri dei robot e setto i registri bloccati in base ai punti vita attuali del robot
        ArrayList<String> robotsUpdated = new ArrayList<>();
        MatchRobot robotWinner = null;
        for(Map.Entry<String, List<MatchRobot>> robotlist : ownedRobots.entrySet()) {
            for (MatchRobot robot : robotlist.getValue()) {
                if (robot.getLifePoints() < 1) { // rimuovo robot se non ha più punti vita
                    robotsUpdated.add(robot.getName()+":dead");
                    robotlist.getValue().remove(robot);
                    continue;
                }
                if (robot.checkpointsAllTouched()) robotWinner = robot;
                robot.resetRegistries();
                int hitpoints = robot.getHitPoints();
                int regAval = 5;
                // setta registri bloccati in base agli hp
                switch (hitpoints) {
                    case 1:
                        robot.setRegistryLock(1, true);
                        regAval--;
                    case 2:
                        robot.setRegistryLock(2, true);
                        regAval--;
                    case 3:
                        robot.setRegistryLock(3, true);
                        regAval--;
                    case 4:
                        robot.setRegistryLock(4, true);
                        regAval--;
                    case 5:
                        robot.setRegistryLock(5, true);
                        regAval--;
                }
                robotsUpdated.add(robot.getName()+":"+hitpoints+":"+robot.getLifePoints()+":"+regAval);
            }
        }
        log("Manche end: "+robotsUpdated.size()+" robots updated");
        if (robotsUpdated.size() == 0)
            robotsUpdated.add("alldied:matchend");
        if (robotWinner != null)
            robotsUpdated.add(robotWinner.getName()+":matchend");

        return robotsUpdated.toString().replaceAll("[\\[\\]\\s]", "");
    }

    /**
     * ritorna array di tutti i robot che sono vicini alla posizione data e che verrebbero
     * spostati se un robot si muovesse di 1 in quella direzione.
     * Se si trovasse un secondo robot anchesso vicino al primo robot e che verrebbe spostato,
     * allora anch'esso è incluso in questa lista, e così via.
     * @param targetRobot robot di cui si vuole controllare le vicinanze
     * @return arraylist di robot vicini, oppure null se non ci si può muovere in quella direzione causa muro
     */
    private ArrayList<MatchRobot> getAdiacentRobots(MatchRobot targetRobot, Direction dir) {
        ArrayList<MatchRobot> affectedRobots = new ArrayList<>();
        Position position = targetRobot.getPosition().clone();
        if (dir != null) position.setDirection(dir);
        ArrayList<MatchRobot> allrobots = new ArrayList<>();
        for (Map.Entry<String, List<MatchRobot>> robotlist : ownedRobots.entrySet()) {
            for (MatchRobot robot : robotlist.getValue()) {
                if (robot != targetRobot) allrobots.add(robot);
            }
        }
        try {

            if (this.theRobodrome.pathHasWall(position.getPosX(), position.getPosY(), position.getDirection())) {
                //return null;
                return affectedRobots;
            }

            position.changePosition(1, Rotation.NO);

            boolean complete = false;

            while (!complete) {
                boolean found = false;
                // per ogni robot sul robodromo cerco se ce n'è uno davanti alla pos data
                for (MatchRobot robot : allrobots) {
                    Position rpos = robot.getPosition();
                    if (rpos.getPosX() == position.getPosX() && rpos.getPosY() == position.getPosY()) {
                        affectedRobots.add(robot);
                        allrobots.remove(robot);
                        found = true;
                        break;
                    }
                }
                if (!found) complete = true;
                if (this.theRobodrome.pathHasWall(position.getPosX(), position.getPosY(), position.getDirection())) {
                    //return null;
                    return affectedRobots;
                }
                position.changePosition(1, Rotation.NO);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            return affectedRobots;
        }
        return affectedRobots;
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

        if (currentHP <= 0) { currentLP--; currentHP = 10; }

        currentLP -= LPDamage;

        robot.setHitPoints(currentHP);
        robot.setLifePoints(currentLP);
        if (currentLP > 0) {
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
     * manda messaggio in broadcast dei robot che sono morti/finiti in buchi neri/usciti dal robodromo
     * utilizzato nelle sottofasi di move e activation
     * @param repositionMessage
     */
    private void syncRePositions(String repositionMessage) {
        broadcastMessage(repositionMessage, Match.MancheRobotsRepositionsMsg);
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
                Logger.getLogger(Match.class.getName()).log(Level.SEVERE,
                        "Unable to send broadcast message "+messageType+" to player: "+nickname, ex);
            }
            i++;
        }
        log("Broadcast messages sent: "+i+" of type: "+messageType+"...");
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
            this.robots[i] = new MatchRobot(Match.ROBOT_NAMES[i], Match.ROBOT_COLORS[i], this.theRobodrome.getCheckpointsCount());
        }

        this.waiting = new HashMap<>();
        this.players = new HashMap<>();
        this.status = State.Created;

        this.ownedRobots = new HashMap<>();

        this.repositions = new ArrayList<>();

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
                    Logger.getLogger(Match.class.getName()).log(Level.SEVERE,
                            "MancheProgrammedRegistriesMsg: unable to cast message arguments", e);
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
        System.out.println("-> PR: programmed registries received from player "+nickname+"; ");
        for (MatchRobot robot : ownedRobots.get(nickname)) {
            System.out.println("->-> PR: "+registries.get(robot.getName()));
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

    public void setOwnedRobots(HashMap<String, List<MatchRobot>> ownedRobots) {
        this.ownedRobots = ownedRobots;
    }

    public HashMap<String, List<MatchRobot>> getOwnedRobots() {
        return ownedRobots;
    }

    public void printRobots() {
        for(MatchRobot rb : this.robots) {
            System.out.println(rb.toString());
        }
    }
}
