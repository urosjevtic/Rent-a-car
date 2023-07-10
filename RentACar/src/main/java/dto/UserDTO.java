package dto;

import beans.enums.CustomerType;
import beans.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;

public class UserDTO {
    public String username;
    public String name;
    public String lastName;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    public Date dateOfBirth;
    public String gender;
    public Role role;

    public boolean isBlocked;

    public CustomerType customerType;
    public int points;

    public boolean isSus;
}
