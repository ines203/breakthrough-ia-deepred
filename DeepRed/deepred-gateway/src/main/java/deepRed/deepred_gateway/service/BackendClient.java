package deepRed.deepred_gateway.service;

import deepRed.deepred_gateway.model.Move;
import deepRed.deepred_gateway.model.message.IaStatusMessage;
import deepRed.deepred_gateway.model.message.MoveResponseMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class BackendClient {
    private final RestClient backendRestClient;
    private final String aiId;
    private final String gatewayAddress;
    private final String aiStatusApiEndpoint;
    private final String makeMoveApiEndpoint;

    public BackendClient(@Value("${backend.service.address}") String backendServiceAddress,
                         @Value("${deepred.id}") String aiId,
                         @Value("${deepred.gateway.address}") String gatewayAddress,
                         @Value("${backend.service.aistatus.api}") String aiStatusApiEndpoint,
                         @Value("${backend.service.makemove.api}") String makeMoveApiEndpoint) {
        this.backendRestClient = RestClient.builder()
                .baseUrl(backendServiceAddress)
                .build();
        this.aiId = aiId;
        this.gatewayAddress = gatewayAddress;
        this.aiStatusApiEndpoint = aiStatusApiEndpoint;
        this.makeMoveApiEndpoint = makeMoveApiEndpoint;
    }

    public void sendStatus(boolean isReady) {
        IaStatusMessage message = new IaStatusMessage(aiId, isReady, gatewayAddress);

        backendRestClient.post()
                .uri(aiStatusApiEndpoint)
                .contentType(APPLICATION_JSON)
                .body(message)
                .retrieve()
                .toBodilessEntity();
    }

    public void sendMove(Move move) {
        MoveResponseMessage message = new MoveResponseMessage(aiId, move);

        backendRestClient.post()
                .uri(makeMoveApiEndpoint)
                .contentType(APPLICATION_JSON)
                .body(message)
                .retrieve()
                .toBodilessEntity();
    }
}
