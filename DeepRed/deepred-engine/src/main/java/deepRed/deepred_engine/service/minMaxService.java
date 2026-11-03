package deepRed.deepred_engine.service;

import deepRed.deepred_engine.model.Board;
import deepRed.deepred_engine.model.Move;
import deepRed.deepred_engine.service.evaluationService;

public class minMaxService 
{
    public static int minMax(
    Board board,
    int depth,
    int currentPlayer,
    int aiColor,
    int alpha,
    int beta)
    {
        int opponent = (currentPlayer == 1) ? 2 : 1;

        if (depth == 0 || board.isGameOver()) {
            return evaluationService.evaluateBoard(board, aiColor);
            //return evaluationService.evaluateBoard(board, aiColor);
        }

        boolean maximizing = currentPlayer == aiColor;

        int bestScore = maximizing
            ? Integer.MIN_VALUE
            : Integer.MAX_VALUE;

        for (Move move : board.getLegalMoves(currentPlayer)) {

            Board next = new Board();
            next.setBoard(board.getBoard());
            next.applyMove(move);

            int score = minMax(
                next,
                depth - 1,
                opponent,
                aiColor,
                alpha,
                beta
            );

            if (maximizing) {
                bestScore = Math.max(bestScore, score);
                alpha = Math.max(alpha, score);
            } else {
                bestScore = Math.min(bestScore, score);
                beta = Math.min(beta, score);
            }

            if (beta <= alpha) {
                break;
            }
        }
        return bestScore;
    }
}
