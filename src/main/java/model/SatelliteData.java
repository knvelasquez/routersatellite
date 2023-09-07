package model;

import java.util.List;

public class SatelliteData {
    private List<SatelliteModel> satellites;

    public SatelliteData() {
    }

    public SatelliteData(List<SatelliteModel> satellites) {
        this.satellites = satellites;
    }

    public List<SatelliteModel> getSatellites() {
        return satellites;
    }
}
