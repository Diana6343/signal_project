package com.data_management;

import java.io.IOException;

/**
 * Interface for reading patient data into the system.
 * Implementations should handle data retrieval from various sources, such as files or real-time streams like WebSocket.
 */
public interface DataReader {

    /**
     * Reads data from the source and stores it in the provided DataStorage.
     * For real-time sources (e.g., WebSocket), this method should handle continuous data reception.
     *
     * @param dataStorage the storage where the read data will be stored
     * @throws IOException if an error occurs during data reading
     */
    void readData(DataStorage dataStorage) throws IOException;

    /**
     * Closes the data source connection, if applicable (e.g., WebSocket or file streams).
     * Implementations should ensure resources are properly released.
     *
     * @throws IOException if an error occurs while closing the connection
     */
    void close() throws IOException;
}
