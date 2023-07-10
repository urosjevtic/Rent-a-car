package beans;

import beans.enums.FuelType;
import beans.enums.TransmissionType;
import beans.enums.VehicleType;

public class Vehicle {

    private String id;
    private String make;
    private String model;
    private double price;
    private VehicleType vehicleType;
    private TransmissionType transmissionType;
    private FuelType fuelType;
    private double fuelConsumption;
    private int numberOfDoors;
    private int seatNumber;
    private String description;
    private String image;
    private String companyId;
    private boolean isRented;


    public Vehicle(String make, String model, double price, VehicleType vehicleType, TransmissionType transmissionType, FuelType fuelType, double fuelConsumption, int numberOfDoors, int seatNumber, String description, String image, String companyId, boolean isRented) {
        this.make = make;
        this.model = model;
        this.price = price;
        this.vehicleType = vehicleType;
        this.transmissionType = transmissionType;
        this.fuelType = fuelType;
        this.fuelConsumption = fuelConsumption;
        this.numberOfDoors = numberOfDoors;
        this.seatNumber = seatNumber;
        this.description = description;
        this.image = image;
        this.companyId = companyId;
        this.isRented = isRented;
    }

    public Vehicle() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMake() {
        return make;
    }

    public void setMake(String make) {
        this.make = make;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public VehicleType getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(VehicleType vehicleType) {
        this.vehicleType = vehicleType;
    }

    public TransmissionType getTransmissionType() {
        return transmissionType;
    }

    public void setTransmissionType(TransmissionType transmissionType) {
        this.transmissionType = transmissionType;
    }

    public FuelType getFuelType() {
        return fuelType;
    }

    public void setFuelType(FuelType fuelType) {
        this.fuelType = fuelType;
    }

    public double getFuelConsumption() {
        return fuelConsumption;
    }

    public void setFuelConsumption(double fuelConsumption) {
        this.fuelConsumption = fuelConsumption;
    }

    public int getNumberOfDoors() {
        return numberOfDoors;
    }

    public void setNumberOfDoors(int numberOfDoors) {
        this.numberOfDoors = numberOfDoors;
    }

    public int getSeatNumber() {
        return seatNumber;
    }

    public void setSeatNumber(int seatNumber) {
        this.seatNumber = seatNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public boolean isRented() {
        return isRented;
    }

    public void setRented(boolean rented) {
        isRented = rented;
    }

    public boolean areValuesValid(){
        return (make != null && !make.isBlank()) &&
                (model != null && !model.isBlank()) &&
                price > 0 && fuelConsumption > 0 && seatNumber > 0 && numberOfDoors > 0 &&
                        (image != null && !image.isBlank()) &&
                        (companyId != null && !companyId.isBlank());
    }

}
