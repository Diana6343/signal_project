package com.data_management;

import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

/**
 * A DataReader implementation that reads patient data from a WebSocket server in real-time.
 * Connects to the specified WebSocket URI, parses incoming messages, and stores them in DataStorage.
 */
public class WebSocketClient implements DataReader {
    private static final Logger LOGGER = Logger.getLogger(WebSocketClient.class.getName());
    private final Client client;
    private final AtomicBoolean isRunning;

    /**
     * Constructs a WebSocketClient that connects to the specified WebSocket server.
     *
     * @param serverUri the URI of the WebSocket server (e.g., ws://localhost:8080)
     * @throws URISyntaxException if the serverUri is invalid
     */
    public WebSocketClient(String serverUri) throws URISyntaxException {
        this.client = new Client(new URI(serverUri));
        this.isRunning = new AtomicBoolean(false);
    }

    @Override
    public void readData(DataStorage dataStorage) throws IOException {
        if (isRunning.get()) {
            throw new IOException("WebSocketClient is already running");
        }
        isRunning.set(true);
        client.setDataStorage(dataStorage);
        try {
            client.connectBlocking();
            LOGGER.info("Connected to WebSocket server: " + client.getURI());
        } catch (InterruptedException e) {
            isRunning.set(false);
            throw new IOException("Failed to connect to WebSocket server", e);
        }
    }

    @Override
    public void close() throws IOException {
        if (isRunning.get()) {
            try {
                client.closeBlocking();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            isRunning.set(false);
            LOGGER.info("WebSocketClient closed");
        }
    }

    private static class Client extends org.java_websocket.client.WebSocketClient {
        private DataStorage dataStorage;

        public Client(URI serverUri) {
            super(serverUri);
        }

        public void setDataStorage(DataStorage dataStorage) {
            this.dataStorage = dataStorage;
        }

        @Override
        public void onOpen(ServerHandshake handshake) {
            LOGGER.info("WebSocket connection opened");
        }

        @Override
        public void onMessage(String message) {
            try {
                parseAndStoreMessage(message, dataStorage);
            } catch (IllegalArgumentException e) {
                LOGGER.warning("Invalid message format: " + message + ", Error: " + e.getMessage());
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            LOGGER.info("WebSocket connection closed: Code=" + code + ", Reason=" + reason);
        }

        @Override
        public void onError(Exception ex) {
            LOGGER.severe("WebSocket error: " + ex.getMessage());
        }

        private void parseAndStoreMessage(String message, DataStorage dataStorage) {
            String[] parts = message.split(",");
            if (parts.length != 4) {
                throw new IllegalArgumentException("Expected 4 fields, got " + parts.length);
            }
            try {
                int patientId = Integer.parseInt(parts[0]);
                long timestamp = Long.parseLong(parts[1]);
                String label = parts[2];
                double measurementValue = Double.parseDouble(parts[3]);
                synchronized (dataStorage) {
                    dataStorage.addPatientData(patientId, measurementValue, label, timestamp);
                }
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number format in message: " + message, e);
            }
        }
    }
}