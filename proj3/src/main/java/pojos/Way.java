package pojos;

public class Way {
    private final Long id;
    private int maxSpeed;
    private boolean highway;
    private String name;

    public Way(Long id) {
        this.id = id;
    }

    public Way(Long id, int maxSpeed, boolean highway, String name) {
        this.id = id;
        this.maxSpeed = maxSpeed;
        this.highway = highway;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public boolean isHighway() {
        return highway;
    }

    public String getName() {
        return name;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public void setHighway(boolean highway) {
        this.highway = highway;
    }

    public void setName(String name) {
        this.name = name;
    }
}
