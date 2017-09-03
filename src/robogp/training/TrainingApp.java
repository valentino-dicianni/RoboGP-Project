package robogp.training;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.plaf.metal.MetalBorders;

import net.miginfocom.swing.*;
import robogp.common.Instruction;
import robogp.robodrome.Direction;
import robogp.robodrome.Position;
import robogp.robodrome.Rotation;
import robogp.robodrome.view.RobodromeView;

/**
 * @author valka getz
 */
public class TrainingApp implements Observer {

    private static TrainingApp singleInstance;
    private final IniziareTrainingControl inizPartCtrl;
    private RobodromeView rv;
    private ArrayList<Position> dockPos;



    private TrainingApp(){
        initComponents();
        this.inizPartCtrl = IniziareTrainingControl.getInstance();
        Training training = Training.getInstance();
        training.addObserver(this);
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
        if(progList.getModel().getSize() != 0){
            DefaultListModel model = (DefaultListModel) progList.getModel();
            model.toArray();
            trainingFrame.dispose();
            rv = new RobodromeView(inizPartCtrl.getRobodrome(), 55);
            int pos = Integer.parseInt(((String)dockChooser.getSelectedItem()).split("\\.")[0]);
            inizPartCtrl.start(model.toArray(), dockPos.get(pos));
            rv.placeRobot(inizPartCtrl.getTrainingRobot(), dockPos.get(pos).getDirection(),dockPos.get(pos).getPosX(),dockPos.get(pos).getPosY(),true);
            trainPanel.add(rv,BorderLayout.CENTER);
            rv.addObserver(Training.getInstance());
            playFrame.setSize(1050,800);
            playFrame.setVisible(true);
        }
        else{
            JOptionPane.showMessageDialog(TrainingApp.getAppInstance().trainingFrame,
                    "Devi selezionare almeno un'istruzione!","Attenzione:",
                    JOptionPane.WARNING_MESSAGE);
        }


    }

    private void backButtonActionPerformed(ActionEvent e) {
        ((CardLayout) TrainingApp.getAppInstance().trainingFrame.getContentPane().getLayout()).show(
                TrainingApp.getAppInstance().trainingFrame.getContentPane(), "robodromo");
        DefaultListModel model = (DefaultListModel) progList.getModel();
        model.removeAllElements();

    }

    private void createUIComponents() {}

    private void iniziaButtonActionPerformed(ActionEvent e) {
        inizPartCtrl.inizia();
    }


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
        progRenderList = new JList<>();
        buttonPanel = new JPanel();
        iniziaButton = new JButton();
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

                    //---- progRenderList ----
                    progRenderList.setBackground(Color.black);
                    progRenderList.setModel(new DefaultListModel());
                    scrollPane1.setViewportView(progRenderList);
                }
                trainPanel.add(scrollPane1, BorderLayout.EAST);

                //======== buttonPanel ========
                {
                    buttonPanel.setBackground(Color.white);
                    buttonPanel.setLayout(new FlowLayout());

                    //---- iniziaButton ----
                    iniziaButton.setText("Start");
                    iniziaButton.addActionListener(e -> iniziaButtonActionPerformed(e));
                    buttonPanel.add(iniziaButton);

                    //---- button2 ----
                    button2.setText("Stop");
                    buttonPanel.add(button2);

                    //---- button3 ----
                    button3.setText("Pause");
                    buttonPanel.add(button3);

                    //---- button4 ----
                    button4.setText("Resume");
                    buttonPanel.add(button4);
                }
                trainPanel.add(buttonPanel, BorderLayout.SOUTH);
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
        /* azione iniziale per visualizzare lista di istruzioni del programma */
        if (arg instanceof ArrayList){
            renderProgramList((ArrayList<Instruction>)arg);
        }
        /* azione per fare vedere le animazioni del robot sulla robodrome view, lista di stringhe tipo "stepsToTake:direction:rotation" */
        if (arg instanceof String[]) {
            // le istruzioni sono da mettere in coda e poi eseguire
            String[] strar = (String[])arg;
            System.out.println("Animations to exec: "+strar[0]+" .. "+strar[1]);
            for (String inst: strar) {
                if (inst != null) {
                    String[] animdata = inst.split(":");
                    int movement = Integer.parseInt(animdata[0]);
                    Direction dir = Direction.valueOf(animdata[1]);
                    Rotation rot = Rotation.valueOf(animdata[2]);
                    rv.addRobotMove(Training.getInstance().getRobot(), movement, dir, rot);
                }
            }
            rv.play();


        }

    }

    private void renderProgramList(ArrayList<Instruction> prog) {
        DefaultListModel model = (DefaultListModel) progRenderList.getModel();
        for(Instruction instr : prog){
            model.addElement(instr);
        }
        progRenderList.setCellRenderer(new MyListCellRenderer());
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
    private JList<Instruction> progRenderList;
    private JPanel buttonPanel;
    private JButton iniziaButton;
    private JButton button2;
    private JButton button3;
    private JButton button4;
    // JFormDesigner - End of variables declaration  //GEN-END:variables
}


class MyListCellRenderer extends JLabel implements ListCellRenderer<Instruction> {

    public MyListCellRenderer() {
        setBorder(new MetalBorders.InternalFrameBorder());
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Instruction> list, Instruction value, int index, boolean isSelected, boolean cellHasFocus) {
        ImageIcon imageIcon = new ImageIcon(value.getImage(100));
        setIcon(imageIcon);
        return this;
    }
}