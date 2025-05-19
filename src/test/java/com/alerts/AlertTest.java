package com.alerts;

import com.alerts.decorator_pattern.ConcreteAlert;
import com.alerts.factory_pattern.AlertFactory;
import com.alerts.factory_pattern.BloodOxygenAlertFactory;
import com.alerts.factory_pattern.BloodPressureAlertFactory;
import com.alerts.factory_pattern.ECGAlertFactory;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AlertFactoryTest {
    @Test
    void testBloodPressureAlertFactory() {
        AlertFactory factory = new BloodPressureAlertFactory();
        ConcreteAlert alert = factory.createAlert("1", "CriticalSystolic", 1000L);
        assertEquals("1", alert.getPatientId());
        assertEquals("CriticalSystolic", alert.getCondition());
        assertEquals(1000L, alert.getTimestamp());
        assertEquals("BloodPressure", alert.getAlertType());
    }

    @Test
    void testBloodOxygenAlertFactory() {
        AlertFactory factory = new BloodOxygenAlertFactory();
        ConcreteAlert alert = factory.createAlert("2", "LowSaturation", 2000L);
        assertEquals("2", alert.getPatientId());
        assertEquals("LowSaturation", alert.getCondition());
        assertEquals(2000L, alert.getTimestamp());
        assertEquals("BloodOxygen", alert.getAlertType());
    }

    @Test
    void testECGAlertFactory() {
        AlertFactory factory = new ECGAlertFactory();
        ConcreteAlert alert = factory.createAlert("3", "ECGPeak", 3000L);
        assertEquals("3", alert.getPatientId());
        assertEquals("ECGPeak", alert.getCondition());
        assertEquals(3000L, alert.getTimestamp());
        assertEquals("ECG", alert.getAlertType());
    }
}