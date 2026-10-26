package com.kleblanc.randomAi.model.message;

import com.kleblanc.randomAi.model.Move;
import com.kleblanc.randomAi.model.iMove;

import java.util.ArrayList;

// Pour le board, un pion blanc est 1, un pion noir est 2
// X est la première dimension (largeur), Y est la deuxième (hauteur)
public record MoveRequestMessage(int activeColor, int timeLimit, int[][] board, ArrayList<Move> legalMoves) { }
