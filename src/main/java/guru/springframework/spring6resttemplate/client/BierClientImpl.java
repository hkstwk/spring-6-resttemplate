package guru.springframework.spring6resttemplate.client;

import guru.springframework.spring6resttemplate.model.BeerDTO;
import org.springframework.data.domain.Page;

public class BierClientImpl implements BierClient {
    @Override
    public Page<BeerDTO> listBeers() {
        return null;
    }
}
