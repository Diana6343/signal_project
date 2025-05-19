package com.alerts.decorator_pattern;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class AlertDecoratorTest {
    private ByteArrayOutputStream outContent;
    private PrintStream originalOut;
    private PrintStream originalErr;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        originalOut = System.out;
        originalErr = System.err;
        PrintStream newStream = new PrintStream(outContent);
        System.setOut(newStream);
        System.setErr(newStream); // Capture System.err as well
        System.out.println("Debug: System.out redirected for test");
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
    }

    @Test
    void testRepeatedAlertDecorator() {
        Alert alert = new ConcreteAlert("1", "CriticalSystolic", 1000L, "BloodPressure");
        Alert decorated = new RepeatedAlertDecorator(alert, 3);
        decorated.trigger();
        String output = outContent.toString().trim();
        boolean containsAll = output.contains("Repeated alert 1 of 3") &&
                output.contains("Repeated alert 2 of 3") &&
                output.contains("Repeated alert 3 of 3");
        assertTrue(containsAll,
                "Expected repeated alerts in output, but got: '" + output + "'");
    }

    @Test
    void testPriorityAlertDecorator() {
        Alert alert = new ConcreteAlert("1", "CriticalSystolic", 1000L, "BloodPressure");
        Alert decorated = new PriorityAlertDecorator(alert, "High");
        decorated.trigger();
        String output = outContent.toString().trim();
        assertTrue(output.contains("Priority: High"),
                "Expected 'Priority: High' in output, but got: '" + output + "'");
    }
}