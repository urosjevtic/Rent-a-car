package dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class RentDateDTO {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date rentStartDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date rentEndDate;
}
