package robogp.gameplayer;

import connection.Connection;
import connection.Message;
import connection.PartnerShutDownException;


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
