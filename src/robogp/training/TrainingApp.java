package robogp.training;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
import net.miginfocom.swing.*;
import robogp.robodrome.Position;
import robogp.robodrome.view.RobodromeView;

/**
 * @author valka getz
 */
public class TrainingApp implements Observer {

    private static TrainingApp singleInstance;
    private final IniziareTrainingControl inizPartCtrl;
    private final Training training;
    private RobodromeView rv;
    private ArrayList<Position> dockPos;



    private TrainingApp(){
        initComponents();
        this.inizPartCtrl = IniziareTrainingControl.getInstance();
        this.training = Training.getInstance();
        this.training.addObserver(this);
    }
    public static TrainingApp getAppInstance() {
        return TrainingApp.singleInstance;
    }


    private void initButtonActionPerformed(ActionEvent e) {
        dockPos = inizPartCtrl.setRobodrome((String)robodromeChoose.getSelectedItem());
        DefaultComboBoxModel model = (DefaultComboBoxModel)dockChooser.getModel();
        model.removeAllElements();
        for(int i = 0; i<dockPos.size();i++){
            String pos = i +"." + " " +dockPos.get(i).toString();
            model.addElement(pos);
        }
        label4.setEnabled(true);
        dockChooser.setEnabled(true);
        continueButton.setEnabled(true);

    }
    private void continueButtonActionPerformed(ActionEvent e) {
        ((CardLayout) TrainingApp.getAppInstance().trainingFrame.getContentPane().getLayout()).show(
              TrainingApp.getAppInstance().trainingFrame.getContentPane(), "prog");
    }

    private void addButtonActionPerformed(ActionEvent e) {
        DefaultListModel model = (DefaultListModel) progList.getModel();
        model.addElement(cardList.getSelectedValue());
    }

    private void deleteButtonActionPerformed(ActionEvent e) {
        DefaultListModel model = (DefaultListModel) progList.getModel();
        int selectedIndex = progList.getSelectedIndex();
        if (selectedIndex != -1) {
            model.remove(selectedIndex);
        }
    }

    private void changeButtonActionPerformed(ActionEvent e) {
        int[] elems = progList.getSelectedIndices();
        if(elems.length == 2) {
            DefaultListModel model = (DefaultListModel) progList.getModel();
            Object[] arr = model.toArray();
            Object temp = arr[elems[0]];
            arr[elems[0]] = arr[elems[1]];
            arr[elems[1]] = temp;
            model.removeAllElements();
            for(Object s : arr){
                model.addElement(s);
            }
        }
        else{
            JOptionPane.showMessageDialog(TrainingApp.getAppInstance().trainingFrame,
                    "Selezionare due schede istruzione!","Attenzione:",
                    JOptionPane.WARNING_MESSAGE);
        }


    }

    private void startTrainingActionPerformed(ActionEvent e) {
        //TODO aggiungere al metodo start la posizione iniziale

        DefaultListModel model = (DefaultListModel) progList.getModel();
        model.toArray();
        inizPartCtrl.start(model.toArray());
        trainingFrame.dispose();
        rv = new RobodromeView(inizPartCtrl.getRobodrome(), 55);
        int pos= Integer.parseInt(((String)dockChooser.getSelectedItem()).split("\\.")[0]);
        rv.placeRobot(inizPartCtrl.getTrainingRobot(), dockPos.get(pos).getRotation(),dockPos.get(pos).getPosX(),dockPos.get(pos).getPosY(),true);
        trainPanel.add(rv,BorderLayout.CENTER);
        playFrame.setSize(1000,800);
        playFrame.setVisible(true);


    }

    private void backButtonActionPerformed(ActionEvent e) {
        ((CardLayout) TrainingApp.getAppInstance().trainingFrame.getContentPane().getLayout()).show(
                TrainingApp.getAppInstance().trainingFrame.getContentPane(), "robodromo");
        DefaultListModel model = (DefaultListModel) progList.getModel();
        model.removeAllElements();

    }

    private void createUIComponents() {}


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - valka getz
        createUIComponents();

        trainingFrame = new JFrame();
        roboPanel = new JPanel();
        label1 = new JLabel();
        label2 = new JLabel();
        robodromeChoose = new JComboBox<>();
        chooseButton = new JButton();
        label4 = new JLabel();
        dockChooser = new JComboBox<>();
        continueButton = new JButton();
        progPanel = new JPanel();
        label3 = new JLabel();
        scrollPaneLeft = new JScrollPane();
        cardList = new JList<>();
        scrollPaneRight = new JScrollPane();
        addButton = new JButton();
        deleteButton = new JButton();
        changeButton = new JButton();
        startTraining = new JButton();
        backButton = new JButton();
        playFrame = new JFrame();
        trainPanel = new JPanel();
        label6 = new JLabel();
        scrollPane1 = new JScrollPane();
        list1 = new JList<>();
        panel1 = new JPanel();
        button1 = new JButton();
        button2 = new JButton();
        button3 = new JButton();
        button4 = new JButton();

        //======== trainingFrame ========
        {
            trainingFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            Container trainingFrameContentPane = trainingFrame.getContentPane();
            trainingFrameContentPane.setLayout(new CardLayout());

            //======== roboPanel ========
            {

                // JFormDesigner evaluation mark
                roboPanel.setBorder(new javax.swing.border.CompoundBorder(
                    new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                        "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                        javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                        java.awt.Color.red), roboPanel.getBorder())); roboPanel.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

                roboPanel.setLayout(new MigLayout(
                    "hidemode 3",
                    // columns
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
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
                    "[]"));

                //---- label1 ----
                label1.setText("Benvenuto nel programma di Allenamento RoboGP");
                label1.setFont(new Font("Lucida Grande", Font.BOLD, 18));
                roboPanel.add(label1, "cell 5 0");

                //---- label2 ----
                label2.setText("Seleziona un robodromo per continuare:");
                roboPanel.add(label2, "cell 5 2");

                //---- robodromeChoose ----
                robodromeChoose.setModel(new DefaultComboBoxModel<>(new String[] {
                    "checkmate",
                    "riskyexchange"
                }));
                roboPanel.add(robodromeChoose, "cell 5 3");

                //---- chooseButton ----
                chooseButton.setText("Scegli");
                chooseButton.addActionListener(e -> initButtonActionPerformed(e));
                roboPanel.add(chooseButton, "cell 5 5");

                //---- label4 ----
                label4.setText("Seleziona un Dock di Partenza:");
                label4.setEnabled(false);
                roboPanel.add(label4, "cell 5 7");

                //---- dockChooser ----
                dockChooser.setEnabled(false);
                dockChooser.setModel(new DefaultComboBoxModel<>(new String[] {
                    "--selezionare un elemento--"
                }));
                roboPanel.add(dockChooser, "cell 5 8");

                //---- continueButton ----
                continueButton.setText("Continua");
                continueButton.setEnabled(false);
                continueButton.addActionListener(e -> continueButtonActionPerformed(e));
                roboPanel.add(continueButton, "cell 5 9");
            }
            trainingFrameContentPane.add(roboPanel, "robodromo");

            //======== progPanel ========
            {
                progPanel.setLayout(new MigLayout(
                    "hidemode 3",
                    // columns
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
                    "[fill]" +
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
                    "[]"));

                //---- label3 ----
                label3.setText("Scrivi il programma per l'allenamento ");
                label3.setFont(new Font("Lucida Grande", Font.BOLD, 18));
                progPanel.add(label3, "cell 5 0");

                //======== scrollPaneLeft ========
                {

                    //---- cardList ----
                    cardList.setModel(new AbstractListModel<String>() {
                        String[] values = {
                            "backup",
                            "move1",
                            "move2",
                            "move3",
                            "turnL",
                            "turnR",
                            "uturn"
                        };
                        @Override
                        public int getSize() { return values.length; }
                        @Override
                        public String getElementAt(int i) { return values[i]; }
                    });
                    scrollPaneLeft.setViewportView(cardList);
                }
                progPanel.add(scrollPaneLeft, "cell 5 1 1 4");

                //======== scrollPaneRight ========
                {

                    //---- progList ----
                    progList = new JList();
                    progList.setModel(new DefaultListModel());
                    scrollPaneRight.setViewportView(progList);
                }
                progPanel.add(scrollPaneRight, "cell 5 1 1 4");

                //---- addButton ----
                addButton.setText("Aggiungi");
                addButton.addActionListener(e -> addButtonActionPerformed(e));
                progPanel.add(addButton, "cell 7 1");

                //---- deleteButton ----
                deleteButton.setText("Elimina");
                deleteButton.addActionListener(e -> deleteButtonActionPerformed(e));
                progPanel.add(deleteButton, "cell 7 2");

                //---- changeButton ----
                changeButton.setText("Scambia");
                changeButton.addActionListener(e -> changeButtonActionPerformed(e));
                progPanel.add(changeButton, "cell 7 3");

                //---- startTraining ----
                startTraining.setText("Inizia ");
                startTraining.addActionListener(e -> startTrainingActionPerformed(e));
                progPanel.add(startTraining, "cell 5 5");

                //---- backButton ----
                backButton.setText("Indietro");
                backButton.addActionListener(e -> backButtonActionPerformed(e));
                progPanel.add(backButton, "cell 5 5");
            }
            trainingFrameContentPane.add(progPanel, "prog");
            trainingFrame.pack();
            trainingFrame.setLocationRelativeTo(trainingFrame.getOwner());
        }

        //======== playFrame ========
        {
            playFrame.setVisible(true);
            playFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            Container playFrameContentPane = playFrame.getContentPane();
            playFrameContentPane.setLayout(new BorderLayout());

            //======== trainPanel ========
            {

                // JFormDesigner evaluation mark
                trainPanel.setBorder(new javax.swing.border.CompoundBorder(
                    new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
                        "JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
                        javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
                        java.awt.Color.red), trainPanel.getBorder())); trainPanel.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

                trainPanel.setLayout(new BorderLayout());

                //---- label6 ----
                label6.setText("Il Robodromo \u00e8 pronto!");
                trainPanel.add(label6, BorderLayout.NORTH);

                //======== scrollPane1 ========
                {

                    //---- list1 ----
                    list1.setModel(new AbstractListModel<String>() {
                        String[] values = {
                            "scheda1",
                            "scheda2",
                            "scheda3"
                        };
                        @Override
                        public int getSize() { return values.length; }
                        @Override
                        public String getElementAt(int i) { return values[i]; }
                    });
                    scrollPane1.setViewportView(list1);
                }
                trainPanel.add(scrollPane1, BorderLayout.EAST);

                //======== panel1 ========
                {
                    panel1.setLayout(new FlowLayout());

                    //---- button1 ----
                    button1.setText("Start");
                    panel1.add(button1);

                    //---- button2 ----
                    button2.setText("Stop");
                    panel1.add(button2);

                    //---- button3 ----
                    button3.setText("Pause");
                    panel1.add(button3);

                    //---- button4 ----
                    button4.setText("Resume");
                    panel1.add(button4);
                }
                trainPanel.add(panel1, BorderLayout.SOUTH);
            }
            playFrameContentPane.add(trainPanel, BorderLayout.CENTER);
            playFrame.pack();
            playFrame.setLocationRelativeTo(playFrame.getOwner());
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    @Override
    /**
     * viene chiamato da training quando bisogna fare delle animazioni sul robodromo
     * e come argomento avrà la lista di animazioni da fare,
     * oppure sarà un avvertimento che il training è stato messo in pausa o resume
     * (tale avvertimento è più utile per training helper)
     */
    public void update(Observable o, Object arg) {
        /* */
    }

    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(() -> {
            TrainingApp.singleInstance = new TrainingApp();
            TrainingApp.singleInstance.trainingFrame.setVisible(true);
        });
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - valka getz
    private JFrame trainingFrame;
    private JPanel roboPanel;
    private JLabel label1;
    private JLabel label2;
    private JComboBox<String> robodromeChoose;
    private JButton chooseButton;
    private JLabel label4;
    private JComboBox<String> dockChooser;
    private JButton continueButton;
    private JPanel progPanel;
    private JLabel label3;
    private JScrollPane scrollPaneLeft;
    private JList<String> cardList;
    private JScrollPane scrollPaneRight;
    private JList progList;
    private JButton addButton;
    private JButton deleteButton;
    private JButton changeButton;
    private JButton startTraining;
    private JButton backButton;
    private JFrame playFrame;
    private JPanel trainPanel;
    private JLabel label6;
    private JScrollPane scrollPane1;
    private JList<String> list1;
    private JPanel panel1;
    private JButton button1;
    private JButton button2;
    private JButton button3;
    private JButton button4;


    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

