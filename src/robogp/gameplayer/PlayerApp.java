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
import javax.swing.border.*;
import connection.Connection;
import connection.Message;
import connection.MessageObserver;
import net.miginfocom.swing.*;
import robogp.matchmanager.Match;
import robogp.matchmanager.RobotMarker;
import robogp.robodrome.Robodrome;
import robogp.robodrome.view.RobodromeView;

/**
 * @author valka getz
 */
public class PlayerApp implements MessageObserver {
    PlayerController controller = new PlayerController();
    private RobodromeView rv;


    public PlayerApp() {
        initComponents();
    }

    private void accessButtonActionPerformed(ActionEvent e) {
        if(ceckCredentials()){
            InetAddress address;
            try {
                address = InetAddress.getByName(addressInput.getText());
                controller.setConnection(Connection.connectToHost(address, Integer.parseInt(portInput.getText())));
                controller.getConnection().addMessageObserver(this);
                Message msg = new Message(Match.MatchJoinRequestMsg);
                Object[] pars = new Object[2];
                pars[0] = nickInput.getText();
                pars[1] = psswdInput.getPassword();
                msg.setParameters(pars);
                controller.sendMessage(msg);
                ((CardLayout) setupFrame.getContentPane().getLayout()).show(
                        setupFrame.getContentPane(), "wait");

            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }

    private boolean ceckCredentials(){
        int port = Integer.parseInt(portInput.getText());
        String address = addressInput.getText();
        String nick = nickInput.getText();

        if((port < 1024 || port > 65535)){
            System.out.println("PORTA : "+port);
            JOptionPane.showMessageDialog(setupFrame, "La porta deve essere un numero\ncompreso fra 1024 e 65535");
            return false;
        }
        else if(nick.equals("")){
            JOptionPane.showMessageDialog(setupFrame, "Devi inserire un nickname per giocare");
            return false;
        }
        else if(address.equals("")){
            JOptionPane.showMessageDialog(setupFrame, "Devi inserire un indirizzoIP per giocare");
            return false;
        }
        else return true;
    }

    private void backButtonActionPerformed(ActionEvent e) {
        ((CardLayout) setupFrame.getContentPane().getLayout()).show(
                setupFrame.getContentPane(), "access");
    }

    private void playButtonActionPerformed(ActionEvent e) {
        rv = new RobodromeView(new Robodrome("robodromes/checkmate.txt"), 55);
        robodromePanel.add(rv,BorderLayout.CENTER);
        setupFrame.dispose();
        playFrame.setVisible(true);
    }

    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - valka getz
        setupFrame = new JFrame();
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
        playButton = new JButton();
        label6 = new JLabel();
        robotLabel = new JLabel();
        playFrame = new JFrame();
        robodromePanel = new JPanel();
        label2 = new JLabel();
        scrollPane1 = new JScrollPane();
        playerMoves = new JList();
        panel2 = new JPanel();
        panel3 = new JPanel();
        scrollPane3 = new JScrollPane();
        logText = new JTextArea();
        scrollPane2 = new JScrollPane();
        robotList = new JList();
        panel4 = new JPanel();
        button8 = new JButton();
        button9 = new JButton();
        button10 = new JButton();
        button11 = new JButton();

        //======== setupFrame ========
        {
            setupFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            setupFrame.setTitle("RoboGP: The Game");
            Container setupFrameContentPane = setupFrame.getContentPane();
            setupFrameContentPane.setLayout(new CardLayout());

            //======== accessPanel ========
            {

                // JFormDesigner evaluation mark
                accessPanel.setBorder(new javax.swing.border.CompoundBorder(
                    new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                        "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                        javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                        java.awt.Color.red), accessPanel.getBorder())); accessPanel.addPropertyChangeListener(e -> {if("border".equals(e.getPropertyName()))throw new RuntimeException();});

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

                //---- portInput ----
                portInput.setText("2222");
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
            setupFrameContentPane.add(accessPanel, "access");

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
                label7.setHorizontalAlignment(SwingConstants.LEFT);
                refusePanel.add(label7, "cell 0 0");

                //---- refuseLabel ----
                refuseLabel.setText("text");
                refuseLabel.setHorizontalAlignment(SwingConstants.LEFT);
                refusePanel.add(refuseLabel, "cell 0 1");

                //---- backButton ----
                backButton.setText("Indietro");
                backButton.addActionListener(e -> backButtonActionPerformed(e));
                refusePanel.add(backButton, "cell 0 3");
            }
            setupFrameContentPane.add(refusePanel, "refuse");

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
                waitLabel.setHorizontalAlignment(SwingConstants.LEFT);
                waitPanel.add(waitLabel, "cell 0 1");

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
            setupFrameContentPane.add(waitPanel, "wait");
            setupFrame.pack();
            setupFrame.setLocationRelativeTo(setupFrame.getOwner());
        }

        //======== playFrame ========
        {
            playFrame.setTitle("RoboGP: The Game");
            playFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            Container playFrameContentPane = playFrame.getContentPane();
            playFrameContentPane.setLayout(new CardLayout());

            //======== robodromePanel ========
            {
                robodromePanel.setMinimumSize(new Dimension(500, 400));
                robodromePanel.setVisible(false);
                robodromePanel.setPreferredSize(new Dimension(900, 700));

                // JFormDesigner evaluation mark
                robodromePanel.setBorder(new javax.swing.border.CompoundBorder(
                    new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                        "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                            javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                        java.awt.Color.red), robodromePanel.getBorder())); robodromePanel.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

                robodromePanel.setLayout(new BorderLayout());

                //---- label2 ----
                label2.setText("AVVISI: ");
                robodromePanel.add(label2, BorderLayout.NORTH);

                //======== scrollPane1 ========
                {
                    scrollPane1.setViewportView(playerMoves);
                }
                robodromePanel.add(scrollPane1, BorderLayout.EAST);

                //======== panel2 ========
                {
                    panel2.setLayout(new BorderLayout());

                    //======== panel3 ========
                    {
                        panel3.setBorder(new TitledBorder("LOG Area"));
                        panel3.setLayout(new FlowLayout());

                        //======== scrollPane3 ========
                        {

                            //---- logText ----
                            logText.setText("Area di testo:");
                            logText.setEditable(false);
                            scrollPane3.setViewportView(logText);
                        }
                        panel3.add(scrollPane3);
                    }
                    panel2.add(panel3, BorderLayout.EAST);

                    //======== scrollPane2 ========
                    {
                        scrollPane2.setViewportView(robotList);
                    }
                    panel2.add(scrollPane2, BorderLayout.CENTER);

                    //======== panel4 ========
                    {
                        panel4.setLayout(new MigLayout(
                            "hidemode 3",
                            // columns
                            "[fill]",
                            // rows
                            "[]" +
                            "[]" +
                            "[]" +
                            "[]" +
                            "[]"));

                        //---- button8 ----
                        button8.setText("text");
                        panel4.add(button8, "cell 0 0");

                        //---- button9 ----
                        button9.setText("text");
                        panel4.add(button9, "cell 0 1");

                        //---- button10 ----
                        button10.setText("text");
                        panel4.add(button10, "cell 0 2");

                        //---- button11 ----
                        button11.setText("text");
                        panel4.add(button11, "cell 0 3");
                    }
                    panel2.add(panel4, BorderLayout.WEST);
                }
                robodromePanel.add(panel2, BorderLayout.SOUTH);
            }
            playFrameContentPane.add(robodromePanel, "play");
            playFrame.pack();
            playFrame.setLocationRelativeTo(playFrame.getOwner());
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - valka getz
    private JFrame setupFrame;
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
    private JButton playButton;
    private JLabel label6;
    private JLabel robotLabel;
    private JFrame playFrame;
    private JPanel robodromePanel;
    private JLabel label2;
    private JScrollPane scrollPane1;
    private JList playerMoves;
    private JPanel panel2;
    private JPanel panel3;
    private JScrollPane scrollPane3;
    private JTextArea logText;
    private JScrollPane scrollPane2;
    private JList robotList;
    private JPanel panel4;
    private JButton button8;
    private JButton button9;
    private JButton button10;
    private JButton button11;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            PlayerApp player = new PlayerApp();
            player.setupFrame.setVisible(true);
        });
    }

    @Override
    public void notifyMessageReceived(Message msg) {
        if(msg.getName().equals(Match.MatchJoinReplyMsg)) {
            boolean reply = (Boolean)msg.getParameter(0);
            if(reply){
                waitLabel.setText("Richiesta Accettata");
                //gameRobot
                RobotMarker[] robots= (RobotMarker[]) msg.getParameter(1);
                for(RobotMarker robot : robots)
                    robotLabel.setText(robotLabel.getText() +"<br>- "+robot.getName()+ " al dock: " +robot.getDock());
                robotLabel.setText(robotLabel.getText() +"</html>");
                robotLabel.setVisible(true);
                label6.setVisible(true);

            }
            else{
                ((CardLayout) setupFrame.getContentPane().getLayout()).show(
                        setupFrame.getContentPane(), "refuse");
                refuseLabel.setText("La tua richiesta di partecipazione è stata rifiutata dal managaer di partita");

            }
        }//Se password è errata
        else if(msg.getName().equals((Match.MatchErrorMsg))){
            ((CardLayout) setupFrame.getContentPane().getLayout()).show(
                        setupFrame.getContentPane(), "refuse");
            refuseLabel.setText((String)msg.getParameter(0));

        }
        else if(msg.getName().equals((Match.MatchStartMsg))){
            playButton.setVisible(true);

        }

    }
}
