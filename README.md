# MARS-OPS Organized Simple Team Project

This is the organized MARS-OPS team project using a simple beginner-level design. It does **not** use Java enums. Mission types, mission states, emergency types, facility types, environmental conditions, health statuses, and resource names are represented with strings.

## Folder structure

```text
MARS-OPS-Organized-Simple/
├── README.md
├── docs/
│   └── PROJECT-STRUCTURE.md
└── src/
    ├── common/
    │   ├── ResourceNames.java
    │   ├── ResourceInventory.java
    │   └── LifeSupportSystem.java
    ├── mission/
    │   └── Mission.java
    ├── agents/
    │   ├── Agent.java
    │   ├── CrewMember.java
    │   ├── Scientist.java
    │   ├── Engineer.java
    │   ├── MedicalOfficer.java
    │   ├── Robot.java
    │   ├── ExplorationRover.java
    │   ├── RepairRobot.java
    │   └── Drone.java
    ├── facilities/
    │   ├── Facility.java
    │   ├── Habitat.java
    │   ├── Laboratory.java
    │   ├── StorageFacility.java
    │   ├── PowerStation.java
    │   └── CommunicationStation.java
    ├── emergency/
    │   ├── Emergency.java
    │   ├── EmergencyResponder.java
    │   ├── FireEmergency.java
    │   ├── MedicalEmergency.java
    │   ├── OxygenLeak.java
    │   └── RoverBreakdown.java
    ├── colony/
    │   └── MarsColony.java
    └── app/
        └── Main.java
```

## No-enum rule

The project uses strings such as `"EXPLORATION"`, `"ASSIGNED"`, `"DUST_STORM"`, and `"OXYGEN"`. This keeps the code close to the simple design agreed for the team. `ResourceNames.ALL` is a normal string array used when the status report needs to visit every resource.

## Design assumptions

- When resources are consumed, operational facilities supply them in the order they were added to the colony. A request is checked against colony-wide totals first, so an unsuccessful request consumes nothing.
- Exploration missions need a scientist with the requested skill and an exploration rover carrying the required equipment. Repair missions need an engineer, a repair robot, and a positive spare-parts request. Research missions need a skilled scientist, a laboratory with the requested equipment, and a positive resource request. Rescue missions need available personnel, a drone, and communication capability.
- An oxygen leak immediately removes the scenario's oxygen-loss amount from its habitat. If no suitable responders or repair resources are available, the same amount is removed one more time. Oxygen is capped at zero. Successful oxygen-leak repairs require an engineer, a repair robot, one spare part, and two battery-power units.
- Fire response requires an engineer or other capable crew member, a repair robot, and one oxygen unit. The laboratory is marked unavailable and its equipment is marked as damaged. Medical emergencies require an available medical officer, one medical-supplies unit, and an operational facility. Rover repairs require an engineer, a repair robot, and three spare parts.
- Responders must be available, usable, and report that they can handle the emergency. They are released after a response. An emergency does not take agents from an active mission. A rover breakdown is a mission failure when the broken rover is assigned to that mission, as required by the rover-breakdown rule.
- A dust storm rejects new exploration missions and fails active exploration missions. A communication disruption rejects rescue missions, prevents remote drone control, and fails an active mission that needs remote drone control. An environment-caused mission failure releases its agents. Failed missions are not retried; create a new mission to try again.
- Invalid operations use boolean results and descriptive mission or emergency status messages instead of Java exceptions. Unknown resource totals return `-1`, while invalid resource additions and consumption requests return `false`.

## Compile and run

From the project root in PowerShell:

```powershell
if (Test-Path out) { Remove-Item out -Recurse -Force }
New-Item -ItemType Directory out | Out-Null
$files = Get-ChildItem src -Filter *.java -Recurse | ForEach-Object { $_.FullName }
javac -Xlint:all -d out $files
java -cp out app.Main
```

The Java package names match the source folders. `app.Main` is the application entry point.
