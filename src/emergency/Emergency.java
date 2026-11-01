package emergency;

public class Emergency {
    private final String location;
    private final String emergencyType;
    private String state = "RESPONDING";
    private String outcome = "";

    public Emergency(String location, String emergencyType) {
        this.location = location == null ? "Unknown location" : location;
        this.emergencyType = emergencyType == null ? "UNKNOWN" : emergencyType;
    }

    public String getLocation() {
        return location;
    }

    public String getEmergencyType() {
        return emergencyType;
    }

    public String getState() {
        return state;
    }

    public boolean isResolved() {
        return "RESOLVED".equals(state);
    }

    public boolean IsResolved() {
        return isResolved();
    }

    public String getOutcome() {
        return outcome;
    }

    public String getFailureReason() {
        return isResolved() ? "" : outcome;
    }

    public boolean setState(String state) {
        if (!"RESPONDING".equals(state)
                && !"RESOLVED".equals(state)
                && !"UNRESOLVED".equals(state)) {
            return false;
        }
        this.state = state;
        return true;
    }

    public void setOutcome(String outcome) {
        this.outcome = outcome == null ? "" : outcome;
    }

    @Override
    public String toString() {
        return emergencyType + " at " + location + " [state=" + state
                + (outcome.isEmpty() ? "" : ", outcome=" + outcome) + "]";
    }
}
