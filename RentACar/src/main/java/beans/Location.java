package beans;

public class Location {


    //geo location
    private double Latitude;
    private double Longitude;

    //address
    private String street;
    private String streetNumber;
    private String city;
    private String zipCode;

    public Location() {
    }

    public Location(double latitude, double longitude, String street, String streetNumber, String city, String zipCode) {
        Latitude = latitude;
        Longitude = longitude;
        this.street = street;
        this.streetNumber = streetNumber;
        this.city = city;
        this.zipCode = zipCode;
    }


    public double getLatitude() {
        return Latitude;
    }

    public void setLatitude(double latitude) {
        Latitude = latitude;
    }

    public double getLongitude() {
        return Longitude;
    }

    public void setLongitude(double longitude) {
        Longitude = longitude;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public boolean areValuesValid(){
        return (city != null && !city.isBlank()) &&
                (street != null && !street.isBlank()) &&
                (zipCode != null && !zipCode.isBlank()) &&
                (streetNumber != null && !streetNumber.isBlank());
    }
}
