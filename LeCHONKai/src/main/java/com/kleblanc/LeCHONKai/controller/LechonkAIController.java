package com.kleblanc.LeCHONKai.controller;

import com.kleblanc.LeCHONKai.model.Coordinate;
import com.kleblanc.LeCHONKai.model.Move;
import com.kleblanc.LeCHONKai.model.message.MoveRequestMessage;
import com.kleblanc.LeCHONKai.service.SendApiRequestBackendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Optional;

@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@RestController
public class LechonkAIController {
    private final SendApiRequestBackendService apiBackendService;

    @Autowired
    public LechonkAIController(SendApiRequestBackendService apiBackendService) {
        this.apiBackendService = apiBackendService;
    }

    @PostMapping("/moverequest")
    public void moveRequest(@RequestBody MoveRequestMessage request) {
        int[][] board = request.board();
        ArrayList<Move> moves = request.legalMoves();

        Optional<Move> winningMove = moves.stream()
                .filter(move -> isWinningMove(move, board))
                .findFirst();

        if(winningMove.isPresent()) {
            apiBackendService.sendMoveSelected(winningMove.get());
        }

        Optional<Move> captureMove = moves.stream()
                .filter(move -> canCaptureEnemy(move, board))
                .findFirst();

        if(captureMove.isPresent()) {
            apiBackendService.sendMoveSelected(captureMove.get());
        }

        Move selectedMove = (Move) request.legalMoves().get((int)(Math.random() * request.legalMoves().size()));

        apiBackendService.sendMoveSelected(selectedMove);
    }

    private boolean isWinningMove(Move move, int[][] board) {
        return move.target().x() == 0 || move.target().x() == 7;
    }

    private boolean canCaptureEnemy(Move move, int[][] board) {
        return (board[move.target().x()][move.target().y()] != 0);
    }
}
