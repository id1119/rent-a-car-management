package model;

import java.time.LocalDate;

public class Subscription {
    private SubscriptionStatus subscriptionStatus;
    private LocalDate expirationDate;

    public Subscription(SubscriptionStatus subscriptionStatus, LocalDate expirationDate) {
        this.subscriptionStatus = subscriptionStatus;
        this.expirationDate = expirationDate;
    }

    public SubscriptionStatus getSubscriptionStatus() {
        return subscriptionStatus;
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
