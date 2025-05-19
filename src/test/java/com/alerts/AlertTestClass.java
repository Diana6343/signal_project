package com.alerts;

import static org.junit.jupiter.api.Assertions.*;

import com.alerts.decorator_pattern.ConcreteAlert;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AlertTest {
    @Test
    void testAlertCreation() {
        ConcreteAlert alert = new ConcreteAlert("1", "CriticalSystolic", 1000L, "BloodPressure");
        assertEquals("1", alert.getPatientId());
        assertEquals("CriticalSystolic", alert.getCondition());
        assertEquals(1000L, alert.getTimestamp());
        assertEquals("BloodPressure", alert.getAlertType());
    }
}