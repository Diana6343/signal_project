package com.data_management;

import com.alerts.AlertGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Manages storage and retrieval of patient data in a healthcare monitoring system.
 * Supports real-time data updates with thread-safe operations.
 */
public class DataStorage {
    private static final Logger LOGGER = Logger.getLogger(DataStorage.class.getName());
    private final Map<Integer, Patient> patientMap;

    public DataStorage() {
        this.patientMap = new HashMap<>();
    }

    /**
     * Adds or updates patient data in a thread-safe manner.
     *
     * @param patientId        the unique identifier of the patient
     * @param measurementValue the value of the health metric
     * @param recordType       the type of record (e.g., BloodPressureSystolic)
     * @param timestamp        the time of measurement in milliseconds
     */
    public synchronized void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
        Patient patient = patientMap.computeIfAbsent(patientId, Patient::new);
        patient.addRecord(measurementValue, recordType, timestamp);
    }

    /**
     * Retrieves patient records within a time range.
     *
     * @param patientId the patient ID
     * @param startTime the start of the time range
     * @param endTime   the end of the time range
     * @return a list of PatientRecord objects
     */
    public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        Patient patient = patientMap.get(patientId);
        return patient != null ? patient.getRecords(startTime, endTime) : new ArrayList<>();
    }

    /**
     * Retrieves all patients in the storage.
     *
     * @return a list of all patients
     */
    public List<Patient> getAllPatients() {
        return new ArrayList<>(patientMap.values());
    }

    public static void main(String[] args) {
        DataStorage dataStorage = new DataStorage();
        AlertGenerator alertGenerator = new AlertGenerator(dataStorage);
        try {
            DataReader reader = new WebSocketClient("ws://localhost:8080");
            reader.readData(dataStorage);
            // Continuously evaluate data for alerts
            new Thread(() -> {
                while (true) {
                    for (Patient patient : dataStorage.getAllPatients()) {
                        alertGenerator.evaluateData(patient);
                    }
                    try {
                        Thread.sleep(1000); // Check every second
                    } catch (InterruptedException e) {
                        LOGGER.severe("Alert evaluation interrupted: " + e.getMessage());
                        break;
                    }
                }
            }).start();
        } catch (Exception e) {
            LOGGER.severe("Failed to start WebSocketClient: " + e.getMessage());
        }
    }
}