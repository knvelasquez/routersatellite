package model;

public class SatelliteModel {
    private String name;
    private Float distance;
    private String[] message;

    public SatelliteModel() {
    }

    public SatelliteModel(String name, Float distance, String[] message) {
        this.name = name;
        this.distance = distance;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public Float getDistance() {
        return distance;
    }

    public String[] getMessage() {
        return message;
    }
}
