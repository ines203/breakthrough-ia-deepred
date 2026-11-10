package deepRed.deepred_gateway.service;

import deepRed.deepred_gateway.model.Move;
import deepRed.deepred_gateway.model.message.EngineResponse;
import deepRed.deepred_gateway.model.message.MoveRequestMessage;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

@Service
public class MoveSearchService {
    private final EngineClient engineClient;
    private final BackendClient backendClient;
    private final int packetCount;
    private final long safetyMarginMs;
    private final long engineMarginMs;

    private final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    public MoveSearchService(EngineClient engineClient,
                             BackendClient backendClient,
                             @Value("${gateway.packet.count}") int packetCount,
                             @Value("${gateway.safety.margin.ms}") long safetyMarginMs,
                             @Value("${gateway.engine.margin.ms}") long engineMarginMs) {
        this.engineClient = engineClient;
        this.backendClient = backendClient;
        this.packetCount = packetCount;
        this.safetyMarginMs = safetyMarginMs;
        this.engineMarginMs = engineMarginMs;
    }

    public void searchAsync(MoveRequestMessage request, long receivedAtMs) {
        executor.submit(() -> search(request, receivedAtMs));
    }

    private void search(MoveRequestMessage request, long receivedAtMs) {
        List<Move> legalMoves = request.legalMoves();

        if (legalMoves == null || legalMoves.isEmpty()) {
            System.err.println("Aucun coup legal recu, rien a jouer");
            return;
        }

        long finalDeadlineMs = receivedAtMs + request.timeLimit() - safetyMarginMs;
        long engineDeadlineMs = finalDeadlineMs - engineMarginMs;

        List<List<Move>> packets = SearchLogic.splitIntoPackets(legalMoves, packetCount);

        List<Callable<EngineResponse>> tasks = new ArrayList<>();
        for (List<Move> packet : packets) {
            tasks.add(() -> engineClient.evaluate(request.board(), request.activeColor(), packet, engineDeadlineMs));
        }

        List<EngineResponse> responses = new ArrayList<>();

        try {
            long waitMs = Math.max(0, finalDeadlineMs - System.currentTimeMillis());
            List<Future<EngineResponse>> futures = executor.invokeAll(tasks, waitMs, TimeUnit.MILLISECONDS);

            for (int i = 0; i < futures.size(); i++) {
                collectResponse(futures.get(i), packets.get(i), responses);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        Move chosenMove = SearchLogic.pickBest(responses)
                .map(EngineResponse::move)
                .orElse(legalMoves.get(0));

        sendMove(chosenMove);
    }

    private void collectResponse(Future<EngineResponse> future, List<Move> packet, List<EngineResponse> responses) {
        if (future.isCancelled()) {
            System.err.println("Un paquet n'a pas repondu a temps");
            return;
        }

        try {
            EngineResponse response = future.get();

            if (response != null && response.move() != null && packet.contains(response.move())) {
                responses.add(response);
            }
        } catch (ExecutionException e) {
            System.err.println("Erreur du moteur : " + e.getCause().getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private void sendMove(Move move) {
        try {
            backendClient.sendMove(move);
            System.out.println("Coup envoye : " + move);
        } catch (RestClientException e) {
            System.err.println("Echec de l'envoi du coup : " + e.getMessage());
        }
    }

    @PreDestroy
    public void shutdown() {
        executor.shutdownNow();
    }
}
