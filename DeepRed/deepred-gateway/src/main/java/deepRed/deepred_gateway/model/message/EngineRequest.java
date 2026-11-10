package deepRed.deepred_gateway.model.message;

import deepRed.deepred_gateway.model.Move;

import java.util.List;

public record EngineRequest(int[][] board, int activeColor, List<Move> moves, long deadlineMs) {}
