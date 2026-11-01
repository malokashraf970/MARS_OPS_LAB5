package agents;

import mission.Mission;

public class Drone extends Robot {
    public Drone(String name, String id, String location, int batteryLevel) { super(name, id, location, batteryLevel); }
    @Override
    public boolean canPerform(Mission mission) { return "RESCUE".equals(mission.getType()) && getBatteryLevel() >= 10; }
    @Override
    public boolean canHandle(String type) { return "ROVER_BREAKDOWN".equals(type); }
    @Override
    public boolean canProvide(String capability) {
        return "RESCUE_VEHICLE".equals(capability)
                || "COMMUNICATION".equals(capability)
                || "REMOTE_CONTROL".equals(capability)
                || super.canProvide(capability);
    }
    @Override
    public String role() { return "Drone"; }
}
