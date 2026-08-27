package cloudflight.integra.backend.geocoding.model;

public record CoordinateDto (
    String name,
    Double latitude,
    Double longitude
) {}
