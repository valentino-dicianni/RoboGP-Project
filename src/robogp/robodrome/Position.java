package robogp.robodrome;

public class Position {
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

    public void changePosition(int movement, Rotation rotation) {
        if (rotation == Rotation.NO) {
            // movimento senza rotazione
            if (this.dir == Direction.E) this.posX += movement;
            else if (this.dir == Direction.W) this.posX -= movement;
            else if (this.dir == Direction.N) this.posY += movement;
            else if (this.dir == Direction.S) this.posY -= movement;
        } else {
            // movimento con rotazione
            this.dir = Rotation.changeDirection(this.dir, rotation);
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
