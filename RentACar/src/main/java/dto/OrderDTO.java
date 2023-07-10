package dto;

import beans.enums.OrderStatus;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public class OrderDTO {
    public String uniqueId;
    public List<String> vehicleNames;
    public String objectName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date rentalStartDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date rentalEndDate;

    public double price;
    public OrderStatus status;


    public boolean isReviewable;
    public String reasonForDeclining;

}
