package robogp.robodrome;

import java.io.Serializable;

public class Position implements Serializable{
    private int posX;
    private int posY;
    private Direction dir;

    public Position(int posX, int posY, Direction dir) {
        this.posX = posX;
        this.posY = posY;
        this.dir = dir;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public Direction getDirection() {
        return this.dir;
    }

    public void setDirection(Direction dir) {
        this.dir = dir;
    }

    public void changePosition(int movement, Rotation rotation) {
        if (rotation == Rotation.NO) {
            // movimento senza rotazione
            if (this.dir == Direction.E) this.posY += movement;
            else if (this.dir == Direction.W) this.posY -= movement;
            else if (this.dir == Direction.N) this.posX -= movement;
            else if (this.dir == Direction.S) this.posX += movement;
        } else {
            // movimento con rotazione
            this.dir = Rotation.changeDirection(this.dir, rotation);
        }
    }

    public void changePosition(int movement, Direction dir, Rotation rotation) {
        if (rotation == Rotation.NO) {
            // movimento senza rotazione
            switch (dir) {
                case E:
                    this.posY += movement;
                    break;
                case W:
                    this.posY -= movement;
                    break;
                case N:
                    this.posX -= movement;
                    break;
                case S:
                    this.posX += movement;
                    break;
            }
        } else {
            // movimento con rotazione
            this.dir = Rotation.changeDirection(dir, rotation);
        }
    }

    @Override
    public String toString() {
        return "Dock position: posX = "+posX+", posY = "+posY+", Direction = " + dir;
    }

    @Override
    public Position clone() {
        return new Position(posX, posY, dir);
    }
}
