package com.alerts.strategy_pattern;

import com.alerts.Alert;
import com.alerts.decorator_pattern.ConcreteAlert;
import com.alerts.factory_pattern.AlertFactory;
import com.data_management.PatientRecord;
import java.util.List;
import java.util.logging.Logger;

public class OxygenSaturationStrategy implements AlertStrategy {
    private static final Logger LOGGER = Logger.getLogger(OxygenSaturationStrategy.class.getName());

    @Override
    public void checkAlert(List<PatientRecord> records, int patientId, AlertFactory alertFactory) {
        // Low Saturation Alert
        for (PatientRecord record : records) {
            if (record.getRecordType().equals("BloodSaturation") && record.getMeasurementValue() < 92) {
                triggerAlert(alertFactory.createAlert(String.valueOf(patientId), "LowSaturation", record.getTimestamp()));
            }
        }
        // Rapid Drop Alert
        for (int i = 1; i < records.size(); i++) {
            if (records.get(i).getRecordType().equals("BloodSaturation") &&
                    records.get(i-1).getRecordType().equals("BloodSaturation")) {
                PatientRecord curr = records.get(i);
                PatientRecord prev = records.get(i-1);
                if (curr.getTimestamp() - prev.getTimestamp() <= 600000 &&
                        prev.getMeasurementValue() - curr.getMeasurementValue() >= 5) {
                    triggerAlert(alertFactory.createAlert(String.valueOf(patientId), "RapidDropSaturation", curr.getTimestamp()));
                }
            }
        }
    }

    private void triggerAlert(ConcreteAlert alert) {
        LOGGER.info("Alert triggered: Patient " + alert.getPatientId() + ", Condition: " +
                alert.getCondition() + ", Type: " + alert.getAlertType() + ", Timestamp: " + alert.getTimestamp());
    }
}