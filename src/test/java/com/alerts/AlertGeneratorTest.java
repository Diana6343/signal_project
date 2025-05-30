package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class AlertGeneratorTest {
    private DataStorage storage;
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;
    private PrintStream originalErr;

    @BeforeEach
    void setUp() {
        storage = DataStorage.getInstance();
        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        originalErr = System.err;
        PrintStream newStream = new PrintStream(outContent);
        System.setOut(newStream);
        System.setErr(newStream); // Capture System.err as well
        System.out.println("Debug: System.out redirected for test");
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testBloodPressureCriticalAlert() {
        storage.addPatientData(1, 190.0, "BloodPressureSystolic", 1000L);
        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);
        String output = outContent.toString().trim();
        assertTrue(output.contains("Alert triggered: Patient 1, Condition: CriticalSystolic"),
                "Expected 'Alert triggered: Patient 1, Condition: CriticalSystolic' in output, but got: '" + output + "'");
    }

    @Test
    void testBloodSaturationLowAlert() {
        storage.addPatientData(1, 90.0, "BloodSaturation", 1000L);
        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);
        String output = outContent.toString().trim();
        assertTrue(output.contains("Alert triggered: Patient 1, Condition: LowSaturation"),
                "Expected 'Alert triggered: Patient 1, Condition: LowSaturation' in output, but got: '" + output + "'");
    }

    @Test
    void testNoAlertsForNormalData() {
        storage.addPatientData(1, 120.0, "BloodPressureSystolic", 1000L);
        storage.addPatientData(1, 95.0, "BloodSaturation", 1000L);
        AlertGenerator generator = new AlertGenerator(storage);
        Patient patient = storage.getAllPatients().get(0);
        generator.evaluateData(patient);
        String output = outContent.toString().trim();
        assertFalse(output.contains("Alert triggered"),
                "Expected no alerts, but got: '" + output + "'");
    }
}