package people;

import items.VehicleRegistration;
import org.json.JSONObject;
import java.time.LocalDate;

public class Driver extends Passenger {
    //technicak ma zvlast, passport ma v inventari
    private VehicleRegistration vehicleRegistration;

    /** Constructor for class Driver
     * @param birthday birthday of a person
     * @param name name
     * @param surname surname
     * @param gender gender
     * @param hairColor hair color
     * @param eyeColor eye color
     * @param weight weight*/
    public Driver(LocalDate birthday, String name, String surname, String gender, String hairColor, String eyeColor, int weight) {
        super(birthday, name, surname, gender, hairColor, eyeColor, weight);
        this.vehicleRegistration = null;
    }

    /** A default constructor*/
    public Driver() {
        super();
    }

    /** Returns vehicle registration
     * @return vehicle registration*/
    public VehicleRegistration getVehicleRegistration() {
        return this.vehicleRegistration;
    }

    /** Sets vehicle registration
     * @param vehicleRegistration  vehicle registration*/
    public void setVehicleRegistration(VehicleRegistration vehicleRegistration) {
        this.vehicleRegistration = vehicleRegistration;
    }

    @Override
    public JSONObject marshal() {
        JSONObject json = super.marshal();
        json.put("vehicleRegistration", this.vehicleRegistration.marshal());
        json.put("type", "Driver");
        return json;
    }

    @Override
    public void unmarshal(JSONObject json) {
        super.unmarshal(json);
        this.vehicleRegistration = new VehicleRegistration();
        this.vehicleRegistration.unmarshal(json);
    }

    @Override
    public String getDescription() {
        return String.format(
                "Description of a driver\n" +
                "[1] Gender: %s\n" +
                "[2] Hair color: %s\n" +
                "[3] Eye color: %s\n",
                this.getGender(), this.getHairColor(), this.getEyeColor());
    }
}
