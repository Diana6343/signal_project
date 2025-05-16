package com.cardio_generator.generators;

import java.util.Random;

import com.cardio_generator.outputs.OutputStrategy;

public class AlertGenerator implements PatientDataGenerator {

    // Changed constant name to UPPER_SNAKE_CASE to comply with Google Java Style Guide
    public static final Random RANDOM_GENERATOR = new Random();

    // Changed variable name to camelCase to comply with Google Java Style Guide
    private boolean[] alertStates; // false = resolved, true = pressed

    public AlertGenerator(int patientCount) {
        // Changed variable name to camelCase to comply with Google Java Style Guide
        alertStates = new boolean[patientCount + 1];
    }

    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (RANDOM_GENERATOR.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                // Changed variable name to camelCase to comply with Google Java Style Guide
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency

                // Changed variable name to camelCase to comply with Google Java Style Guide
                double probability = -Math.expm1(-lambda);

                boolean alertTriggered = RANDOM_GENERATOR.nextDouble() < probability;
                if (alertTriggered) {
                    alertStates[patientId] = true;
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "triggered");
                }
            }
        } catch (Exception e) {
            System.err.println("An error occurred while generating alert data for patient " + patientId);
            e.printStackTrace();
        }
    }
}
