package dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.List;

public class CreateOrderDTO {
    public List<String> vehiclesId;
    public String objectId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date rentalStartDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date rentalEndDate;

    public double price;
    public String customerId;

    public boolean isValid()
    {
        if(vehiclesId.size() == 0 || rentalStartDate.getTime() > rentalEndDate.getTime()){
            return false;
        }
        return true;
    }
}
