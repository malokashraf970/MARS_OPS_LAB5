package emergency;


public interface EmergencyResponder {
    boolean canHandleEmergency(String emergencyType);
    boolean isAvailable(); 
    void respondToEmergency();
    void setAvailable(boolean status);
         
}
