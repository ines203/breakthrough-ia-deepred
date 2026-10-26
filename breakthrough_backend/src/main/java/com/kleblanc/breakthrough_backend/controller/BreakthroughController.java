package com.kleblanc.breakthrough_backend.controller;

import com.kleblanc.breakthrough_backend.model.*;
import com.kleblanc.breakthrough_backend.model.message.IAStatusMessage;
import com.kleblanc.breakthrough_backend.model.message.MoveResponseMessage;
import com.kleblanc.breakthrough_backend.service.BreakthroughAIService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@CrossOrigin(originPatterns = "*", allowCredentials = "true")
@RequestMapping("/board")
@RestController
public class BreakthroughController {
    private final Board board;
    private final Lock boardLock = new ReentrantLock();
    private int timeoutCheckCount;

    private final BreakthroughAIService breakthroughAIService;

    private final ConcurrentHashMap<String, RestClient> readyPlayerAPIs = new ConcurrentHashMap<>();

    private final List<SseEmitter> boardEmitters = new CopyOnWriteArrayList<>();
    private final List<SseEmitter> legalMovesEmitters = new CopyOnWriteArrayList<>();
    private final List<SseEmitter> gameStatusEmitters = new CopyOnWriteArrayList<>();
    private final List<SseEmitter> playersReadyEmitters = new CopyOnWriteArrayList<>();
    private final List<SseEmitter> playedmovesEmitters = new CopyOnWriteArrayList<>();

    @Autowired
    public BreakthroughController(Board board, BreakthroughAIService breakthroughAIService) {
        this.board = board;
        this.breakthroughAIService = breakthroughAIService;
    }

    // REST routes ---------------------------------------------------------------------------------

    @GetMapping("/")
    public int[][] board() {
        return this.board.getBoard();
    }

    @PostMapping("/newgame")
    public void newGame(@RequestBody StartGameRequest startGameRequest) {
        boardLock.lock();

        try {
            this.board.abortGame();
            dispatchGameToClients();

            this.board.resetGame();
            this.timeoutCheckCount = 0;

            this.board.setPlayerId(GameStatusId.TURN_WHITE.getPlayerPawn(),
                    startGameRequest.whitePlayerId());
            this.board.setPlayerId(GameStatusId.TURN_BLACK.getPlayerPawn(),
                    startGameRequest.blackPlayerId());
            this.board.setMoveTimeout(startGameRequest.moveTimeout());

            if (!this.board.getIsPlayerHuman(GameStatusId.TURN_WHITE.getPlayerPawn())) {
                // Wait a few seconds to allow time for IAs to register properly
                ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
                executorService.schedule(this::sendMoveRequestIfIATurn, 3, TimeUnit.SECONDS);
            }

            dispatchGameToClients();
        } finally {
            boardLock.unlock();
        }
    }

    @PostMapping("/abortgame")
    public void abortGame() {
        boardLock.lock();
        try {
            this.board.abortGame();

            dispatchGameToClients();
        } finally {
            boardLock.unlock();
        }
    }

    @GetMapping("/string")
    public String boardString() {
        return this.board.toString();
    }

    @GetMapping("/legalmoves")
    public List<? extends iMove> legalMoves() {
        return this.board.getLegalMoves();
    }

    @GetMapping("/gamestatus")
    public GameStatus gameStatus() {
        return this.board.getCurrentGameStatus().makeGameStatus();
    }

    @PostMapping("/makemove")
    public ExecutedMoveStatus executeMove(@RequestBody Move move) {
        if (!board.getIsActivePlayerHuman()) {
            return new ExecutedMoveStatus(false, "Not a human player turn");
        }

        boardLock.lock();
        try {
            boolean moveSuccess = board.executeMove(move);

            Object status;
            if (moveSuccess) {
                // Il n'y aura pas de check de timeout pour un joueur humain
                timeoutCheckCount++;

                status = board.endTurn().makeGameStatus();

                dispatchGameToClients();

                if (board.isGameInProgress()) {
                    sendMoveRequestIfIATurn();
                }
            } else {
                status = "Illegal move";
            }

            return new ExecutedMoveStatus(moveSuccess, status);
        } finally {
            boardLock.unlock();
        }
    }

    @GetMapping("/playedmoves")
    public List<? extends iMove> playedMoves() {
        return this.board.getMovesPlayed();
    }

    @GetMapping("/readyplayerids")
    public Set<String> readyPlayerIds() {
        Set<String> readyPlayerIds = new CopyOnWriteArraySet<>(this.readyPlayerAPIs.keySet());
        readyPlayerIds.add(Constants.HUMAN_PLAYER_ID);

        return readyPlayerIds;
    }

    @GetMapping("/isplayerhuman/{playerColor}")
    public boolean isPlayerHuman(@PathVariable int playerColor) {
        return this.board.getIsPlayerHuman(playerColor);
    }

    @GetMapping("/getplayerid/{playerColor}")
    public String getPlayerId(@PathVariable int playerColor) {
        return this.board.getPlayerId(playerColor);
    }

    @PutMapping("/setplayerid/{playerColor}")
    public void setPlayerId(@PathVariable int playerColor, @RequestBody String playerId) {
        this.board.setPlayerId(playerColor, playerId);
    }

    // PUB/SUB ----------------------------------------------------------------------------------------------

    private void dispatchGameToClients() {
        dispatchBoard();
        dispatchLegalMovesForHumanPlayers();
        dispatchGameStatus();
        dispatchPlayedMoves();
    }

    @GetMapping("/subscribe/board")
    public SseEmitter subscribeBoard() {
        return subscribeTo(boardEmitters);
    }

    private void dispatchBoard() {
        dispatchTo(boardEmitters, this.board.getBoard());
    }

    @GetMapping("/subscribe/playedmoves")
    public SseEmitter subscribePlayedMoves() {
        return subscribeTo(playedmovesEmitters);
    }

    private void dispatchPlayedMoves() {
        dispatchTo(playedmovesEmitters, this.board.getMovesPlayed());
    }


    @GetMapping("/subscribe/legalmoves")
    public SseEmitter subscribeLegalMoves() {
        return subscribeTo(legalMovesEmitters);
    }

    private void dispatchLegalMovesForHumanPlayers() {
        List<? extends iMove> legalMoves = this.board.getIsPlayerHuman(this.board.getCurrentGameStatus().getPlayerPawn())
                ? this.board.getLegalMoves()
                : List.of();
        dispatchTo(legalMovesEmitters, legalMoves);
    }

    @GetMapping("/subscribe/gamestatus")
    public SseEmitter subscribeGameStatus() {
        return subscribeTo(gameStatusEmitters);
    }

    private void dispatchGameStatus() {
        dispatchTo(gameStatusEmitters, this.board.getCurrentGameStatus().makeGameStatus());
    }

    @GetMapping("/subscribe/readyplayerids")
    public SseEmitter subscribeReadyPlayerIds() {
        return subscribeTo(playersReadyEmitters);
    }

    private void dispatchReadyPlayerIds() {
        dispatchTo(playersReadyEmitters, this.readyPlayerIds());
    }

    private SseEmitter subscribeTo(List<SseEmitter> emitters) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        emitters.add(emitter);

        emitter.onCompletion(() -> emitters.remove(emitter));
        emitter.onTimeout(() -> emitters.remove(emitter));
        emitter.onError(e -> emitters.remove(emitter));

        return emitter;
    }

    private void dispatchTo(List<SseEmitter> emitters, Object data) {
        List<SseEmitter> deadEmitters = new ArrayList<>();

        emitters.forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event().name("message").data(data));
            } catch (Exception e) {
                deadEmitters.add(emitter);
            }
        });

        emitters.removeAll(deadEmitters);
    }

    // AI api routes ------------------------------------------------------------------------------

    @PostMapping("/ai/setstatus")
    public void setAiStatus(@RequestBody IAStatusMessage iaStatus) {
        if (iaStatus.isReady()) {
            System.out.println("IA " + iaStatus.id() + " s'est connecté à l'adresse " + iaStatus.apiAddress());

            RestClient iaRestClient = RestClient.builder()
                    .baseUrl(iaStatus.apiAddress())
                    .build();
            readyPlayerAPIs.put(iaStatus.id(), iaRestClient);
        } else {
            System.out.println("IA " + iaStatus.id() + " s'est déconnecté");

            readyPlayerAPIs.remove(iaStatus.id());

            boardLock.lock();
            try {
                if (board.getPlayerId(GameStatusId.TURN_WHITE.getPlayerPawn()).equals(iaStatus.id())) {
                    board.setWhitePlayerNotReady();
                    dispatchGameToClients();
                } else if (board.getPlayerId(GameStatusId.TURN_BLACK.getPlayerPawn()).equals(iaStatus.id())) {
                    board.setBlackPlayerNotReady();
                    dispatchGameToClients();
                }
            } finally {
                boardLock.unlock();
            }
        }
        dispatchReadyPlayerIds();
    }

    @PostMapping("/ai/makemove")
    public void executeAIMoveResponse(@RequestBody MoveResponseMessage moveResponse) {
        executeAIMove(moveResponse.move(), moveResponse.aiId());
    }

    // AI moves functions ----------------------------------------------------------------------------------

    private void sendMoveRequestIfIATurn() {
        if (this.board.getIsPlayerHuman(board.getCurrentGameStatus().getPlayerPawn()))
            return;

        breakthroughAIService.sendMoveRequest(board, readyPlayerAPIs.get(board.getActivePlayerId()));

        ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.schedule(this::checkIfTimedOut, board.getMoveTimeout(), TimeUnit.MILLISECONDS);
    }

    private void executeAIMove(Move move, String aiId) {
        boardLock.lock();
        try {
            if (!board.getActivePlayerId().equals(aiId))
                return;

            boolean moveSuccess = board.executeMove(move);

            if (moveSuccess) {
                board.endTurn();
                dispatchGameToClients();

                System.out.println("IA " + aiId + " a effectué le coup " + move.toString());

                if (board.isGameInProgress()) {
                    sendMoveRequestIfIATurn();
                }
            }
        } finally {
            boardLock.unlock();
        }
    }

    private void checkIfTimedOut() {
        boardLock.lock();
        try {
            timeoutCheckCount++;

            if (timeoutCheckCount > board.getMovesPlayed().size()) {
                board.timeoutActivePlayer();

                dispatchGameToClients();
            }
        } finally {
            boardLock.unlock();
        }
    }
}
