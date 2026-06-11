package model;

import java.time.LocalDate;

public class Agent extends Employee {

    public Agent(int id, String name, String lastName, String gender, LocalDate birthday, String phoneNumber, String address, String userName, String password, LevelOfEducation levelOfEducation, int yearsOfExperience) {
        super(id, name, lastName, gender, birthday, phoneNumber, address, userName, password, levelOfEducation, yearsOfExperience);
    }
}
