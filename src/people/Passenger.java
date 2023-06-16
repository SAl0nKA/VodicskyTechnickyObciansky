package people;

import generator.Generator;
import iomanagement.IJSONable;
import items.Drug;
import items.Gun;
import items.ITem;
import items.Passport;
import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;

public class Passenger implements IJSONable {
    private LocalDate birthday;
    private String name;
    private String surname;
    private String gender;
    private String hairColor;
    private String eyeColor;
    private int weight;
    private ArrayList<ITem> inventory;
    private boolean accused;

    /** Constructor for class Passenger
     * @param birthday birthday of a person
     * @param name name
     * @param surname surname
     * @param gender gender
     * @param hairColor hair color
     * @param eyeColor eye color
     * @param weight weight*/
    public Passenger(LocalDate birthday, String name, String surname, String gender, String hairColor, String eyeColor, int weight) {
        this.birthday = birthday;
        this.name = name;
        this.surname = surname;
        this.gender = gender;
        this.hairColor = hairColor;
        this.eyeColor = eyeColor;
        this.weight = weight;
        this.inventory = new ArrayList<>();
        this.accused = false;
    }

    /** A default constructor*/
    public Passenger() {

    }

    /** Returns birthday
     * @return birthday*/
    public LocalDate getBirthday() {
        return this.birthday;
    }

    /** Returns age
     * @return age*/
    public int getAge() {
        return Period.between(this.birthday, LocalDate.now()).getYears();
    }

    /** Returns name
     * @return name*/
    public String getName() {
        return this.name;
    }

    /** Returns surname
     * @return surname*/
    public String getSurname() {
        return this.surname;
    }

    /** Returns gender
     * @return gender*/
    public String getGender() {
        return this.gender;
    }

    /** Returns hair color
     * @return hair color*/
    public String getHairColor() {
        return this.hairColor;
    }

    /** Returns eye color
     * @return eye color*/
    public String getEyeColor() {
        return this.eyeColor;
    }

    /** Returns weight
     * @return weight*/
    public int getWeight() {
        return this.weight;
    }

    /** Returns inventory of a passenger
     * @return inventory of a passenger*/
    public ArrayList<ITem> getInventory() {
        return new ArrayList<>(this.inventory);
    }

    /** Removes item from inventory
     * @return item from inventory
     * @param index index of an item*/
    public ITem removeFromInventory(int index) {
        if (index < 0 || index >= this.inventory.size()) {
            return null;
        }
        return this.inventory.remove(index);
    }

    /** Returns true if passenger is accused
     * @return true if passenger is accused*/
    public boolean isAccused() {
        return this.accused;
    }

    /** Sets passenger as accused
     * @param accused is accused*/
    public void setAccused(boolean accused) {
        this.accused = accused;
    }

    /** Adds item to inventory
     * @param item item*/
    public void addToInventory(ITem item) {
        this.inventory.add(item);
    }

    /** Returns description of a passenger
     * @return description of a passenger*/
    public String getDescription() {
        return String.format(
                "Description of a passenger\n" +
                "[1] Gender: %s\n" +
                "[2] Hair color: %s\n" +
                "[3] Eye color: %s\n",
                this.gender, this.hairColor, this.eyeColor);
    }

    @Override
    public JSONObject marshal() {
        JSONObject json = new JSONObject();
        json.put("type", "Passenger");
        json.put("birthday", Generator.normalizeDate(this.birthday));
        json.put("name", this.name);
        json.put("surname", this.surname);
        json.put("gender", this.gender);
        json.put("hairColor", this.hairColor);
        json.put("eyeColor", this.eyeColor);
        json.put("weight", this.weight);

        json.put("accused", this.accused);
        JSONArray arr = new JSONArray();
        for (ITem item : this.inventory) {
            arr.put(item.marshal());
        }
        json.put("inventory", arr);
        return json;
    }

    @Override
    public void unmarshal(JSONObject json) {
        this.birthday = Generator.unnormalizeDate(json.getString("birthday"));
        this.name = json.getString("name");
        this.surname = json.getString("surname");
        this.gender = json.getString("gender");
        this.hairColor = json.getString("hairColor");
        this.eyeColor = json.getString("eyeColor");
        this.weight = json.getInt("weight");
        this.accused = json.getBoolean("accused");
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
                case "Passport" -> {
                    Passport passport = new Passport();
                    passport.unmarshal(item);
                    this.inventory.add(passport);
                }
            }
        }
    }
}
