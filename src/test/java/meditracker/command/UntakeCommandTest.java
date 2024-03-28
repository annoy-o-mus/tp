package meditracker.command;

import meditracker.DailyMedication;
import meditracker.DailyMedicationManager;
import meditracker.exception.ArgumentNotFoundException;
import meditracker.exception.FileReadWriteException;
import meditracker.medication.MedicationManager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

public class UntakeCommandTest {
    @Test
    void execute_inOrderArgument_expectDailyMedicationUntaken() throws ArgumentNotFoundException,
            FileReadWriteException {
        MedicationManager medicationManager = new MedicationManager();
        DailyMedication dailyMedication = new DailyMedication("Medication_A");
        DailyMedicationManager.addDailyMedication(dailyMedication);

        String inputString = "untake -l 1";
        UntakeCommand command = new UntakeCommand(inputString);
        command.execute(null);

        assertFalse(dailyMedication.isTaken());
    }
}
