package deepRed.service;

import deepRed.model.Point;
import deepRed.model.Board;
import deepRed.model.Move;

public class evaluationService
{
    public static int evaluateBoard(Board board)
    {
        for (int y = 0; y < 8; y++)
        {
            if (board.getPoint(new Point(0, y)) == 1)
                return 100000;

            if (board.getPoint(new Point(7, y)) == 2)
                return -100000;
        }

        // Normal evaluation
        int score = 0;

        for (int x = 0; x < 8; x++)
        {
            for (int y = 0; y < 8; y++)
            {
                int piece = board.getPoint(new Point(x, y));

                if (piece == 1)
                {
                    score += 10 + (7 - x) * 2;
                }
                else if (piece == 2)
                {
                    score -= 10 + x * 2;
                }
            }
        }
        return score;
    }

    /*
    board: the board being analyzed
    depth: How many moves ahead the algorithm should look
    maximizing: Whether it's your AI's turn (true) or the opponent's turn (false)
    alpha: The best score the maximizing player can guarantee so far
    beta: The best score the minimizing player can guarantee so far
     */
    public static int minMax(Board board, int depth, boolean maximizing,
        int alpha, int beta) 
    {
        if (depth == 0 || board.isGameOver())
        {
            return evaluateBoard(board);
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