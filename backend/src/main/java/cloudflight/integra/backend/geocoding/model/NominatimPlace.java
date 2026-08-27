package cloudflight.integra.backend.geocoding.model;

import java.util.List;

public record NominatimPlace(
    Long place_id,
    String licence,
    String osm_type,
    Long osm_id,
    Double lat,
    Double lon,
    String category,
    String type,
    Long place_rank,
    Double importance,
    String addresstype,
    String name,
    String display_name,
    List<String> boundingbox
) {
}
