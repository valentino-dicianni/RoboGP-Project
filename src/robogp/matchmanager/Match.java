package robogp.matchmanager;

import connection.Connection;
import connection.Message;
import connection.MessageObserver;
import connection.PartnerShutDownException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
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

    public enum EndGame {
        First, First3, AllButLast
    };

    public enum State {
        Created, Started, Canceled
    };

    private static final String[] ROBOT_COLORS = {"blue", "red", "yellow", "emerald", "violet", "orange", "turquoise", "green"};
    private static final String[] ROBOT_NAMES = {"robot-blue", "robot-red", "robot-yellow", "robot-emerald", "robot-violet", "robot-orange", "robot-turquoise", "robot-green"};
    private final Robodrome theRobodrome;
    private final RobotMarker[] robots;
    private final EndGame endGameCondition;
    private final int nMaxPlayers;
    private final int nRobotsXPlayer;
    private final boolean initUpgrades;
    private State status;
    //private int readyPlayers;
    //private MatchHelper matchThread;

    private final HashMap<String, Connection> waiting;
    private final HashMap<String, Connection> players;

    /* Gestione pattern singleton */
    private static Match singleInstance;


    /** TODO
     * 	private class MatchHelper implements Runnable {
     *
     * 	    controlla ogni tot tempo che ci sia il numero di giocatori
     * 	    necessari per iniziare la partita invocando checkPlayersReady(){}
     * 	}
     *
     *
     *
     *
     * 	public syncronized boolean checkPlayersReady(){
     * 	    while (!available) {
     * 	        wait();
     * 	    }
     * 	    notifyAll();
     * 	    return readyPlayers == availablePlayers ;
     *
     * 	}
     */

    private Match(String rbdName, int nMaxPlayers, int nRobotsXPlayer, EndGame endGameCond, boolean initUpg) {
        this.nMaxPlayers = nMaxPlayers;
        this.nRobotsXPlayer = nRobotsXPlayer;
        this.endGameCondition = endGameCond;
        this.initUpgrades = initUpg;
        String rbdFileName = "robodromes/" + rbdName + ".txt";
        this.robots = new RobotMarker[Match.ROBOT_NAMES.length];
        this.theRobodrome = new Robodrome(rbdFileName);
        for (int i = 0; i < Match.ROBOT_NAMES.length; i++) {
            this.robots[i] = new RobotMarker(Match.ROBOT_NAMES[i], Match.ROBOT_COLORS[i]);
        }

        waiting = new HashMap<>();
        players = new HashMap<>();
        this.status = State.Created;
        //TODO
        //matchThread = new MatchHelper();
        //(new Thread(matchThread)).start();

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
        }

        /**
         * else{  msg.getName().equals(Match.ALTRO) } nel caso il match debba ricevere altri messaggi
         **/

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

        Message msg = new Message(Match.MatchStartMsg);

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

    public ArrayList<RobotMarker> getAvailableRobots() {
        ArrayList<RobotMarker> ret = new ArrayList<>();
        for (RobotMarker m : this.robots) {
            if (!m.isAssigned()) {
                ret.add(m);
            }
        }
        return ret;
    }

    public ArrayList<RobotMarker> getAllRobots() {
        ArrayList<RobotMarker> ret = new ArrayList<>();
        for (RobotMarker m : this.robots) {
            ret.add(m);
        }
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

    public boolean addPlayer(String nickname, List<RobotMarker> selection) {
        boolean added = false;
        try {
            for (RobotMarker rob : selection) {
                int dock = this.getFreeDock();
                rob.assign(nickname, dock);
            }

            Connection conn = this.waiting.get(nickname);

            Message reply = new Message(Match.MatchJoinReplyMsg);
            Object[] parameters = new Object[2];
            parameters[0] = new Boolean(true);
            parameters[1] = selection.toArray(new RobotMarker[selection.size()]);
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
        for (RobotMarker rob : this.robots) {
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
}
