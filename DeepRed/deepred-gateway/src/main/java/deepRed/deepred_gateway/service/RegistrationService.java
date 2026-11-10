package deepRed.deepred_gateway.service;

import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class RegistrationService {
    private final BackendClient backendClient;
    private final long retryDelayMs;

    private final ExecutorService registrationExecutor = Executors.newSingleThreadExecutor();

    public RegistrationService(BackendClient backendClient,
                               @Value("${gateway.register.retry.delay.ms}") long retryDelayMs) {
        this.backendClient = backendClient;
        this.retryDelayMs = retryDelayMs;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void registerAtStartup() {
        registrationExecutor.submit(this::registerWithRetry);
    }

    private void registerWithRetry() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                backendClient.sendStatus(true);
                System.out.println("IA enregistree aupres du backend");
                return;
            } catch (RestClientException e) {
                System.err.println("Backend pas pret, nouvel essai dans " + retryDelayMs + " ms : " + e.getMessage());
            }

            try {
                Thread.sleep(retryDelayMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @PreDestroy
    public void unregisterAtShutdown() {
        registrationExecutor.shutdownNow();

        try {
            backendClient.sendStatus(false);
            System.out.println("IA retiree de la liste du backend");
        } catch (RestClientException e) {
            System.err.println("Impossible de se desinscrire : " + e.getMessage());
        }
    }
}
