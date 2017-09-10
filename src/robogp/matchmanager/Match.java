package robogp.matchmanager;

import connection.Connection;
import connection.Message;
import connection.MessageObserver;
import connection.PartnerShutDownException;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import robogp.robodrome.Robodrome;

/**
 *
 * @author claudia
 */
public class Match implements MessageObserver {

    public static final int ROBOTSINGAME = 8;
    public static final String MatchJoinRequestMsg = "joinMatchRequest";
    public static final String MatchJoinReplyMsg = "joinMatchReply";
    public static final String MatchStartMsg = "startMatch";
    public static final String MatchCancelMsg = "cancelMatch";
    public static final String MatchErrorMsg = "errorMessage";
    public static final String MatchReadyMsg = "readyMessage";

    public static final String MancheInstructionPoolMsg = "instructionPool";
    public static final String MancheProgrammedRegistriesMsg = "programmedRegistries";

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
                System.out.println("\t-->Giocatori Pronti");
                sendInstructionPools();
                getReadyPlayers();
                System.out.println("\t-->Robot programmati");
                // a questo punto tutti i giocatori hanno programmato i propri robot
                // inizio ciclo principale della manche
                printRobots();
                for(int i = 0; i < 5; i++){
                    //manda schede
                    getReadyPlayers();
                    //dichiarazione
                    getReadyPlayers();
                    //esecuzione con robodromo
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

    /**/

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
        if (msg.getName().equals(Match.MatchJoinRequestMsg)) {
            String nickName = (String) msg.getParameter(0);
            boolean isCorrect = true;
            if(msg.getParameter(1) != null){
                char[] psswd =  MatchManagerApp.getAppInstance().getIniziarePartitaController().getServerAccessKey().toCharArray();
                isCorrect = Arrays.equals(psswd, (char[])msg.getParameter(1));
                System.out.println("\tpassword->  " +( isCorrect ? "Correct" : "Incorrect"));
                if(!isCorrect) {
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
            if(isCorrect){
                this.waiting.put(nickName, msg.getSenderConnection());
                MatchManagerApp.getAppInstance().getIniziarePartitaController().matchJoinRequestArrived(msg);
            }
        } else if (msg.getName().equals(Match.MatchReadyMsg)) {
            // messaggio da un giocatore, indica che è pronto per iniziare il match
            setReadyPlayers();
        } else if (msg.getName().equals(Match.MancheProgrammedRegistriesMsg)) {
            // messaggio da un giocatore contenente i registri dei suoi robot programmati, arg0 = nome giocatore
            try {
                setProgrammedRegistries((String) msg.getParameter(0), (HashMap<String, String>) msg.getParameter(1));
            } catch (Exception e) {
                Logger.getLogger(Match.class.getName()).log(Level.SEVERE, "MancheProgrammedRegistriesMsg: unable to cast message arguments", e);
            }
            setReadyPlayers();
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
        for (MatchRobot robot : getPlayerRobots(nickname)) {
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
        Object[]param = new Object[1];
        param[0] = theRobodrome.getName();
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

    public ArrayList<MatchRobot> getPlayerRobots(String nickname) {
        ArrayList<MatchRobot> playerRobots = new ArrayList<MatchRobot>();
        for (MatchRobot robot : robots) {
            if (robot != null && robot.getOwner().equals(nickname)) {
                playerRobots.add(robot);
            }
        }
        return playerRobots;
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
