package com.kleblanc.breakthrough_backend.model.message;

import com.kleblanc.breakthrough_backend.model.iMove;

import java.util.ArrayList;

public record MoveRequestMessage(int activeColor, int timeLimit, int[][] board, ArrayList<? extends iMove> legalMoves) { }
