/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robogp.robodrome;

/**
 *
 * @author claudia
 */
public enum Direction {
    W, N, E, S;

    public static Direction getOppositeDirection(Direction direction) {
        switch (direction) {
            case N:
                return S;
            case E:
                return W;
            case W:
                return E;
            case S:
                return N;
            default:
                return null;
        }
    }

    public static int getDirectionAxis(Direction direction) {
        switch (direction) {
            case N:
                return -1;
            case E:
                return 1;
            case W:
                return -1;
            case S:
                return 1;
            default:
                return 0;
        }
    }

    public static boolean isHorizontal(Direction direction) {
        switch (direction) {
            case N:
            case S:
                return false;
            case W:
            case E:
                return true;
        }
        return false;
    }
};

