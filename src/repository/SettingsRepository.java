package repository;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class SettingsRepository {
    private static final String FILE_PATH = "data/settings.csv";
    private static final int DEFAULT_RENTAL_DURATION_DAYS = 3;

    public int loadDefaultRentalDurationDays() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";", -1);
                if (parts.length == 2 && parts[0].equals("defaultRentalDurationDays")) {
                    return Integer.parseInt(parts[1]);
                }
            }
        } catch (IOException | NumberFormatException e) {
            return DEFAULT_RENTAL_DURATION_DAYS;
        }

        return DEFAULT_RENTAL_DURATION_DAYS;
    }

    public void saveDefaultRentalDurationDays(int defaultRentalDurationDays) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("key;value");
            writer.newLine();
            writer.write("defaultRentalDurationDays;" + defaultRentalDurationDays);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing settings file: " + e.getMessage());
        }
    }
}
