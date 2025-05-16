package com.cardio_generator.generators;

import java.util.Random;
import com.cardio_generator.outputs.OutputStrategy;

/**
 * A class that generates simulated alert events for patients in the cardiovascular data simulator.
 * This class implements the {@link PatientDataGenerator} interface and produces alert events that are either
 * "triggered" (indicating a critical health condition) or "resolved" (indicating the condition has stabilized).
 * Alerts are modeled using a state machine with a probabilistic transition mechanism, where triggered alerts
 * have a high chance of resolving, and new alerts are triggered based on a Poisson-like probability.
 *
 * <p>This class is used in the simulator to periodically generate alert events for each patient, which are then
 * output using the specified {@link OutputStrategy} (e.g., console, file, WebSocket, or TCP).</p>
 *
 * @author [Your Name or Author Name]
 */
public class AlertGenerator implements PatientDataGenerator {

    /** Random number generator for simulating probabilistic alert triggering and resolution. */
    public static final Random RANDOM_GENERATOR = new Random();

    /** Array tracking the alert state for each patient, indexed by patient ID. True indicates a triggered alert, false indicates resolved. */
    private boolean[] alertStates;

    /**
     * Constructs an {@code AlertGenerator} for the specified number of patients.
     * Initializes the alert state for each patient to false (resolved).
     *
     * @param patientCount The number of patients for whom to generate alerts. Must be a positive integer.
     */
    public AlertGenerator(int patientCount) {
        alertStates = new boolean[patientCount + 1];
    }

    /**
     * Generates an alert event for the specified patient and outputs it using the provided strategy.
     * If the patient has an active (triggered) alert, there is a 90% chance it will resolve, outputting "resolved".
     * If no alert is active, a new alert may be triggered based on a Poisson-like probability (controlled by a lambda of 0.1),
     * outputting "triggered". The alert state is updated accordingly.
     *
     * @param patientId      The unique identifier of the patient for whom to generate an alert. Must be a positive integer
     *                      within the range [1, patientCount] specified in the constructor.
     * @param outputStrategy The strategy used to output the generated alert (e.g., console, file, WebSocket, TCP).
     * @throws IllegalArgumentException If the patientId is invalid (e.g., non-positive or exceeds the patient count).
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            if (alertStates[patientId]) {
                if (RANDOM_GENERATOR.nextDouble() < 0.9) { // 90% chance to resolve
                    alertStates[patientId] = false;
                    outputStrategy.output(patientId, System.currentTimeMillis(), "Alert", "resolved");
                }
            } else {
                double lambda = 0.1; // Average rate (alerts per period), adjust based on desired frequency
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