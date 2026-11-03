package deepRed.deepred_engine.model;

import deepRed.deepred_engine.model.Move;

public record Réponse(Move move, int score, int depth) {}