package vehicles;

import iomanagement.IJSONable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import people.Driver;
import people.Passenger;

import java.util.ArrayList;

public abstract class Vehicle implements IJSONable {
    private String registrationNumber;
    private String color;
    private String brand;
    private int numberOfWheels;
    private int maxNumberOfPassengers;
    private Driver driver;
    private ArrayList<Passenger> passengers;

    /** Constructor for class Vehicle
     * @param registrationNumber registration number
     * @param color color
     * @param brand brand
     * @param numberOfWheels number of wheels
     * @param maxPassengers maximum number of passengers */
    public Vehicle(String registrationNumber, String color, String brand, int numberOfWheels, int maxPassengers) {
        this.registrationNumber = registrationNumber;
        this.color = color;
        this.brand = brand;
        this.numberOfWheels = numberOfWheels;
        this.maxNumberOfPassengers = maxPassengers;
        this.passengers = new ArrayList<>();
    }

    /** A default constructor*/
    public Vehicle() {
        super();
    }

    /** Returns registration number
     * @return registration number*/
    public String getRegistrationNumber() {
        return this.registrationNumber;
    }

    /** Returns color
     * @return color*/
    public String getColor() {
        return this.color;
    }

    /** Returns number of wheels
     * @return number of wheels*/
    public int getNumberOfWheels() {
        return this.numberOfWheels;
    }

    /** Returns max number of passengers
     * @return max number of passengers*/
    public int getMaxNumberOfPassengers() {
        return this.maxNumberOfPassengers;
    }

    /** Returns brand
     * @return brand*/
    public String getBrand() {
        return this.brand;
    }

    /** Returns driver
     * @return driver*/
    public Driver getDriver() {
        return this.driver;
    }

    /** Sets driver
     * @param driver driver*/
    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    /** Returns passengers
     * @return passengers*/
    public ArrayList<Passenger> getPassengers() {
        return new ArrayList<>(this.passengers);
    }

    /** Adds passenger to vehicle
     * @param passenger passenger*/
    public void addPassenger(Passenger passenger) {
        this.passengers.add(passenger);
    }

    /** Removes passenger from vehicle
     * @return passenger
     * @param index index of a passenger*/
    public Passenger removePassenger(int index) {
        if (index < 0 || index >= this.passengers.size()) {
            return null;
        }
        return this.passengers.remove(index);
    }

    /** Removes passenger from vehicle
     * @return true if passenger was removed
     * @param passenger passenger to remove*/
    public boolean removePassenger(Passenger passenger) {
        if (passenger == null) {
            return false;
        }
        return this.passengers.remove(passenger);
    }

    /** Removed driver from a vehicle
     * @return driver*/
    public Driver removeDriver() {
        Driver tmpDriver = this.driver;
        this.driver = null;
        return tmpDriver;
    }

    /** Returns vehicle description
     * @return vehicle description*/
    public abstract String getVehicleDescription();

    @Override
    public JSONObject marshal() {
        JSONObject json = new JSONObject();
        json.put("registrationNumber", this.registrationNumber);
        json.put("color", this.color);
        json.put("brand", this.brand);
        json.put("numberOfWheels", this.numberOfWheels);
        json.put("maxNumberOfPassengers", this.maxNumberOfPassengers);
        json.put("driver", this.driver == null ? null : this.driver.marshal());
        JSONArray arr = new JSONArray();
        for (Passenger passenger : this.passengers) {
            arr.put(passenger.marshal());
        }
        json.put("passengers", arr);
        return json;
    }

    @Override
    public void unmarshal(JSONObject json) {
        this.registrationNumber = json.getString("registrationNumber");
        this.color = json.getString("color");
        this.brand = json.getString("brand");
        this.numberOfWheels = json.getInt("numberOfWheels");
        this.maxNumberOfPassengers = json.getInt("maxNumberOfPassengers");
        this.driver = new Driver();
        try {
            this.driver.unmarshal(json.getJSONObject("driver"));
        } catch (JSONException e) {
            this.driver = null;
        }
        this.passengers = new ArrayList<>();
        for (Object obj : json.getJSONArray("passengers")) {
            Passenger passenger = new Passenger();
            passenger.unmarshal((JSONObject)obj);
            this.passengers.add(passenger);
        }
    }
}
