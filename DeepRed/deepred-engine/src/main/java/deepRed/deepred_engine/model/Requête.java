package deepRed.deepred_engine.model;

import deepRed.deepred_engine.model.Board;
import deepRed.deepred_engine.model.Move;
import java.util.ArrayList;
import java.util.List;

// Pour le board, un pion blanc est 1, un pion noir est 2
// timelimit is in millisSeconds
public record Requête(int[][] board, int activeColor, List<Move> moves, int deadlineMs)
{
    public boolean isValid() 
    {
    return (board != null && (activeColor == 1 || activeColor == 2) && moves != null && deadlineMs != 0);
    }
}
