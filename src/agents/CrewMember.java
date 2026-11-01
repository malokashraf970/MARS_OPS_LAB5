package agents;
import java.util.ArrayList;
import java.util.List;

public abstract class CrewMember extends Agent {
    private String healthStatus = "HEALTHY";
    private int fatigue;
    private final List<String> skills = new ArrayList<>();

    protected CrewMember(String name, String id, String location) { super(name, id, location); }
    public String getHealthStatus() { return healthStatus; }
    public void setHealthStatus(String healthStatus) { this.healthStatus = healthStatus; }
    public int getFatigue() { return fatigue; }
    public void setFatigue(int fatigue) { this.fatigue = Math.max(0, Math.min(100, fatigue)); }
    public List<String> getSkills() { return skills; }
    public void addSkill(String skill) { skills.add(skill); }
    public boolean hasSkill(String skill) { return skill == null || skills.contains(skill); }
    public String toString() { return role() + " " + getName() + " (available=" + isAvailable() + ", location=" + getCurrentLocation() + ")"; }
}
