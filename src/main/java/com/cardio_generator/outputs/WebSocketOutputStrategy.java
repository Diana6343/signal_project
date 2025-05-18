package com.cardio_generator.outputs;

import org.java_websocket.WebSocket;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.logging.Logger;

/**
 * An OutputStrategy that broadcasts patient health data to WebSocket clients in real-time.
 * Starts a WebSocket server on the specified port and sends data in the format: patientId,timestamp,label,data.
 */
public class WebSocketOutputStrategy implements OutputStrategy {
    private static final Logger LOGGER = Logger.getLogger(WebSocketOutputStrategy.class.getName());
    private final WebSocketServer server;

    public WebSocketOutputStrategy(int port) {
        server = new SimpleWebSocketServer(new InetSocketAddress(port));
        LOGGER.info("WebSocket server created on port: " + port);
        server.start();
    }

    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
        synchronized (server.getConnections()) {
            for (WebSocket conn : server.getConnections()) {
                try {
                    conn.send(message);
                    LOGGER.fine("Sent message to " + conn.getRemoteSocketAddress() + ": " + message);
                } catch (Exception e) {
                    LOGGER.warning("Failed to send message to " + conn.getRemoteSocketAddress() + ": " + e.getMessage());
                }
            }
        }
    }

    private static class SimpleWebSocketServer extends WebSocketServer {
        public SimpleWebSocketServer(InetSocketAddress address) {
            super(address);
        }

        @Override
        public void onOpen(WebSocket conn, org.java_websocket.handshake.ClientHandshake handshake) {
            LOGGER.info("New connection: " + conn.getRemoteSocketAddress());
        }

        @Override
        public void onClose(WebSocket conn, int code, String reason, boolean remote) {
            LOGGER.info("Closed connection: " + conn.getRemoteSocketAddress() + ", Code: " + code + ", Reason: " + reason);
        }

        @Override
        public void onMessage(WebSocket conn, String message) {
            // Not used in this context
        }

        @Override
        public void onError(WebSocket conn, Exception ex) {
            LOGGER.severe("Server error: " + (conn != null ? conn.getRemoteSocketAddress() : "unknown") + ", " + ex.getMessage());
        }

        @Override
        public void onStart() {
            LOGGER.info("WebSocket server started successfully");
        }
    }
}