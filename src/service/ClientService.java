package service;

import model.Client;

import java.util.ArrayList;
import java.util.List;

public class ClientService {
    private List<Client> clients = new ArrayList<>();

    public ClientService() {
    }

    public List<Client> getClients() {
        return clients;
    }

    public void addClient(Client client) {
        if (client != null) {
            clients.add(client);
        }
    }

    public void removeClient(Client client) {
        clients.remove(client);
    }
}
