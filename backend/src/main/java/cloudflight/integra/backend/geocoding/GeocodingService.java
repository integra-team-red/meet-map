package cloudflight.integra.backend.geocoding;

import cloudflight.integra.backend.geocoding.model.Coordinate;
import cloudflight.integra.backend.geocoding.model.NominatimPlace;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class GeocodingService {
    private final RestClient client = RestClient.create();

    @Cacheable("coordinates")
    public Coordinate getCoordinates(String city, String address) {
        try{
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
            if(response == null) throw new ResponseStatusException(
                HttpStatus.BAD_GATEWAY, "Invalid response from geocoding service"
            );
            NominatimPlace place = response.getFirst();
            return new Coordinate(place.name(), place.lat(), place.lon());
        } catch (ResourceAccessException e) {
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Geocoding service unreachable");
        } catch (NoSuchElementException e) {
            throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Could not fetch the location name for the provided coordinates."
            );
        }
    }
}
