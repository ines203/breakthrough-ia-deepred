package deepRed.deepred_engine.controller;

import deepRed.deepred_engine.model.Board;
import deepRed.deepred_engine.model.Move;
import deepRed.deepred_engine.model.Requête;
import deepRed.deepred_engine.model.Réponse;
import deepRed.deepred_engine.service.handleMovesService;

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
            return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Unable to get request");
            //return new Réponse();
        }
        return handleMoves(request);
    }
}

