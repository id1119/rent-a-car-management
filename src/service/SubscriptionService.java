package service;

import model.Client;
import model.Subscription;
import model.SubscriptionStatus;

import java.time.LocalDate;
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

    public String getRenewalRequestError(Client client) {
        if (client == null || client.getSubscription() == null) {
            return "Client has no subscription.";
        }

        if (client.getSubscription().getSubscriptionStatus() == SubscriptionStatus.PENDING_APPROVAL) {
            return "Renewal request is already pending.";
        }

        if (client.getSubscription().getSubscriptionStatus() == SubscriptionStatus.ACTIVE &&
                client.getSubscription().getExpirationDate().isAfter(LocalDate.now().plusDays(30))) {
            return "Subscription can be renewed only in the last 30 days before expiration.";
        }

        if (!canRenewSubscription(client)) {
            return "Renewal request rejected because of too many late returns.";
        }

        return null;
    }

    public boolean requestRenewal(Client client) {
        String error = getRenewalRequestError(client);
        if (error != null) {
            if (client != null && client.getSubscription() != null && !canRenewSubscription(client)) {
                client.getSubscription().setSubscriptionStatus(SubscriptionStatus.EXPIRED);
            }
            return false;
        }

        if (!canRenewSubscription(client)) {
            client.getSubscription().setSubscriptionStatus(SubscriptionStatus.EXPIRED);
            return false;
        }

        client.getSubscription().setSubscriptionStatus(SubscriptionStatus.PENDING_APPROVAL);
        return true;
    }

    public boolean approveRenewal(Client client) {
        if (client == null || client.getSubscription() == null) {
            return false;
        }

        if (client.getSubscription().getSubscriptionStatus() != SubscriptionStatus.PENDING_APPROVAL) {
            return false;
        }

        if (!canRenewSubscription(client)) {
            client.getSubscription().setSubscriptionStatus(SubscriptionStatus.EXPIRED);
            return false;
        }

        client.getSubscription().setSubscriptionStatus(SubscriptionStatus.ACTIVE);
        client.getSubscription().setExpirationDate(LocalDate.now().plusYears(1));
        return true;
    }

    public boolean rejectRenewal(Client client) {
        if (client == null || client.getSubscription() == null) {
            return false;
        }

        if (client.getSubscription().getSubscriptionStatus() != SubscriptionStatus.PENDING_APPROVAL) {
            return false;
        }

        client.getSubscription().setSubscriptionStatus(SubscriptionStatus.EXPIRED);
        return true;
    }
}
