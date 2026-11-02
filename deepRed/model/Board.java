package deepRed.model;

import deepRed.model.Move;

public class Board {
    private byte[][] board = new byte[8][8];;

    public Board() {
    }

    public byte[][] getBoard() {
    byte[][] copy = new byte[8][8];

    for (int i = 0; i < 8; i++) {
        System.arraycopy(board[i], 0, copy[i], 0, 8);
    }

    return copy;
    }

    public void setBoard(byte[][] newBoard) {
    if (newBoard.length != 8) {
        throw new IllegalArgumentException("Board must have 8 rows");
    }

    for (int i = 0; i < 8; i++) {
        if (newBoard[i].length != 8) {
            throw new IllegalArgumentException("Board must have 8 columns");
        }

        System.arraycopy(newBoard[i], 0, board[i], 0, 8);
    }
}

    public void setPoint(Point point, byte value) {
        board[point.getX()][point.getY()] =  value;
    }

    public byte getPoint(Point point) {
        return board[point.getX()][point.getY()];
    }
}