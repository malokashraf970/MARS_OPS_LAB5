package emergency;

import agents.ExplorationRover;

public class RoverBreakdown extends Emergency {
    private final ExplorationRover affectedRover;

    public RoverBreakdown(ExplorationRover affectedRover) {
        super(affectedRover.getCurrentLocation(), "ROVER_BREAKDOWN");
        this.affectedRover = affectedRover;
    }

    public ExplorationRover getAffectedRover() {
        return affectedRover;
    }
}
