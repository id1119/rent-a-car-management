package service;

import model.Administrator;
import model.Agent;
import model.Client;
import model.LoginResult;
import model.UserRole;

import java.util.List;

public class AuthService {
    private ClientService clientService;
    private List<Agent> agents;
    private List<Administrator> administrators;

    public AuthService(ClientService clientService, List<Agent> agents, List<Administrator> administrators) {
        this.clientService = clientService;
        this.agents = agents;
        this.administrators = administrators;
    }

    public LoginResult login(String username, String password) {
        if (username == null || password == null) {
            return null;
        }

        Client client = clientService.findClientByUsername(username);
        if (client != null && password.equals(client.getPassword())) {
            return new LoginResult(client, UserRole.CLIENT);
        }

        for (Agent agent : agents) {
            if (username.equals(agent.getUserName()) && password.equals(agent.getPassword())) {
                return new LoginResult(agent, UserRole.AGENT);
            }
        }

        for (Administrator administrator : administrators) {
            if (username.equals(administrator.getUserName()) && password.equals(administrator.getPassword())) {
                return new LoginResult(administrator, UserRole.ADMINISTRATOR);
            }
        }

        return null;
    }
}
