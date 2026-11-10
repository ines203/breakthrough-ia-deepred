package deepRed.deepred_gateway.service;

import deepRed.deepred_gateway.model.Move;
import deepRed.deepred_gateway.model.message.EngineResponse;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class SearchLogic {
    private SearchLogic() {}

    public static List<List<Move>> splitIntoPackets(List<Move> moves, int packetCount) {
        int count = Math.max(1, Math.min(packetCount, moves.size()));

        List<List<Move>> packets = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            packets.add(new ArrayList<>());
        }

        for (int i = 0; i < moves.size(); i++) {
            packets.get(i % count).add(moves.get(i));
        }

        return packets;
    }

    public static Optional<EngineResponse> pickBest(List<EngineResponse> responses) {
        return responses.stream()
                .filter(Objects::nonNull)
                .filter(response -> response.move() != null)
                .max(Comparator.comparingInt(EngineResponse::score)
                        .thenComparingInt(EngineResponse::depth));
    }
}
