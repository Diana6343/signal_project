package com.data_management;

import org.java_websocket.server.WebSocketServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

class WebSocketClientTest {
    private static final Logger LOGGER = Logger.getLogger(WebSocketClientTest.class.getName());
    private DataStorage dataStorage;
    private WebSocketClient client;
    private TestWebSocketServer server;

    @BeforeEach
    void setUp() throws Exception {
        dataStorage = new DataStorage();
        server = new TestWebSocketServer(new InetSocketAddress(8084));
        server.start();
        client = new WebSocketClient("ws://localhost:8084");
        LOGGER.info("Test setup completed, server started on port 8084");
    }

    @AfterEach
    void tearDown() throws Exception {
        client.close();
        server.stop();
        LOGGER.info("Test teardown completed");
    }

    @Test
    void testReadDataAndParseMessage() throws IOException {
        client.readData(dataStorage);
        server.broadcast("1,1700000000000,BloodPressureSystolic,120");
        try {
            Thread.sleep(100); // Wait for message processing
        } catch (InterruptedException e) {
            fail("Interrupted during test", e);
        }
        List<PatientRecord> records = dataStorage.getRecords(1, 0, Long.MAX_VALUE);
        assertEquals(1, records.size(), "Expected one record in DataStorage");
        assertEquals(120.0, records.get(0).getMeasurementValue(), "Incorrect measurement value");
        assertEquals("BloodPressureSystolic", records.get(0).getRecordType(), "Incorrect record type");
    }

    @Test
    void testInvalidMessageFormat() throws IOException {
        client.readData(dataStorage);
        server.broadcast("invalid_message");
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            fail("Interrupted during test", e);
        }
        assertTrue(dataStorage.getRecords(1, 0, Long.MAX_VALUE).isEmpty(), "Expected no records for invalid message");
    }

    static class TestWebSocketServer extends WebSocketServer {
        private static final Logger LOGGER = Logger.getLogger(TestWebSocketServer.class.getName());

        public TestWebSocketServer(InetSocketAddress address) {
            super(address);
        }

        @Override
        public void onOpen(org.java_websocket.WebSocket conn, org.java_websocket.handshake.ClientHandshake handshake) {
            LOGGER.info("WebSocket connection opened");
        }

        @Override
        public void onClose(org.java_websocket.WebSocket conn, int code, String reason, boolean remote) {
            LOGGER.info("WebSocket connection closed: Code=" + code + ", Reason=" + reason);
        }

        @Override
        public void onMessage(org.java_websocket.WebSocket conn, String message) {
            LOGGER.info("Message received: " + message);
        }

        @Override
        public void onError(org.java_websocket.WebSocket conn, Exception ex) {
            LOGGER.severe("WebSocket error: " + ex.getMessage());
        }

        @Override
        public void onStart() {
            LOGGER.info("WebSocket server started");
        }
    }
}