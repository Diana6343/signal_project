package com.data_management;

import com.alerts.AlertGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntegrationTest.class);
    private static final int TEST_PORT = 8083;
    private static final long TIMEOUT_MS = 1000; // 1 second timeout
    private DataStorage dataStorage;
    private WebSocketClient client;
    private AlertGenerator alertGenerator;
    private WebSocketClientTest.TestWebSocketServer server;
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;
    private PrintStream originalErr;

    @BeforeEach
    void setUp() throws Exception {
        // Initialize dependencies
        dataStorage = DataStorage.getInstance();
        alertGenerator = new AlertGenerator(dataStorage);
        server = new WebSocketClientTest.TestWebSocketServer(new InetSocketAddress(TEST_PORT));
        server.start();
        client = new WebSocketClient("ws://localhost:" + TEST_PORT);

        // Set up output capture
        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        originalErr = System.err;
        PrintStream newStream = new PrintStream(outContent);
        System.setOut(newStream);
        System.setErr(newStream); // Capture System.err as well
        System.out.println("Debug: System.out redirected for test");

        LOGGER.info("Test setup completed, server started on port {}", TEST_PORT);
    }

    @AfterEach
    void tearDown() throws Exception {
        // Clean up resources safely
        if (client != null) {
            try {
                client.close();
            } catch (Exception e) {
                LOGGER.warn("Failed to close WebSocketClient: {}", e.getMessage());
            }
        }
        if (server != null) {
            try {
                server.stop(1000); // Stop with 1-second timeout
            } catch (Exception e) {
                LOGGER.warn("Failed to stop TestWebSocketServer: {}", e.getMessage());
            }
        }
        // Restore output streams
        System.setOut(originalOut);
        System.setErr(originalErr);
        // Clear DataStorage to avoid interference
        LOGGER.info("Test teardown completed");
    }

    @Test
    void testRealTimeDataToAlert() throws Exception {
        // Start reading data
        client.readData(dataStorage);

        // Simulate real-time data
        server.broadcast("1,1000,BloodPressureSystolic,190");

        // Wait for data to be processed (polling-based synchronization)
        long startTime = System.currentTimeMillis();
        while (dataStorage.getAllPatients().isEmpty() && (System.currentTimeMillis() - startTime) < TIMEOUT_MS) {
            Thread.sleep(10); // Short sleep to avoid busy-waiting
        }

        // Verify data was received
        List<Patient> patients = dataStorage.getAllPatients();
        assertFalse(patients.isEmpty(), "No patients found in DataStorage after timeout");

        // Evaluate data to trigger alert
        alertGenerator.evaluateData(patients.get(0));
        String output = outContent.toString().trim();
        LOGGER.info("Console output: {}", output);
        assertTrue(output.contains("Alert triggered: Patient 1, Condition: CriticalSystolic"),
                "Expected 'Alert triggered: Patient 1, Condition: CriticalSystolic' in output, but got: " + output);

    }
}