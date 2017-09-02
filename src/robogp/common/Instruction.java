package robogp.common;

import robogp.robodrome.Rotation;
import robogp.robodrome.image.ImageUtil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Instruction {
    private transient BufferedImage cardImage;
    private String name;
    private int stepsToTake;
    private Rotation rotation;

    public Instruction(String name, int stepsToTake, Rotation rotation) {
        this.name = name;
        this.stepsToTake = stepsToTake;
        this.rotation = rotation;
    }

    public int getStepsToTake() {
        return this.stepsToTake;
    }

    public Rotation getRotation() {
        return this.rotation;
    }

    public static Instruction getInstructionByName(String instructionName) {
        switch (instructionName) {
            case "move1":
                return new Instruction(instructionName,1, Rotation.NO);
            case "move2":
                return new Instruction(instructionName,2, Rotation.NO);
            case "move3":
                return new Instruction(instructionName,3, Rotation.NO);
            case "turnL":
                return new Instruction(instructionName,0, Rotation.CCW90);
            case "turnR":
                return new Instruction(instructionName,0, Rotation.CW90);
            case "uturn":
                return new Instruction(instructionName,0, Rotation.CW180);
            case "backup":
                return new Instruction(instructionName,-1, Rotation.NO);
        }
        return null;
    }

    public BufferedImage getImage(int size) {
        if (this.cardImage == null) {
            String imgFile = "tiles/card-" + name + ".png";
            try {
                cardImage = ImageIO.read(new File(imgFile));
            } catch (IOException ex) {
                System.out.println("Scheda immagine non caricata correttamente");
            }
        }
        return ImageUtil.scale(ImageUtil.superImpose(null, this.cardImage), size, size);
    }

    @Override
    public String toString() {
        return "Steps: "+stepsToTake+", Angle: "+rotation.toString();
    }
}
