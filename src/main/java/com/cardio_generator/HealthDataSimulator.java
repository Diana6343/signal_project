package com.cardio_generator;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import com.cardio_generator.generators.AlertGenerator;
import com.cardio_generator.generators.BloodPressureDataGenerator;
import com.cardio_generator.generators.BloodSaturationDataGenerator;
import com.cardio_generator.generators.BloodLevelsDataGenerator;
import com.cardio_generator.generators.ECGDataGenerator;
import com.cardio_generator.outputs.ConsoleOutputStrategy;
import com.cardio_generator.outputs.FileOutputStrategy;
import com.cardio_generator.outputs.OutputStrategy;
import com.cardio_generator.outputs.TcpOutputStrategy;
import com.cardio_generator.outputs.WebSocketOutputStrategy;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

/**
 * A health data simulator that generates real-time cardiovascular data for multiple patients.
 * This class produces data such as ECG, blood pressure, blood saturation, and blood levels,
 * and outputs them through various strategies (console, file, WebSocket, TCP). It uses a task
 * scheduler to manage periodic data generation for each patient.
 *
 * <p>This class serves as the main entry point of the application and supports command-line
 * arguments to configure the number of patients and output type. By default, it generates
 * data for 50 patients and outputs to the console.</p>
 *
 * @author [Your Name or Author Name]
 */
public class HealthDataSimulator {

    /** Default number of patients for simulation. */
    private static int patientCount = 50;

    /** Task scheduler for managing periodic data generation. */
    private static ScheduledExecutorService scheduler;

    /** Output strategy for sending simulated data. Default: console. */
    private static OutputStrategy outputStrategy = new ConsoleOutputStrategy();

    /** Random number generator for task scheduling and patient ID assignment. */
    private static final Random random = new Random();

    /**
     * The main entry point of the application that initializes the health data simulation.
     * It processes command-line arguments to configure the number of patients and output
     * strategy, initializes patient IDs, and schedules data generation tasks.
     *
     * @param args Command-line arguments, such as "--patient-count" for the number of
     *             patients and "--output" for the output type (e.g., "console",
     *             "file:<directory>", "websocket:<port>", "tcp:<port>").
     * @throws IOException If an error occurs while creating the file output directory.
     */
    public static void main(String[] args) throws IOException {
        parseArguments(args);
        scheduler = Executors.newScheduledThreadPool(patientCount * 4);
        List<Integer> patientIds = initializePatientIds(patientCount);
        Collections.shuffle(patientIds); // Randomize the order of patient IDs
        scheduleTasksForPatients(patientIds);
    }

    /**
     * Parses command-line arguments to configure the number of patients and output strategy.
     * Supported options include "-h" for help, "--patient-count" for the number of patients,
     * and "--output" for the output type. Prints error messages or uses default values for
     * invalid arguments.
     *
     * @param args Command-line arguments to parse.
     * @throws IOException If an error occurs while creating the file output directory.
     */
    private static void parseArguments(String[] args) throws IOException {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-h":
                    printHelp();
                    System.exit(0);
                    break;
                case "--patient-count":
                    if (i + 1 < args.length) {
                        try {
                            patientCount = Integer.parseInt(args[++i]);
                        } catch (NumberFormatException e) {
                            System.err.println("Error: Invalid number of patients. Using default value: " + patientCount);
                        }
                    }
                    break;
                case "--output":
                    if (i + 1 < args.length) {
                        String outputArg = args[++i];
                        if (outputArg.equals("console")) {
                            outputStrategy = new ConsoleOutputStrategy();
                        } else if (outputArg.startsWith("file:")) {
                            String baseDirectory = outputArg.substring(5);
                            Path outputPath = Paths.get(baseDirectory);
                            if (!Files.exists(outputPath)) {
                                Files.createDirectories(outputPath);
                            }
                            outputStrategy = new FileOutputStrategy(baseDirectory);
                        } else if (outputArg.startsWith("websocket:")) {
                            try {
                                int port = Integer.parseInt(outputArg.substring(10));
                                outputStrategy = new WebSocketOutputStrategy(port);
                                System.out.println("WebSocket output will be on port: " + port);
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid port for WebSocket output. Please specify a valid port number.");
                            }
                        } else if (outputArg.startsWith("tcp:")) {
                            try {
                                int port = Integer.parseInt(outputArg.substring(4));
                                outputStrategy = new TcpOutputStrategy(port);
                                System.out.println("TCP socket output will be on port: " + port);
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid port for TCP output. Please specify a valid port number.");
                            }
                        } else {
                            System.err.println("Unknown output type. Using default (console).");
                        }
                    }
                    break;
                default:
                    System.err.println("Unknown option '" + args[i] + "'");
                    printHelp();
                    System.exit(1);
            }
        }
    }

    /**
     * Prints the usage guide for the application, including command-line options and an example.
     * This method is called when the "-h" option is used or an invalid argument is provided.
     */
    private static void printHelp() {
        System.out.println("Usage: java HealthDataSimulator [options]");
        System.out.println("Options:");
        System.out.println("  -h                       Show help and exit.");
        System.out.println("  --patient-count <count>  Specify the number of patients to simulate data for (default: 50).");
        System.out.println("  --output <type>          Define the output method. Options are:");
        System.out.println("                             'console' for console output,");
        System.out.println("                             'file:<directory>' for file output,");
        System.out.println("                             'websocket:<port>' for WebSocket output,");
        System.out.println("                             'tcp:<port>' for TCP socket output.");
        System.out.println("Example:");
        System.out.println("  java HealthDataSimulator --patient-count 100 --output websocket:8080");
        System.out.println("  This command simulates data for 100 patients and sends the output to WebSocket clients connected to port 8080.");
    }

    /**
     * Initializes a list of patient IDs based on the specified number of patients.
     * IDs are assigned sequentially from 1 to patientCount.
     *
     * @param patientCount The number of patients for which to generate IDs.
     * @return A list of patient IDs as integers.
     */
    private static List<Integer> initializePatientIds(int patientCount) {
        List<Integer> patientIds = new ArrayList<>();
        for (int i = 1; i <= patientCount; i++) {
            patientIds.add(i);
        }
        return patientIds;
    }

    /**
     * Schedules data generation tasks for each patient.
     * For each patient, tasks for generating ECG, blood saturation, blood pressure, blood levels,
     * and alerts are scheduled with specific intervals.
     *
     * @param patientIds A list of patient IDs for which to simulate data.
     */
    private static void scheduleTasksForPatients(List<Integer> patientIds) {
        ECGDataGenerator ecgDataGenerator = new ECGDataGenerator(patientCount);
        BloodSaturationDataGenerator bloodSaturationDataGenerator = new BloodSaturationDataGenerator(patientCount);
        BloodPressureDataGenerator bloodPressureDataGenerator = new BloodPressureDataGenerator(patientCount);
        BloodLevelsDataGenerator bloodLevelsDataGenerator = new BloodLevelsDataGenerator(patientCount);
        AlertGenerator alertGenerator = new AlertGenerator(patientCount);

        for (int patientId : patientIds) {
            scheduleTask(() -> ecgDataGenerator.generate(patientId, outputStrategy), 1, TimeUnit.SECONDS);
            scheduleTask(() -> bloodSaturationDataGenerator.generate(patientId, outputStrategy), 1, TimeUnit.SECONDS);
            scheduleTask(() -> bloodPressureDataGenerator.generate(patientId, outputStrategy), 1, TimeUnit.MINUTES);
            scheduleTask(() -> bloodLevelsDataGenerator.generate(patientId, outputStrategy), 2, TimeUnit.MINUTES);
            scheduleTask(() -> alertGenerator.generate(patientId, outputStrategy), 20, TimeUnit.SECONDS);
        }
    }

    /**
     * Schedules a task with a random initial delay and a fixed repetition period.
     * Tasks are executed using the ScheduledExecutorService.
     *
     * @param task The task to execute (as a Runnable).
     * @param period The repetition period for the task (in the specified time unit).
     * @param timeUnit The time unit for the period (e.g., seconds or minutes).
     */
    private static void scheduleTask(Runnable task, long period, TimeUnit timeUnit) {
        scheduler.scheduleAtFixedRate(task, random.nextInt(5), period, timeUnit);
    }
}