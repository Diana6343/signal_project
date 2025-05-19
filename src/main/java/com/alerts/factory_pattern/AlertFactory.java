package com.alerts.factory_pattern;

import com.alerts.Alert;
import com.alerts.decorator_pattern.ConcreteAlert;

public interface AlertFactory {
    ConcreteAlert createAlert(String patientId, String condition, long timestamp);
}