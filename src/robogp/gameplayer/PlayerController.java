package robogp.gameplayer;

import connection.Connection;
import connection.Message;
import connection.PartnerShutDownException;
import robogp.common.Instruction;
import robogp.matchmanager.MatchRobot;
import robogp.robodrome.Robodrome;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerController {
    private Connection connection;
    private HashMap<String,MatchRobot> robots = new HashMap<>();


    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public Connection getConnection() {
        return connection;
    }

    public void sendMessage(Message msg){
        try {
            connection.sendMessage(msg);
        } catch (PartnerShutDownException e) {
            e.printStackTrace();
        }
    }

    public void setRobots(MatchRobot[]robots) {
        for(MatchRobot rob : robots){
            this.robots.put(rob.getName(),rob);
        }
    }
    public ArrayList<Integer> getLockedReg(MatchRobot robot){
        return (robots.get(robot.getName())).getLocked();
    }


    //TODO
    public void setRegistry(MatchRobot robot, int regNum, Instruction instr){}


    //TODO
    public Robodrome getRobodrome(){
        return null;
    }

}
