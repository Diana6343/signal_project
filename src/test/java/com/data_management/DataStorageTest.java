package com.data_management;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataStorageTest {
    @Test
    void testAddAndGetRecords() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 100.0, "BloodPressure", 1714376789050L);
        storage.addPatientData(1, 200.0, "BloodPressure", 1714376789051L);
        List<PatientRecord> records = storage.getRecords(1, 1714376789050L, 1714376789051L);
        assertEquals(2, records.size());
        assertEquals(100.0, records.get(0).getMeasurementValue());
        assertEquals(200.0, records.get(1).getMeasurementValue());
    }

    @Test
    void testGetRecordsNonExistentPatient() {
        DataStorage storage = new DataStorage();
        List<PatientRecord> records = storage.getRecords(999, 0L, Long.MAX_VALUE);
        assertTrue(records.isEmpty());
    }

    @Test
    void testGetRecordsInvalidTimeRange() {
        DataStorage storage = new DataStorage();
        storage.addPatientData(1, 100.0, "BloodPressure", 1000L);
        List<PatientRecord> records = storage.getRecords(1, 2000L, 1000L);
        assertTrue(records.isEmpty());
    }

}