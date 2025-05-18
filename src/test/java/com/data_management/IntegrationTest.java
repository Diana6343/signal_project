package com.data_management;

import com.alerts.AlertGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.net.InetSocketAddress;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class IntegrationTest {
    private static final Logger LOGGER = Logger.getLogger(IntegrationTest.class.getName());
    private static final int TEST_PORT = 8083; // Change if port 8083 is in use
    private DataStorage dataStorage;
    private WebSocketClient client;
    private AlertGenerator alertGenerator;
    private WebSocketClientTest.TestWebSocketServer server;
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @BeforeEach
    void setUp() throws Exception {
        dataStorage = new DataStorage();
        alertGenerator = new AlertGenerator(dataStorage);
        server = new WebSocketClientTest.TestWebSocketServer(new InetSocketAddress(TEST_PORT));
        server.start();
        client = new WebSocketClient("ws://localhost:" + TEST_PORT);
        System.setOut(new PrintStream(outContent));
        LOGGER.info("Test setup completed, server started on port " + TEST_PORT);
    }

    @AfterEach
    void tearDown() throws Exception {
        client.close();
        server.stop();
        System.setOut(System.out);
        LOGGER.info("Test teardown completed");
    }

    @Test
    void testRealTimeDataToAlert() throws Exception {
        client.readData(dataStorage);
        server.broadcast("1,1700000000000,BloodPressureSystolic,190");
        try {
            Thread.sleep(100); // Wait for message processing
        } catch (InterruptedException e) {
            fail("Interrupted during test", e);
        }
        assertFalse(dataStorage.getAllPatients().isEmpty(), "No patients found in DataStorage");
        alertGenerator.evaluateData(dataStorage.getAllPatients().get(0));
        String output = outContent.toString();
        LOGGER.info("Console output: " + output);
        assertTrue(output.contains("Alert triggered: Patient 1, Condition: CriticalSystolic"),
                "Expected 'Alert triggered: Patient 1, Condition: CriticalSystolic' in output, but got: " + output);
    }
}