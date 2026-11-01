
package facilities;

import common.ResourceInventory;
import java.util.ArrayList;
import java.util.List;
public class Facility {
    private final String name;
    private final String type;
    private final String location;
    private final int capacity;
    private boolean operational = true;
    private final ResourceInventory resources = new ResourceInventory();
    private final List<String> equipment = new ArrayList<>();

    public Facility(String name, String type, String location, int capacity) {
        this.name = name; this.type = type; this.location = location; this.capacity = capacity;
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public String getLocation() { return location; }
    public int getCapacity() { return capacity; }
    public boolean isOperational() { return operational; }
    public void setOperational(boolean operational) { this.operational = operational; }
    public ResourceInventory getResources() { return resources; }
    public List<String> getEquipment() { return equipment; }
    public void addEquipment(String item) { equipment.add(item); }

    public String toString() {
        return name + " (" + type + ", operational=" + operational + ", " + resources.summary() + ")";
    }
}
