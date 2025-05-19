package com.alerts.strategy_pattern;


import com.alerts.factory_pattern.AlertFactory;
import com.data_management.PatientRecord;
import java.util.List;

public interface AlertStrategy {
    void checkAlert(List<PatientRecord> records, int patientId, AlertFactory alertFactory);
}
