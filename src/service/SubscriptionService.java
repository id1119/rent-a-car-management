package service;

import model.Client;
import model.Subscription;

import java.util.ArrayList;
import java.util.List;

public class SubscriptionService {
    private List<Subscription> subscriptions = new ArrayList<>();

    public SubscriptionService() {
    }

    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    public void addSubscription(Subscription subscription) {
        if (subscription != null) {
            subscriptions.add(subscription);
        }
    }

    public void removeSubscription(Subscription subscription) {
        subscriptions.remove(subscription);
    }

    public boolean canRenewSubscription(Client client) {
        if (client == null) {
            return false;
        }
        return client.getLateReturnsCount() <= 5;
    }
}
