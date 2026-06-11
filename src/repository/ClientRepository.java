package repository;

import model.Client;
import model.ClientCategory;
import model.Subscription;
import model.SubscriptionStatus;

import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ClientRepository {
    private static final String FILE_PATH = "data/clients.csv";

    public List<Client> loadAll() {
        List<Client> clients = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;

            reader.readLine();

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";", -1);
                if (parts.length != 15) {
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
                LocalDate driversLicenseIssueDate = LocalDate.parse(parts[9]);

                ClientCategory clientCategory = null;
                if (!parts[10].isEmpty()) {
                    clientCategory = ClientCategory.valueOf(parts[10]);
                }

                int subscriptionId = Integer.parseInt(parts[11]);
                SubscriptionStatus subscriptionStatus = SubscriptionStatus.valueOf(parts[12]);
                LocalDate subscriptionExpirationDate = LocalDate.parse(parts[13]);
                Subscription subscription = new Subscription(subscriptionId, subscriptionStatus, subscriptionExpirationDate);
                int lateReturnsCount = Integer.parseInt(parts[14]);

                Client client = new Client(
                        id,
                        name,
                        lastName,
                        gender,
                        birthday,
                        phoneNumber,
                        address,
                        userName,
                        password,
                        driversLicenseIssueDate,
                        clientCategory,
                        subscription
                );
                client.setLateReturnsCount(lateReturnsCount);
                clients.add(client);
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }

        return clients;
    }

    public void saveAll(List<Client> clients) {
        if (clients == null) {
            return;
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("id;name;lastName;gender;birthday;phoneNumber;address;userName;password;driversLicenseIssueDate;clientCategory;subscriptionId;subscriptionStatus;subscriptionExpirationDate;lateReturnsCount");
            writer.newLine();

            for (Client client : clients) {
                String clientCategory = "";
                if (client.getClientCategory() != null) {
                    clientCategory = client.getClientCategory().name();
                }

                Subscription subscription = client.getSubscription();

                String line = String.join(";",
                        String.valueOf(client.getId()),
                        client.getName(),
                        client.getLastName(),
                        client.getGender(),
                        client.getBirthday().toString(),
                        client.getPhoneNumber(),
                        client.getAddress(),
                        client.getUserName(),
                        client.getPassword(),
                        client.getDriversLicenseIssueDate().toString(),
                        clientCategory,
                        String.valueOf(subscription.getId()),
                        subscription.getSubscriptionStatus().name(),
                        subscription.getExpirationDate().toString(),
                        String.valueOf(client.getLateReturnsCount())
                );

                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing file: " + e.getMessage());
        }
    }
}
