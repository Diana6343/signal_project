package com.alerts.strategy_pattern;

import com.alerts.factory_pattern.BloodOxygenAlertFactory;
import com.alerts.factory_pattern.BloodPressureAlertFactory;
import com.alerts.factory_pattern.ECGAlertFactory;
import com.data_management.DataStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class AlertStrategyTest {
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
    void testBloodPressureStrategy() {
        storage.addPatientData(1, 190.0, "BloodPressureSystolic", 1000L);
        AlertStrategy strategy = new BloodPressureStrategy();
        strategy.checkAlert(storage.getRecords(1, 0, Long.MAX_VALUE), 1, new BloodPressureAlertFactory());
        String output = outContent.toString().trim();
        assertTrue(output.contains("Alert triggered: Patient 1, Condition: CriticalSystolic"),
                "Expected 'Alert triggered: Patient 1, Condition: CriticalSystolic' in output, but got: '" + output + "'");
    }

    @Test
    void testOxygenSaturationStrategy() {
        storage.addPatientData(1, 90.0, "BloodSaturation", 1000L);
        AlertStrategy strategy = new OxygenSaturationStrategy();
        strategy.checkAlert(storage.getRecords(1, 0, Long.MAX_VALUE), 1, new BloodOxygenAlertFactory());
        String output = outContent.toString().trim();
        assertTrue(output.contains("Alert triggered: Patient 1, Condition: LowSaturation"),
                "Expected 'Alert triggered: Patient 1, Condition: LowSaturation' in output, but got: '" + output + "'");
    }

    @Test
    void testHeartRateStrategy() {
        for (int i = 0; i < 10; i++) {
            storage.addPatientData(1, 1.0, "ECG", 1000L + i);
        }
        storage.addPatientData(1, 5.0, "ECG", 1010L);
        AlertStrategy strategy = new HeartRateStrategy();
        strategy.checkAlert(storage.getRecords(1, 0, Long.MAX_VALUE), 1, new ECGAlertFactory());
        String output = outContent.toString().trim();
        assertTrue(output.contains("Alert triggered: Patient 1, Condition: ECGPeak"),
                "Expected 'Alert triggered: Patient 1, Condition: ECGPeak' in output, but got: '" + output + "'");
    }
}