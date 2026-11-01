package app;
import agents.Engineer;
import agents.ExplorationRover;
import agents.MedicalOfficer;
import agents.RepairRobot;
import agents.Scientist;
import colony.MarsColony;
import emergency.Emergency;
import facilities.Habitat;
import facilities.Laboratory;
import facilities.StorageFacility;
import java.util.Arrays;
import mission.Mission;

public class Main {
    public static void main(String[] args) {
        MarsColony colony = new MarsColony("Ares Research Colony");

        Habitat habitat = new Habitat("Habitat A", "Sector A", 20);
        habitat.getResources().add("OXYGEN", 300);
        habitat.getResources().add("WATER", 100);

        StorageFacility storage = new StorageFacility("Central Storage", "Sector B", 100);
        storage.getResources().add("OXYGEN", 200);
        storage.getResources().add("SPARE_PARTS", 10);
        storage.getResources().add("BATTERY_POWER", 20);
        storage.getResources().add("MEDICAL_SUPPLIES", 5);

        Laboratory laboratory = new Laboratory("Mars Lab", "Sector C", 8);
        laboratory.addEquipment("Spectrometer");

        colony.addFacility(habitat);
        colony.addFacility(storage);
        colony.addFacility(laboratory);

        Scientist scientist = new Scientist("Dr. Lina", "C-001", "Habitat A");
        scientist.addSkill("geology");
        Engineer engineer = new Engineer("Eng. Omar", "C-002", "Habitat A");
        engineer.addSkill("repair");
        MedicalOfficer medic = new MedicalOfficer("Nurse Sara", "C-003", "Habitat A");

        ExplorationRover rover = new ExplorationRover("Rover-1", "R-001", "Garage", 80);
        RepairRobot repairRobot = new RepairRobot("RepairBot-1", "R-002", "Garage", 60);
        rover.addEquipment("Spectrometer");

        colony.addCrewMember(scientist);
        colony.addCrewMember(engineer);
        colony.addCrewMember(medic);
        colony.addRobot(rover);
        colony.addRobot(repairRobot);

        System.out.println("=== MARS-OPS Demonstration ===");

        System.out.println("\n--- Scenario 1: Successful Exploration ---");
        System.out.println("Colony before mission:");
        System.out.println(colony.statusReport());

        Mission exploration = new Mission(
                "Explore Jezero Crater", "EXPLORATION", 3,
                "geology", "Spectrometer", null);
        if (!exploration.requireResource("BATTERY_POWER", 5)) {
            System.out.println("Could not add the mission resource requirement.");
        }
        System.out.println("Rover carries Spectrometer: " + rover.hasEquipment("Spectrometer"));
        if (!colony.submitMission(exploration, Arrays.asList(scientist, rover))) {
            System.out.println("Mission submission failed: " + exploration.getFailureReason());
        }
        System.out.println("After submission: " + exploration);
        if (!colony.advanceTime(3)) {
            System.out.println("Time advance failed: " + colony.getLastOperationMessage());
        }
        System.out.println("After mission execution: " + exploration);
        System.out.println("Colony after mission:");
        System.out.println(colony.statusReport());

        System.out.println("\n--- Scenario 2: Mission Rejection ---");
        Mission rejectedMission = new Mission(
                "Unsuitable Exploration", "EXPLORATION", 2,
                "geology", "Spectrometer", laboratory);
        if (!colony.submitMission(rejectedMission, Arrays.asList(medic, rover))) {
            System.out.println("Mission rejected: " + rejectedMission.getFailureReason());
        }
        System.out.println("Mission result: " + rejectedMission);
        System.out.println("Active mission: "
                + (colony.getActiveMission() == null
                ? "none" : colony.getActiveMission().getName()));

        System.out.println("\n--- Scenario 3: Resource Shortage ---");
        int fuelBefore = colony.totalResource("FUEL");
        int batteryBefore = colony.totalResource("BATTERY_POWER");
        Mission shortageMission = new Mission(
                "Fuel-Hungry Repair", "REPAIR", 2,
                "repair", null, storage);
        shortageMission.requireResource("SPARE_PARTS", 2);
        shortageMission.requireResource("BATTERY_POWER", 2);
        shortageMission.requireResource("FUEL", 999);
        if (!colony.submitMission(shortageMission, Arrays.asList(engineer, repairRobot))) {
            System.out.println("Mission rejected: " + shortageMission.getFailureReason());
        }
        System.out.println("Mission result: " + shortageMission);
        System.out.println("Fuel before/after: " + fuelBefore + "/"
                + colony.totalResource("FUEL"));
        System.out.println("Battery power before/after: " + batteryBefore + "/"
                + colony.totalResource("BATTERY_POWER"));

        System.out.println("\n--- Scenario 4: Oxygen Leak Emergency ---");
        int oxygenBefore = habitat.getResources().get("OXYGEN");
        System.out.println("Available responders:");
        System.out.println("  Engineer: " + engineer.isAvailable()
                + ", can respond: " + engineer.canHandle("OXYGEN_LEAK"));
        System.out.println("  Repair robot: " + repairRobot.isAvailable()
                + ", can respond: " + repairRobot.canHandle("OXYGEN_LEAK"));
        Emergency oxygenEmergency = colony.handleOxygenLeak(habitat, 25);
        System.out.println("Emergency result: " + oxygenEmergency);
        System.out.println("Habitat oxygen before/after: " + oxygenBefore + "/"
                + habitat.getResources().get("OXYGEN"));
        System.out.println("Habitat leak active: " + habitat.hasOxygenLeak());
        System.out.println("Spare parts remaining: " + colony.totalResource("SPARE_PARTS"));
        System.out.println("Battery power remaining: " + colony.totalResource("BATTERY_POWER"));

        System.out.println("\n--- Scenario 5: Dust Storm ---");
        if (!colony.setEnvironment("DUST_STORM")) {
            System.out.println("Environment update failed: " + colony.getLastOperationMessage());
        }
        Mission stormMission = new Mission(
                "Storm Exploration", "EXPLORATION", 2,
                "geology", "Spectrometer", laboratory);
        if (!colony.submitMission(stormMission, Arrays.asList(scientist, rover))) {
            System.out.println("Mission rejected: " + stormMission.getFailureReason());
        }
        System.out.println("Environment: " + colony.getEnvironment());
        System.out.println("Mission result: " + stormMission);
        System.out.println("Active mission: "
                + (colony.getActiveMission() == null
                ? "none" : colony.getActiveMission().getName()));
    }
}
