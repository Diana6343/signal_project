package com.data_management.data_reader_impl;


import com.data_management.DataReader;
import com.data_management.DataStorage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Logger;

/**
 * A DataReader implementation that reads patient data from a CSV file.
 * The CSV file is expected to have the format: patientId,measurementValue,recordType,timestamp.
 */
public class FileDataReader implements DataReader {
    private static final Logger LOGGER = Logger.getLogger(FileDataReader.class.getName());
    private final String filePath;
    private BufferedReader reader;

    /**
     * Constructs a FileDataReader for the specified CSV file.
     *
     * @param filePath the path to the CSV file
     */
    public FileDataReader(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        reader = new BufferedReader(new FileReader(filePath));
        String line;
        boolean isFirstLine = true;
        while ((line = reader.readLine()) != null) {
            if (isFirstLine) {
                isFirstLine = false;
                continue; // Skip header
            }
            try {
                String[] parts = line.split(",");
                if (parts.length != 4) {
                    LOGGER.warning("Invalid line format: " + line);
                    continue;
                }
                int patientId = Integer.parseInt(parts[0]);
                double measurementValue = Double.parseDouble(parts[1]);
                String recordType = parts[2];
                long timestamp = Long.parseLong(parts[3]);
                dataStorage.addPatientData(patientId, measurementValue, recordType, timestamp);
            } catch (NumberFormatException e) {
                LOGGER.warning("Invalid number format in line: " + line);
            }
        }
    }

    @Override
    public void close() throws IOException {
        if (reader != null) {
            reader.close();
            LOGGER.info("FileDataReader closed for: " + filePath);
        }
    }
}