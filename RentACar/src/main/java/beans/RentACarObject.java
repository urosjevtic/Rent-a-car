package beans;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.awt.*;


public class RentACarObject {

    private String id;
    private String name;
    //private List<Vehicle> vehicles
    private boolean isOpen;
    private Location location;
    private String image;
    private double rating;

    private WorkingHours workingHours;
    public RentACarObject() {
    }

    public RentACarObject(String name, boolean isOpen, Location location, String image, double rating,WorkingHours workingHours) {
        this.name = name;
        this.isOpen = isOpen;
        this.location = location;
        this.image = image;
        this.rating = rating;
        this.workingHours = workingHours;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        this.isOpen = open;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public WorkingHours getWorkingHours() {
        return workingHours;
    }

    public void setWorkingHours(WorkingHours workingHours) {
        this.workingHours = workingHours;
    }

    public boolean areValuesValid(){
        return (name != null && !name.isBlank()) &&
                (location != null && location.areValuesValid()) &&
                (image != null && !image.isBlank()) &&
                rating >= 0 && rating <= 5;

    }
}
