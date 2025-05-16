
package com.cardio_generator.outputs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.Executors;

/**
 * An implementation of the {@link OutputStrategy} interface that sends patient health data to a TCP client.
 * This class establishes a TCP server on a specified port, accepts a single client connection, and sends data
 * in a comma-separated format. Client connection handling runs in a separate thread to avoid blocking the main thread.
 *
 * <p>This class is used in the cardiovascular data simulator when the output is configured to stream data to a TCP client
 * (e.g., using the "--output tcp:<port>" command-line argument).</p>
 *
 * @author [Your Name or Author Name]
 */
public class TcpOutputStrategy implements OutputStrategy {

    /** The server socket that listens for client connections. */
    private ServerSocket serverSocket;

    /** The socket connected to the client. */
    private Socket clientSocket;

    /** The writer used to send data to the connected client. */
    private PrintWriter out;

    /**
     * Constructs a {@code TcpOutputStrategy} that starts a TCP server on the specified port.
     * The server listens for a single client connection, which is accepted in a separate thread to avoid blocking.
     * Connection errors are printed to the standard error stream.
     *
     * @param port The port number on which the TCP server will listen. Must be a valid port number (1-65535).
     * @throws IllegalArgumentException If the port number is invalid (e.g., negative or exceeds 65535).
     */
    public TcpOutputStrategy(int port) {
        try {
            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            // Accept clients in a new thread to not block the main thread
            Executors.newSingleThreadExecutor().submit(() -> {
                try {
                    clientSocket = serverSocket.accept();
                    out = new PrintWriter(clientSocket.getOutputStream(), true);
                    System.out.println("Client connected: " + clientSocket.getInetAddress());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends patient health data to the connected TCP client in a comma-separated format.
     * The data is formatted as "<patientId>,<timestamp>,<label>,<data>" and sent only if a client is connected.
     * If no client is connected, the data is silently ignored.
     *
     * @param patientId  The unique identifier of the patient associated with the data. Must be a positive integer.
     * @param timestamp  The time at which the data was generated, represented as milliseconds since epoch.
     * @param label      A string identifying the type of data (e.g., "ECG", "BloodPressure", "Alert").
     * @param data       The actual health data to output, formatted as a string (e.g., "120/80" for blood pressure).
     */
    @Override
    public void output(int patientId, long timestamp, String label, String data) {
        if (out != null) {
            String message = String.format("%d,%d,%s,%s", patientId, timestamp, label, data);
            out.println(message);
        }
    }
}
