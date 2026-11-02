package deepRed.model;

import deepRed.model.Board;
import deepRed.model.Move;
import java.util.ArrayList;
import java.util.List;

// Pour le board, un pion blanc est 1, un pion noir est 2
// timelimit is in millisSeconds
public record MoveRequestMessage(int activeColor, int timeLimit, Board board, List<Move> legalMoves) { }
