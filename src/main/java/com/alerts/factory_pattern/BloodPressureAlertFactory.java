package com.alerts.factory_pattern;

import com.alerts.Alert;
import com.alerts.decorator_pattern.ConcreteAlert;

public class BloodPressureAlertFactory implements AlertFactory {
    @Override
    public ConcreteAlert createAlert(String patientId, String condition, long timestamp) {
        return new ConcreteAlert(patientId, condition, timestamp, "BloodPressure");
    }
}