package beans;

import beans.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public class Order {
    private String id;
    private String uniqueId;
    private List<String> vehiclesId;
    private String objectId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date rentalStartDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date rentalEndDate;

    private double price;
    private String customerId;

    private OrderStatus status;

    private boolean isDeleted;

    private String rejectionReason;


    public Order()
    {

    }
    public Order(String uniqueId, List<String> vehiclesId, String objectId, Date rentalStartDate, Date rentalEndDate, double price, String customerId, OrderStatus status, boolean isDeleted, String rejectionReason) {
        this.uniqueId = uniqueId;
        this.vehiclesId = vehiclesId;
        this.objectId = objectId;
        this.rentalStartDate = rentalStartDate;
        this.rentalEndDate = rentalEndDate;
        this.price = price;
        this.customerId = customerId;
        this.status = status;
        this.isDeleted = isDeleted;
        this.rejectionReason = rejectionReason;
    }

    public String getId(){return id;}
    public void setId(String id) {this.id = id;}
    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public List<String> getVehiclesId() {
        return vehiclesId;
    }

    public void setVehiclesId(List<String> vehiclesId) {
        this.vehiclesId = vehiclesId;
    }

    public String getObjectId() {
        return objectId;
    }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public Date getRentalStartDate() {
        return rentalStartDate;
    }

    public void setRentalStartDate(Date rentalStartDate) {
        this.rentalStartDate = rentalStartDate;
    }

    public Date getRentalEndDate() {
        return rentalEndDate;
    }

    public void setRentalEndDate(Date rentalEndDate) {
        this.rentalEndDate = rentalEndDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomer(String customerId) {
        this.customerId = customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(boolean isDeleted) { this.isDeleted = isDeleted; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }
}
