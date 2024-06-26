package meditracker.dailymedication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import meditracker.command.AddCommand;
import meditracker.exception.ArgumentException;
import meditracker.exception.HelpInvokedException;
import meditracker.exception.InsufficientQuantityException;
import meditracker.exception.MediTrackerException;
import meditracker.exception.MedicationNotFoundException;
import meditracker.exception.MedicationUnchangedException;
import meditracker.medication.Medication;
import meditracker.medication.MedicationManager;
import meditracker.medication.MedicationManagerTest;
import meditracker.time.Period;


public class DailyMedicationManagerTest {

    // @@author T0nyLin
    public static void resetDailyMedicationManager() throws InvocationTargetException,
            IllegalAccessException, NoSuchMethodException {
        Method resetDailyMedicationManagerMethod =
                DailyMedicationManager.class.getDeclaredMethod("clearDailyMedication");
        resetDailyMedicationManagerMethod.setAccessible(true);
        resetDailyMedicationManagerMethod.invoke(DailyMedicationManager.class);
    }
    // @@author

    @BeforeEach
    @AfterEach
    public void resetManagers() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        resetDailyMedicationManager();
        MedicationManagerTest.resetMedicationManager();
    }

    @Test
    public void addDailyMedication_genericDailyMedication_dailyMedicationAdded()
            throws HelpInvokedException, ArgumentException {
        String inputString = "add -n Medication A -q 60.0 -e 2025-07-01 -dM 500.0 -dA 250.0 "
                + "-dE 300.0 -r cause_dizziness -rep 1";
        AddCommand command = new AddCommand(inputString);
        command.execute();

        DailyMedication morningMeds = new DailyMedication("Medication A", 500, Period.MORNING);

        DailyMedication afternoonMeds = new DailyMedication("Medication A", 250, Period.AFTERNOON);

        DailyMedication eveningMeds = new DailyMedication("Medication A", 300, Period.EVENING);


        int actualIndex = 1; // 1-based indexing
        DailyMedication morningMedicationTest = DailyMedicationManager.getDailyMedication(actualIndex, Period.MORNING);
        DailyMedication afternoonMedicationTest =
                DailyMedicationManager.getDailyMedication(actualIndex, Period.AFTERNOON);
        DailyMedication eveningMedicationTest = DailyMedicationManager.getDailyMedication(actualIndex, Period.EVENING);

        assertEquals(morningMeds.toString(), morningMedicationTest.toString());
        assertEquals(afternoonMeds.toString(), afternoonMedicationTest.toString());
        assertEquals(eveningMeds.toString(), eveningMedicationTest.toString());
    }

    @Test
    public void takeDailyMedication_genericDailyMedication_dailyMedicationTaken()
            throws InsufficientQuantityException, MedicationNotFoundException, MedicationUnchangedException,
            MediTrackerException {
        String medicationName = "TestMedication";
        double oldQuantity = 60;
        double dosage = 10;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedExpiryDate = LocalDate.parse("2025-07-01", dateTimeFormatter);
        Medication medication = new Medication(
                medicationName,
                oldQuantity,
                dosage,
                0.0,
                0.0,
                parsedExpiryDate,
                "cause_dizziness",
                1,
                87);
        MedicationManager.addMedication(medication);

        DailyMedication dailyMedication = new DailyMedication(medicationName, dosage, Period.MORNING);
        assertFalse(dailyMedication.isTaken());
        DailyMedicationManager.addDailyMedication(dailyMedication);

        int actualIndex = 1; // 1-based indexing
        DailyMedicationManager.takeDailyMedication(actualIndex, Period.MORNING);
        DailyMedication dailyMedicationTest = DailyMedicationManager.getDailyMedication(actualIndex, Period.MORNING);
        assertTrue(dailyMedicationTest.isTaken());
        double expectedQuantity = oldQuantity - dosage;
        assertEquals(medication.getQuantity(), expectedQuantity);
    }

    @Test
    public void takeDailyMedication_lowQuantityMedication_insufficientQuantity() throws MediTrackerException {
        String medicationName = "TestMedication";
        double oldQuantity = 5;
        double dosage = 10;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedExpiryDate = LocalDate.parse("2025-07-01", dateTimeFormatter);
        Medication medication = new Medication(
                medicationName,
                oldQuantity,
                dosage,
                0.0,
                0.0,
                parsedExpiryDate,
                "cause_dizziness",
                1,
                87);
        MedicationManager.addMedication(medication);

        DailyMedication dailyMedication = new DailyMedication(medicationName, dosage, Period.MORNING);
        assertFalse(dailyMedication.isTaken());
        DailyMedicationManager.addDailyMedication(dailyMedication);

        int actualIndex = 1; // 1-based indexing
        assertThrows(
                InsufficientQuantityException.class, ()
                        -> DailyMedicationManager.takeDailyMedication(actualIndex, Period.MORNING));
    }

    @Test
    public void untakeDailyMedication_genericDailyMedication_dailyMedicationNotTaken()
            throws MedicationNotFoundException, MedicationUnchangedException, MediTrackerException {
        String medicationName = "TestMedication";
        double oldQuantity = 60;
        double dosage = 10;
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate parsedExpiryDate = LocalDate.parse("2025-07-01", dateTimeFormatter);
        Medication medication = new Medication(
                medicationName,
                oldQuantity,
                dosage,
                0.0,
                0.0,
                parsedExpiryDate,
                "cause_dizziness",
                1,
                87);
        MedicationManager.addMedication(medication);

        DailyMedication dailyMedication = new DailyMedication(medicationName, dosage, Period.MORNING);
        dailyMedication.take();
        assertTrue(dailyMedication.isTaken());
        DailyMedicationManager.addDailyMedication(dailyMedication);

        int actualIndex = 1; // 1-based indexing
        DailyMedicationManager.untakeDailyMedication(actualIndex, Period.MORNING);
        DailyMedication dailyMedicationTest = DailyMedicationManager.getDailyMedication(actualIndex, Period.MORNING);
        assertFalse(dailyMedicationTest.isTaken());
        double expectedQuantity = oldQuantity + dosage;
        assertEquals(medication.getQuantity(), expectedQuantity);
    }
}
