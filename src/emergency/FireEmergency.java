package emergency;

import facilities.Laboratory;

public class FireEmergency extends Emergency {
    private final Laboratory affectedLab;
    private final int oxygenConsumed;

    public FireEmergency(Laboratory affectedLab, int oxygenConsumed) {
        super(affectedLab.getLocation(), "FIRE");
        this.affectedLab = affectedLab;
        this.oxygenConsumed = oxygenConsumed;
    }

    public Laboratory getAffectedLab() {
        return affectedLab;
    }

    public int getOxygenConsumed() {
        return oxygenConsumed;
    }
}
