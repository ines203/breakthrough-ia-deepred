package com.kleblanc.randomAi.controller;

import com.kleblanc.randomAi.model.Move;
import com.kleblanc.randomAi.model.message.MoveRequestMessage;
import com.kleblanc.randomAi.service.SendApiRequestBackendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@RestController
public class RandomAIController {
    private final SendApiRequestBackendService apiBackendService;

    @Autowired
    public RandomAIController(SendApiRequestBackendService apiBackendService) {
        this.apiBackendService = apiBackendService;
    }

    @PostMapping("/moverequest")
    public void moveRequest(@RequestBody MoveRequestMessage request) {
        Move selectedMove = (Move) request.legalMoves().get((int)(Math.random() * request.legalMoves().size()));

        apiBackendService.sendMoveSelected(selectedMove);
    }
}
