package model;

import java.time.LocalDate;

public abstract class Employee extends User {

    public static final double baseSalary = 500;

    private LevelOfEducation levelOfEducation;
    private int yearsOfExperience;

    public Employee(int id, String name, String lastName, String gender, LocalDate birthday, String phoneNumber, String address, String userName, String password, LevelOfEducation levelOfEducation, int yearsOfExperience) {
        super(id, name, lastName, gender, birthday, phoneNumber, address, userName, password);
        this.levelOfEducation = levelOfEducation;
        this.yearsOfExperience = yearsOfExperience;
    }

    public double getSalary() {
        return baseSalary * (levelOfEducation.getCoefficient() + 0.004 * this.yearsOfExperience);
    }

    public LevelOfEducation getLevelOfEducation() {
        return levelOfEducation;
    }

    public void setLevelOfEducation(LevelOfEducation levelOfEducation) {
        this.levelOfEducation = levelOfEducation;
    }

    public int getYearsOfExperience() {
        return yearsOfExperience;
    }

    public void setYearsOfExperience(int yearsOfExperience) {
        this.yearsOfExperience = yearsOfExperience;
    }
}
