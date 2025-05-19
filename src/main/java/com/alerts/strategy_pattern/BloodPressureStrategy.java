package com.alerts.strategy_pattern;


import com.alerts.Alert;
import com.alerts.decorator_pattern.ConcreteAlert;
import com.alerts.factory_pattern.AlertFactory;
import com.data_management.PatientRecord;
import java.util.List;
import java.util.logging.Logger;

public class BloodPressureStrategy implements AlertStrategy {
    private static final Logger LOGGER = Logger.getLogger(BloodPressureStrategy.class.getName());

    @Override
    public void checkAlert(List<PatientRecord> records, int patientId, AlertFactory alertFactory) {
        // Trend Alert
        for (int i = 2; i < records.size(); i++) {
            if (records.get(i).getRecordType().startsWith("BloodPressure")) {
                double curr = records.get(i).getMeasurementValue();
                double prev1 = records.get(i-1).getMeasurementValue();
                double prev2 = records.get(i-2).getMeasurementValue();
                if ((curr - prev1 > 10 && prev1 - prev2 > 10) || (curr - prev1 < -10 && prev1 - prev2 < -10)) {
                    triggerAlert(alertFactory.createAlert(String.valueOf(patientId), "BloodPressureTrend", System.currentTimeMillis()));
                }
            }
        }
        // Critical Threshold Alert
        for (PatientRecord record : records) {
            if (record.getRecordType().equals("BloodPressureSystolic")) {
                double value = record.getMeasurementValue();
                if (value > 180 || value < 90) {
                    triggerAlert(alertFactory.createAlert(String.valueOf(patientId), "CriticalSystolic", record.getTimestamp()));
                }
            } else if (record.getRecordType().equals("BloodPressureDiastolic")) {
                double value = record.getMeasurementValue();
                if (value > 120 || value < 60) {
                    triggerAlert(alertFactory.createAlert(String.valueOf(patientId), "CriticalDiastolic", record.getTimestamp()));
                }
            }
        }
    }

    private void triggerAlert(ConcreteAlert alert) {
        LOGGER.info("Alert triggered: Patient " + alert.getPatientId() + ", Condition: " +
                alert.getCondition() + ", Type: " + alert.getAlertType() + ", Timestamp: " + alert.getTimestamp());
    }
}
