package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ConcurrentHashMap;

/**
 * An implementation of the {@link OutputStrategy} interface that writes patient health data to text files.
 * Each data type (label) is stored in a separate file within the specified base directory, with data appended in a
 * thread-safe manner. The class ensures the base directory is created if it does not exist and maps labels to file
 * paths for efficient access.
 *
 * <p>This class is used in the cardiovascular data simulator when the output is configured to write to files
 * (e.g., using the "--output file:<directory>" command-line argument).</p>
 *
 * @author [Your Name or Author Name]
 */
public class FileOutputStrategy implements OutputStrategy {

    /** The base directory where output files are stored. */
    private String baseDirectory;

    /** A thread-safe map associating data labels with their corresponding file paths. */
    public final ConcurrentHashMap<String, String> fileMap = new ConcurrentHashMap<>();

    /**
     * Constructs a {@code FileOutputStrategy} with the specified base directory for storing output files.
     *
     * @param baseDirectory The path to the directory where output files will be written. Must be a valid directory path.
     */
    public FileOutputStrategy(String baseDirectory) {
        this.baseDirectory = baseDirectory;
    }

    /**
     * Outputs patient health data to a text file corresponding to the specified label.
     * The data is appended to a file named "<label>.txt" in the base directory, with the format:
     * "Patient ID: <patientId>, Timestamp: <timestamp>, Label: <label>, Data: <data>".
     * The base directory is created if it does not exist, and file access is thread-safe.
     *
     * @param patientId  The unique identifier of the patient associated with the data. Must be a positive integer.
     * @param timestamp  The time at which the data was generated, represented as milliseconds since epoch.
     * @param label      A string identifying the type of data (e.g., "ECG", "BloodPressure", "Alert").
     * @param data       The actual health data to output, formatted as a string (e.g., "120/80" for blood pressure).
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        try {
            Files.createDirectories(Paths.get(baseDirectory));
        } catch (IOException e) {
            System.err.println("Error creating base directory: " + e.getMessage());
            return;
        }

        String filePath = fileMap.computeIfAbsent(label, k -> Paths.get(baseDirectory, label + ".txt").toString());

        try (PrintWriter out = new PrintWriter(
                Files.newBufferedWriter(Paths.get(filePath), StandardOpenOption.CREATE, StandardOpenOption.APPEND))) {
            out.printf("Patient ID: %d, Timestamp: %d, Label: %s, Data: %s%n", patientId, timestamp, label, data);
        } catch (Exception e) {
            System.err.println("Error writing to file " + filePath + ": " + e.getMessage());
        }
    }
}