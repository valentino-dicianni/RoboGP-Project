package robogp.robodrome;

public class Position {
    private int posX;
    private int posY;
    private Direction dir;

    public Position(int posX, int posY, Direction rotation) {
        this.posX = posX;
        this.posY = posY;
        this.dir = rotation;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public Direction getRotation() {
        return this.dir;
    }

    @Override
    public String toString() {
        return "Dock position: posX = "+posX+", posY = "+posY+", Direction = " + dir;
    }
}
