package deepRed.deepred_gateway.controller;

import deepRed.deepred_gateway.model.message.MoveRequestMessage;
import deepRed.deepred_gateway.service.MoveSearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MoveRequestController {
    private final MoveSearchService moveSearchService;

    public MoveRequestController(MoveSearchService moveSearchService) {
        this.moveSearchService = moveSearchService;
    }

    @PostMapping("/moverequest")
    public void moveRequest(@RequestBody MoveRequestMessage request) {
        moveSearchService.searchAsync(request, System.currentTimeMillis());
    }
}
