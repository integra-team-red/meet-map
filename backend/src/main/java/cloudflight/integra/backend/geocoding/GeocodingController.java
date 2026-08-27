package cloudflight.integra.backend.geocoding;

import cloudflight.integra.backend.geocoding.model.CoordinateDto;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/geocoding")
public class GeocodingController {

    private final GeocodingService service;
    private final CoordinateMapper mapper;

    public GeocodingController(GeocodingService service, CoordinateMapper mapper) {
        this.service = service;
        this.mapper = mapper;
    }

    @GetMapping(value = "/coordinates", produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(
        summary = "Get the coordinates for a given city and address",
        operationId = "getCoordinates"
    )
    ResponseEntity<CoordinateDto> getCoordinates(@RequestParam String city, @RequestParam String address) {
        return ResponseEntity.status(HttpStatus.OK).body(mapper.toDto(service.getCoordinates(city, address)));
    }
}
