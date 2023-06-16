package items;

import org.json.JSONObject;

/** Class representing a gun with set type and ammo which can act as contraband in inventory*/
public class Gun implements ITem {
    private String gunType;
    private int ammo;

    /** A constructor for class Gun
     * @param gunType gun type
     * @param ammo number of bullets*/
    public Gun(String gunType, int ammo) {
        this.gunType = gunType;
        this.ammo = ammo;
    }

    /** A default constructor*/
    public Gun() {

    }

    /** Return number of bullets
     * @return number of bullets*/
    public int getAmmo() {
        return this.ammo;
    }

    @Override
    public String getDescription() {
        return "a weapon of type %s with %d rounds".formatted(this.gunType, this.ammo);
    }

    @Override
    public double getWeight() {
        //2.5 je pevna vaha zbrane
        return 2.5 + this.ammo * 0.05;
    }

    @Override
    public JSONObject marshal() {
        JSONObject json = new JSONObject();
        json.put("type", "Gun");
        json.put("gunType", this.gunType);
        return json;
    }

    @Override
    public void unmarshal(JSONObject json) {
        this.gunType = json.getString("type");
    }
}
