package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class BeerClientImplTest {

    @Autowired
    BeerClientImpl beerClient;

    @Test
    void createBeer() {
        BeerDTO newDto = BeerDTO.builder()
                .price(BigDecimal.valueOf(123.45))
                .beerName("H-Ale")
                .beerStyle(BeerStyle.ALE)
                .quantityOnHand(345)
                .upc("1234234")
                .build();

        BeerDTO savedDto = beerClient.createBeer(newDto);
        assertNotNull(savedDto);
    }

    @Test
    void getBeerById() {
        Page<BeerDTO> beerDTOPage = beerClient.listBeers();
        BeerDTO dto = beerDTOPage.getContent().get(0);
        BeerDTO beerByIdDTO = beerClient.getBeerById(dto.getId());
        assertNotNull(beerByIdDTO);

    }

    @Test
    void listBeersNoParams() {
        beerClient.listBeers();
    }

    @Test
    void listBeersWithNameParam() {
        beerClient.listBeers("ALE", null, null, null);
    }
}