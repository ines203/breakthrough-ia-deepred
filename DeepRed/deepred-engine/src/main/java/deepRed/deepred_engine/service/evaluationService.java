package deepRed.deepred_engine.service;

import deepRed.deepred_engine.model.Point;
import deepRed.deepred_engine.model.Board;
import deepRed.deepred_engine.model.Move;

public class evaluationService
{
    public static int evaluateBoard(Board board, int aiColor)
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
        return aiColor == 1 ? score : -score;
    }
}