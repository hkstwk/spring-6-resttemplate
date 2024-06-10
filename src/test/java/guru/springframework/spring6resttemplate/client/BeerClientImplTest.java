package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import guru.springframework.spring6resttemplate.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.web.client.HttpClientErrorException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
class BeerClientImplTest {

    @Autowired
    BeerClientImpl beerClient;

    @Test
    void deleteBeerById() {
        BeerDTO newDto = BeerDTO.builder()
                .price(BigDecimal.valueOf(987.65))
                .beerName("H-Ale-Before-Delete")
                .beerStyle(BeerStyle.ALE)
                .quantityOnHand(765)
                .upc("676767546")
                .build();

        BeerDTO savedDto = beerClient.createBeer(newDto);

        beerClient.deleteBeer(savedDto.getId());

        assertThrows(HttpClientErrorException.class, () -> {
            beerClient.getBeerById(savedDto.getId());
        });
    }


    @Test
    void updateBeerById() {
        BeerDTO newDto = BeerDTO.builder()
                .price(BigDecimal.valueOf(987.65))
                .beerName("H-Ale")
                .beerStyle(BeerStyle.ALE)
                .quantityOnHand(765)
                .upc("676767546")
                .build();

        BeerDTO savedDto = beerClient.createBeer(newDto);

        final String newName = "H-Ale-Updated";
        savedDto.setBeerName(newName);

        BeerDTO updatedBeer = beerClient.updateBeer(savedDto);

        assertEquals(newName, updatedBeer.getBeerName());
    }

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