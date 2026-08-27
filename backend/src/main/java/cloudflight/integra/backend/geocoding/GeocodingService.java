package cloudflight.integra.backend.geocoding;

import cloudflight.integra.backend.geocoding.model.Coordinate;
import cloudflight.integra.backend.geocoding.model.NominatimPlace;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GeocodingService {
    private final RestClient client = RestClient.create();

    @Cacheable("coordinates")
    public Coordinate getCoordinates(String city, String address) {
        List<NominatimPlace> response = client.get()
            .uri(uriBuilder -> uriBuilder
                .scheme("https")
                .host("nominatim.openstreetmap.org")
                .path("/search")
                .queryParam("city", city)
                .queryParam("street", address)
                .queryParam("format", "jsonv2")
                .build()
            )
            .header("User-Agent", "meet-map") // usage policy req.
            .retrieve()
            .body(new ParameterizedTypeReference<>() {});

        if(response == null) throw new NoSuchElementException();
        NominatimPlace place = response.getFirst();
        return new Coordinate(place.name(), place.lat(), place.lon());
    }
}
