package com.alerts;

import com.alerts.decorator_pattern.ConcreteAlert;
import com.alerts.factory_pattern.AlertFactory;
import com.alerts.factory_pattern.BloodOxygenAlertFactory;
import com.alerts.factory_pattern.BloodPressureAlertFactory;
import com.alerts.factory_pattern.ECGAlertFactory;
import com.alerts.strategy_pattern.AlertStrategy;
import com.alerts.strategy_pattern.BloodPressureStrategy;
import com.alerts.strategy_pattern.HeartRateStrategy;
import com.alerts.strategy_pattern.OxygenSaturationStrategy;
import com.data_management.DataStorage;
import com.data_management.Patient;
import com.data_management.PatientRecord;

import java.util.List;
import java.util.logging.Logger;

public class AlertGenerator {
    private static final Logger LOGGER = Logger.getLogger(AlertGenerator.class.getName());
    private DataStorage dataStorage;
    private List<AlertStrategy> strategies;

    public AlertGenerator(DataStorage dataStorage) {
        this.dataStorage = dataStorage;
        this.strategies = List.of(
                new BloodPressureStrategy(),
                new OxygenSaturationStrategy(),
                new HeartRateStrategy()
        );
    }

    public void evaluateData(Patient patient) {
        List<PatientRecord> records = dataStorage.getRecords(patient.getPatientId(), 0, Long.MAX_VALUE);
        int patientId = patient.getPatientId();
        AlertFactory bpFactory = new BloodPressureAlertFactory();
        AlertFactory oxygenFactory = new BloodOxygenAlertFactory();
        AlertFactory ecgFactory = new ECGAlertFactory();

        for (AlertStrategy strategy : strategies) {
            if (strategy instanceof BloodPressureStrategy) {
                strategy.checkAlert(records, patientId, bpFactory);
            } else if (strategy instanceof OxygenSaturationStrategy) {
                strategy.checkAlert(records, patientId, oxygenFactory);
            } else if (strategy instanceof HeartRateStrategy) {
                strategy.checkAlert(records, patientId, ecgFactory);
            }
        }

        // Hypotensive Hypoxemia Alert
        for (PatientRecord bpRecord : records) {
            if (bpRecord.getRecordType().equals("BloodPressureSystolic") && bpRecord.getMeasurementValue() < 90) {
                for (PatientRecord satRecord : records) {
                    if (satRecord.getRecordType().equals("BloodSaturation") &&
                            satRecord.getMeasurementValue() < 92 &&
                            Math.abs(bpRecord.getTimestamp() - satRecord.getTimestamp()) < 60000) {
                        ConcreteAlert alert = bpFactory.createAlert(String.valueOf(patientId), "HypotensiveHypoxemia", bpRecord.getTimestamp());
                        LOGGER.info("Alert triggered: Patient " + alert.getPatientId() + ", Condition: " +
                                alert.getCondition() + ", Type: " + alert.getAlertType() + ", Timestamp: " + alert.getTimestamp());
                    }
                }
            }
        }

        // Triggered Alerts
        for (PatientRecord record : records) {
            if (record.getRecordType().equals("Alert")) {
                ConcreteAlert alert = bpFactory.createAlert(String.valueOf(patientId), "TriggeredAlert", record.getTimestamp());
                LOGGER.info("Alert triggered: Patient " + alert.getPatientId() + ", Condition: " +
                        alert.getCondition() + ", Type: " + alert.getAlertType() + ", Timestamp: " + alert.getTimestamp());
            }
        }
    }
}