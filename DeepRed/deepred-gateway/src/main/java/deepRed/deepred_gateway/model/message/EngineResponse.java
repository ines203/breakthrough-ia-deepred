package deepRed.deepred_gateway.model.message;

import deepRed.deepred_gateway.model.Move;

public record EngineResponse(Move move, int score, int depth) {}
