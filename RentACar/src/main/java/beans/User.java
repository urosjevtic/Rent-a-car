package beans;

import beans.enums.CustomerType;
import beans.enums.Role;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class User {

    private String userId;
    private String username;
    private String password;
    private String name;
    private String lastName;
    private String gender;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    private Date birthDate;

    private Role role;

    private boolean isBlocked;
    private int points;
    private CustomerType customerType;

    private String rentACarObjectId;


    public User() {
    }

    public User(String userId, String username, String password, String name, String lastName, String gender, Date birthDate, Role role, boolean isBlocked,int points, CustomerType customerType, String rentACarObjectId) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.name = name;
        this.lastName = lastName;
        this.gender = gender;
        this.birthDate = birthDate;
        this.role = role;
        this.isBlocked = isBlocked;
        this.points = points;
        this.customerType = customerType;
        this.rentACarObjectId = rentACarObjectId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }
    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public CustomerType getCustomerType() {
        return customerType;
    }

    public void setCustomerType(CustomerType customerType) {
        this.customerType = customerType;
    }

    public String getRentACarObjectId() {
        return rentACarObjectId;
    }

    public void setRentACarObjectId(String rentACarObjectId) {
        this.rentACarObjectId = rentACarObjectId;
    }

    public boolean getIsBlocked(){ return isBlocked; }
    public void setIsBlocked(boolean isBlocked) { this.isBlocked = isBlocked; }

    public boolean ValidateRegistrationData(){
        return UserNameValidation(username) &
        PasswordValidation(password) &
        NameAndSurnameValidation(name, lastName);
    }

    private boolean UserNameValidation(String username)
    {
        String pattern = "^[a-zA-Z0-9_-]{3,16}$";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(username);

        return matcher.matches();
    }

    private boolean PasswordValidation(String password)
    {
        String pattern = "^(?=.*[A-Za-z])(?=.*\\d)[A-Za-z\\d]{8,}$";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcher = regex.matcher(password);

        return matcher.matches();
    }

    private boolean NameAndSurnameValidation(String name, String surname)
    {
        String pattern = "^[A-Za-z\\p{L}][A-Za-z\\p{L}\\-']*$";
        Pattern regex = Pattern.compile(pattern);
        Matcher matcherName = regex.matcher(name);
        Matcher matcherSurname = regex.matcher(surname);

        return matcherName.matches() & matcherSurname.matches();

    }
}
