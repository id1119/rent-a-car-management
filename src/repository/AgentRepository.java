package repository;

import model.Agent;
import model.LevelOfEducation;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class AgentRepository {
    private static final String FILE_PATH = "data/agents.csv";

    public List<Agent> loadAll() {
        List<Agent> agents = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";", -1);
                if (parts.length != 11) {
                    continue;
                }

                try {
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

                    Agent agent = new Agent(
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

                    agents.add(agent);
                } catch (RuntimeException e) {
                    System.out.println("Skipping invalid agent row: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return agents;
    }

    public void saveAll(List<Agent> agents) {
        if (agents == null) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("id;name;lastName;gender;birthday;phoneNumber;address;userName;password;levelOfEducation;yearsOfExperience");
            writer.newLine();

            for (Agent agent : agents) {
                String line = String.join(";",
                        String.valueOf(agent.getId()),
                        agent.getName(),
                        agent.getLastName(),
                        agent.getGender(),
                        agent.getBirthday().toString(),
                        agent.getPhoneNumber(),
                        agent.getAddress(),
                        agent.getUserName(),
                        agent.getPassword(),
                        agent.getLevelOfEducation().name(),
                        String.valueOf(agent.getYearsOfExperience())
                );

                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }
}
