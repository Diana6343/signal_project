package com.alerts.decorator_pattern;


public interface Alert {
    String getPatientId();
    String getCondition();
    long getTimestamp();
    String getAlertType();
    void trigger(); // New method for decorator
}