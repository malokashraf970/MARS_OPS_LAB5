package mission;

import common.ResourceNames;
import facilities.Facility;
import java.util.HashMap;
import java.util.Map;

public class Mission {
    private final String name;
    private final String type;
    private final int duration;
    private final String requiredSkill;
    private final String requiredEquipment;
    private final Facility targetFacility;
    private final Map<String, Integer> resourceRequirements = new HashMap<>();
    private String state = "ASSIGNED";
    private int elapsedTime;
    private String failureReason = "";

    public Mission(String name, String type, int duration, String requiredSkill,
                   String requiredEquipment, Facility targetFacility) {
        this.name = name; this.type = type; this.duration = duration;
        this.requiredSkill = requiredSkill; this.requiredEquipment = requiredEquipment;
        this.targetFacility = targetFacility;
        if (name == null || type == null || duration <= 0) {
            state = "FAILED";
            failureReason = "Mission needs a name, a type, and a positive duration.";
        }
    }

    public String getName() { return name; }
    public String getType() { return type; }
    public int getDuration() { return duration; }
    public String getRequiredSkill() { return requiredSkill; }
    public String getRequiredEquipment() { return requiredEquipment; }
    public Facility getTargetFacility() { return targetFacility; }
    public String getState() { return state; }
    public int getElapsedTime() { return elapsedTime; }
    public String getFailureReason() { return failureReason; }
    public Map<String, Integer> getResourceRequirements() {
        return new HashMap<>(resourceRequirements);
    }
    public boolean requireResource(String type, int amount) {
        boolean knownResource = false;
        for (String resource : ResourceNames.ALL) {
            if (resource.equals(type)) {
                knownResource = true;
                break;
            }
        }
        if (!knownResource || amount < 0 || isFinished()) {
            if (!isFinished()) {
                fail("Resource requirement has an unknown name or negative amount.");
            }
            return false;
        }
        resourceRequirements.put(type, amount);
        return true;
    }
    public void fail(String reason) {
        if (!isFinished()) {
            state = "FAILED";
            failureReason = reason;
        }
    }
    public void startProgress() { if ("ASSIGNED".equals(state)) state = "IN_PROGRESS"; }
    public void advanceOneUnit() {
        if ("IN_PROGRESS".equals(state)) {
            elapsedTime++;
            if (elapsedTime >= duration) state = "COMPLETED";
        }
    }
    public boolean isFinished() {
        return "COMPLETED".equals(state) || "FAILED".equals(state);
    }
    public String toString() {
        return name + " [" + type + ", state=" + state + ", progress=" + elapsedTime + "/" + duration +
                (failureReason.isEmpty() ? "" : ", reason=" + failureReason) + "]";
    }
}
