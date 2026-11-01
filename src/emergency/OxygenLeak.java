package emergency;

import facilities.Habitat;

public class OxygenLeak extends Emergency {
    private final Habitat affectedHabitat;
    private final int oxygenLossAmount;

    public OxygenLeak(Habitat affectedHabitat, int oxygenLossAmount) {
        super(affectedHabitat.getLocation(), "OXYGEN_LEAK");
        this.affectedHabitat = affectedHabitat;
        this.oxygenLossAmount = oxygenLossAmount;
    }

    public Habitat getAffectedHabitat() {
        return affectedHabitat;
    }

    public int getOxygenLossAmount() {
        return oxygenLossAmount;
    }
}
