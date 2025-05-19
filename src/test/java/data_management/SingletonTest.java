package data_management;


import com.cardio_generator.HealthDataSimulator;
import com.data_management.DataStorage;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SingletonTest {
    @Test
    void testDataStorageSingleton() {
        DataStorage instance1 = DataStorage.getInstance();
        DataStorage instance2 = DataStorage.getInstance();
        assertSame(instance1, instance2, "DataStorage instances should be the same");
    }

    @Test
    void testHealthDataSimulatorSingleton() {
        HealthDataSimulator instance1 = HealthDataSimulator.getInstance();
        HealthDataSimulator instance2 = HealthDataSimulator.getInstance();
        assertSame(instance1, instance2, "HealthDataSimulator instances should be the same");
    }
}