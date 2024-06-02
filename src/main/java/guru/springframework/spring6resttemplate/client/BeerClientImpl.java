package guru.springframework.spring6resttemplate.client;

import com.fasterxml.jackson.databind.JsonNode;
import guru.springframework.spring6resttemplate.model.BeerDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@RequiredArgsConstructor
@Service
public class BeerClientImpl implements BeerClient {

    private static final String BASE_URL = "http://localhost:8080";
    private static final String GET_BEER_PATH = "/api/v1/beer";

    private final RestTemplateBuilder restTemplateBuilder;

    @Override

    public Page<BeerDTO> listBeers() {
        RestTemplate restTemplate = restTemplateBuilder.build();

        ResponseEntity<String> stringResponseEntity =
                restTemplate.getForEntity(BASE_URL + GET_BEER_PATH, String.class);
        System.out.println(stringResponseEntity.getBody());

        ResponseEntity<Map> mapResponseEntity =
                restTemplate.getForEntity(BASE_URL + GET_BEER_PATH, Map.class);
        System.out.println(mapResponseEntity.getBody());

        ResponseEntity<JsonNode> jsonResponseEntity =
                restTemplate.getForEntity(BASE_URL + GET_BEER_PATH, JsonNode.class);
        jsonResponseEntity.getBody().findPath("content").forEach(jsonNode -> System.out.println(jsonNode.get("beerName").asText()));

        return null;
    }
}
