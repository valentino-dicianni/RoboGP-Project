package robogp.matchmanager;


import javax.swing.ImageIcon;

/**
 *
 * @author claudia
 */
public class RobotStatePanel extends javax.swing.JPanel {

    private final MatchRobot robot;
    /**
     * Creates new form RobotStatePanel
     */
    public RobotStatePanel(MatchRobot rob) {
        this.robot = rob;
        initComponents();
        this.update();
    }
    
    public void update() {
        this.robotLabel.setIcon(new ImageIcon(robot.getImage(80)));
        String text = (robot.isAssigned() ? robot.getOwner() : "Available");
        this.robotLabel.setText(text);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        robotLabel = new javax.swing.JLabel();

        robotLabel.setFont(new java.awt.Font("Lucida Grande", 0, 16)); // NOI18N
        robotLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        robotLabel.setIcon(new javax.swing.ImageIcon("/Users/claudia/Documents/Didattica/SvilASW/RoboGP-IniziarePartita/robots/robot-green.png")); // NOI18N
        robotLabel.setText("Player");
        robotLabel.setAlignmentX(0.5F);
        robotLabel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        robotLabel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        add(robotLabel);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel robotLabel;
    // End of variables declaration//GEN-END:variables
}
