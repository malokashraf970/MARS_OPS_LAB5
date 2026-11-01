package agents;

import mission.Mission;

public class RepairRobot extends Robot {
    public RepairRobot(String name, String id, String location, int batteryLevel) { super(name, id, location, batteryLevel); }
    @Override
    public boolean canPerform(Mission mission) { return "REPAIR".equals(mission.getType()) && getBatteryLevel() >= 5; }
    @Override
    public boolean canHandle(String type) { return "OXYGEN_LEAK".equals(type) || "FIRE".equals(type) || "ROVER_BREAKDOWN".equals(type); }
    @Override
    public boolean canProvide(String capability) {
        return "REPAIR_UNIT".equals(capability) || super.canProvide(capability);
    }
    @Override
    public String role() { return "Repair Robot"; }
}
