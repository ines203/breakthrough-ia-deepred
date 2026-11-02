package deepRed.model;

import java.util.ArrayList;
import java.util.List;

import deepRed.model.Move;

public class Board {
    private byte[][] board = new byte[8][8];;

    public Board() {
    }

    public byte[][] getBoard() 
    {
        byte[][] copy = new byte[8][8];

        for (int i = 0; i < 8; i++) 
        {
            System.arraycopy(board[i], 0, copy[i], 0, 8);
        }
        return copy;
    }

    public void setBoard(byte[][] newBoard) 
    {
        if (newBoard.length != 8) 
        {
            throw new IllegalArgumentException("Board must have 8 rows");
        }

        for (int i = 0; i < 8; i++) 
        {
            if (newBoard[i].length != 8) 
            {
                throw new IllegalArgumentException("Board must have 8 columns");
            }
            System.arraycopy(newBoard[i], 0, board[i], 0, 8);
        }
    }

    public void setPoint(Point point, byte value)
    {
        board[point.getX()][point.getY()] =  value;
    }

    public byte getPoint(Point point) 
    {
        return board[point.getX()][point.getY()];
    }

    // This doesn't check if the move is legal
    public void applyMove(Move move)
    {
        byte valueMoved = board­[move.getSource().getX()][move.getSource().getY()];

        if (valueMoved == 0)
            throw new IllegalArgumentException("Source position is empty");

        board­[move.getSource().getX()][move.getSource().getY()] = 0;
        board­[move.getTarget().getX()][move.getTarget().getY()] = valueMoved;
    }

    public List<Move> getLegalMoves(byte activeColor)
    {
        if (activeColor != 1 && activeColor != 2)
            throw new IllegalArgumentException("Invalid player color");

        List<Move> legalMoves = new ArrayList<>();
        int direction = (activeColor == 1) ? -1 : 1;
        byte opponentColor = (activeColor == 1) ? (byte) 2 : (byte) 1;

        for (int x = 0; x < 8; x++) 
        {
            for (int y = 0; y < 8; y++)
            {
                // Only consider pawns belonging to the active player
                if (board[x][y] != activeColor)
                {
                    continue;
                }

                int nextX = x + direction;

                // Cannot move outside the board
                if (nextX < 0 || nextX >= 8) {
                    continue;
                }

                // 1. Straight forward movement
                if (board[nextX][y] == 0) {
                    legalMoves.add(new Move(
                        new Point(x, y),
                        new Point(nextX, y)
                    ));
                }

                // 2. Diagonal movements
                for (int dy : new int[]{-1, 1}) {
                    int nextY = y + dy;

                    // Check board boundaries
                    if (nextY < 0 || nextY >= 8) {
                        continue;
                    }

                    // Can only capture opponent pawns diagonally
                    if (board[nextX][nextY] == opponentColor) {
                        legalMoves.add(new Move(
                            new Point(x, y),
                            new Point(nextX, nextY)
                        ));
                    }
                }
            }
        }
        return legalMoves;
    }

    public boolean isGameOver()
    {
        for (int i = 0; i < 8; i++) 
            if (board[0][i] == 1)
                return true;
        for (int i = 0; i < 8; i++) 
            if (board[7][i] == 2)
                return true;
        return false;
    }
}