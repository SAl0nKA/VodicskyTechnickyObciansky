package items;

import iomanagement.IJSONable;

/** Interface representing objects which can act as an item in inventory*/
public interface ITem extends IJSONable {
    /** Returns description of an item
     * @return description of an item*/
    String getDescription();

    /** Return weight of an item
     * @return weight of an item*/
    double getWeight();
}
