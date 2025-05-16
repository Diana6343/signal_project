package com.cardio_generator.generators;

import com.cardio_generator.outputs.OutputStrategy;

/**
 * An interface defining a contract for generating patient health data in the cardiovascular data simulator.
 * Implementing classes are responsible for producing specific types of health data (e.g., ECG, blood pressure,
 * blood saturation) for a given patient and sending the data through a specified output strategy.
 *
 * <p>This interface is used by data generator classes to ensure a consistent method for generating and outputting
 * patient data, enabling flexible integration with various output mechanisms such as console, file, WebSocket, or TCP.</p>
 *
 * @author [Your Name or Author Name]
 */
public interface PatientDataGenerator {

    /**
     * Generates health data for a specific patient and sends it through the provided output strategy.
     * Implementing classes define the type of data generated (e.g., ECG, blood pressure) and the format
     * in which it is sent to the output strategy.
     *
     * @param patientId      The unique identifier of the patient for whom data is generated. Must be a positive integer.
     * @param outputStrategy The strategy used to output the generated data (e.g., console, file, WebSocket, TCP).
     */
    void generate(int patientId, OutputStrategy outputStrategy);
}