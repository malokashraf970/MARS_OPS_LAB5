package facilities;

import common.LifeSupportSystem;

public class Habitat extends Facility {
    private final LifeSupportSystem lifeSupportSystem = new LifeSupportSystem();
    private boolean oxygenLeak;

    public Habitat(String name, String location, int capacity) {
        super(name, "HABITAT", location, capacity);
    }
    public LifeSupportSystem getLifeSupportSystem() { return lifeSupportSystem; }
    public boolean hasOxygenLeak() { return oxygenLeak; }
    public void setOxygenLeak(boolean oxygenLeak) { this.oxygenLeak = oxygenLeak; }
}
