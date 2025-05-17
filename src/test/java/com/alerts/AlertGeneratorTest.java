package com.alerts;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.data_management.DataStorage;
import com.data_management.Patient;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class AlertGeneratorTest {
    @Test
    void testBloodPressureCriticalAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 190.0, "BloodPressureSystolic", 1000L);
        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);

        // Redirect System.out to capture alerts
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        generator.evaluateData(patient);
        assertTrue(outContent.toString().contains("CriticalSystolic"));
    }

    @Test
    void testBloodSaturationLowAlert() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 90.0, "BloodSaturation", 1000L);
        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        generator.evaluateData(patient);
        assertTrue(outContent.toString().contains("LowSaturation"));
    }

    @Test
    void testNoAlertsForNormalData() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 120.0, "BloodPressureSystolic", 1000L);
        storage.addPatientData(1, 95.0, "BloodSaturation", 1000L);
        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);

        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));

        generator.evaluateData(patient);
        assertEquals("", outContent.toString());
    }
}