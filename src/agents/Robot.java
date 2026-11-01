package agents;

import java.util.ArrayList;
import java.util.List;

public abstract class Robot extends Agent {
    private int batteryLevel;
    private final List<String> equipment = new ArrayList<>();

    protected Robot(String name, String id, String location, int batteryLevel) { super(name, id, location); this.batteryLevel = Math.max(0, batteryLevel); }
    public int getBatteryLevel() { return batteryLevel; }
    public void setBatteryLevel(int batteryLevel) { this.batteryLevel = Math.max(0, batteryLevel); }
    public boolean useBattery(int amount) {
        if (amount < 0 || amount > batteryLevel) return false;
        setBatteryLevel(batteryLevel - amount);
        return true;
    }
    public void addEquipment(String item) { equipment.add(item); }
    @Override
    public boolean hasEquipment(String item) { return equipment.contains(item); }
    @Override
    public boolean canProvide(String capability) {
        return "ROBOT".equals(capability);
    }
    public String toString() { return role() + " " + getName() + " (battery=" + batteryLevel + ", available=" + isAvailable() + ")"; }
}
