package deepRed;

import java.util.List;

import deepRed.model.*;
import deepRed.service.*;

public class deepRedIaApplication {

    private final MinimaxService minimaxService;

    public EngineService(MinimaxService minimaxService) {
        this.minimaxService = minimaxService;
    }

    public MoveResponseMessage evaluate(MoveRequestMessage request) {

        long deadline = System.nanoTime()
            + request.timeLimit() * 1_000_000L;

        int aiColor = request.activeColor();

        Board board = new Board();
        board.setBoard(request.board());

        // These are the root moves assigned by the gateway.
        List<Move> legalMoves = request.legalMoves();

        if (legalMoves == null || legalMoves.isEmpty()) {
            throw new IllegalArgumentException(
                "No legal moves provided"
            );
        }

        Move bestMove = legalMoves.get(0);
        int bestScore = Integer.MIN_VALUE;
        int completedDepth = 0;

        // Iterative deepening
        for (int depth = 1; ; depth++) {

            if (System.nanoTime() >= deadline) {
                break;
            }

            Move iterationBestMove = bestMove;
            int iterationBestScore = Integer.MIN_VALUE;

            try {
                for (Move move : legalMoves) {

                    if (System.nanoTime() >= deadline) {
                        throw new SearchTimeoutException();
                    }

                    Board nextBoard = new Board();
                    nextBoard.setBoard(board.getBoard());
                    nextBoard.applyMove(move);

                    int score = minimaxService.minimax(
                        nextBoard,
                        depth - 1,
                        false,
                        Integer.MIN_VALUE,
                        Integer.MAX_VALUE,
                        aiColor,
                        deadline
                    );

                    if (score > iterationBestScore) {
                        iterationBestScore = score;
                        iterationBestMove = move;
                    }
                }

                // Only save results from a fully completed depth.
                bestMove = iterationBestMove;
                bestScore = iterationBestScore;
                completedDepth = depth;

            } catch (SearchTimeoutException e) {
                break;
            }
        }

        return new EvaluateResponse(
            bestMove,
            bestScore,
            completedDepth
        );
    }
}
