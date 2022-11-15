package pojos;

import java.util.HashSet;
import java.util.Set;

public class Vertex {
    private final long id;
    private final double lon;
    private final double lat;
    private String name;
    private Set<Long> wayIds;

    public Vertex(long id, double lon, double lat) {
        this.id = id;
        this.lon = lon;
        this.lat = lat;
        wayIds = new HashSet<>();
    }

    public long getId() {
        return id;
    }

    public double getLon() {
        return lon;
    }

    public double getLat() {
        return lat;
    }

    public String getName() {
        return name;
    }

    public Set<Long> getWayIds() {
        return wayIds;
    }

    public void setName(String name) {
        this.name = name;
    }
}
