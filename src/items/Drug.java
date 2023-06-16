package items;

import org.json.JSONObject;

/** Class representing a drug with set name and ammount whcih can act as contraband in inventory*/
public class Drug implements ITem {
    private String name;
    private double amount;

    /** A constructor for class Drug
     * @param name name of a drug
     * @param amount amount of drug*/
    public Drug(String name, double amount) {
        this.name = name;
        this.amount = amount;
    }

    /** A default constructor*/
    public Drug() {

    }

    /** Returns name of a drug
     * @return name of a drug*/
    public String getName() {
        return this.name;
    }

    /** Returns amount of a drug
     * @return amount of a drug*/
    public double getAmount() {
        return this.amount;
    }

    @Override
    public String getDescription() {
        return "%s with weight of %f".formatted(this.name, 0.3);
    }

    @Override
    public double getWeight() {
        return this.amount * 0.2;
    }

    @Override
    public JSONObject marshal() {
        JSONObject json = new JSONObject();
        json.put("type", "Drug");
        json.put("name", this.name);
        json.put("amount", this.amount);
        return json;
    }

    @Override
    public void unmarshal(JSONObject json) {
        this.name = json.getString("name");
        this.amount = json.getDouble("amount");
    }
}
