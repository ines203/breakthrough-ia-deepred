package deepRed.deepred_gateway.model.message;

public record IaStatusMessage(String id, boolean isReady, String apiAddress) {}
