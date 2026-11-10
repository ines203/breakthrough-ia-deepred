package main.java.deepRed.deepred_engine.service;

import deepRed.deepred_engine.model.Requête;
import deepRed.deepred_engine.model.Réponse;

import deepRed.deepred_engine.model.Board;
import deepRed.deepred_engine.model.Move;
import java.util.List;

public class handleMovesService
{
    public static Réponse handleMoves(Requête request)
    {
        int aiColor = request.activeColor();
        int opponent = aiColor == 1 ? 2 : 1;

        Board board = new Board();
        board.setBoard(request.board());

        List<Move> legalMoves = request.moves();

        if (legalMoves == null || legalMoves.isEmpty()) {
            throw new IllegalArgumentException("No legal moves provided");
        }

        Move bestMove = legalMoves.get(0);
        int bestScore = Integer.MIN_VALUE;
        int completedDepth = 0;

        for (int depth = 1; ; depth++) {

            if (System.currentTimeMillis() >= request.deadlineMs()) {
                break;
            }

            Move iterationBestMove = bestMove;
            int iterationBestScore = Integer.MIN_VALUE;
            boolean timedOut = false;

            for (Move move : legalMoves) {

                if (System.currentTimeMillis() >= request.deadlineMs()) {
                    timedOut = true;
                    break;
                }

                Board nextBoard = new Board();
                nextBoard.setBoard(board.getBoard());
                nextBoard.applyMove(move);

                int score = minMaxService.minMax(
                    nextBoard,
                    depth - 1,
                    opponent,
                    aiColor,
                    Integer.MIN_VALUE,
                    Integer.MAX_VALUE
                );

                if (score > iterationBestScore) {
                    iterationBestScore = score;
                    iterationBestMove = move;
                }
            }

            // Do not save an incomplete iteration.
            if (timedOut) {
                break;
            }

            bestMove = iterationBestMove;
            bestScore = iterationBestScore;
            completedDepth = depth;
        }

        return new Réponse(
            bestMove,
            bestScore,
            completedDepth
        );
    }
}
