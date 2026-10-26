package com.kleblanc.LeCHONKai.service;

import com.kleblanc.LeCHONKai.model.Move;
import com.kleblanc.LeCHONKai.model.message.IAStatusMessage;
import com.kleblanc.LeCHONKai.model.message.MoveResponseMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import static org.springframework.http.MediaType.APPLICATION_JSON;

@Service
public class SendApiRequestBackendService {
    @Value(value = "${lechonkai.service.address}")
    private String lechonkAiServiceAddress;

    @Value(value = "${backend.service.aistatus.api}")
    private String aiStatusApiEndpoint;

    @Value(value = "${backend.service.makemove.api}")
    private String makeMoveApiEndpoint;

    @Value(value = "${lechonkai.id}")
    private String aiId;

    private final RestClient backendRestClient;

    public SendApiRequestBackendService(@Value("${backend.service.address}") final String backendServiceAddress) {
        backendRestClient = RestClient.builder()
                .baseUrl(backendServiceAddress)
                .build();
    }

    public void sendIaStatusReady() {
        try {
            IAStatusMessage message = new IAStatusMessage(aiId, true, lechonkAiServiceAddress);

            backendRestClient.post()
                    .uri(aiStatusApiEndpoint)
                    .contentType(APPLICATION_JSON)
                    .body(message)
                    .retrieve()
                    .toBodilessEntity();
        } catch (RestClientException e) {
            System.err.println("Error calling API at startup: " + e.getMessage());
        }
    }

    public void sendMoveSelected(Move move) {
        MoveResponseMessage message = new MoveResponseMessage(aiId, move);

        backendRestClient.post()
                .uri(makeMoveApiEndpoint)
                .contentType(APPLICATION_JSON)
                .body(message)
                .retrieve()
                .toBodilessEntity();
    }
}
