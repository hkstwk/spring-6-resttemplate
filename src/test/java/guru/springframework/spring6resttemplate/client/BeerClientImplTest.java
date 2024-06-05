package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BeerClientImplTest {

    @Autowired
    BeerClientImpl bierClient;

    @Test
    void listBeersNoParams() {
        bierClient.listBeers();
    }

    @Test
    void listBeersWithNameParam() {
        bierClient.listBeers("ALE", null, null, null);
    }
}