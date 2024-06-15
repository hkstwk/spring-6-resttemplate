package guru.springframework.spring6resttemplate.client;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6resttemplate.config.RestTemplateBuilderConfig;
import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerDTOPageImpl;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.web.client.MockServerRestTemplateCustomizer;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.math.BigDecimal;
import java.net.URI;
import java.util.Collections;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withAccepted;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withNoContent;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withResourceNotFound;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest
@Import(RestTemplateBuilderConfig.class)
public class BeerClientMockTest {

    private static final String URL = "http://localhost:8080";
    public static final String AUTHORIZATION = "Basic dXNlcjE6cGFzc3dvcmQ=";

    BeerClient beerClient;

    MockRestServiceServer mockServer;

    @Autowired
    RestTemplateBuilder restTemplateBuilderConfigured;

    @Autowired
    ObjectMapper objectMapper;

    @Mock
    RestTemplateBuilder mockedRestTemplateBuilder = new RestTemplateBuilder(new MockServerRestTemplateCustomizer());

    private BeerDTO beerDTO;
    private String payload;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        RestTemplate restTemplate = restTemplateBuilderConfigured.build();
        mockServer = MockRestServiceServer.bindTo(restTemplate).build();
        when(mockedRestTemplateBuilder.build()).thenReturn(restTemplate);
        beerClient = new BeerClientImpl(mockedRestTemplateBuilder);

        beerDTO = getBeerDTO();
        payload = objectMapper.writeValueAsString(beerDTO);
    }

    @Test
    void testListBeers() throws JsonProcessingException {
        String payload = objectMapper.writeValueAsString(getPage());

        mockServer.expect(method(HttpMethod.GET))
                .andExpect(header("Authorization", AUTHORIZATION))
                .andExpect(requestTo(URL + BeerClientImpl.GET_BEER_PATH))
                .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));

        Page<BeerDTO> beers = beerClient.listBeers();
        System.out.println(beers);
        beers.forEach(System.out::println);
        assertThat(beers.getContent().size()).isPositive();
    }

    @Test
    void testGetBeerById() {
        mockGetOperartion();

        BeerDTO responseBeerDTO = beerClient.getBeerById(beerDTO.getId());
        assertThat(responseBeerDTO.getId()).isEqualTo(beerDTO.getId());
    }

    @Test
    void testCreateBeer()  {
        URI uri = UriComponentsBuilder.fromPath(BeerClientImpl.GET_BEER_BY_ID_PATH).build(beerDTO.getId());

        mockServer.expect(method(HttpMethod.POST))
                .andExpect(header("Authorization", AUTHORIZATION))
                .andExpect(requestTo(URL + BeerClientImpl.GET_BEER_PATH))
                .andRespond(withAccepted().location(uri));

        mockGetOperartion();

        BeerDTO responseBeerDTO = beerClient.createBeer(beerDTO);
        assertThat(responseBeerDTO.getId()).isEqualTo(beerDTO.getId());
    }

    @Test
    void testUpdateBeer() {
        mockServer.expect(method(HttpMethod.PUT))
                .andExpect(header("Authorization", AUTHORIZATION))
                .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH, beerDTO.getId()))
                .andRespond(withNoContent());

        mockGetOperartion();

        BeerDTO responseBeerDTO = beerClient.updateBeer(beerDTO);

        assertThat(responseBeerDTO.getId()).isEqualTo(beerDTO.getId());
    }

    @Test
    void testDeleteBeer() {
        mockServer.expect(method(HttpMethod.DELETE))
                .andExpect(header("Authorization", AUTHORIZATION))
                .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH, beerDTO.getId()))
                .andRespond(withNoContent());

        beerClient.deleteBeer(beerDTO.getId());

        mockServer.verify();
    }

    @Test
    void testDeleteBeerNotFound() {
        mockServer.expect(method(HttpMethod.DELETE))
                .andExpect(header("Authorization", AUTHORIZATION))
                .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH, beerDTO.getId()))
                .andRespond(withResourceNotFound());

        assertThrows(HttpClientErrorException.class, () -> beerClient.deleteBeer(beerDTO.getId()));

        mockServer.verify();
    }

    @Test
    void testListBeersWithQueryParams() throws JsonProcessingException {
        String response = objectMapper.writeValueAsString(getPage());

        URI uri = UriComponentsBuilder.fromHttpUrl(URL + BeerClientImpl.GET_BEER_PATH)
                .queryParam("beerName", "H-Ale-Bob")
                .build().toUri();

        mockServer.expect(method(HttpMethod.GET))
                .andExpect(header("Authorization", AUTHORIZATION))
                .andExpect(requestTo(uri))
                .andExpect(queryParam("beerName", "H-Ale-Bob"))
                .andRespond(withSuccess(response, MediaType.APPLICATION_JSON));

        Page<BeerDTO> responsePage = beerClient
                .listBeers("H-Ale-Bob", null, null, null);

        assertThat(responsePage.getContent().size()).isEqualTo(1);
    }

    private void mockGetOperartion() {
        mockServer.expect(method(HttpMethod.GET))
                .andExpect(header("Authorization", AUTHORIZATION))
                .andExpect(requestToUriTemplate(URL + BeerClientImpl.GET_BEER_BY_ID_PATH, beerDTO.getId()))
                .andRespond(withSuccess(payload, MediaType.APPLICATION_JSON));
    }

    private BeerDTO getBeerDTO() {
        return BeerDTO.builder()
                .id(UUID.randomUUID())
                .price(BigDecimal.valueOf(12.76))
                .beerName("H-Ale-Bob")
                .beerStyle(BeerStyle.LAGER)
                .quantityOnHand(4545)
                .upc("234234")
                .build();
    }

    private BeerDTOPageImpl<BeerDTO> getPage() {
        return new BeerDTOPageImpl<BeerDTO>(Collections.singletonList(getBeerDTO()), 0, 25, 1);
    }
}
