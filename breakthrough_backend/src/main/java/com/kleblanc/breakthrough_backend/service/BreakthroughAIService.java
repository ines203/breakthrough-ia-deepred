package com.kleblanc.breakthrough_backend.service;

import com.kleblanc.breakthrough_backend.model.Board;
import com.kleblanc.breakthrough_backend.model.message.MoveRequestMessage;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class BreakthroughAIService {

    public void sendMoveRequest (Board board, RestClient targetPlayerApiClient) {
        if (board.getIsPlayerHuman(board.getCurrentGameStatus().getPlayerPawn())) {
            throw new IllegalArgumentException("This is a human player turn");
        }

        if (!board.isGameInProgress()) {
            throw new IllegalArgumentException("Game is not in progress");
        }

        MoveRequestMessage moveRequestMessage = new MoveRequestMessage(
                board.getCurrentGameStatus().getPlayerPawn(),
                board.getMoveTimeout(),
                board.getBoard(),
                board.getLegalMoves()
        );

        new Thread(() -> {
            targetPlayerApiClient.post()
                    .uri("/moverequest")
                    .contentType(APPLICATION_JSON)
                    .body(moveRequestMessage)
                    .retrieve()
                    .toBodilessEntity();
        }).start();
    }
}
