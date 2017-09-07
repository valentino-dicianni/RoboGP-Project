package robogp.gameplayer;

import connection.Connection;
import connection.Message;
import connection.PartnerShutDownException;
import robogp.robodrome.Robodrome;

public class PlayerController {
    Connection connection;


    public PlayerController(){}

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

    //TODO
    public Robodrome getRobodrome(){
        return null;
    }
}
