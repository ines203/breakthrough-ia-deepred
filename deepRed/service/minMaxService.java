package deepRed.service;

import deepRed.model.Board;
import deepRed.model.Move;

public class minMaxService 
{
    public static int minMax(Board board, int depth, boolean maximizing,
        int alpha, int beta) 
    {
        if (depth == 0 || board.isGameOver())
        {
            return evaluationService.evaluateBoard(board);
        }

        if (maximizing)
        {
            int maxScore = Integer.MIN_VALUE;

            for (Move move : board.getLegalMoves(1))
            {
                Board next = new Board();
                next.setBoard(board.getBoard());
                next.applyMove(move);

                int score = minMax(next, depth - 1, false, alpha, beta);

                maxScore = Math.max(maxScore, score);
                alpha = Math.max(alpha, score);

                if (beta <= alpha) break;
            }
            return maxScore;
        }

        // Minimizing player's turn
        int minScore = Integer.MAX_VALUE;

        for (Move move : board.getLegalMoves(2)) {
            Board next = new Board();
            next.setBoard(board.getBoard());
            next.applyMove(move);

            int score = minMax(next, depth - 1, true, alpha, beta);

            minScore = Math.min(minScore, score);
            beta = Math.min(beta, score);

            if (beta <= alpha) break;
        }
        return minScore;
    }
}
