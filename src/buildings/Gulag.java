package buildings;

import org.json.JSONArray;
import people.Driver;
import people.Passenger;
import org.json.JSONObject;

import java.util.ArrayList;

/** A class containing detained passengers from vehicles and actions with them*/
public class Gulag extends Building {
    private ArrayList<Passenger> prisoners;

    /** A default constructor*/
    public Gulag() {
        super(30);
        this.prisoners = new ArrayList<>();
    }

    /** Add a prisoner to ArrayList
     * @param passenger prisoner to be added
     * @return true if prisoner was added*/
    public boolean addPrisoner(Passenger passenger) {
        if (this.prisoners.size() == this.getCapacity()) {
            return false;
        }
        this.prisoners.add(passenger);
        return true;
    }

    /** Returns a prisoner at provided index
     * @param index index of a prisoner
     * @return null if prisoner wasn't found, otherwise returns prisoner*/
    public Passenger getPrisoner(int index) {
        if (index < 0 || index >= this.prisoners.size()) {
            return null;
        }
        return this.prisoners.get(index);
    }

    @Override
    public int sellContent() {
        int sum = 0;
        for (Passenger prisoner : this.prisoners) {
            sum += 100;
        }
        this.prisoners.clear();
        return sum;
    }

    @Override
    public int getDailyOperationPrice() {
        return super.getDailyOperationPrice() * this.getCapacity();
    }

    @Override
    public boolean upgrade(int budget) {
        if (this.getUpgradeLevel() == 3) {
            System.out.println("Gulag is already maxed out");
            return false;
        }

        if (budget < this.getNextUpgradePrice()) {
            System.out.printf("You don't have enough money, the gulag upgrade costs %d money\n", this.getNextUpgradePrice());
            return false;
        }
        switch (this.getUpgradeLevel()) {
            case 0 -> this.setCapacity(this.getCapacity() + 5);
            case 1 -> this.setCapacity(this.getCapacity() + 5);
            case 2 -> this.setCapacity(this.getCapacity() + 10);
        }
        this.setUpgradeLevel(this.getUpgradeLevel() + 1);

        return true;
    }

    @Override
    public int getNextUpgradePrice() {
        return switch (this.getUpgradeLevel()) {
            case 0 -> 900;
            case 1 -> 1800;
            case 2 -> 4500;
            default -> 0;
        };
    }

    @Override
    public JSONObject marshal() {
        JSONObject json = super.marshal();
        json.put("type", "Gulag");
        JSONArray arr = new JSONArray();
        for (Passenger prisoner : this.prisoners) {
            //polymorfizmus
            arr.put(prisoner.marshal());
        }
        json.put("prisoners", arr);
        return json;
    }

    @Override
    public void unmarshal(JSONObject json) {
        super.unmarshal(json);
        this.prisoners = new ArrayList<>();
        JSONArray arr = json.getJSONArray("prisoners");
        for (Object obj : arr) {
            JSONObject jsonPrisoner = (JSONObject)obj;
            switch (jsonPrisoner.getString("type")) {
                case "Driver" -> {
                    Driver driver = new Driver();
                    driver.unmarshal(jsonPrisoner);
                    this.prisoners.add(driver);
                }
                case "Passenger" -> {
                    Passenger passenger = new Passenger();
                    passenger.unmarshal(jsonPrisoner);
                    this.prisoners.add(passenger);
                }
            }
        }
    }
}
