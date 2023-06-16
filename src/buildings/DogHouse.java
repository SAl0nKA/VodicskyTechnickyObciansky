package buildings;

import items.Drug;
import items.Gun;
import items.ITem;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/** Class containing a dog house which can boost chance for successful search and can detain items*/
public class DogHouse extends Building {
    public static final int DOG_HOUSE_PRICE = 600;
    private double chanceForSuccessfulSearch;
    private ArrayList<ITem> detainedItems;

    /** A default constructor*/
    public DogHouse() {
        super(10);
        this.chanceForSuccessfulSearch = 0.1;
        this.detainedItems = new ArrayList<>();
    }

    /** Returns additional chance for successful search
     * @return additional chance for successful search*/
    public double getChanceForSuccessfulSearch() {
        return this.chanceForSuccessfulSearch;
    }

    /** Add an item to ArrayList
     * @param item item to be added
     * @return true if item was added*/
    public boolean addItem(ITem item) {
        if (this.detainedItems.size() == this.getCapacity()) {
            System.out.println("Dog house is at full capacity");
            return false;
        }
        this.detainedItems.add(item);
        return true;
    }

    @Override
    public int getDailyOperationPrice() {
        return super.getDailyOperationPrice() * this.getUpgradeLevel();
    }

    @Override
    public boolean upgrade(int budget) {
        if (this.getUpgradeLevel() == 3) {
            System.out.println("Dog house is already maxed out");
            return false;
        }

        if (budget < this.getNextUpgradePrice()) {
            System.out.printf("You don't have enough money, the dog house upgrade costs %d money\n", this.getNextUpgradePrice());
            return false;
        }
        switch (this.getUpgradeLevel()) {
            case 0 -> {
                this.setCapacity(this.getCapacity() + 4);
                this.chanceForSuccessfulSearch = 0.2;
            }
            case 1 -> {
                this.setCapacity(this.getCapacity() + 6);
                this.chanceForSuccessfulSearch = 0.3;
            }
            case 2 -> {
                this.setCapacity(this.getCapacity() + 10);
                this.chanceForSuccessfulSearch = 0.4;
            }
        }
        this.setUpgradeLevel(this.getUpgradeLevel() + 1);
        return true;
    }

    @Override
    public int getNextUpgradePrice() {
        return switch (this.getUpgradeLevel()) {
            case 0 -> 800;
            case 1 -> 2000;
            case 2 -> 4500;
            default -> 0;
        };
    }

    @Override
    public int sellContent() {
        int sum = 0;
        for (ITem detainedItem : this.detainedItems) {
            if (detainedItem instanceof Gun gun) {
                sum += 100 + gun.getAmmo() * 5;
            } else if (detainedItem instanceof Drug drug) {
                sum += drug.getAmount() * 50;
            }
        }
        this.detainedItems.clear();
        return sum;
    }

    @Override
    public JSONObject marshal() {
        JSONObject json = super.marshal();
        json.put("type", "DogHouse");
        json.put("chanceForSuccessfulSearch", this.chanceForSuccessfulSearch);
        JSONArray arr = new JSONArray();
        for (ITem detainedItem : this.detainedItems) {
            //polymorfizmus
            arr.put(detainedItem.marshal());
        }
        json.put("detainedItems", arr);
        return json;
    }

    @Override
    public void unmarshal(JSONObject json) {
        super.unmarshal(json);
        this.chanceForSuccessfulSearch = json.getDouble("chanceForSuccessfulSearch");
        this.detainedItems = new ArrayList<>();
        for (Object obj : json.getJSONArray("detainedItems")) {
            JSONObject jsonItem = (JSONObject)obj;
            switch (jsonItem.getString("type")) {
                case "Gun" -> {
                    Gun gun = new Gun();
                    gun.unmarshal(jsonItem);
                    this.detainedItems.add(gun);
                }
                case "Drug" -> {
                    Drug drug = new Drug();
                    drug.unmarshal(jsonItem);
                    this.detainedItems.add(drug);
                }
            }
        }
    }
}
