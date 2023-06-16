package items;

import generator.Generator;
import org.json.JSONObject;
import vehicles.Vehicle;

import java.time.LocalDate;

/** Class representing vehicle registration for a specified vehicle which can act as an item in inventory*/
public class VehicleRegistration extends License {
    private String registrationNumber;
    private String brand;
    private String color;
    private int numberOfWheels;
    private int maxNumberOfPassengers;

    /** Constructor for class VehicleRegistration
     * @param issueDate issue date
     * @param expirationDate expiration date
     * @param issueCountry issue country
     * @param vehicle vehicle which registration is for*/
    public VehicleRegistration(LocalDate issueDate, LocalDate expirationDate, String issueCountry, Vehicle vehicle) {
        super(issueDate, expirationDate, issueCountry);
        this.registrationNumber = vehicle.getRegistrationNumber();
        this.color = vehicle.getColor();
        this.numberOfWheels = vehicle.getNumberOfWheels();
        this.maxNumberOfPassengers = vehicle.getMaxNumberOfPassengers();
        this.brand = vehicle.getBrand();
    }

    /** A default constructor*/
    public VehicleRegistration() {
        super();
    }

    /** Returns registration number
     * @return registration number*/
    public String getRegistrationNumber() {
        return this.registrationNumber;
    }

    /** Sets registration number
     * @param registrationNumber registration number*/
    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    /** Returns brand
     * @return brand*/
    public String getBrand() {
        return this.brand;
    }

    /** Sets brand
     * @param brand brand*/
    public void setBrand(String brand) {
        this.brand = brand;
    }

    /** Returns color
     * @return color*/
    public String getColor() {
        return this.color;
    }

    /** Sets color
     * @param color color*/
    public void setColor(String color) {
        this.color = color;
    }

    /** Returns number of wheels
     * @return number of wheels*/
    public int getNumberOfWheels() {
        return this.numberOfWheels;
    }

    /** Sets number of wheels
     * @param numberOfWheels number of wheels*/
    public void setNumberOfWheels(int numberOfWheels) {
        this.numberOfWheels = numberOfWheels;
    }

    /** Returns max number of passengers
     * @return max number of passengers*/
    public int getMaxNumberOfPassengers() {
        return this.maxNumberOfPassengers;
    }

    @Override
    public String getDescription() {
        return String.format(
                "Vehicle registration\n" +
                "[1] Registration number: %s\n" +
                "[2] Brand: %s\n" +
                "[3] Color: %s\n" +
                "[4] Number of wheels: %s\n" +
                "[5] Maximum number of passengers: %d\n" +
                "[6] Issue date: %s\n" +
                "[7] Expiration date: %s\n" +
                "[8] Issue country: %s\n",
                this.registrationNumber, this.brand, this.color, this.numberOfWheels, this.maxNumberOfPassengers,
                Generator.normalizeDate(this.getIssueDate()), Generator.normalizeDate(this.getExpirationDate()), this.getIssueCountry());
    }

    @Override
    public JSONObject marshal() {
        JSONObject json = super.marshal();
        json.put("registrationNumber", this.registrationNumber);
        json.put("brand", this.brand);
        json.put("color", this.color);
        json.put("numberOfWheels", this.numberOfWheels);
        json.put("maxNumberOfPassengers", this.maxNumberOfPassengers);
        return json;
    }

    @Override
    public void unmarshal(JSONObject json) {
        super.unmarshal(json);
        this.registrationNumber = json.getString("registrationNumber");
        this.brand = json.getString("brand");
        this.color = json.getString("color");
        this.numberOfWheels = json.getInt("numberOfWheels");
        this.maxNumberOfPassengers = json.getInt("maxNumberOfPassengers");
    }
}
