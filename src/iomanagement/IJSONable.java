package iomanagement;

import org.json.JSONObject;

/** Interface representing objects which can be marshaled and then unmarshalled from a JSON*/
public interface IJSONable {
    /** Returns a JSONObject with data from current instance
     * @return a JSONObject with data from current instance*/
    JSONObject marshal();

    /** Sets instance data to JSONObject data
     * @param json JSONObject containing data of an object instance*/
    void unmarshal(JSONObject json);
}
