package colony;

import agents.*;
import common.ResourceNames;
import emergency.Emergency;
import emergency.FireEmergency;
import emergency.MedicalEmergency;
import emergency.OxygenLeak;
import emergency.RoverBreakdown;
import facilities.Facility;
import facilities.Habitat;
import facilities.Laboratory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import mission.Mission;

public class MarsColony {
    private final String name;
    private final List<CrewMember> crew = new ArrayList<>();
    private final List<Robot> robots = new ArrayList<>();
    private final List<Facility> facilities = new ArrayList<>();
    private final List<Mission> missions = new ArrayList<>();
    private final List<Emergency> emergencies = new ArrayList<>();
    private final List<Agent> activeMissionAgents = new ArrayList<>();
    private Mission activeMission;
    private String environment = "NORMAL";
    private int simulationTime;
    private String lastOperationMessage = "";

    public MarsColony(String name) { this.name = name; }
    public boolean addCrewMember(CrewMember person) {
        if (person == null || crew.contains(person)) return false;
        crew.add(person);
        return true;
    }
    public boolean addRobot(Robot robot) {
        if (robot == null || robots.contains(robot)) return false;
        robots.add(robot);
        return true;
    }
    public boolean addFacility(Facility facility) {
        if (facility == null || facilities.contains(facility)) return false;
        facilities.add(facility);
        return true;
    }
    public List<CrewMember> getCrew() { return crew; }
    public List<Robot> getRobots() { return robots; }
    public List<Facility> getFacilities() { return facilities; }
    public List<Mission> getMissions() { return missions; }
    public List<Emergency> getEmergencies() { return emergencies; }
    public Mission getActiveMission() { return activeMission; }
    public String getEnvironment() { return environment; }
    public String getLastOperationMessage() { return lastOperationMessage; }
    public boolean setEnvironment(String condition) {
        if (!"NORMAL".equals(condition)
                && !"DUST_STORM".equals(condition)
                && !"COMMUNICATION_DISRUPTION".equals(condition)) {
            lastOperationMessage = "Unknown environment condition.";
            return false;
        }
        environment = condition;
        if ("DUST_STORM".equals(condition) && activeMission != null
                && "EXPLORATION".equals(activeMission.getType())) {
            finishMissionWithFailure("Dust storm caused the active surface mission to fail.");
        }
        if ("COMMUNICATION_DISRUPTION".equals(condition) && activeMission != null
                && hasMissionCapability(activeMissionAgents, activeMission, "REMOTE_CONTROL")) {
            finishMissionWithFailure("Communication disruption stopped remote drone control.");
        }
        lastOperationMessage = "Environment changed to " + condition + ".";
        return true;
    }
    public int getSimulationTime() { return simulationTime; }

    public int totalResource(String type) {
        if (!isKnownResource(type)) return -1;
        long total = 0;
        for (Facility facility : facilities) {
            if (facility.isOperational()) {
                total += facility.getResources().get(type);
            }
        }
        if (total > Integer.MAX_VALUE) return Integer.MAX_VALUE;
        return (int) total;
    }

    public boolean consumeResources(Map<String, Integer> required) {
        if (required == null) return false;
        for (Map.Entry<String, Integer> entry : required.entrySet()) {
            Integer amount = entry.getValue();
            if (!isKnownResource(entry.getKey()) || amount == null || amount < 0
                    || totalResource(entry.getKey()) < amount) return false;
        }
        // First operational facilities supply resources first; this deterministic policy is documented in README.
        for (Map.Entry<String, Integer> entry : required.entrySet()) {
            int remaining = entry.getValue();
            for (Facility facility : facilities) {
                if (remaining == 0) break;
                if (!facility.isOperational()) continue;
                int amount = Math.min(remaining, facility.getResources().get(entry.getKey()));
                if (!facility.getResources().remove(entry.getKey(), amount)) {
                    return false;
                }
                remaining -= amount;
            }
        }
        return true;
    }

    private boolean isKnownResource(String type) {
        for (String resource : ResourceNames.ALL) {
            if (resource.equals(type)) return true;
        }
        return false;
    }

    private boolean hasValidResourceRequests(Mission mission) {
        for (Map.Entry<String, Integer> entry : mission.getResourceRequirements().entrySet()) {
            if (!isKnownResource(entry.getKey()) || entry.getValue() == null || entry.getValue() < 0) {
                return false;
            }
        }
        return true;
    }

    private List<Agent> allAgents() {
        List<Agent> agents = new ArrayList<>(); agents.addAll(crew); agents.addAll(robots); return agents;
    }

    private boolean hasMissionCapability(List<Agent> agents, Mission mission, String capability) {
        for (Agent agent : agents) {
            if (agent.canPerform(mission) && agent.canProvide(capability)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasMissionEquipment(List<Agent> agents, Mission mission, String equipment) {
        for (Agent agent : agents) {
            if (agent.canPerform(mission)
                    && agent.canProvide("EXPLORATION_VEHICLE")
                    && agent.hasEquipment(equipment)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasRequiredMissionTeam(Mission mission, List<Agent> agents) {
        if ("EXPLORATION".equals(mission.getType())) {
            if (mission.getRequiredEquipment() == null) {
                mission.fail("An exploration mission must specify required equipment.");
                return false;
            }
            if (!hasMissionEquipment(agents, mission, mission.getRequiredEquipment())) {
                mission.fail("The exploration rover does not carry the required equipment.");
                return false;
            }
            if (mission.getRequiredSkill() == null
                    || !hasMissionCapability(agents, mission, "SCIENTIFIC_SKILL")) {
                mission.fail("An exploration mission requires a scientist with the required skill.");
                return false;
            }
            if (!hasMissionCapability(agents, mission, "EXPLORATION_VEHICLE")) {
                mission.fail("An exploration mission requires an available exploration rover.");
                return false;
            }
        } else if ("REPAIR".equals(mission.getType())) {
            if (!hasMissionCapability(agents, mission, "ENGINEERING")) {
                mission.fail("A repair mission requires an engineer with the required skill.");
                return false;
            }
            if (!hasMissionCapability(agents, mission, "REPAIR_UNIT")) {
                mission.fail("A repair mission requires an available repair robot.");
                return false;
            }
            Integer spareParts = mission.getResourceRequirements().get("SPARE_PARTS");
            if (spareParts == null || spareParts < 1) {
                mission.fail("A repair mission must request spare parts.");
                return false;
            }
        } else if ("RESEARCH".equals(mission.getType())) {
            if (mission.getRequiredSkill() == null
                    || !hasMissionCapability(agents, mission, "SCIENTIFIC_SKILL")) {
                mission.fail("A research mission requires a scientist with the required skill.");
                return false;
            }
            if (mission.getTargetFacility() == null
                    || !"LABORATORY".equals(mission.getTargetFacility().getType())) {
                mission.fail("A research mission requires a laboratory.");
                return false;
            }
            if (mission.getRequiredEquipment() == null) {
                mission.fail("A research mission must specify required equipment.");
                return false;
            }
            boolean hasResourceRequirement = false;
            for (Integer amount : mission.getResourceRequirements().values()) {
                if (amount != null && amount > 0) {
                    hasResourceRequirement = true;
                    break;
                }
            }
            if (!hasResourceRequirement) {
                mission.fail("A research mission must request resources.");
                return false;
            }
        } else if ("RESCUE".equals(mission.getType())) {
            if (!hasMissionCapability(agents, mission, "PERSONNEL")) {
                mission.fail("A rescue mission requires available personnel.");
                return false;
            }
            if (!hasMissionCapability(agents, mission, "RESCUE_VEHICLE")) {
                mission.fail("A rescue mission requires an available rescue vehicle.");
                return false;
            }
            if (!hasMissionCapability(agents, mission, "COMMUNICATION")) {
                mission.fail("A rescue mission requires communication capability.");
                return false;
            }
        }
        return true;
    }

    public boolean submitMission(Mission mission, List<Agent> agents) {
        if (mission == null) {
            lastOperationMessage = "Mission was not submitted because it is missing.";
            return false;
        }
        if (missions.contains(mission) || !"ASSIGNED".equals(mission.getState())) {
            if (!missions.contains(mission)) missions.add(mission);
            lastOperationMessage = "Only a new assigned mission can be submitted.";
            return false;
        }
        missions.add(mission);
        if (activeMission != null && !activeMission.isFinished()) {
            mission.fail("Another mission is already active.");
            lastOperationMessage = mission.getFailureReason();
            return false;
        }
        if (!isKnownMissionType(mission.getType())) {
            mission.fail("Unknown mission type.");
            lastOperationMessage = mission.getFailureReason();
            return false;
        }
        if (mission.getTargetFacility() != null && !facilities.contains(mission.getTargetFacility())) {
            mission.fail("Target facility does not belong to this colony.");
            lastOperationMessage = mission.getFailureReason();
            return false;
        }
        if ("EXPLORATION".equals(mission.getType()) && "DUST_STORM".equals(environment)) {
            mission.fail("Surface operations are not allowed during a dust storm.");
            lastOperationMessage = mission.getFailureReason();
            return false;
        }
        if ("RESCUE".equals(mission.getType()) && "COMMUNICATION_DISRUPTION".equals(environment)) {
            mission.fail("Rescue cannot start during communication disruption.");
            lastOperationMessage = mission.getFailureReason();
            return false;
        }
        if (mission.getTargetFacility() != null && !mission.getTargetFacility().isOperational()
                && !"REPAIR".equals(mission.getType())) {
            mission.fail("Target facility is not operational.");
            lastOperationMessage = mission.getFailureReason();
            return false;
        }
        if (!"EXPLORATION".equals(mission.getType()) && mission.getRequiredEquipment() != null
                && (mission.getTargetFacility() == null
                || !mission.getTargetFacility().getEquipment().contains(mission.getRequiredEquipment()))) {
            mission.fail("The target facility does not have the required equipment.");
            lastOperationMessage = mission.getFailureReason();
            return false;
        }
        if (agents == null || agents.isEmpty()) {
            mission.fail("At least one available agent must be assigned.");
            lastOperationMessage = mission.getFailureReason();
            return false;
        }
        List<Agent> checkedAgents = new ArrayList<>();
        for (Agent agent : agents) {
            if (agent == null || !allAgents().contains(agent) || !agent.isAvailable()) {
                mission.fail("An assigned agent is unavailable.");
                lastOperationMessage = mission.getFailureReason();
                return false;
            }
            if (checkedAgents.contains(agent)) {
                mission.fail("An agent cannot be assigned more than once.");
                lastOperationMessage = mission.getFailureReason();
                return false;
            }
            if (!agent.isUsable() || !agent.canPerform(mission)) {
                mission.fail("Agent " + agent.getName() + " does not possess the required capability.");
                lastOperationMessage = mission.getFailureReason();
                return false;
            }
            checkedAgents.add(agent);
        }
        if ("COMMUNICATION_DISRUPTION".equals(environment)
                && hasMissionCapability(agents, mission, "REMOTE_CONTROL")) {
            mission.fail("Remote drones cannot be controlled during a communication disruption.");
            lastOperationMessage = mission.getFailureReason();
            return false;
        }
        if (!hasRequiredMissionTeam(mission, agents)) {
            lastOperationMessage = mission.getFailureReason();
            return false;
        }
        if (!hasValidResourceRequests(mission)) {
            mission.fail("Mission resource request is invalid.");
            lastOperationMessage = mission.getFailureReason();
            return false;
        }
        if (!consumeResources(mission.getResourceRequirements())) {
            mission.fail("Insufficient resources; no resources were consumed.");
            lastOperationMessage = mission.getFailureReason();
            return false;
        }
        activeMissionAgents.clear();
        for (Agent agent : agents) {
            agent.setAvailable(false);
            activeMissionAgents.add(agent);
            if ("EXPLORATION".equals(mission.getType())) agent.moveTo("Martian Surface");
        }
        activeMission = mission;
        lastOperationMessage = "Mission assigned successfully.";
        return true;
    }

    private boolean isKnownMissionType(String type) {
        return "EXPLORATION".equals(type) || "REPAIR".equals(type)
                || "RESEARCH".equals(type) || "RESCUE".equals(type);
    }

    public boolean advanceTime(int units) {
        if (units < 0 || units > Integer.MAX_VALUE - simulationTime) {
            lastOperationMessage = "Time advance must be a non-negative amount within the supported range.";
            return false;
        }
        for (int i = 0; i < units; i++) {
            simulationTime++;
            if (activeMission == null) continue;
            if (activeMission.isFinished()) {
                completeRepairTarget();
                releaseMissionAgents();
                continue;
            }
            if ("ASSIGNED".equals(activeMission.getState())) activeMission.startProgress();
            activeMission.advanceOneUnit();
            if (activeMission.isFinished()) {
                completeRepairTarget();
                releaseMissionAgents();
            }
        }
        lastOperationMessage = "Simulation advanced by " + units + " time units.";
        return true;
    }

    private void completeRepairTarget() {
        if ("COMPLETED".equals(activeMission.getState())
                && "REPAIR".equals(activeMission.getType())
                && activeMission.getTargetFacility() != null) {
            activeMission.getTargetFacility().setOperational(true);
        }
    }

    private void finishMissionWithFailure(String reason) { if (activeMission != null) { activeMission.fail(reason); releaseMissionAgents(); } }
    private void releaseMissionAgents() {
        for (Agent agent : activeMissionAgents) agent.setAvailable(true);
        activeMissionAgents.clear();
        activeMission = null;
    }

    private List<Agent> availableResponders(String type, boolean needHuman, boolean needRepairRobot) {
        List<Agent> result = new ArrayList<>();
        for (Agent agent : allAgents()) if (agent.isAvailable() && agent.isUsable() && agent.canHandle(type)) result.add(agent);
        boolean human = false, robot = false;
        for (Agent agent : result) {
            human |= agent.canProvide("PERSONNEL");
            robot |= agent.canProvide("REPAIR_UNIT");
        }
        if ((needHuman && !human) || (needRepairRobot && !robot)) return new ArrayList<>();
        return result;
    }

    private Emergency beginEmergency(Emergency emergency) {
        emergencies.add(emergency);
        emergency.setState("RESPONDING");
        return emergency;
    }

    private Emergency rejectEmergency(String type, String location, String reason) {
        Emergency emergency = new Emergency(location, type);
        emergency.setState("UNRESOLVED");
        emergency.setOutcome(reason);
        emergencies.add(emergency);
        lastOperationMessage = reason;
        return emergency;
    }

    private void dispatch(List<Agent> responders, String location) {
        for (Agent responder : responders) {
            responder.respondToEmergency();
            responder.setAvailable(false);
            responder.moveTo(location);
        }
    }

    private void releaseResponders(List<Agent> responders) {
        for (Agent responder : responders) {
            responder.setAvailable(true);
        }
    }

    private void loseOxygen(Habitat habitat, int amount) {
        int oxygenAvailable = habitat.getResources().get("OXYGEN");
        int oxygenLost = Math.min(oxygenAvailable, amount);
        habitat.getResources().remove("OXYGEN", oxygenLost);
    }

    public Emergency handleOxygenLeak(Habitat habitat, int oxygenLoss) {
        if (habitat == null || !facilities.contains(habitat)) {
            return rejectEmergency("OXYGEN_LEAK", habitat == null ? null : habitat.getLocation(),
                    "The habitat must belong to this colony.");
        }
        if (oxygenLoss <= 0) {
            return rejectEmergency("OXYGEN_LEAK", habitat.getLocation(),
                    "Oxygen loss must be greater than zero.");
        }
        Emergency emergency = beginEmergency(new OxygenLeak(habitat, oxygenLoss));
        habitat.setOxygenLeak(true);
        loseOxygen(habitat, oxygenLoss);
        List<Agent> responders = availableResponders("OXYGEN_LEAK", true, true);
        if (responders.isEmpty()) {
            loseOxygen(habitat, oxygenLoss);
            emergency.setState("UNRESOLVED");
            emergency.setOutcome("No suitable human and robotic responders were available; the leak continued.");
            lastOperationMessage = emergency.getOutcome();
            return emergency;
        }
        Map<String, Integer> responseCost = new HashMap<>();
        responseCost.put("SPARE_PARTS", 1);
        responseCost.put("BATTERY_POWER", 2);
        if (!consumeResources(responseCost)) {
            loseOxygen(habitat, oxygenLoss);
            emergency.setState("UNRESOLVED");
            emergency.setOutcome("Response resources were insufficient; the leak continued.");
            lastOperationMessage = emergency.getOutcome();
            return emergency;
        }
        dispatch(responders, habitat.getLocation());
        habitat.setOxygenLeak(false);
        releaseResponders(responders);
        emergency.setState("RESOLVED");
        emergency.setOutcome("Leak repaired. Responders: " + responders.size());
        lastOperationMessage = emergency.getOutcome();
        return emergency;
    }

    public Emergency handleMedicalEmergency(CrewMember patient, Facility facility) {
        if (patient == null || !crew.contains(patient) || facility == null || !facilities.contains(facility)) {
            return rejectEmergency("MEDICAL_EMERGENCY",
                    facility == null ? null : facility.getLocation(),
                    "The patient and facility must belong to this colony.");
        }
        Emergency emergency = beginEmergency(new MedicalEmergency(patient));
        patient.setHealthStatus("INJURED");
        List<Agent> responders = availableResponders("MEDICAL_EMERGENCY", true, false);
        if (responders.isEmpty() || !facility.isOperational()) {
            emergency.setState("UNRESOLVED");
            emergency.setOutcome("No available medical officer or the facility is not operational.");
            lastOperationMessage = emergency.getOutcome();
            return emergency;
        }
        Map<String, Integer> cost = new HashMap<>();
        cost.put("MEDICAL_SUPPLIES", 1);
        if (!consumeResources(cost)) {
            emergency.setState("UNRESOLVED");
            emergency.setOutcome("There are not enough medical supplies.");
            lastOperationMessage = emergency.getOutcome();
            return emergency;
        }
        dispatch(responders, facility.getLocation());
        patient.setHealthStatus("HEALTHY");
        releaseResponders(responders);
        emergency.setState("RESOLVED");
        emergency.setOutcome("Patient treated successfully.");
        lastOperationMessage = emergency.getOutcome();
        return emergency;
    }

    public Emergency handleFire(Laboratory lab) {
        if (lab == null || !facilities.contains(lab)) {
            return rejectEmergency("FIRE", lab == null ? null : lab.getLocation(),
                    "The laboratory must belong to this colony.");
        }
        Emergency emergency = beginEmergency(new FireEmergency(lab, 1));
        List<Agent> responders = availableResponders("FIRE", true, true);
        if (responders.isEmpty()) {
            lab.setOperational(false);
            emergency.setState("UNRESOLVED");
            emergency.setOutcome("No suitable human and robotic responders were available.");
            lastOperationMessage = emergency.getOutcome();
            return emergency;
        }
        Map<String, Integer> cost = new HashMap<>();
        cost.put("OXYGEN", 1);
        if (!consumeResources(cost)) {
            lab.setOperational(false);
            emergency.setState("UNRESOLVED");
            emergency.setOutcome("There is not enough oxygen to contain the fire.");
            lastOperationMessage = emergency.getOutcome();
            return emergency;
        }
        dispatch(responders, lab.getLocation());
        lab.setOperational(false);
        lab.addEquipment("Fire-damaged equipment");
        releaseResponders(responders);
        emergency.setState("RESOLVED");
        emergency.setOutcome("Fire contained; the damaged laboratory is unavailable for now.");
        lastOperationMessage = emergency.getOutcome();
        return emergency;
    }

    public Emergency handleRoverBreakdown(ExplorationRover rover, Mission mission) {
        if (rover == null || !robots.contains(rover)) {
            return rejectEmergency("ROVER_BREAKDOWN", rover == null ? null : rover.getCurrentLocation(),
                    "The rover must belong to this colony.");
        }
        if (mission != null && !missions.contains(mission)) {
            return rejectEmergency("ROVER_BREAKDOWN", rover.getCurrentLocation(),
                    "The mission must belong to this colony.");
        }
        if (activeMission != null && activeMissionAgents.contains(rover)
                && mission != null && mission != activeMission) {
            return rejectEmergency("ROVER_BREAKDOWN", rover.getCurrentLocation(),
                    "The rover is assigned to a different active mission.");
        }
        Emergency emergency = beginEmergency(new RoverBreakdown(rover));
        rover.setDamaged(true);
        if (activeMission != null && activeMissionAgents.contains(rover)) {
            finishMissionWithFailure("Exploration rover broke down during the mission.");
        } else if (mission != null && !mission.isFinished()) {
            mission.fail("Exploration rover broke down during the mission.");
        }
        List<Agent> responders = availableResponders("ROVER_BREAKDOWN", true, true);
        if (responders.isEmpty()) {
            emergency.setState("UNRESOLVED");
            emergency.setOutcome("No suitable repair responders were available; the rover remains damaged.");
            lastOperationMessage = emergency.getOutcome();
            return emergency;
        }
        Map<String, Integer> cost = new HashMap<>();
        cost.put("SPARE_PARTS", 3);
        if (!consumeResources(cost)) {
            emergency.setState("UNRESOLVED");
            emergency.setOutcome("There are not enough spare parts; the rover remains damaged.");
            lastOperationMessage = emergency.getOutcome();
            return emergency;
        }
        dispatch(responders, rover.getCurrentLocation());
        rover.setDamaged(false);
        releaseResponders(responders);
        emergency.setState("RESOLVED");
        emergency.setOutcome("Rover repaired successfully.");
        lastOperationMessage = emergency.getOutcome();
        return emergency;
    }

    public String statusReport() {
        StringBuilder report = new StringBuilder("\n=== " + name + " STATUS (time " + simulationTime + ") ===\n");
        report.append("Environment: ").append(environment).append("\nResources: ");
        for (String type : ResourceNames.ALL) report.append(type).append("=").append(totalResource(type)).append(" ");
        report.append("\nCrew:\n"); for (CrewMember person : crew) report.append("  ").append(person).append("\n");
        report.append("Robots:\n"); for (Robot robot : robots) report.append("  ").append(robot).append("\n");
        report.append("Facilities:\n"); for (Facility facility : facilities) report.append("  ").append(facility).append("\n");
        report.append("Active mission: ").append(activeMission == null ? "none" : activeMission).append("\n");
        report.append("Emergencies: ").append(emergencies).append("\n");
        return report.toString();
    }
}
