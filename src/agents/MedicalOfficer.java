package agents;

import mission.Mission;

public class MedicalOfficer extends CrewMember {
    public MedicalOfficer(String name, String id, String location) { super(name, id, location); }
    @Override
    public boolean canPerform(Mission mission) { return "RESCUE".equals(mission.getType()); }
    @Override
    public boolean canHandle(String type) { return "MEDICAL_EMERGENCY".equals(type) || "FIRE".equals(type); }
    @Override
    public boolean canProvide(String capability) {
        return "MEDICAL".equals(capability) || "PERSONNEL".equals(capability);
    }
    @Override
    public String role() { return "Medical Officer"; }
}
