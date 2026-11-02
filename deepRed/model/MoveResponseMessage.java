package deepRed.model;

import deepRed.model.Move;

public record MoveResponseMessage(Move move, int score, int depth) {}