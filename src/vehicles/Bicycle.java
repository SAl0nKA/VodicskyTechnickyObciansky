package vehicles;

import org.json.JSONObject;
public class Bicycle extends Vehicle {
    /** Constructor for class Bicycle
     * @param registrationNumber registration number
     * @param color color
     * @param brand brand
     * @param numberOfWheels number of wheels
     * @param maxPassengers maximum number of passengers */
    public Bicycle(String registrationNumber, String color, String brand, int numberOfWheels, int maxPassengers) {
        super(registrationNumber, color, brand, numberOfWheels, maxPassengers);
    }

    /** A default constructor*/
    public Bicycle() {
        super();
    }

    @Override
    public String getVehicleDescription() {
        return String.format(
                "Bicycle information\n" +
                "[0] Registration number: %s\n" +
                "[1] Brand: %s\n" +
                "[2] Color: %s\n" +
                "[3] Number of wheels: %s\n", this.getRegistrationNumber(), this.getBrand(), this.getColor(), this.getNumberOfWheels());
    }

    @Override
    public JSONObject marshal() {
        JSONObject json = super.marshal();
        json.put("type", "Bicycle");
        return json;
    }

    @Override
    public void unmarshal(JSONObject json) {
        super.unmarshal(json);
    }
}
