package agents;

import mission.Mission;

public class ExplorationRover extends Robot {
    public ExplorationRover(String name, String id, String location, int batteryLevel) { super(name, id, location, batteryLevel); }
    @Override
    public boolean canPerform(Mission mission) { return "EXPLORATION".equals(mission.getType()) && getBatteryLevel() >= 10; }
    @Override
    public boolean canHandle(String type) { return "ROVER_BREAKDOWN".equals(type); }
    @Override
    public boolean canProvide(String capability) {
        return "EXPLORATION_VEHICLE".equals(capability) || super.canProvide(capability);
    }
    @Override
    public String role() { return "Exploration Rover"; }
}
