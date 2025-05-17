package com.data_management;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

class PatientTest {
    @Test
    void testGetRecordsInTimeRange() {
        Patient patient = new Patient(1);
        patient.addRecord(100.0, "BloodPressure", 1000L);
        patient.addRecord(200.0, "BloodPressure", 2000L);
        patient.addRecord(300.0, "BloodPressure", 3000L);
        List<PatientRecord> records = patient.getRecords(1500L, 2500L);
        assertEquals(1, records.size());
        assertEquals(200.0, records.get(0).getMeasurementValue());
    }

    @Test
    void testGetRecordsEmptyRange() {
        Patient patient = new Patient(1);
        patient.addRecord(100.0, "BloodPressure", 1000L);
        List<PatientRecord> records = patient.getRecords(2000L, 3000L);
        assertTrue(records.isEmpty());
    }

    @Test
    void testAddRecord() {
        Patient patient = new Patient(1);
        patient.addRecord(100.0, "BloodPressure", 1000L);
        List<PatientRecord> records = patient.getRecords(0L, Long.MAX_VALUE);
        assertEquals(1, records.size());
        assertEquals(100.0, records.get(0).getMeasurementValue());
        assertEquals("BloodPressure", records.get(0).getRecordType());
        assertEquals(1000L, records.get(0).getTimestamp());
    }
}