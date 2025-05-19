package com.data_management;

import com.alerts.AlertGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class DataStorage {
    private static final Logger LOGGER = Logger.getLogger(DataStorage.class.getName());
    private static DataStorage instance;
    private final Map<Integer, Patient> patientMap;

    public DataStorage() {
        this.patientMap = new HashMap<>();
    }

    public static synchronized DataStorage getInstance() {
        if (instance == null) {
            instance = new DataStorage();
        }
        return instance;
    }

    public synchronized void addPatientData(int patientId, double measurementValue, String recordType, long timestamp) {
        Patient patient = patientMap.computeIfAbsent(patientId, Patient::new);
        patient.addRecord(measurementValue, recordType, timestamp);
    }

    public List<PatientRecord> getRecords(int patientId, long startTime, long endTime) {
        Patient patient = patientMap.get(patientId);
        return patient != null ? patient.getRecords(startTime, endTime) : new ArrayList<>();
    }

    public List<Patient> getAllPatients() {
        return new ArrayList<>(patientMap.values());
    }

    public static void main(String[] args) {
        DataStorage dataStorage = DataStorage.getInstance();
        AlertGenerator alertGenerator = new AlertGenerator(dataStorage);
        try {
            DataReader reader = new WebSocketClient("ws://localhost:8080");
            reader.readData(dataStorage);
            new Thread(() -> {
                while (true) {
                    for (Patient patient : dataStorage.getAllPatients()) {
                        alertGenerator.evaluateData(patient);
                    }
                    try {
                        Thread.sleep(1000);
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