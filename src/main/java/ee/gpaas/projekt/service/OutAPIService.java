package ee.gpaas.projekt.service;

import ee.gpaas.projekt.dto.Asukoht;
import ee.gpaas.projekt.dto.Kohtunikud;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;

@Service
public class OutAPIService {

    private final RestClient restClient;

    public OutAPIService() {
        this.restClient = RestClient.builder()
                .baseUrl("https://69fda39c30ad0a6fd1c12a52.mockapi.io")
                .build();
    }

    public List<Kohtunikud> getKohtunikud() {
        return restClient.get()
                .uri("/kohtunikud")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }

    public List<Asukoht> getAsukohad() {
        return restClient.get()
                .uri("/asukohad")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});
    }
}