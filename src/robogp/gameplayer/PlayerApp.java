/*
 * Created by JFormDesigner on Tue Sep 05 11:17:31 CEST 2017
 */

package robogp.gameplayer;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import javax.swing.*;
import connection.Connection;
import connection.Message;
import connection.MessageObserver;
import connection.PartnerShutDownException;
import net.miginfocom.swing.*;
import robogp.matchmanager.Match;
import robogp.matchmanager.RobotMarker;

/**
 * @author valka getz
 */
public class PlayerApp extends JFrame implements MessageObserver {
    Connection connection;

    public PlayerApp() {
        initComponents();
    }

    private void accessButtonActionPerformed(ActionEvent e) {
        InetAddress address;
        try {
            address = InetAddress.getByName(addressInput.getText());
            this.connection = Connection.connectToHost(address, Integer.parseInt(portInput.getText()));
            connection.addMessageObserver(this);
            Message msg = new Message(Match.MatchJoinRequestMsg);
            Object[] pars = new Object[2];
            pars[0] = nickInput.getText();
            pars[1] = psswdInput.getPassword();
            msg.setParameters(pars);
            connection.sendMessage(msg);
            ((CardLayout) this.getContentPane().getLayout()).show(
                    this.getContentPane(), "wait");

        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        } catch (PartnerShutDownException e1) {
            e1.printStackTrace();
        }

    }

    private void backButtonActionPerformed(ActionEvent e) {
        ((CardLayout) this.getContentPane().getLayout()).show(
                this.getContentPane(), "access");
    }

    private void playButtonActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - valka getz
        accessPanel = new JPanel();
        label3 = new JLabel();
        label4 = new JLabel();
        label8 = new JLabel();
        addressInput = new JTextField();
        label1 = new JLabel();
        portInput = new JTextField();
        nickLabel = new JLabel();
        nickInput = new JTextField();
        psswdLabel = new JLabel();
        psswdInput = new JPasswordField();
        accessButton = new JButton();
        refusePanel = new JPanel();
        label7 = new JLabel();
        refuseLabel = new JLabel();
        backButton = new JButton();
        waitPanel = new JPanel();
        waitLabel = new JLabel();
        indietroButton = new JButton();
        playButton = new JButton();
        label6 = new JLabel();
        robotLabel = new JLabel();

        //======== this ========
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("RoboGP: The Game");
        Container contentPane = getContentPane();
        contentPane.setLayout(new CardLayout());

        //======== accessPanel ========
        {

            // JFormDesigner evaluation mark
            accessPanel.setBorder(new javax.swing.border.CompoundBorder(
                new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                    "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                    javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                    java.awt.Color.red), accessPanel.getBorder())); accessPanel.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

            accessPanel.setLayout(new MigLayout(
                "hidemode 3,alignx center",
                // columns
                "[fill]" +
                "[fill]" +
                "[fill]" +
                "[fill]",
                // rows
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]"));

            //---- label3 ----
            label3.setText("Benvenuto nella modalit\u00e0 sfida di RoboGP");
            label3.setFont(new Font("Lucida Grande", Font.BOLD, 16));
            accessPanel.add(label3, "cell 1 0");

            //---- label4 ----
            label4.setText("Accedi al server per giocare con i tuoi amici!");
            label4.setHorizontalAlignment(SwingConstants.CENTER);
            accessPanel.add(label4, "cell 1 1");

            //---- label8 ----
            label8.setText("IndirizzoIP:");
            accessPanel.add(label8, "cell 0 3");

            //---- addressInput ----
            addressInput.setText("localhost");
            accessPanel.add(addressInput, "cell 1 3");

            //---- label1 ----
            label1.setText("Porta: ");
            accessPanel.add(label1, "cell 0 4");
            accessPanel.add(portInput, "cell 1 4");

            //---- nickLabel ----
            nickLabel.setText("Nickname:");
            accessPanel.add(nickLabel, "cell 0 7");
            accessPanel.add(nickInput, "cell 1 7");

            //---- psswdLabel ----
            psswdLabel.setText("Password: ");
            accessPanel.add(psswdLabel, "cell 0 8");
            accessPanel.add(psswdInput, "cell 1 8");

            //---- accessButton ----
            accessButton.setText("Accedi");
            accessButton.addActionListener(e -> accessButtonActionPerformed(e));
            accessPanel.add(accessButton, "cell 1 10");
        }
        contentPane.add(accessPanel, "access");

        //======== refusePanel ========
        {
            refusePanel.setLayout(new MigLayout(
                "hidemode 3,alignx center",
                // columns
                "[fill]" +
                "[fill]",
                // rows
                "[]" +
                "[]" +
                "[]" +
                "[]"));

            //---- label7 ----
            label7.setText("Richiesta rifiutata:");
            label7.setFont(new Font("Lucida Grande", Font.BOLD, 16));
            refusePanel.add(label7, "cell 0 0");

            //---- refuseLabel ----
            refuseLabel.setText("text");
            refusePanel.add(refuseLabel, "cell 0 1");

            //---- backButton ----
            backButton.setText("Indietro");
            backButton.addActionListener(e -> backButtonActionPerformed(e));
            refusePanel.add(backButton, "cell 0 3");
        }
        contentPane.add(refusePanel, "refuse");

        //======== waitPanel ========
        {
            waitPanel.setLayout(new MigLayout(
                "hidemode 3,alignx center",
                // columns
                "[fill]" +
                "[fill]",
                // rows
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]" +
                "[]"));

            //---- waitLabel ----
            waitLabel.setText("Elaborazione Richiesta in corso...");
            waitLabel.setFont(new Font("Lucida Grande", Font.BOLD, 16));
            waitLabel.setHorizontalAlignment(SwingConstants.CENTER);
            waitPanel.add(waitLabel, "cell 0 1");

            //---- indietroButton ----
            indietroButton.setText("Indietro");
            indietroButton.setVisible(false);
            indietroButton.addActionListener(e -> backButtonActionPerformed(e));
            waitPanel.add(indietroButton, "cell 0 3");

            //---- playButton ----
            playButton.setText("Gioca");
            playButton.setVisible(false);
            playButton.addActionListener(e -> playButtonActionPerformed(e));
            waitPanel.add(playButton, "cell 0 8");

            //---- label6 ----
            label6.setText("In attesa di altri giocatori...");
            label6.setHorizontalAlignment(SwingConstants.CENTER);
            label6.setVisible(false);
            waitPanel.add(label6, "cell 0 7");

            //---- robotLabel ----
            robotLabel.setVisible(false);
            robotLabel.setText("<html>Robot assegnato:<br> ");
            robotLabel.setHorizontalAlignment(SwingConstants.CENTER);
            waitPanel.add(robotLabel, "cell 0 2");
        }
        contentPane.add(waitPanel, "wait");
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - valka getz
    private JPanel accessPanel;
    private JLabel label3;
    private JLabel label4;
    private JLabel label8;
    private JTextField addressInput;
    private JLabel label1;
    private JTextField portInput;
    private JLabel nickLabel;
    private JTextField nickInput;
    private JLabel psswdLabel;
    private JPasswordField psswdInput;
    private JButton accessButton;
    private JPanel refusePanel;
    private JLabel label7;
    private JLabel refuseLabel;
    private JButton backButton;
    private JPanel waitPanel;
    private JLabel waitLabel;
    private JButton indietroButton;
    private JButton playButton;
    private JLabel label6;
    private JLabel robotLabel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            PlayerApp player= new PlayerApp();
            player.setVisible(true);
        });
    }

    @Override
    public void notifyMessageReceived(Message msg) {
        if(msg.getName().equals(Match.MatchJoinReplyMsg)) {
            boolean reply = (Boolean)msg.getParameter(0);
            if(reply){
                waitLabel.setText("Richiesta Accettata");
//                waitLabel.setHorizontalAlignment(SwingConstants.CENTER);
                //gameRobot
                RobotMarker[] robots= (RobotMarker[]) msg.getParameter(1);
                for(RobotMarker robot : robots)
                    robotLabel.setText(robotLabel.getText() +"<br>- "+robot.getName()+ " al dock: " +robot.getDock());
                robotLabel.setText(robotLabel.getText() +"</html>");
                robotLabel.setVisible(true);
                label6.setVisible(true);

            }
            else{
                waitLabel.setText("Richiesta Rifiutata");
                indietroButton.setVisible(true);

            }

        }



    }
}
