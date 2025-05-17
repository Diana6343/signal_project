package com.data_management.data_reader_impl;

import com.data_management.DataStorage;
import com.data_management.PatientRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class FileDataReaderTest {
    @Test
    void testReadData(@TempDir Path tempDir) throws IOException {
        Path tempFile = tempDir.resolve("dataPatient.csv");
        Files.writeString(tempFile,
                "patientId,measurementValue,recordType,timestamp\n" +
                        "1,120.0,BloodPressure,1700000000000\n" +
                        "1,90.0,BloodSaturation,1700000000001");

        DataStorage storage = new DataStorage();
        FileDataReader reader = new FileDataReader(tempFile.toString());
        reader.readData(storage);

        List<PatientRecord> records = storage.getRecords(1, 0L, Long.MAX_VALUE);
        assertEquals(2, records.size());
        assertEquals(120.0, records.get(0).getMeasurementValue());
        assertEquals("BloodPressure", records.get(0).getRecordType());
        assertEquals(90.0, records.get(1).getMeasurementValue());
        assertEquals("BloodSaturation", records.get(1).getRecordType());
    }

    @Test
    void testReadInvalidFile(@TempDir Path tempDir) {
        FileDataReader reader = new FileDataReader(tempDir.resolve("nonexistent.csv").toString());
        DataStorage storage = new DataStorage();
        assertThrows(IOException.class, () -> reader.readData(storage));
    }
}