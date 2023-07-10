package dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class CartOrderDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date rentStartDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date rentEndDate;
    public String objectId;

    public boolean isValid()
    {
        if(rentStartDate.getTime() > rentEndDate.getTime() || objectId == null)
        {
            return false;
        }
        return true;
    }
}
