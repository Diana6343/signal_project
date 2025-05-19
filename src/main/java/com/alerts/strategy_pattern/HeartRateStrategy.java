package com.alerts.strategy_pattern;


import com.alerts.Alert;
import com.alerts.decorator_pattern.ConcreteAlert;
import com.alerts.factory_pattern.AlertFactory;
import com.data_management.PatientRecord;
import java.util.List;
import java.util.logging.Logger;

public class HeartRateStrategy implements AlertStrategy {
    private static final Logger LOGGER = Logger.getLogger(HeartRateStrategy.class.getName());

    @Override
    public void checkAlert(List<PatientRecord> records, int patientId, AlertFactory alertFactory) {
        int windowSize = 10;
        for (int i = windowSize; i < records.size(); i++) {
            if (records.get(i).getRecordType().equals("ECG")) {
                double sum = 0;
                for (int j = i - windowSize; j < i; j++) {
                    sum += records.get(j).getMeasurementValue();
                }
                double avg = sum / windowSize;
                double current = records.get(i).getMeasurementValue();
                if (current > avg * 2) {
                    triggerAlert(alertFactory.createAlert(String.valueOf(patientId), "ECGPeak", records.get(i).getTimestamp()));
                }
            }
        }
    }

    private void triggerAlert(ConcreteAlert alert) {
        LOGGER.info("Alert triggered: Patient " + alert.getPatientId() + ", Condition: " +
                alert.getCondition() + ", Type: " + alert.getAlertType() + ", Timestamp: " + alert.getTimestamp());
    }
}
