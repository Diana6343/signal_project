package com.cardio_generator;

import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        if (args.length > 0 && args[0].equals("DataStorage")) {
            com.data_management.DataStorage.main(new String[]{});
        } else {
            try {
                HealthDataSimulator.main(args);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}