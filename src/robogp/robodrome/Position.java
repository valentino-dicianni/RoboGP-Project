package robogp.robodrome;

public class Position {
    private int posX;
    private int posY;
    private Rotation rotation;

    public Position(int posX, int posY, Rotation rotation) {
        this.posX = posX;
        this.posY = posY;
        this.rotation = rotation;
    }

    public int getPosX() {
        return posX;
    }

    public int getPosY() {
        return posY;
    }

    public Rotation getRotation() {
        return rotation;
    }
}
