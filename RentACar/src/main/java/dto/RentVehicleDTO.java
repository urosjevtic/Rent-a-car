package dto;

import beans.enums.FuelType;
import beans.enums.TransmissionType;
import beans.enums.VehicleType;

public class RentVehicleDTO {
    public String id;
    public String make;
    public String model;
    public double price;
    public VehicleType vehicleType;
    public TransmissionType transmissionType;
    public FuelType fuelType;
    public double fuelConsumption;
    public int numberOfDoors;
    public int seatNumber;
    public String description;
    public String image;

    public String objectName;
    public String objectLogo;
}
