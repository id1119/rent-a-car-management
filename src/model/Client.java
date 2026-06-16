package model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class Client extends User {
    private LocalDate driversLicenseIssueDate;
    private ClientCategory clientCategory;
    private Subscription subscription;
    private int lateReturnsCount = 0;
    private LocalDateTime reservationBlockedUntil;

    public Client(int id, String name, String lastName, String gender, LocalDate birthday, String phoneNumber, String address, String userName, String password, LocalDate driversLicenseIssueDate, ClientCategory clientCategory, Subscription subscription) {
        super(id, name, lastName, gender, birthday, phoneNumber, address, userName, password);
        this.driversLicenseIssueDate = driversLicenseIssueDate;
        this.clientCategory = clientCategory;
        this.subscription = subscription;
    }

    public Client(int id, String name, String lastName, String gender, LocalDate birthday, String phoneNumber, String address, String userName, String password, LocalDate driversLicenseIssueDate, Subscription subscription) {
        super(id, name, lastName, gender, birthday, phoneNumber, address, userName, password);
        this.driversLicenseIssueDate = driversLicenseIssueDate;
        this.subscription = subscription;
    }

    public Subscription getSubscription() {
        return subscription;
    }

    public void setSubscription(Subscription subscription) {
        this.subscription = subscription;
    }

    public LocalDate getDriversLicenseIssueDate() {
        return driversLicenseIssueDate;
    }

    public void setDriversLicenseIssueDate(LocalDate driversLicenseIssueDate) {
        this.driversLicenseIssueDate = driversLicenseIssueDate;
    }

    public ClientCategory getClientCategory() {
        return clientCategory;
    }

    public void setClientCategory(ClientCategory clientCategory) {
        this.clientCategory = clientCategory;
    }

    public boolean canRent() {
        if (this.driversLicenseIssueDate == null) {
            return false;
        }
        LocalDate twoYearsAgo = LocalDate.now().minusYears(2);
        return !this.driversLicenseIssueDate.isAfter(twoYearsAgo);
    }

    public boolean canMakeReservationNow() {
        return reservationBlockedUntil == null || !LocalDateTime.now().isBefore(reservationBlockedUntil);
    }

    public int getLateReturnsCount() {
        return lateReturnsCount;
    }

    public void setLateReturnsCount(int lateReturnsCount) {
        this.lateReturnsCount = lateReturnsCount;
    }

    public LocalDateTime getReservationBlockedUntil() {
        return reservationBlockedUntil;
    }

    public void setReservationBlockedUntil(LocalDateTime reservationBlockedUntil) {
        this.reservationBlockedUntil = reservationBlockedUntil;
    }
}
