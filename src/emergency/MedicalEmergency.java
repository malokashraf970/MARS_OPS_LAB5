package emergency;

import agents.CrewMember;

public class MedicalEmergency extends Emergency {
    private final CrewMember patient;

    public MedicalEmergency(CrewMember patient) {
        super(patient.getCurrentLocation(), "MEDICAL_EMERGENCY");
        this.patient = patient;
    }

    public CrewMember getPatient() {
        return patient;
    }
}
