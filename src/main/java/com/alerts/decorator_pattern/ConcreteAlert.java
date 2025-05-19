package com.alerts.decorator_pattern;


import java.util.logging.Logger;

public class ConcreteAlert implements Alert {
    private static final Logger LOGGER = Logger.getLogger(ConcreteAlert.class.getName());
    private String patientId;
    private String condition;
    private long timestamp;
    private String alertType;

    public ConcreteAlert(String patientId, String condition, long timestamp, String alertType) {
        this.patientId = patientId;
        this.condition = condition;
        this.timestamp = timestamp;
        this.alertType = alertType;
    }

    @Override
    public String getPatientId() {
        return patientId;
    }

    @Override
    public String getCondition() {
        return condition;
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String getAlertType() {
        return alertType;
    }

    @Override
    public void trigger() {
        LOGGER.info("Alert triggered: Patient " + patientId + ", Condition: " +
                condition + ", Type: " + alertType + ", Timestamp: " + timestamp);
    }
}