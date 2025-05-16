package com.cardio_generator.generators;

import java.util.Random;
import com.cardio_generator.outputs.OutputStrategy;

/**
 * A class that generates simulated blood saturation data for patients in the cardiovascular data simulator.
 * This class implements the {@link PatientDataGenerator} interface and produces blood saturation values (in percentage)
 * with small, realistic fluctuations within a healthy range (90–100%). Each patient's data is tracked to ensure continuity
 * in subsequent generations.
 *
 * <p>This class is used in the simulator to periodically generate blood saturation data for each patient, which is then
 * output using the specified {@link OutputStrategy} (e.g., console, file, WebSocket, or TCP).</p>
 *
 * @author [Your Name or Author Name]
 */
public class BloodSaturationDataGenerator implements PatientDataGenerator {

    /** Random number generator for introducing small variations in blood saturation values. */
    private static final Random random = new Random();

    /** Array storing the most recent blood saturation value for each patient, indexed by patient ID. */
    private int[] lastSaturationValues;

    /**
     * Constructs a {@code BloodSaturationDataGenerator} for the specified number of patients.
     * Initializes baseline blood saturation values (between 95% and 100%) for each patient to ensure realistic starting points.
     *
     * @param patientCount The number of patients for whom to generate data. Must be a positive integer.
     */
    public BloodSaturationDataGenerator(int patientCount) {
        lastSaturationValues = new int[patientCount + 1];

        // Initialize with baseline saturation values for each patient
        for (int i = 1; i <= patientCount; i++) {
            lastSaturationValues[i] = 95 + random.nextInt(6); // Initializes with a value between 95 and 100
        }
    }

    /**
     * Generates a simulated blood saturation value for the specified patient and outputs it using the provided strategy.
     * The new value is based on the patient's previous saturation value with a small random variation (-1, 0, or +1),
     * constrained to a realistic range (90–100%). The generated data is formatted as a percentage string (e.g., "95%").
     *
     * @param patientId      The unique identifier of the patient for whom to generate data. Must be a positive integer
     *                      within the range [1, patientCount] specified in the constructor.
     * @param outputStrategy The strategy used to output the generated data (e.g., console, file, WebSocket, TCP).
     * @throws IllegalArgumentException If the patientId is invalid (e.g., non-positive or exceeds the patient count).
     */
    @Override
    public void generate(int patientId, OutputStrategy outputStrategy) {
        try {
            // Simulate blood saturation values
            int variation = random.nextInt(3) - 1; // -1, 0, or 1 to simulate small fluctuations
            int newSaturationValue = lastSaturationValues[patientId] + variation;

            // Ensure the saturation stays within a realistic and healthy range
            newSaturationValue = Math.min(Math.max(newSaturationValue, 90), 100);
            lastSaturationValues[patientId] = newSaturationValue;
            outputStrategy.output(patientId, System.currentTimeMillis(), "Saturation",
                    Double.toString(newSaturationValue) + "%");
        } catch (Exception e) {
            System.err.println("An error occurred while generating blood saturation data for patient " + patientId);
            e.printStackTrace(); // This will print the stack trace to help identify where the error occurred.
        }
    }
}