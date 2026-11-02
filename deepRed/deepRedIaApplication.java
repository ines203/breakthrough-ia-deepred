package deepRed;

import java.util.List;

import deepRed.model.*;
import deepRed.service.*;

public class deepRedIaApplication {

    public static MoveResponseMessage handle(MoveRequestMessage request) {

        long deadline = System.nanoTime() + request.timeLimit() * 1_000_000L;

        boolean invertScore = false;
        if (request.activeColor() == 2)
            invertScore = true;

        Board board = new Board();
        board.setBoard(request.board().getBoard());

        // These are the root moves assigned by the gateway.
        List<Move> legalMoves = request.legalMoves();

        if (legalMoves == null || legalMoves.isEmpty()) {
            throw new IllegalArgumentException(
                "No legal moves provided"
            );
        }

        Move bestMove = legalMoves.get(0);
        int bestScore = Integer.MIN_VALUE;
        if (invertScore)
            bestScore = Integer.MAX_VALUE;
        int completedDepth = 0;

        // Iterative deepening
        for (int depth = 1; ; ++depth) 
        {

            if (System.nanoTime() >= deadline) {
                break;
            }

            Move iterationBestMove = bestMove;
            int iterationBestScore = Integer.MIN_VALUE;

            for (Move move : legalMoves) 
            {
                if (System.nanoTime() >= deadline) {
                    break;
                }

                Board nextBoard = new Board();
                nextBoard.setBoard(board.getBoard());
                nextBoard.applyMove(move);
                    
                int score = evaluationService.minMax(
                    nextBoard,
                    depth - 1,
                    false,
                    Integer.MIN_VALUE,
                    Integer.MAX_VALUE
                );

                if (score > iterationBestScore && !invertScore ||
                    score < iterationBestScore && invertScore) 
                {
                    iterationBestScore = score;
                    iterationBestMove = move;
                }

                // Only save results from a fully completed depth.
                bestMove = iterationBestMove;
                bestScore = iterationBestScore;
                completedDepth = depth;
            }
        }
        return new MoveResponseMessage(
            bestMove,
            bestScore,
            completedDepth
        );
    }
}
