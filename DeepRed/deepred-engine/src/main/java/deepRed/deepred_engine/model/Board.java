package deepRed.deepred_engine.model;

import java.util.ArrayList;
import java.util.List;

import deepRed.deepred_engine.model.Move;

/*
  01234567 Y

0 22222222
1 20222222
2 00200000
3 00000000
4 00000000
5 00000000
6 11111111
7 11111111
X
 */

public class Board {
    private int[][] board = new int[8][8];;

    public Board() {
    }

    public int[][] getBoard() 
    {
        int[][] copy = new int[8][8];

        for (int i = 0; i < 8; i++) 
        {
            System.arraycopy(board[i], 0, copy[i], 0, 8);
        }
        return copy;
    }

    public void setBoard(int[][] newBoard) 
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

    public void setPoint(Point point, int value)
    {
        board[point.getX()][point.getY()] =  value;
    }

    public int getPoint(Point point) 
    {
        return board[point.getX()][point.getY()];
    }

    public int getPoint(int x, int y) 
    {
        if (x < 0 || x > 7 || y < 0 || y > 7)
            return 0;
        return board[x][y];
    }

    // This doesn't check if the move is legal
    public void applyMove(Move move)
    {
        int valueMoved = board­[move.getSource().getX()][move.getSource().getY()];

        if (valueMoved == 0)
            throw new IllegalArgumentException("Source position is empty");

        board­[move.getSource().getX()][move.getSource().getY()] = 0;
        board­[move.getTarget().getX()][move.getTarget().getY()] = valueMoved;
    }

    public List<Move> getLegalMoves(int activeColor)
    {
        if (activeColor != 1 && activeColor != 2)
            throw new IllegalArgumentException("Invalid player color");

        List<Move> legalMoves = new ArrayList<>();
        int direction = (activeColor == 1) ? -1 : 1;
        int opponentColor = (activeColor == 1) ? (int) 2 : (int) 1;

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