package agents;

import mission.Mission;

public class Engineer extends CrewMember {
    public Engineer(String name, String id, String location) { super(name, id, location); }
    @Override
    public boolean canPerform(Mission mission) { return "REPAIR".equals(mission.getType()) && hasSkill(mission.getRequiredSkill()); }
    @Override
    public boolean canHandle(String type) { return "OXYGEN_LEAK".equals(type) || "FIRE".equals(type) || "ROVER_BREAKDOWN".equals(type); }
    @Override
    public boolean canProvide(String capability) {
        return "ENGINEERING".equals(capability) || "PERSONNEL".equals(capability);
    }
    @Override
    public String role() { return "Engineer"; }
}
