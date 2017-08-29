/*
 * Created by JFormDesigner on Thu Aug 24 12:15:26 CEST 2017
 */

package robogp.training;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import net.miginfocom.swing.*;
import robogp.robodrome.Robodrome;
import robogp.robodrome.view.RobodromeView;

/**
 * @author valka getz
 */
public class TrainingApp extends JFrame {
    private  RobodromeView toShow;

    public TrainingApp() {
        initComponents();
    }

    private void startTrainingActionPerformed(ActionEvent e) {
        // TODO add your code here
        textArea1.setVisible(true);
        remove(startButton);
        remove(textArea1);
        showRobodrome();

    }

    private void showRobodrome(){
        toShow = new RobodromeView(new Robodrome("robodromes/riskyexchange.txt"), 35);
        this.getContentPane().add(toShow, BorderLayout.CENTER);


    }


    // JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
    // Generated using JFormDesigner Evaluation license - valka getz
    private JButton startButton;
    private JTextArea textArea1;
    // JFormDesigner - End of variables declaration  //GEN-END:variables


    private void initComponents() {
        // JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
        // Generated using JFormDesigner Evaluation license - valka getz
        startButton = new JButton();
        textArea1 = new JTextArea();

        //======== this ========
        Container contentPane = getContentPane();
        contentPane.setLayout(new MigLayout(
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
            "[]"));

        //---- startButton ----
        startButton.setText("Inizia Allenamento");
        startButton.addActionListener(e -> startTrainingActionPerformed(e));
        contentPane.add(startButton, "cell 11 2");

        //---- textArea1 ----
        textArea1.setText("Caricamento...");
        textArea1.setEditable(false);
        textArea1.setVisible(false);
        contentPane.add(textArea1, "cell 11 3");
        pack();
        setLocationRelativeTo(getOwner());
        // JFormDesigner - End of component initialization  //GEN-END:initComponents
    }


    public static void main(String args[]) {
        TrainingApp training = new TrainingApp();
        training.setVisible(true);

    }
}