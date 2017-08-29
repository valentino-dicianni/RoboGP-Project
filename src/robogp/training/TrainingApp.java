/*
 * Created by JFormDesigner on Thu Aug 24 12:15:26 CEST 2017
 */

package robogp.training;

import javax.swing.*;

/**
 * @author valka getz
 */
public class TrainingApp extends JFrame {
    private TrainingGUI mainPanel = new TrainingGUI();


    public TrainingApp() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        add(mainPanel);
        setVisible(true);
        mainPanel.setSize(2000,2000);
        pack();
    }

    public static void main(String args[]) {
        TrainingApp training = new TrainingApp();
        training.setVisible(true);

    }
}