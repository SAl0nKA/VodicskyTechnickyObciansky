package countries;

import iomanagement.IJSONable;
import org.json.JSONObject;

/** Class representing a country and it's relationship*/
public class Country implements IJSONable {
    private String name;
    private Relationship relationship;

    /** Constructor for class Country
     * @param name name of a country*/
    public Country(String name) {
        this.name = name;
        this.relationship = null;
    }

    /** A default constructor*/
    public Country() {

    }

    /** Returns country's relationship type
     * @return country's relationship type*/
    public Relationship getRelationship() {
        return this.relationship;
    }

    /** Sets relationship of a country
     * @param relationship relationship type*/
    public void setRelationship(Relationship relationship) {
        this.relationship = relationship;
    }

    /** Returns name of a country
     * @return name of a country*/
    public String getName() {
        return this.name;
    }

    @Override
    public JSONObject marshal() {
        JSONObject json = new JSONObject();
        json.put("name", this.name);
        json.put("relationship", this.relationship.name());
        return json;
    }

    @Override
    public void unmarshal(JSONObject json) {
        this.name = json.getString("name");
        this.relationship = Relationship.valueOf(json.getString("relationship"));
    }
}
