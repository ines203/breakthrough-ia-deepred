package deepRed.deepred_gateway.model.message;

import deepRed.deepred_gateway.model.Move;

public record MoveResponseMessage(String aiId, Move move) {}
