package com.alerts;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AlertTest {
    @Test
    void testAlertCreation() {
        Alert alert = new Alert("1", "CriticalSystolic", 1000L);
        assertEquals("1", alert.getPatientId());
        assertEquals("CriticalSystolic", alert.getCondition());
        assertEquals(1000L, alert.getTimestamp());
    }
}