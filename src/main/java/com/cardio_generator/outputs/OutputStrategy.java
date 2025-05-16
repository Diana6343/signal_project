package com.cardio_generator.outputs;

/**
 * An interface defining a contract for outputting patient health data in the cardiovascular data simulator.
 * Implementing classes handle the specific mechanism for sending data, such as to the console, files, WebSocket, or TCP connections.
 *
 * <p>This interface ensures a consistent method for outputting simulated health data, enabling the simulator to support multiple output strategies
 * without modifying the core data generation logic.</p>
 *
 * @author [Your Name or Author Name]
 */
public interface OutputStrategy {

    /**
     * Outputs patient health data using the specified output mechanism.
     * Implementing classes define how the data is formatted and sent based on the provided parameters.
     *
     * @param patientId  The unique identifier of the patient associated with the data. Must be a positive integer.
     * @param timestamp  The time at which the data was generated, represented as milliseconds since epoch.
     * @param label      A string identifying the type of data (e.g., "ECG", "BloodPressure", "Alert").
     * @param data       The actual health data to output, formatted as a string (e.g., "120/80" for blood pressure).
     */
    void output(int patientId, long timestamp, String label, String data);
}