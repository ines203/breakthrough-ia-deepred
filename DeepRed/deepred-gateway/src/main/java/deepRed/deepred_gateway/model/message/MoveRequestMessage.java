package deepRed.deepred_gateway.model.message;

import deepRed.deepred_gateway.model.Move;

import java.util.List;

public record MoveRequestMessage(int activeColor, int timeLimit, int[][] board, List<Move> legalMoves) {}
