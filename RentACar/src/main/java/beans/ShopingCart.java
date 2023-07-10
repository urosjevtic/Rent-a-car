package beans;


import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public class ShopingCart {
    private String id;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date rentDateStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date rentDateEnd;
    private List<String> vehicleIds;
    private String userId;
    private double price;
    private String objectId;

    public ShopingCart()
    {

    }
    public ShopingCart(Date rentDateStart, Date rentDateEnd, List<String> vehicles, String userId, double price, String objectId) {
        this.vehicleIds = vehicles;
        this.rentDateStart = rentDateStart;
        this.rentDateEnd = rentDateEnd;
        this.userId = userId;
        this.price = price;
        this.objectId = objectId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getRentDateStart() {
        return rentDateStart;
    }

    public void setRentDateStart(Date rentDateStart) {
        this.rentDateStart = rentDateStart;
    }

    public Date getRentDateEnd() {
        return rentDateEnd;
    }

    public void setRentDateEnd(Date rentDateEnd) {
        this.rentDateEnd = rentDateEnd;
    }

    public List<String> getVehicleIds() {
        return vehicleIds;
    }

    public void setVehicleIds(List<String> vehicleIds) {
        this.vehicleIds = vehicleIds;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public boolean addVehicle(String vehicalId)
    {
        return vehicleIds.add(vehicalId);
    }

    public boolean removeVehicle(String vehicalId)
    {
        return vehicleIds.remove(vehicalId);
    }

    /*public boolean isValid()
    {
        return !(vehicleIds.size()==0 || rentDateStart.getTime() > rentDateEnd.getTime());
    }*/
}
