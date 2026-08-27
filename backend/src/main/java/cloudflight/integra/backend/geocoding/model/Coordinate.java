package cloudflight.integra.backend.geocoding.model;

public class Coordinate {
    private String name;
    private Double latitude;
    private Double longitude;

    public Coordinate(String name, Double latitude, Double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public Coordinate setName(String name) {
        this.name = name;
        return this;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Coordinate setLatitude(Double latitude) {
        this.latitude = latitude;
        return this;
    }

    public Double getLongitude() {
        return longitude;
    }

    public Coordinate setLongitude(Double longitude) {
        this.longitude = longitude;
        return this;
    }

    public Coordinate() {}
}
