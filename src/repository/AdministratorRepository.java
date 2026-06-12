package repository;

import model.Administrator;
import model.LevelOfEducation;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AdministratorRepository {
    private static final String FILE_PATH = "data/administrators.csv";

    public List<Administrator> loadAll() {
        List<Administrator> administrators = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";", -1);
                if (parts.length != 11) {
                    continue;
                }

                int id = Integer.parseInt(parts[0]);
                String name = parts[1];
                String lastName = parts[2];
                String gender = parts[3];
                LocalDate birthday = LocalDate.parse(parts[4]);
                String phoneNumber = parts[5];
                String address = parts[6];
                String userName = parts[7];
                String password = parts[8];
                LevelOfEducation levelOfEducation = LevelOfEducation.valueOf(parts[9]);
                int yearsOfExperience = Integer.parseInt(parts[10]);

                Administrator administrator = new Administrator(
                        id,
                        name,
                        lastName,
                        gender,
                        birthday,
                        phoneNumber,
                        address,
                        userName,
                        password,
                        levelOfEducation,
                        yearsOfExperience
                );

                administrators.add(administrator);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return administrators;
    }

    public void saveAll(List<Administrator> administrators) {
        if (administrators == null) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("id;name;lastName;gender;birthday;phoneNumber;address;userName;password;levelOfEducation;yearsOfExperience");
            writer.newLine();

            for (Administrator administrator : administrators) {
                String line = String.join(";",
                        String.valueOf(administrator.getId()),
                        administrator.getName(),
                        administrator.getLastName(),
                        administrator.getGender(),
                        administrator.getBirthday().toString(),
                        administrator.getPhoneNumber(),
                        administrator.getAddress(),
                        administrator.getUserName(),
                        administrator.getPassword(),
                        administrator.getLevelOfEducation().name(),
                        String.valueOf(administrator.getYearsOfExperience())
                );

                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }
}
