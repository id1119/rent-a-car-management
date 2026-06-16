package service;

import model.Administrator;
import model.Agent;
import model.Client;
import model.LoginResult;
import model.UserRole;

import java.util.ArrayList;
import java.util.List;

public class AuthService {
    private ClientService clientService;
    private List<Agent> agents;
    private List<Administrator> administrators;

    public AuthService(ClientService clientService, List<Agent> agents, List<Administrator> administrators) {
        this.clientService = clientService == null ? new ClientService() : clientService;
        this.agents = agents == null ? new ArrayList<>() : agents;
        this.administrators = administrators == null ? new ArrayList<>() : administrators;
    }

    public LoginResult login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }

        String normalizedUsername = username.trim();
        if (normalizedUsername.isEmpty() || password.isEmpty()) {
            return null;
        }

        Client client = clientService.findClientByUsername(normalizedUsername);
        if (client != null && password.equals(client.getPassword())) {
            return new LoginResult(client, UserRole.CLIENT);
        }

        for (Agent agent : agents) {
            if (agent != null && normalizedUsername.equals(agent.getUserName()) && password.equals(agent.getPassword())) {
                return new LoginResult(agent, UserRole.AGENT);
            }
        }

        for (Administrator administrator : administrators) {
            if (administrator != null && normalizedUsername.equals(administrator.getUserName()) && password.equals(administrator.getPassword())) {
                return new LoginResult(administrator, UserRole.ADMINISTRATOR);
            }
        }

        return null;
    }
}
