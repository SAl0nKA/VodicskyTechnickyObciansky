package buildings;

import org.json.JSONArray;
import vehicles.Bicycle;
import vehicles.Bus;
import vehicles.Car;
import vehicles.Vehicle;
import org.json.JSONObject;
import java.util.ArrayList;

/** Class containing detained vehicles and actions with them*/
public class DetainedVehicles extends Building {
    private ArrayList<Vehicle> vehicles;

    /** A default constructor*/
    public DetainedVehicles() {
        super(50);
        this.vehicles = new ArrayList<>();
    }

    /** Add a vehicle to ArrayList
     * @param vehicle vehicle to be added
     * @return true if vehicle was added*/
    public boolean addVehicle(Vehicle vehicle) {
        if (this.vehicles.size() == this.getCapacity()) {
            return false;
        }
        this.vehicles.add(vehicle);
        return true;
    }

    /** Returns a vehicle at provided index
     * @param index index of a vehicle
     * @return null if vehicle wasn't found, otherwise returns vehicle*/
    public Vehicle getVehicle(int index) {
        if (index < 0 || index >= this.vehicles.size()) {
            return null;
        }
        return this.vehicles.get(index);
    }

    @Override
    public int sellContent() {
        int sum = 0;
        for (Vehicle vehicle : this.vehicles) {
            sum += 250;
        }
        this.vehicles.clear();
        return sum;
    }

    @Override
    public int getDailyOperationPrice() {
        return super.getDailyOperationPrice() * this.getCapacity();
    }

    @Override
    public boolean upgrade(int budget) {
        if (this.getUpgradeLevel() == 3) {
            System.out.println("Parking lot is already maxed out");
            return false;
        }

        if (budget < this.getNextUpgradePrice()) {
            System.out.printf("You don't have enough money, the parking lot upgrade costs %d money\n", this.getNextUpgradePrice());
            return false;
        }
        switch (this.getUpgradeLevel()) {
            case 0 -> this.setCapacity(this.getCapacity() + 3);
            case 1 -> this.setCapacity(this.getCapacity() + 5);
            case 2 -> this.setCapacity(this.getCapacity() + 10);
        }
        this.setUpgradeLevel(this.getUpgradeLevel() + 1);
        return true;
    }

    @Override
    public int getNextUpgradePrice() {
        return switch (this.getUpgradeLevel()) {
            case 0 -> 1000;
            case 1 -> 2000;
            case 2 -> 5000;
            default -> 0;
        };
    }

    @Override
    public JSONObject marshal() {
        JSONObject json = super.marshal();
        json.put("type", "DetainedVehicles");
        JSONArray arr = new JSONArray();
        for (Vehicle vehicle : this.vehicles) {
            //polymorfizmus
            arr.put(vehicle.marshal());
        }
        json.put("vehicles", arr);
        return json;
    }

    @Override
    public void unmarshal(JSONObject json) {
        super.unmarshal(json);
        this.vehicles = new ArrayList<>();
        JSONArray arr = json.getJSONArray("vehicles");
        for (Object obj : arr) {
            JSONObject jsonVehicle = (JSONObject)obj;
            switch (jsonVehicle.getString("type")) {
                case "Car" -> {
                    Car car = new Car();
                    car.unmarshal(jsonVehicle);
                    this.vehicles.add(car);
                }
                case "Bus" -> {
                    Bus bus = new Bus();
                    bus.unmarshal(jsonVehicle);
                    this.vehicles.add(bus);
                }
                case "Bicycle" -> {
                    Bicycle bicycle = new Bicycle();
                    bicycle.unmarshal(jsonVehicle);
                    this.vehicles.add(bicycle);
                }
            }
        }
    }
}
