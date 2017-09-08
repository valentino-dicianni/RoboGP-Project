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

}
