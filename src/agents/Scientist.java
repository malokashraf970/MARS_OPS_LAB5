package agents;

import mission.Mission;

public class Scientist extends CrewMember {
    public Scientist(String name, String id, String location) { super(name, id, location); }
    @Override
    public boolean canPerform(Mission mission) {
        return ("EXPLORATION".equals(mission.getType()) || "RESEARCH".equals(mission.getType()))
                && hasSkill(mission.getRequiredSkill());
    }
    @Override
    public boolean canHandle(String type) { return "FIRE".equals(type); }
    @Override
    public boolean canProvide(String capability) {
        return "SCIENTIFIC_SKILL".equals(capability) || "PERSONNEL".equals(capability);
    }
    @Override
    public String role() { return "Scientist"; }
}
