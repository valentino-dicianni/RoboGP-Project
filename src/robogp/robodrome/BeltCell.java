/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robogp.robodrome;

import java.awt.image.BufferedImage;
import robogp.robodrome.image.ImageUtil;
import robogp.robodrome.image.TileProvider;

/**
 *
 * @author claudia
 */
public class BeltCell extends BoardCell {

    private enum InputDirection {
        Straight, Left, Right
    };
    private final boolean[] inputs;
    private Direction output;

    BeltCell(String[] comps, char subtype) {
        super(subtype);
        inputs = new boolean[3];
        int countin = 0;
        output = Direction.E;

        for (String comp : comps) {
            if (comp.charAt(0) == 'O' && comp.length() >= 2) {
                int dirnum = Integer.parseInt("" + comp.charAt(1));
                output = Direction.values()[dirnum];
            } else if (comp.charAt(0) == 'I' && comp.length() >= 4) {
                for (int j = 0; j < inputs.length; j++) {
                    inputs[j] = (comp.charAt(j + 1) == 'y');
                    if (inputs[j]) {
                        countin++;
                    }
                }
            }
        }

        if (countin <= 0) {
            inputs[0] = true;
        }
    }

    public Direction getOutputDirection() {
        return output;
    }

    public boolean hasInputDirection(Direction dir) {
        if ((Rotation.changeDirection(dir, Rotation.CW180) == output)
                && (inputs[InputDirection.Straight.ordinal()])) {
            return true;
        }
        if ((Rotation.changeDirection(dir, Rotation.CW90) == output)
                && (inputs[InputDirection.Left.ordinal()])) {
            return true;
        }
        if ((Rotation.changeDirection(dir, Rotation.CCW90) == output)
                && (inputs[InputDirection.Right.ordinal()])) {
            return true;
        }
        return false;
    }

    public static Rotation getTurnRotation(BeltCell bcell) {
        switch (bcell.getOutputDirection()) {
            case E:
                if (bcell.hasInputDirection(Direction.N)) {
                    return Rotation.getCounterClockwiseRotation(Direction.N, Direction.E);
                } else {
                    return Rotation.getClockwiseRotation(Direction.S, Direction.E);
                }
            case W:
                if (bcell.hasInputDirection(Direction.N)) {
                    return Rotation.getClockwiseRotation(Direction.N, Direction.W);
                } else {
                    return Rotation.getCounterClockwiseRotation(Direction.S, Direction.W);
                }
            case S:
                if (bcell.hasInputDirection(Direction.E)) {
                    return Rotation.getCounterClockwiseRotation(Direction.E, Direction.S);
                } else {
                    return Rotation.getClockwiseRotation(Direction.W, Direction.S);
                }
            case N:
                if (bcell.hasInputDirection(Direction.E)) {
                    return Rotation.getClockwiseRotation(Direction.E, Direction.N);
                } else {
                    return Rotation.getCounterClockwiseRotation(Direction.W, Direction.N);
                }
        }
        return Rotation.NO;
    }

    @Override
    public BufferedImage getBaseImage() {
        String tilename = this.getType() + "-";
        for (int i = 0; i < inputs.length; i++) {
            if (inputs[i]) {
                tilename += 'y';
            } else {
                tilename += 'n';
            }
        }

        return ImageUtil.superImpose(null, TileProvider.getTileProvider().getTile(tilename, output));
    }

}
