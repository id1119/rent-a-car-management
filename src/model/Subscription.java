package model;

import java.time.LocalDate;

public class Subscription {
    private int id;
    private SubscriptionStatus subscriptionStatus;
    private LocalDate expirationDate;

    public Subscription(int id, SubscriptionStatus subscriptionStatus, LocalDate expirationDate) {
        this.id = id;
        this.subscriptionStatus = subscriptionStatus;
        this.expirationDate = expirationDate;
    }

    public int getId() {
        return id;
    }

    public SubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSubscriptionStatus(SubscriptionStatus subscriptionStatus) {
        this.subscriptionStatus = subscriptionStatus;
    }

    public LocalDate getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDate expirationDate) {
        this.expirationDate = expirationDate;
    }

    public boolean isActive() {
        return subscriptionStatus == SubscriptionStatus.ACTIVE && !expirationDate.isBefore(LocalDate.now());
    }
}
