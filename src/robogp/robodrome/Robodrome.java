/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package robogp.robodrome;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * La classe Robodrome rappresenta un tabellone per il gioco RoboGP. 
 * @author claudia
 */
public class Robodrome {

    private final BoardCell[][] board;
    private final String name;
    private final int rows;
    private final int columns;
    private final java.util.ArrayList<CellLaser> allLasers;
    private int docksCount;
    private ArrayList<Position> dockPos = new ArrayList();

    /**
     * Costruisce un robodromo a partire da un file che lo descrive.
     * @param filename il pathname del file
     */
    public Robodrome(String filename) {
        allLasers = new java.util.ArrayList<>();
        docksCount = 0;
        StringBuilder stringBuilder = new StringBuilder();
        try {
            BufferedReader reader;
            reader = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line.trim());
            }
        } catch (IOException ex) {
            Logger.getLogger(Robodrome.class.getName()).log(Level.SEVERE, null, ex);
        }
        String[] all = stringBuilder.toString().split(";");
        name = all[0].trim();
        String[] dim = all[1].trim().split("x");
        columns = Integer.parseInt(dim[0]);
        rows = Integer.parseInt(dim[1]);
        board = new BoardCell[rows][columns];
        int countcell = 0;
        boolean endfile = false;
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < columns; c++) {
                if (!endfile) {
                    if (countcell + 2 < all.length) {
                        board[r][c] = BoardCell.createBoardCell(all[countcell + 2].trim().split("-"));
                        countcell++;
                        if (countcell + 2 >= all.length) {
                            endfile = true;
                        }
                        java.util.ArrayList<CellLaser> cellLasers = board[r][c].getLasers();
                        for (CellLaser las : cellLasers) {
                            las.setPosition(r, c);
                            allLasers.add(las);
                        }
                    }
                } else {
                    board[r][c] = new FloorCell(new String[0]);
                }
                if (board[r][c] instanceof FloorCell && ((FloorCell)board[r][c]).isDock()) {
                    this.docksCount++;
                    Position doc = new Position(r,c ,((FloorCell)board[r][c]).getDockDirection());
                    dockPos.add(doc);
                }
            }
        }
        for (CellLaser las : allLasers) {
            Direction dir = las.getWall();
            boolean stop = false;
            int r = las.getRow();
            int c = las.getCol();
            switch (dir) {
                case W:
                    while (!stop) {
                        board[r][c].setHorizontalLaser(true);
                        if (board[r][c].hasWall(Direction.E) || (c + 1) == columns || board[r][c + 1].hasWall(Direction.W)) {
                            stop = true;
                        } else {
                            c++;
                        }
                    }
                    break;
                case E:
                    while (!stop) {
                        board[r][c].setHorizontalLaser(true);
                        if (board[r][c].hasWall(Direction.W) || c == 0 || board[r][c - 1].hasWall(Direction.E)) {
                            stop = true;
                        } else {
                            c--;
                        }
                    }
                    break;
                case N:
                    while (!stop) {
                        board[r][c].setVerticalLaser(true);
                        if (board[r][c].hasWall(Direction.S) || (r + 1) == rows || board[r + 1][c].hasWall(Direction.N)) {
                            stop = true;
                        } else {
                            r++;
                        }
                    }
                    break;
                case S:
                    while (!stop) {
                        board[r][c].setVerticalLaser(true);
                        if (board[r][c].hasWall(Direction.N) || r == 0 || board[r - 1][c].hasWall(Direction.S)) {
                            stop = true;
                        } else {
                            r--;
                        }
                    }
                    break;
            }
        }
    }
    
    /**
     * Restituisce la casella nella posizione specificata
     * @param row la riga della casella
     * @param col la colonna della casella
     * @return la casella in posizione (riga, colonna)
     */
    public BoardCell getCell(int row, int col) {
        if (row < rows && col < columns) {
            return board[row][col];
        }
        return null;
    }

    /**
     *
     * @param posX, posY: coordinate della cella di cui si vuole prendere la cella adiacente
     * @param dir direzione in cui è la cella adiacente
     * @return cella adiacente a quella data
     */
    public BoardCell getNextCell(int posX, int posY, Direction dir) {
        if (posX < 0 || posY < 0)
            throw new ArrayIndexOutOfBoundsException();
        switch (dir) {
            case N:
                if (posX == 0)
                    throw new ArrayIndexOutOfBoundsException();
                return getCell(posX - 1, posY);
            case S:
                if (posX == board.length - 1)
                    throw new ArrayIndexOutOfBoundsException();
                return getCell(posX + 1, posY);
            case W:
                if (posY == 0)
                    throw new ArrayIndexOutOfBoundsException();
                return getCell(posX, posY - 1);
            case E:
                if (posY == board[0].length - 1)
                    throw new ArrayIndexOutOfBoundsException();
                return getCell(posX, posY + 1);
            default:
                return null;
        }
    }

    /**
     * data una posizione e una direzione guarda se il le prime due caselle sul percorso hanno un muro
     * che impedirebbe il movimento nella casella successiva
     * TODO: scrivere il metodo i maniera più corta, ciclo while?
     * @param posX
     * @param posY
     * @param dir
     * @return true se il percorso non è ostruito da muro, false altrimenti
     */
    public boolean pathHasWall(int posX, int posY, Direction dir) {
        BoardCell landingCell = getCell(posX, posY);
        if (landingCell instanceof FloorCell) {
            FloorCell fcell = (FloorCell)landingCell;
            if (fcell.hasWall(dir)) {
                return true;
            }
        } else if (landingCell instanceof PitCell) {
            PitCell pcell = (PitCell)landingCell;
            if (pcell.hasWall(dir)) {
                return true;
            }
        }

        landingCell = getNextCell(posX, posY, dir);
        // controlla che la cella successiva non abbia un muro nella direzione opposta
        if (landingCell instanceof  FloorCell) {
            FloorCell nfcell = (FloorCell)landingCell;
            if (nfcell.hasWall(Direction.getOppositeDirection(dir))) {
                return true;
            }
        } else if (landingCell instanceof PitCell) {
            PitCell pcell = (PitCell)landingCell;
            if (pcell.hasWall(dir)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return il numero di righe nel tabellone
     */
    public int getRowCount() {
        return rows;
    }

    /**
     * 
     * @return il numero di colonne nel tabellone
     */
    public int getColumnCount() {
        return columns;
    }

    /**
     *
     * @return il nome di questo robodromo
     */
    public String getName() {
        return name;
    }

    public int getDocksCount() {
        return this.docksCount;
    }

    public ArrayList<Position> getDockPos() {
        return dockPos;
    }
}
