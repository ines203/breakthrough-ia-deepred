package deepRed.deepred_engine.service;

import deepRed.deepred_engine.model.Point;
import deepRed.deepred_engine.model.Board;
import deepRed.deepred_engine.model.Move;

public class evaluationService
{
    public static int evaluateBoard(Board board, int aiColor)
    {
        // Can win?
        for (int y = 0; y < 8; y++)
        {
            if (board.getPoint(new Point(0, y)) == 1)
                return 100000;

            if (board.getPoint(new Point(7, y)) == 2)
                return -100000;
        }

        int score = 0;

        for (int x = 0; x < 8; x++)
        {
            for (int y = 0; y < 8; y++)
            {
                int piece = board.getPoint(new Point(x, y));

                if (piece == 1)
                {
                    score += 10;
                    score += (7 - x) * 2;

                    // more points for edge of board
                    if (x <= 2)
                    score += ((x) * (x)) * 30;

                    // more if protected
                    if (board.getPoint(x + 1, y + 1) == 1)
                        score += 10;
                    if (board.getPoint(x + 1, y - 1) == 1)
                        score += 10;
                }
                else if (piece == 2)
                {
                    score -= 10;
                    score -= x * 2;

                    // more points for edge of board
                    if (x >= 5)
                        score -= ((x - 4) * (x - 4)) * 50;

                    // more if protected
                    if (board.getPoint(x - 1, y + 1) == 2)
                        score -= 10;
                    if (board.getPoint(x - 1, y - 1) == 2)
                        score -= 10;
                }
            }
        }
        return aiColor == 1 ? score : -score;
    }
}