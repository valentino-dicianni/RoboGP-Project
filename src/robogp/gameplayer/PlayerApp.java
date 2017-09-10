/*
 * Created by JFormDesigner on Tue Sep 05 11:17:31 CEST 2017
 */

package robogp.gameplayer;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.border.*;
import connection.Connection;
import connection.Message;
import connection.MessageObserver;
import net.miginfocom.swing.*;
import robogp.common.Instruction;
import robogp.matchmanager.Match;
import robogp.matchmanager.MatchRobot;
import robogp.robodrome.Robodrome;
import robogp.robodrome.view.RobodromeView;

/**
 * @author valka getz
 */
public class PlayerApp implements MessageObserver {
    private PlayerController controller = new PlayerController();
    private String nickname;
    private RobodromeView rv;
    private DefaultListModel<MatchRobot> modelRobot = new DefaultListModel<>();
    private DefaultListModel<String> modelList = new DefaultListModel<>();
    private HashMap<String,String> poolInstr = new HashMap<>();



    public PlayerApp() {
        initComponents();
    }

    private void accessButtonActionPerformed(ActionEvent e) {
        if(ceckCredentials()){
            this.nickname = nickInput.getText();
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
        Message msg = new Message(Match.MatchReadyMsg);
        Object[] pars = new Object[1];
        pars[0] = nickname;
        msg.setParameters(pars);
        controller.sendMessage(msg);
        setupFrame.dispose();
        playFrame.setVisible(true);
    }


    private void setRegister(JComboBox reg){
        reg.removeAllItems();
        String pool = poolInstr.get(((MatchRobot)robotList.getSelectedValue()).getName());
        String[] schede = pool.split(",");
        reg.addItem("--");
        for(String item : schede)
            reg.addItem(item);
    }

    private void progRobotActionPerformed(ActionEvent e) {
        programDialog.setVisible(true);
        setRegister(regI);
    }


    private void ok1ActionPerformed(ActionEvent e) {
        setRegister(regII);
        regII.removeItemAt(regI.getSelectedIndex());
        ok2.setEnabled(true);
        regII.setEnabled(true);
    }

    private void button2ActionPerformed(ActionEvent e) {
        setRegister(regIII);
        regIII.removeItemAt(regI.getSelectedIndex());
        regIII.removeItemAt(regII.getSelectedIndex());
        ok3.setEnabled(true);
        regIII.setEnabled(true);
    }

    private void button3ActionPerformed(ActionEvent e) {
        setRegister(regIV);
        regIV.removeItemAt(regI.getSelectedIndex());
        regIV.removeItemAt(regII.getSelectedIndex());
        regIV.removeItemAt(regIII.getSelectedIndex());
        ok4.setEnabled(true);
        regIV.setEnabled(true);
    }

    private void button4ActionPerformed(ActionEvent e) {
        setRegister(regV);
        regV.removeItemAt(regI.getSelectedIndex());
        regV.removeItemAt(regII.getSelectedIndex());
        regV.removeItemAt(regIII.getSelectedIndex());
        regV.removeItemAt(regIV.getSelectedIndex());
        ok5.setEnabled(true);
        regV.setEnabled(true);
    }

    private void ok5ActionPerformed(ActionEvent e) {
        confirmButton.setEnabled(true);
    }


    private HashMap<String,String > regResponseMsg = new HashMap<>();

    private void confirmButtonActionPerformed(ActionEvent e) {
        String str = "1:"+regI.getSelectedItem()+", "+"2:"+regII.getSelectedItem()+", "+"3:"+regIII.getSelectedItem()+", "+
                "4:"+regIV.getSelectedItem()+", "+"5:"+regV.getSelectedItem();
        str = str.replaceAll("\\s","");
        regResponseMsg.put(((MatchRobot)robotList.getSelectedValue()).getName(), str);
        System.out.println(str);
        programDialog.setVisible(false);
        regII.setEnabled(false);
        ok2.setEnabled(false);
        regII.setSelectedIndex(0);
        regIII.setEnabled(false);
        ok3.setEnabled(false);
        regIII.setSelectedIndex(0);
        regIV.setEnabled(false);
        ok4.setEnabled(false);
        regIV.setSelectedIndex(0);
        regV.setEnabled(false);
        ok5.setEnabled(false);
        confirmButton.setEnabled(false);
        regV.setSelectedIndex(0);
        if(regResponseMsg.size() == modelRobot.size()){
            iniziaButton.setEnabled(true);
        }


    }

    private void iniziaButtonActionPerformed(ActionEvent e) {
        progRobot.setEnabled(false);
        Message msg = new Message(Match.MancheProgrammedRegistriesMsg);
        Object[] pars = new Object[2];
        pars[0] = nickname;
        pars[1] = regResponseMsg;
        msg.setParameters(pars);
        controller.sendMessage(msg);
        logText.append("\nSchede istruzione inviate al server");
        iniziaButton.setEnabled(false);
        notifications.setText("Attendi che tutti i giocatori abbiano programmato i propri robot");
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
        notifications = new JLabel();
        scrollPane1 = new JScrollPane();
        playerMoves = new JList();
        panel2 = new JPanel();
        panel3 = new JPanel();
        scrollPane3 = new JScrollPane();
        logText = new JTextArea();
        scrollPane2 = new JScrollPane();
        robotList = new JList();
        panel4 = new JPanel();
        progRobot = new JButton();
        iniziaButton = new JButton();
        button10 = new JButton();
        button11 = new JButton();
        programDialog = new JDialog();
        label5 = new JLabel();
        label9 = new JLabel();
        regI = new JComboBox<>();
        ok1 = new JButton();
        label10 = new JLabel();
        regII = new JComboBox<>();
        ok2 = new JButton();
        label11 = new JLabel();
        regIII = new JComboBox<>();
        ok3 = new JButton();
        label12 = new JLabel();
        regIV = new JComboBox<>();
        ok4 = new JButton();
        label13 = new JLabel();
        regV = new JComboBox<>();
        ok5 = new JButton();
        confirmButton = new JButton();

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
                waitPanel.add(playButton, "cell 0 7");

                //---- label6 ----
                label6.setText("In attesa di altri giocatori...");
                label6.setHorizontalAlignment(SwingConstants.CENTER);
                label6.setVisible(false);
                waitPanel.add(label6, "cell 0 6");

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

                //---- notifications ----
                notifications.setText("AVVISI: ");
                notifications.setFont(new Font("Lucida Grande", Font.BOLD, 16));
                robodromePanel.add(notifications, BorderLayout.NORTH);

                //======== scrollPane1 ========
                {

                    //---- playerMoves ----
                    playerMoves.setMaximumSize(new Dimension(45, 51));
                    playerMoves.setPreferredSize(new Dimension(45, 51));
                    playerMoves.setModel(modelList);

                    scrollPane1.setViewportView(playerMoves);
                }
                robodromePanel.add(scrollPane1, BorderLayout.EAST);

                //======== panel2 ========
                {
                    panel2.setPreferredSize(new Dimension(437, 150));
                    panel2.setLayout(new BorderLayout());

                    //======== panel3 ========
                    {
                        panel3.setBorder(new TitledBorder("LOG Area"));
                        panel3.setPreferredSize(new Dimension(300, 100));
                        panel3.setLayout(new FlowLayout());

                        //======== scrollPane3 ========
                        {

                            //---- logText ----
                            logText.setText("Area di testo:");
                            logText.setEditable(false);
                            logText.setPreferredSize(new Dimension(280, 110));
                            scrollPane3.setViewportView(logText);
                        }
                        panel3.add(scrollPane3);
                    }
                    panel2.add(panel3, BorderLayout.EAST);

                    //======== scrollPane2 ========
                    {

                        //---- robotList ----
                        robotList.setPreferredSize(new Dimension(39, 120));
                        robotList.setMaximumSize(new Dimension(39, 25));
                        robotList.setSelectedIndex(0);
                        robotList.setModel(modelRobot);
                        robotList.setCellRenderer(new RobotCellRenderer());
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

                        //---- progRobot ----
                        progRobot.setText("Programma Robot");
                        progRobot.setEnabled(false);
                        progRobot.addActionListener(e -> progRobotActionPerformed(e));
                        panel4.add(progRobot, "cell 0 0");

                        //---- iniziaButton ----
                        iniziaButton.setText("Inizia");
                        iniziaButton.setEnabled(false);
                        iniziaButton.addActionListener(e -> iniziaButtonActionPerformed(e));
                        panel4.add(iniziaButton, "cell 0 1");

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

        //======== programDialog ========
        {
            Container programDialogContentPane = programDialog.getContentPane();
            programDialogContentPane.setLayout(new MigLayout(
                "hidemode 3,alignx center",
                // columns
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
                "[]"));

            //---- label5 ----
            label5.setText("Programma i registri del tuo Robot");
            label5.setFont(new Font("Lucida Grande", Font.BOLD, 16));
            programDialogContentPane.add(label5, "cell 1 0");

            //---- label9 ----
            label9.setText("Registo I");
            programDialogContentPane.add(label9, "cell 0 2");

            //---- regI ----
            regI.setModel(new DefaultComboBoxModel<>(new String[] {
                "--"
            }));
            programDialogContentPane.add(regI, "cell 1 2");

            //---- ok1 ----
            ok1.setText("ok");
            ok1.addActionListener(e -> ok1ActionPerformed(e));
            programDialogContentPane.add(ok1, "cell 2 2");

            //---- label10 ----
            label10.setText("Registro II");
            programDialogContentPane.add(label10, "cell 0 3");

            //---- regII ----
            regII.setModel(new DefaultComboBoxModel<>(new String[] {
                "--"
            }));
            regII.setEnabled(false);
            programDialogContentPane.add(regII, "cell 1 3");

            //---- ok2 ----
            ok2.setText("ok");
            ok2.setEnabled(false);
            ok2.addActionListener(e -> button2ActionPerformed(e));
            programDialogContentPane.add(ok2, "cell 2 3");

            //---- label11 ----
            label11.setText("Registro III");
            programDialogContentPane.add(label11, "cell 0 4");

            //---- regIII ----
            regIII.setModel(new DefaultComboBoxModel<>(new String[] {
                "--"
            }));
            regIII.setEnabled(false);
            programDialogContentPane.add(regIII, "cell 1 4");

            //---- ok3 ----
            ok3.setText("ok");
            ok3.setEnabled(false);
            ok3.addActionListener(e -> button3ActionPerformed(e));
            programDialogContentPane.add(ok3, "cell 2 4");

            //---- label12 ----
            label12.setText("Registro IV");
            programDialogContentPane.add(label12, "cell 0 5");

            //---- regIV ----
            regIV.setModel(new DefaultComboBoxModel<>(new String[] {
                "--"
            }));
            regIV.setEnabled(false);
            programDialogContentPane.add(regIV, "cell 1 5");

            //---- ok4 ----
            ok4.setText("ok");
            ok4.setEnabled(false);
            ok4.addActionListener(e -> button4ActionPerformed(e));
            programDialogContentPane.add(ok4, "cell 2 5");

            //---- label13 ----
            label13.setText("Registro V");
            programDialogContentPane.add(label13, "cell 0 6");

            //---- regV ----
            regV.setModel(new DefaultComboBoxModel<>(new String[] {
                "--"
            }));
            regV.setEnabled(false);
            programDialogContentPane.add(regV, "cell 1 6");

            //---- ok5 ----
            ok5.setText("ok");
            ok5.setEnabled(false);
            ok5.addActionListener(e -> ok5ActionPerformed(e));
            programDialogContentPane.add(ok5, "cell 2 6");

            //---- confirmButton ----
            confirmButton.setText("Conferma");
            confirmButton.setEnabled(false);
            confirmButton.addActionListener(e -> confirmButtonActionPerformed(e));
            programDialogContentPane.add(confirmButton, "cell 1 8");
            programDialog.pack();
            programDialog.setLocationRelativeTo(programDialog.getOwner());
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
    private JLabel notifications;
    private JScrollPane scrollPane1;
    private JList playerMoves;
    private JPanel panel2;
    private JPanel panel3;
    private JScrollPane scrollPane3;
    private JTextArea logText;
    private JScrollPane scrollPane2;
    private JList robotList;
    private JPanel panel4;
    private JButton progRobot;
    private JButton iniziaButton;
    private JButton button10;
    private JButton button11;
    private JDialog programDialog;
    private JLabel label5;
    private JLabel label9;
    private JComboBox<String> regI;
    private JButton ok1;
    private JLabel label10;
    private JComboBox<String> regII;
    private JButton ok2;
    private JLabel label11;
    private JComboBox<String> regIII;
    private JButton ok3;
    private JLabel label12;
    private JComboBox<String> regIV;
    private JButton ok4;
    private JLabel label13;
    private JComboBox<String> regV;
    private JButton ok5;
    private JButton confirmButton;
    // JFormDesigner - End of variables declaration  //GEN-END:variables

    public static void main(String[] args) {
        java.awt.EventQueue.invokeLater(() -> {
            PlayerApp player = new PlayerApp();
            player.setupFrame.setVisible(true);
        });
    }

    @Override
    public void notifyMessageReceived(Message msg) {
        switch (msg.getName()) {
            case Match.MatchJoinReplyMsg:
                boolean reply = (Boolean) msg.getParameter(0);
                if (reply) {
                    waitLabel.setText("Richiesta Accettata");
                    //gameRobot
                    MatchRobot[] robots = (MatchRobot[]) msg.getParameter(1);
                    for (MatchRobot robot : robots){
                        modelRobot.addElement(robot);
                        robotLabel.setText(robotLabel.getText() + "<br>- " + robot.getName() + " al dock: " + robot.getDock());
                    }
                    robotLabel.setText(robotLabel.getText() + "</html>");
                    robotLabel.setVisible(true);
                    label6.setVisible(true);

                } else {
                    ((CardLayout) setupFrame.getContentPane().getLayout()).show(
                            setupFrame.getContentPane(), "refuse");
                    refuseLabel.setText("La tua richiesta di partecipazione è stata rifiutata dal managaer di partita");

                }
                logText.append("\nReceived Message: joinMatchReply");
                break;

            case (Match.MatchErrorMsg):
                ((CardLayout) setupFrame.getContentPane().getLayout()).show(
                        setupFrame.getContentPane(), "refuse");
                refuseLabel.setText((String) msg.getParameter(0));
                break;

            case (Match.MatchStartMsg):
                String path = ((String) msg.getParameter(0)).replaceAll("\\s+", "");
                path = "robodromes/" + path.toLowerCase() + ".txt";
                rv = new RobodromeView(new Robodrome(path), 55);
                robodromePanel.add(rv, BorderLayout.CENTER);
                playButton.setVisible(true);
                logText.append("\nReceived Message: startMatch");
                break;

            case (Match.MancheInstructionPoolMsg):
                poolInstr = ( HashMap<String,String>) msg.getParameter(0);
                notifications.setText("AVVISO: Nuovo pool di schede istruzione ricevute. Ora puoi programmare i tuoi robot!");
                logText.append("\nReceived Message: instructionPool");
                progRobot.setEnabled(true);
                break;
            case (Match.MancheDeclarationSubPhaseMsg):
                String[]args = ((String) msg.getParameter(0)).split(",");
                for (String scheda :args){
                    modelList.addElement(scheda);
                }
                playerMoves.setCellRenderer(new ListCellRenderer());
                notifications.setText("AVVISO: Sottofase di Dichiarazione. Guarda cosa hanno scelto i tuoi avversari!");
                break;


        }

    }
}

class RobotCellRenderer extends DefaultListCellRenderer {
    private final JLabel label;

    RobotCellRenderer() {
        label = new JLabel();
        label.setOpaque(true);

    }

    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean selected,
            boolean expanded) {

        MatchRobot robot = (MatchRobot) value;
        label.setIcon(new ImageIcon(robot.getImage(60)));
        label.setText("<html><b>Nome:</b> "+robot.getName()+"<br><b>Punti vita:</b> "+robot.getHitPoints()+"<br><b>Vite rimaste:</b> " +robot.getLifePoints()+"</html>");

        if (selected) {
            label.setBackground(Color.CYAN);
            label.setForeground(Color.BLACK);
        } else {
            label.setBackground(Color.WHITE);
            label.setForeground(Color.BLACK);
        }

        return label;
    }
}

class ListCellRenderer extends DefaultListCellRenderer{
    private  JLabel label;


    ListCellRenderer() {}


    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean selected,
            boolean expanded) {

        String[]args = ((String) value).split(":");
        Instruction instruction = new Instruction(args[1]);

        ImageIcon imageIcon = new ImageIcon(instruction.getImage(100));
        label = new JLabel("<html><b>Nome:</b> "+args[0]+"<br><b>Scheda:</b> "+args[1]+"<br><b>Priorità:</b> " +args[2]+"</html>",imageIcon,JLabel.CENTER);
        label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setOpaque(true);
        return label;
    }
}
