package vehicles;

import items.Drug;
import items.Gun;
import items.ITem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Bus extends Vehicle {
    private ArrayList<ITem> inventory;

    /** Constructor for class Bus
     * @param registrationNumber registration number
     * @param color color
     * @param brand brand
     * @param numberOfWheels number of wheels
     * @param maxPassengers maximum number of passengers */
    public Bus(String registrationNumber, String color, String brand, int numberOfWheels, int maxPassengers) {
        super(registrationNumber, color, brand, numberOfWheels, maxPassengers);
        this.inventory = new ArrayList<>();
    }

    /** A default constructor*/
    public Bus() {
        super();
    }

    /** Returns inventory
     * @return inventory*/
    public ArrayList<ITem> getInventory() {
        return new ArrayList<>(this.inventory);
    }

    /** Adds item to inventory
     * @param item item*/
    public void addItemToInventory(ITem item) {
        this.inventory.add(item);
    }

    /** Removes item from inventory
     * @return item from inventory
     * @param index index of an item*/
    public ITem removeItemFromInventory(int index) {
        if (index < 0 || index >= this.inventory.size()) {
            return null;
        }
        return this.inventory.remove(index);
    }

    @Override
    public String getVehicleDescription() {
        return String.format(
                "Bus information\n" +
                "[0] Registration number: %s\n" +
                "[1] Brand: %s\n" +
                "[2] Color: %s\n" +
                "[3] Number of wheels: %s\n" +
                "%s", this.getRegistrationNumber(), this.getBrand(), this.getColor(), this.getNumberOfWheels(),
                this.inventory.size() != 0 ? "Looks like there's something in the luggage compartment\n" : "");
    }

    @Override
    public JSONObject marshal() {
        JSONObject json = super.marshal();
        json.put("type", "Bus");
        JSONArray arr = new JSONArray();
        for (ITem item : this.inventory) {
            arr.put(item.marshal());
        }
        json.put("inventory", arr);
        return json;
    }

    @Override
    public void unmarshal(JSONObject json) {
        super.unmarshal(json);
        this.inventory = new ArrayList<>();
        for (Object obj : json.getJSONArray("inventory")) {
            JSONObject item = (JSONObject)obj;
            switch (item.getString("type")) {
                case "Gun" -> {
                    Gun gun = new Gun();
                    gun.unmarshal(item);
                    this.inventory.add(gun);
                }
                case "Drug" -> {
                    Drug drug = new Drug();
                    drug.unmarshal(item);
                    this.inventory.add(drug);
                }
            }
        }
    }
}
