package robogp.gameplayer;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.InetAddress;
import java.util.ArrayList;
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
import robogp.robodrome.Direction;
import robogp.robodrome.Position;
import robogp.robodrome.Robodrome;
import robogp.robodrome.Rotation;
import robogp.robodrome.view.RobodromeAnimationObserver;
import robogp.robodrome.view.RobodromeView;


public class PlayerApp implements MessageObserver,RobodromeAnimationObserver {
    private PlayerController controller = new PlayerController();
    private String nickname;
    private RobodromeView rv;
    private ArrayList<MatchRobot> robotsOnRobodrome = new ArrayList<>();
    private DefaultListModel<MatchRobot> modelRobot = new DefaultListModel<>();
    private DefaultListModel<String> modelList = new DefaultListModel<>();
    private HashMap<String,String> poolInstr = new HashMap<>();
    private HashMap<String,String > regResponseMsg = new HashMap<>();




    public PlayerApp() {
        initComponents();
    }


    /**
     * Action Performs methods
     */

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

    private void progRobotActionPerformed(ActionEvent e) {
        initAllReg();
        programDialog.setVisible(true);
        setRegister(regI);
    }

    private  void initAllReg(){
        regI.removeAllItems();regI.addItem("--");
        regII.removeAllItems();regII.addItem("--");
        regIII.removeAllItems();regIII.addItem("--");
        regIV.removeAllItems();regIV.addItem("--");
        regV.removeAllItems();regV.addItem("--");

    }

    private void setRegister(JComboBox reg){
        reg.removeAllItems();
        String pool = poolInstr.get(((MatchRobot)robotList.getSelectedValue()).getName());
        String[] schede = pool.split(",");
        for(String item : schede)
            reg.addItem(item);
        reg.addItem("--");

    }

    private void ok1ActionPerformed(ActionEvent e) {
        setRegister(regII);
        if(!(regI.getSelectedItem()).equals("--"))
            regII.removeItemAt(regI.getSelectedIndex());
        ok2.setEnabled(true);
        regII.setEnabled(true);
    }

    private void button2ActionPerformed(ActionEvent e) {
        setRegister(regIII);
        if(!(regI.getSelectedItem()).equals("--"))
            regIII.removeItemAt(regI.getSelectedIndex());
        if(!(regII.getSelectedItem()).equals("--"))
            regIII.removeItemAt(regII.getSelectedIndex());
        ok3.setEnabled(true);
        regIII.setEnabled(true);
    }

    private void button3ActionPerformed(ActionEvent e) {
        setRegister(regIV);
        if(!(regI.getSelectedItem()).equals("--"))
            regIV.removeItemAt(regI.getSelectedIndex());
        if(!(regII.getSelectedItem()).equals("--"))
            regIV.removeItemAt(regII.getSelectedIndex());
        if(!(regIII.getSelectedItem()).equals("--"))
            regIV.removeItemAt(regIII.getSelectedIndex());
        ok4.setEnabled(true);
        regIV.setEnabled(true);
    }

    private void button4ActionPerformed(ActionEvent e) {
        setRegister(regV);
        if(!(regI.getSelectedItem()).equals("--"))
            regV.removeItemAt(regI.getSelectedIndex());
        if(!(regII.getSelectedItem()).equals("--"))
            regV.removeItemAt(regII.getSelectedIndex());
        if(!(regIII.getSelectedItem()).equals("--"))
            regV.removeItemAt(regIII.getSelectedIndex());
        if(!(regIV.getSelectedItem()).equals("--"))
            regV.removeItemAt(regIV.getSelectedIndex());
        ok5.setEnabled(true);
        regV.setEnabled(true);
    }

    private void ok5ActionPerformed(ActionEvent e) {
        confirmButton.setEnabled(true);
    }

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
        System.out.println("\t\tNUOVE SCHEDE INVIATE: " + msg.getParameter(1).toString());
        controller.sendMessage(msg);
        regResponseMsg = new HashMap<>();
        logText.setText(logText.getText()+"\nSchede istruzione inviate al server");
        iniziaButton.setEnabled(false);
        notifications.setText("Attendi che tutti i giocatori abbiano programmato i propri robot");
    }

    private void nextMancheActionPerformed(ActionEvent e) {
        controller.sendMessage(new Message(Match.MatchReadyMsg));
        nextManche.setEnabled(false);
        notifications.setText("Fine manche. Premi 'prossima manche' per continuare!");
    }

    private void upgradeButtonActionPerformed(ActionEvent e) {
        // TODO add your code here
    }

    //END ACTION PERFORM


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
        robotLabel = new JLabel();
        label6 = new JLabel();
        playButton = new JButton();
        playFrame = new JFrame();
        robodromePanel = new JPanel();
        panel2 = new JPanel();
        panel3 = new JPanel();
        scrollPane3 = new JScrollPane();
        logText = new JTextPane();
        panel4 = new JPanel();
        progRobot = new JButton();
        iniziaButton = new JButton();
        upgradeButton = new JButton();
        nextManche = new JButton();
        scrollPane2 = new JScrollPane();
        robotList = new JList();
        scrollPane1 = new JScrollPane();
        playerMoves = new JList();
        panel1 = new JPanel();
        title = new JLabel();
        notifications = new JLabel();
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
                waitPanel.setMinimumSize(new Dimension(306, 160));
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
                    "[]" +
                    "[]" +
                    "[]"));

                //---- waitLabel ----
                waitLabel.setText("Elaborazione Richiesta in corso...");
                waitLabel.setFont(new Font("Lucida Grande", Font.BOLD, 16));
                waitLabel.setHorizontalAlignment(SwingConstants.LEFT);
                waitPanel.add(waitLabel, "cell 0 1");

                //---- robotLabel ----
                robotLabel.setText("<html><b>Robot assegnati:</b><br> ");
                robotLabel.setHorizontalAlignment(SwingConstants.CENTER);
                robotLabel.setVisible(false);
                waitPanel.add(robotLabel, "cell 0 2");

                //---- label6 ----
                label6.setText("In attesa di altri giocatori...");
                label6.setHorizontalAlignment(SwingConstants.CENTER);
                label6.setVisible(false);
                waitPanel.add(label6, "cell 0 3");

                //---- playButton ----
                playButton.setText("Gioca");
                playButton.setVisible(false);
                playButton.addActionListener(e -> playButtonActionPerformed(e));
                waitPanel.add(playButton, "cell 0 4");
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
                robodromePanel.setLayout(new BorderLayout());

                //======== panel2 ========
                {
                    panel2.setPreferredSize(new Dimension(437, 150));
                    panel2.setLayout(new BorderLayout());

                    //======== panel3 ========
                    {
                        panel3.setBorder(new TitledBorder("LOG Area"));
                        panel3.setPreferredSize(new Dimension(300, 120));
                        panel3.setLayout(new FlowLayout());

                        //======== scrollPane3 ========
                        {
                            scrollPane3.setPreferredSize(new Dimension(280, 115));

                            //---- logText ----
                            logText.setPreferredSize(new Dimension(270, 110));
                            logText.setText("Log Text initialization...");
                            logText.setBackground(Color.white);
                            logText.setEditable(false);
                            scrollPane3.setViewportView(logText);
                        }
                        panel3.add(scrollPane3);
                    }
                    panel2.add(panel3, BorderLayout.EAST);

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

                        //---- upgradeButton ----
                        upgradeButton.setText("text");
                        upgradeButton.setEnabled(false);
                        upgradeButton.addActionListener(e -> upgradeButtonActionPerformed(e));
                        panel4.add(upgradeButton, "cell 0 2");

                        //---- nextManche ----
                        nextManche.setText("Prossima Manche");
                        nextManche.setEnabled(false);
                        nextManche.addActionListener(e -> nextMancheActionPerformed(e));
                        panel4.add(nextManche, "cell 0 3");
                    }
                    panel2.add(panel4, BorderLayout.WEST);

                    //======== scrollPane2 ========
                    {

                        //---- robotList ----
                        robotList.setSelectedIndex(0);
                        robotList.setModel(modelRobot);
                        robotList.setCellRenderer(new RobotCellRenderer());
                        scrollPane2.setViewportView(robotList);
                    }
                    panel2.add(scrollPane2, BorderLayout.CENTER);
                }
                robodromePanel.add(panel2, BorderLayout.SOUTH);

                //======== scrollPane1 ========
                {
                    scrollPane1.setVisible(false);
                    scrollPane1.setPreferredSize(new Dimension(160, 140));

                    //---- playerMoves ----
                    playerMoves.setMaximumSize(new Dimension(160, 24224));
                    playerMoves.setMinimumSize(new Dimension(140, 140));
                    playerMoves.setModel(modelList);
                    scrollPane1.setViewportView(playerMoves);
                }
                robodromePanel.add(scrollPane1, BorderLayout.EAST);

                //======== panel1 ========
                {
                    panel1.setLayout(new FlowLayout(FlowLayout.LEFT));

                    //---- title ----
                    title.setText("AVVISI: ");
                    title.setFont(new Font("Lucida Grande", Font.BOLD, 16));
                    title.setForeground(Color.red);
                    panel1.add(title);

                    //---- notifications ----
                    notifications.setText("text");
                    notifications.setFont(new Font("Lucida Grande", Font.ITALIC, 16));
                    panel1.add(notifications);
                }
                robodromePanel.add(panel1, BorderLayout.NORTH);
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
    private JLabel robotLabel;
    private JLabel label6;
    private JButton playButton;
    private JFrame playFrame;
    private JPanel robodromePanel;
    private JPanel panel2;
    private JPanel panel3;
    private JScrollPane scrollPane3;
    private JTextPane logText;
    private JPanel panel4;
    private JButton progRobot;
    private JButton iniziaButton;
    private JButton upgradeButton;
    private JButton nextManche;
    private JScrollPane scrollPane2;
    private JList robotList;
    private JScrollPane scrollPane1;
    private JList playerMoves;
    private JPanel panel1;
    private JLabel title;
    private JLabel notifications;
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

    /**
     * Metodo in Override di MessageObserver.
     * La connection di ogni giocatore, quando riceve un messaggio
     * dal match, notifica il giocatore mandandogli il messaggio.
     * A seconda del tipo di messaggio, il giocatore esegue un'azine diversa
     */
    @Override
    public void notifyMessageReceived(Message msg) {
        switch (msg.getName()) {
            case Match.MatchJoinReplyMsg:
                boolean reply = (Boolean) msg.getParameter(0);
                if (reply) {
                    waitLabel.setText("Richiesta Accettata");
                    MatchRobot[] robots = (MatchRobot[]) msg.getParameter(1);
                    for (MatchRobot robot : robots){
                        modelRobot.addElement(robot);
                        robotLabel.setText(robotLabel.getText() + "<br>- " + robot.getName() + " al dock: " + robot.getDock());
                    }
                    robotList.setSelectedIndex(0);
                    robotLabel.setText(robotLabel.getText() + "</html>");
                    robotLabel.setVisible(true);
                    label6.setVisible(true);

                } else {
                    ((CardLayout) setupFrame.getContentPane().getLayout()).show(
                            setupFrame.getContentPane(), "refuse");
                    refuseLabel.setText("La tua richiesta di partecipazione è stata rifiutata dal managaer di partita");

                }
                logText.setText(logText.getText()+"\nReceived Message: joinMatchReply");
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
                rv.addObserver(this);
                robodromePanel.add(rv, BorderLayout.CENTER);
                setupRobotsOnRobodrome((ArrayList<MatchRobot>)msg.getParameter(1));
                playButton.setVisible(true);
                logText.setText(logText.getText()+"\nReceived Message: startMatch");
                break;

            case (Match.MancheInstructionPoolMsg):
                poolInstr = ( HashMap<String,String>) msg.getParameter(0);
                notifications.setText("Nuovo pool di schede istruzione ricevute. Ora puoi programmare i tuoi robot!");
                logText.setText(logText.getText()+"\nReceived Message: instructionPool");
                progRobot.setEnabled(true);
                break;

            case (Match.MancheDeclarationSubPhaseMsg):
                String[]args = ((String) msg.getParameter(0)).split(",");
                modelList.removeAllElements();
                for (String scheda :args){
                    modelList.addElement(scheda);
                }
                playerMoves.setCellRenderer(new ListCellRenderer());
                scrollPane1.setVisible(true);
                notifications.setText("Sottofase di Dichiarazione. Guarda cosa hanno scelto i tuoi avversari!");
                logText.setText(logText.getText()+"\nReceived Message: MancheDeclarationSubPhaseMsg");
                controller.sendMessage(new Message(Match.MatchReadyMsg));
                break;

            case (Match.MancheRobotsAnimationsMsg):
                notifications.setText("Sottofase di movimento . Ora i robot eseguiranno le loro mosse!");
                logText.setText(logText.getText()+"\nRicevute Animazioni Robot");
                String[]anim = ((String) msg.getParameter(0)).split(",");
                rv.startFollowingAction();
                for(String a: anim)
                    createAnimation(a);
                rv.play();
                break;

            case(Match.MancheRobodromeActivationMsg):
                notifications.setText("Sottofase di attivazione Robodromo . Verranno eseguite le mosse del robodromo");
                logText.setText(logText.getText()+"\nRicevute Animazioni Robodromo");
                String[]pars = ((String) msg.getParameter(0)).split(",");
                for(String par: pars)
                    createAnimation(par);
                rv.play();
                break;

            case (Match.MancheLasersAndWeaponsMsg):
                notifications.setText("Sottofase Armi & Laser. Ora i robot spareranno");
                logText.setText(logText.getText()+"\nRicevuti Laser");
                String[]weps = ((String) msg.getParameter(0)).split(",");
                for(String wep: weps)
                    createAnimation(wep);
                rv.addHideLaser();
                rv.play();
                break;

            case (Match.MancheRobotsRepositionsMsg):
                String[]reps = ((String) msg.getParameter(0)).split(",");
                for(String rep: reps)
                    repositionRobot(rep);
                controller.sendMessage(new Message(Match.MatchReadyMsg));
                break;

            case (Match.MancheEndMsg):
                notifications.setText("Manche terminata. Premi 'manche successiva' per continuare.");
                nextManche.setEnabled(true);
                break;

            case (Match.MatchCancelMsg):
                JOptionPane.showMessageDialog(playFrame,
                            "La partita è stata annullata dal manager di partita!","Attenzione:",
                            JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
                break;
        }

    }


    /**
     * Metodo che analizza la stringa codificata ricevuta dal server
     * con tutte le animazioni e le aggiunge alla coda di animazioni
     * della robodromeView.
     * La stringa può avere forme diverse:
     *
     * + nomerobot:pitfall  --> animazione di caduta buco nero
     * + nomerobot:passi:direzione:rotazione  --> movimento robot o robodromo
     * + nomerobot:passi:direzione:rotazione:robot1§robot2§..§robotN  --> movimento robot o robodromo con spinta di altri robot
     * + nomerobot:direzionerobot:casellainiziosparo:casellafinesparo:robotcolpito:murocolpito  --> sparo laser
     */

    private void createAnimation(String a) {
        if (a != null) {
            String[] animdata = a.split(":");

            //animazione di caduta buco nero
            if (animdata.length == 2) {
                MatchRobot robot = getRobotByName(animdata[0]);
                String cause = animdata[1];
                if(cause.equals("pitfall")) {
                    rv.addRobotFall(robot);
                }
            }

            // movimento robot o robodromo
            if (animdata.length == 4) {
                MatchRobot robot = getRobotByName(animdata[0]);
                int movement = Integer.parseInt(animdata[1]);
                Direction dir = Direction.valueOf(animdata[2]);
                Rotation rot = Rotation.valueOf(animdata[3]);
                rv.addRobotMove(robot, movement, dir, rot);
            }

            // movimento robot con spinta
            if (animdata.length == 5) {
                String[] robotsName = animdata[4].split("§");

                MatchRobot[] pushRobots= new MatchRobot[robotsName.length];
                for(int i =0;i<robotsName.length;i++){
                    pushRobots[i] = getRobotByName(robotsName[i]);
                }

                MatchRobot robot = getRobotByName(animdata[0]);
                int movement = Integer.parseInt(animdata[1]);
                Direction dir = Direction.valueOf(animdata[2]);
                Rotation rot = Rotation.valueOf(animdata[3]);
                rv.addRobotMove(robot, movement, dir, rot,pushRobots);
            }


            //sparo laser
            if(animdata.length == 6){ //animazione laser
                MatchRobot robot = getRobotByName(animdata[0]);
                Direction dir = Direction.valueOf(animdata[1]);
                int inizioSparo = Integer.parseInt(animdata[2]);
                int fineSparo = Integer.parseInt(animdata[3]);
                String hit = animdata[4];
                Boolean wallHit = Boolean.parseBoolean(animdata[5]);

                if(!hit.equals("false")) {
                    rv.addLaserFire(robot, dir, inizioSparo, fineSparo, true, wallHit);
                    rv.addRobotHit(getRobotByName(hit), Direction.getOppositeDirection(dir));
                    updateHitPointsRobotList(getRobotByName(hit));
                }
                else{rv.addLaserFire(robot, dir, inizioSparo, fineSparo, false, wallHit);}
            }
            rv.addPause(1000);
        }
    }

    private void repositionRobot(String rep) {
        System.out.println("\t\tREPOSITION: "+rep);
        String[] animdata = rep.split(":");

        if (animdata.length == 5){
            MatchRobot robot = getRobotByName(animdata[0]);
            int savedposX = Integer.parseInt(animdata[1]);
            int savedposY = Integer.parseInt(animdata[2]);
            Direction dir = Direction.valueOf(animdata[3]);
            Boolean isAlive = Boolean.parseBoolean(animdata[4]);

            if(isAlive) {
                rv.changeRobotPosition(robot, dir, savedposX, savedposY, true);
                updateLifePointsRobotList(robot);
            }
            else{
                rv.removeRobot(robot != null ? robot.getName() : null);
                System.out.println("-->robot rimosso. Era un tuo robot? "+modelRobot.removeElement(robot));
                robotsOnRobodrome.remove(robot);
            }


        }

    }

    /**
     * Metodo che fa l'update della lista dei robot qualora
     * perdessero punti salute, colpiti da un laser.
     * @param robot è il robot che subisce danno
     */
    private void updateHitPointsRobotList(MatchRobot robot) {
        if(modelRobot.contains(robot)) {
            MatchRobot selected = null;
            for (int i = 0; i < modelRobot.size(); i++) {
                if (robot.getName().equals(modelRobot.elementAt(i).getName())) {
                    selected = modelRobot.getElementAt(i);
                    selected.setHitPoints(selected.getHitPoints() - 1);
                }
            }
            modelRobot.addElement(selected);
            modelRobot.removeElement(selected);
        }

    }

    /**
     * Metodo che fa l'update della lista dei robot qualora
     * perdessero punti vita, cadendo in un buco nero o uscendo
     * dal robodromo.
     *  @param robot è il robot che subisce danno
     */
    private void updateLifePointsRobotList(MatchRobot robot) {
        MatchRobot selected = null;
        if(modelRobot.contains(robot)) {
            for (int i = 0; i < modelRobot.size(); i++) {
                if (robot.getName().equals(modelRobot.elementAt(i).getName())) {
                    selected = modelRobot.getElementAt(i);
                    selected.setLifePoints(selected.getLifePoints() - 1);
                    selected.setHitPoints(10);
                }
            }
            //inserisco e tolgo un doppione per far renderizzare
            //la llista di nuovo --> FIX THIS
            modelRobot.addElement(selected);
            modelRobot.removeElement(selected);
        }
    }

    /**
     * Una volta ricevuta la lista con tutti i robot nel gioco,
     * questo metodo va a posizionare i vari robot sul robodromo
     * a seconda del dock assegnato a ciascun robot dall matchManager
     * @param setupRobots è la lista dei robot effettivamente in partita
     * */
    private void setupRobotsOnRobodrome(ArrayList<MatchRobot> setupRobots) {
        Robodrome theDrome = rv.getDrome();
        HashMap<Integer,Position> dockTable = theDrome.getDockTable();
        for(MatchRobot rob : setupRobots){
           rv.placeRobot(rob,dockTable.get(rob.getDock()).getDirection(),dockTable.get(rob.getDock()).getPosX(),
                   dockTable.get(rob.getDock()).getPosY(),true);
        }
        robotsOnRobodrome.addAll(setupRobots);
    }


    private MatchRobot getRobotByName(String name){
        for(MatchRobot robot : robotsOnRobodrome){
            if(robot.getName().equals(name)){
                return robot;
            }
        }
        return null;
    }


    /**
     * Metodi in ovverride di RobodromeAnimationObserver.
     * una volta iniziate/finite le animazioni sulla robodromeView,
     * la playerApp viene notificata tramite questi metodi, così da passare
     * alla fase successiva
     */

    @Override
    public void animationStarted() {
        logText.setText(logText.getText()+"\nStart RobotAnimations");
        System.out.println("--> ANIMAZIONE INIZIATA <--");

    }

    @Override
    public void animationFinished() {
        System.out.println("--> ANIMAZIONE FINITA <--");
        controller.sendMessage(new Message(Match.MatchReadyMsg));
        logText.setText(logText.getText()+"\nRobotAnimations Finish");
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
        label.setText("<html><b>Nome:</b> "+robot.getName()+"<br><b>Punti vita:</b> "+robot.getHitPoints()+"<br><b>Vite rimaste:</b> "
                +robot.getLifePoints()+"</html>");

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
        if (!args[1].equals("locked")) {
            Instruction instruction = new Instruction(args[1]);
            ImageIcon imageIcon = new ImageIcon(instruction.getImage(100));
            label = new JLabel("<html><b>Nome:</b> " + args[0] + "<br><b>Scheda:</b> " + args[1] +
                    "<br><b>Priorità:</b> " + args[2] + "</html>", imageIcon, JLabel.CENTER);

        }
        //registro bloccato
        else{
            Instruction instruction = new Instruction("lock");
            ImageIcon imageIcon = new ImageIcon(instruction.getImage(100));
            label = new JLabel("<html><b>Nome:</b> " + args[0]+"<br><b>Registro Bloccato</b>", imageIcon, JLabel.CENTER);
        }
        label.setVerticalTextPosition(JLabel.BOTTOM);
        label.setHorizontalTextPosition(JLabel.CENTER);
        label.setOpaque(true);
        return label;
    }
}
