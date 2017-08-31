package robogp.training;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;
import net.miginfocom.swing.*;
import robogp.robodrome.Position;

/**
 * @author valka getz
 */
public class TrainingApp  {

    private static TrainingApp singleInstance;
    private final IniziareTrainingControl inizPartCtrl;




    private TrainingApp(){
        initComponents();
        this.inizPartCtrl = IniziareTrainingControl.getInstance();
    }
    public static TrainingApp getAppInstance() {
        return TrainingApp.singleInstance;
    }


    private void initButtonActionPerformed(ActionEvent e) {
        ArrayList<Position> dockPos = inizPartCtrl.setRobodrome((String)robodromeChoose.getSelectedItem());
        DefaultComboBoxModel model = (DefaultComboBoxModel)dockChooser.getModel();
        model.removeAllElements();
        for(Position pos : dockPos){
            model.addElement(pos.toString());
        }
        label4.setEnabled(true);
        dockChooser.setEnabled(true);
        continueButton.setEnabled(true);

    }
    private void continueButtonActionPerformed(ActionEvent e) {
        ((CardLayout) TrainingApp.getAppInstance().TrainingFrame.getContentPane().getLayout()).show(
              TrainingApp.getAppInstance().TrainingFrame.getContentPane(), "prog");
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
            JOptionPane.showMessageDialog(TrainingApp.getAppInstance().TrainingFrame,
                    "Selezionare due schede istruzione!","Attenzione:",
                    JOptionPane.WARNING_MESSAGE);
        }


    }

    private void startTrainingActionPerformed(ActionEvent e) {
        //TODO aggiungere al metodo start la posizione iniziale

        DefaultListModel model = (DefaultListModel) progList.getModel();
        model.toArray();
        inizPartCtrl.start(model.toArray());
    }

    private void backButtonActionPerformed(ActionEvent e) {
        ((CardLayout) TrainingApp.getAppInstance().TrainingFrame.getContentPane().getLayout()).show(
                TrainingApp.getAppInstance().TrainingFrame.getContentPane(), "robodromo");
        DefaultListModel model = (DefaultListModel) progList.getModel();
        model.removeAllElements();

    }

    private void createUIComponents() {}


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - valka getz
        createUIComponents();

        TrainingFrame = new JFrame();
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
        trainPanel = new JPanel();

        //======== TrainingFrame ========
        {
            TrainingFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            TrainingFrame.setResizable(false);
            Container TrainingFrameContentPane = TrainingFrame.getContentPane();
            TrainingFrameContentPane.setLayout(new CardLayout());

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
            TrainingFrameContentPane.add(roboPanel, "robodromo");

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
            TrainingFrameContentPane.add(progPanel, "prog");

            //======== trainPanel ========
            {
                trainPanel.setLayout(new BorderLayout());
            }
            TrainingFrameContentPane.add(trainPanel, "showTrain");
            TrainingFrame.pack();
            TrainingFrame.setLocationRelativeTo(TrainingFrame.getOwner());
        }
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }

    public static void main(String[] args) {

        java.awt.EventQueue.invokeLater(() -> {
            TrainingApp.singleInstance = new TrainingApp();
            TrainingApp.singleInstance.TrainingFrame.setVisible(true);
        });
    }

    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - valka getz
    private JFrame TrainingFrame;
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
    private JPanel trainPanel;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}

