package com.alerts;

import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;

/**
 * The {@code AlertGenerator} class is responsible for monitoring patient data
 * and generating alerts when certain predefined conditions are met. This class
 * relies on a {@link DataStorage} instance to access patient data and evaluate
 * it against specific health criteria.
 */
public class AlertGenerator {
    private DataStorage dataStorage;

    /**
     * Constructs an {@code AlertGenerator} with a specified {@code DataStorage}.
     * The {@code DataStorage} is used to retrieve patient data that this class
     * will monitor and evaluate.
     *
     * @param dataStorage the data storage system that provides access to patient
     *                    data
     */
    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
    }

    /**
     * Evaluates the specified patient's data to determine if any alert conditions
     * are met. If a condition is met, an alert is triggered via the
     * {@link #triggerAlert}
     * method. This method should define the specific conditions under which an
     * alert
     * will be triggered.
     *
     * @param patient the patient data to evaluate for alert conditions
     *                Analyzes patient data and generates alerts
     *
     */
    public void evaluateData(Patient patient) {
        List<PatientRecord> records = dataStorage.getRecords(patient.getPatientId(), 0, Long.MAX_VALUE);

        checkBloodPressureAlerts(records, patient.getPatientId());
        checkBloodSaturationAlerts(records, patient.getPatientId());
        checkHypotensiveHypoxemiaAlert(records, patient.getPatientId());
        checkECGAlerts(records, patient.getPatientId());
        checkTriggeredAlerts(records, patient.getPatientId());
    }

    private void checkBloodPressureAlerts(List<PatientRecord> records, int patientId) {
        // Trend Alert (Systolic or Diastolic)
        for (int i = 2; i < records.size(); i++) {
            if (records.get(i).getRecordType().startsWith("BloodPressure")) {
                double curr = records.get(i).getMeasurementValue();
                double prev1 = records.get(i-1).getMeasurementValue();
                double prev2 = records.get(i-2).getMeasurementValue();
                if ((curr - prev1 > 10 && prev1 - prev2 > 10) || (curr - prev1 < -10 && prev1 - prev2 < -10)) {
                    triggerAlert(new Alert(String.valueOf(patientId), "BloodPressureTrend", System.currentTimeMillis()));
                }
            }
        }
        // Critical Threshold Alert
        for (PatientRecord record : records) {
            if (record.getRecordType().equals("BloodPressureSystolic")) {
                double value = record.getMeasurementValue();
                if (value > 180 || value < 90) {
                    triggerAlert(new Alert(String.valueOf(patientId), "CriticalSystolic", record.getTimestamp()));
                }
            } else if (record.getRecordType().equals("BloodPressureDiastolic")) {
                double value = record.getMeasurementValue();
                if (value > 120 || value < 60) {
                    triggerAlert(new Alert(String.valueOf(patientId), "CriticalDiastolic", record.getTimestamp()));
                }
            }
        }
    }

    private void checkBloodSaturationAlerts(List<PatientRecord> records, int patientId) {
        // Low Saturation Alert
        for (PatientRecord record : records) {
            if (record.getRecordType().equals("BloodSaturation") && record.getMeasurementValue() < 92) {
                triggerAlert(new Alert(String.valueOf(patientId), "LowSaturation", record.getTimestamp()));
            }
        }
        // Rapid Drop Alert
        for (int i = 1; i < records.size(); i++) {
            if (records.get(i).getRecordType().equals("BloodSaturation") &&
                    records.get(i-1).getRecordType().equals("BloodSaturation")) {
                PatientRecord curr = records.get(i);
                PatientRecord prev = records.get(i-1);
                if (curr.getTimestamp() - prev.getTimestamp() <= 600000 && // 10 minutes
                        prev.getMeasurementValue() - curr.getMeasurementValue() >= 5) {
                    triggerAlert(new Alert(String.valueOf(patientId), "RapidDropSaturation", curr.getTimestamp()));
                }
            }
        }
    }

    private void checkHypotensiveHypoxemiaAlert(List<PatientRecord> records, int patientId) {
        for (PatientRecord bpRecord : records) {
            if (bpRecord.getRecordType().equals("BloodPressureSystolic") && bpRecord.getMeasurementValue() < 90) {
                for (PatientRecord satRecord : records) {
                    if (satRecord.getRecordType().equals("BloodSaturation") &&
                            satRecord.getMeasurementValue() < 92 &&
                            Math.abs(bpRecord.getTimestamp() - satRecord.getTimestamp()) < 60000) { // Within 1 minute
                        triggerAlert(new Alert(String.valueOf(patientId), "HypotensiveHypoxemia", bpRecord.getTimestamp()));
                    }
                }
            }
        }
    }

    private void checkECGAlerts(List<PatientRecord> records, int patientId) {
        int windowSize = 10;
        for (int i = windowSize; i < records.size(); i++) {
            if (records.get(i).getRecordType().equals("ECG")) {
                double sum = 0;
                for (int j = i - windowSize; j < i; j++) {
                    sum += records.get(j).getMeasurementValue();
                }
                double avg = sum / windowSize;
                double current = records.get(i).getMeasurementValue();
                if (current > avg * 2) { // Peak > 2x average
                    triggerAlert(new Alert(String.valueOf(patientId), "ECGPeak", records.get(i).getTimestamp()));
                }
            }
        }
    }

    private void checkTriggeredAlerts(List<PatientRecord> records, int patientId) {
        for (PatientRecord record : records) {
            if (record.getRecordType().equals("Alert")) {
                triggerAlert(new Alert(String.valueOf(patientId), "TriggeredAlert", record.getTimestamp()));
            }
        }
    }


    /**
     * Triggers an alert for the monitoring system. This method can be extended to
     * notify medical staff, log the alert, or perform other actions. The method
     * currently assumes that the alert information is fully formed when passed as
     * an argument.
     *
     * @param alert the alert object containing details about the alert condition
     */
    private void triggerAlert(Alert alert) {
        System.out.println("Alert triggered: Patient " + alert.getPatientId() + ", Condition: " +
                alert.getCondition() + ", Timestamp: " + alert.getTimestamp());
    }



}
