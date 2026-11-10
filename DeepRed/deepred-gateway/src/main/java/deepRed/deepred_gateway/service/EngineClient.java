package deepRed.deepred_gateway.service;

import deepRed.deepred_gateway.model.Move;
import deepRed.deepred_gateway.model.message.EngineRequest;
import deepRed.deepred_gateway.model.message.EngineResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class EngineClient {
    private final RestClient engineRestClient;
    private final String evaluateApiEndpoint;

    public EngineClient(@Value("${engine.service.address}") String engineServiceAddress,
                        @Value("${engine.service.evaluate.api}") String evaluateApiEndpoint) {
        this.engineRestClient = RestClient.builder()
                .baseUrl(engineServiceAddress)
                .build();
        this.evaluateApiEndpoint = evaluateApiEndpoint;
    }

    public EngineResponse evaluate(int[][] board, int activeColor, List<Move> moves, long deadlineMs) {
        EngineRequest request = new EngineRequest(board, activeColor, moves, deadlineMs);

        return engineRestClient.post()
                .uri(evaluateApiEndpoint)
                .contentType(APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(EngineResponse.class);
    }
}
