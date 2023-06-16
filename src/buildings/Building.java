package buildings;

import iomanagement.IJSONable;
import org.json.JSONObject;

/** Class building is a base class for other classes to extend */
public abstract class Building implements IJSONable {
    private int upgradeLevel;
    private int dailyOperationPrice;
    private int capacity;

    /** Constructor
     * @param dailyOperationPrice daily building operating price*/
    public Building(int dailyOperationPrice) {
        this.dailyOperationPrice = dailyOperationPrice;
        this.upgradeLevel = 0;
        this.capacity = 5;
    }

    /** A default constructor*/
    public Building() {

    }

    /** Return capacity of a building*/
    public int getCapacity() {
        return this.capacity;
    }

    /** Returns daily operating price*/
    public int getDailyOperationPrice() {
        return this.dailyOperationPrice;
    }

    /** Sets capacity for a building
     * @param capacity new capacity*/
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /** Sets new daily operating price
     * @param dailyOperationPrice daily operating price*/
    public void setDailyOperationPrice(int dailyOperationPrice) {
        this.dailyOperationPrice = dailyOperationPrice;
    }

    /** Returns upgrade level of a building*/
    public int getUpgradeLevel() {
        return this.upgradeLevel;
    }

    /** Sets new upgrade level of a building
     * @param upgradeLevel new upgrade level*/
    public void setUpgradeLevel(int upgradeLevel) {
        if (upgradeLevel > 3) {
            System.out.println("Building is maxed out");
            return;
        }
        this.upgradeLevel = upgradeLevel;
    }

    /** Upgrades building*/
    public abstract boolean upgrade(int budget);

    /** Returns price of a next upgrade*/
    public abstract int getNextUpgradePrice();

    /** Sells all content of a building*/
    public abstract int sellContent();

    @Override
    public JSONObject marshal() {
        JSONObject json = new JSONObject();
        json.put("capacity", this.capacity);
        json.put("upgradeLevel", this.upgradeLevel);
        json.put("dailyOperationPrice", this.dailyOperationPrice);
        return json;
    }

    @Override
    public void unmarshal(JSONObject json) {
        this.capacity = json.getInt("capacity");
        this.upgradeLevel = json.getInt("upgradeLevel");
        this.dailyOperationPrice = json.getInt("dailyOperationPrice");
    }
}
