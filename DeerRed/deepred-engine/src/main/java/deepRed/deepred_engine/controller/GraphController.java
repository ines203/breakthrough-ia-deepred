package deepRed.deepred_engine.controller;

import deepRed.deepred_engine.model.Board;
import deepRed.deepred_engine.model.Move;
import deepRed.deepred_engine.model.Requête;
import deepRed.deepred_engine.model.Réponse;
import deepRed.deepred_engine.service.minMaxService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@RestController
//@RequestMapping("/api")
public class GraphController {

    @PostMapping("/evaluate")
    public Réponse receiveMoves(@RequestBody Requête request)
    {
        if (!request.isValid())
        {
            return new Réponse();
        }
        return handleMoves(request);
    }

    public Réponse handleMoves(Requête request)
    {
        long deadline = System.nanoTime()
            + request.timeLimit() * 1_000_000L;

        int aiColor = request.activeColor();
        int opponent = aiColor == 1 ? 2 : 1;

        Board board = new Board();
        board.setBoard(request.board());

        List<Move> legalMoves = request.legalMoves();

        if (legalMoves == null || legalMoves.isEmpty()) {
            throw new IllegalArgumentException("No legal moves provided");
        }

        Move bestMove = legalMoves.get(0);
        int bestScore = Integer.MIN_VALUE;
        int completedDepth = 0;

        for (int depth = 1; ; depth++) {

            if (System.nanoTime() >= deadline) {
                break;
            }

            Move iterationBestMove = bestMove;
            int iterationBestScore = Integer.MIN_VALUE;
            boolean timedOut = false;

            for (Move move : legalMoves) {

                if (System.nanoTime() >= deadline) {
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

