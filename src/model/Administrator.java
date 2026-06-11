package model;

import java.time.LocalDate;

public class Administrator extends Employee{
    public Administrator(String name, String lastName, String gender, LocalDate birthday, String phoneNumber, String address, String userName, String password, LevelOfEducation levelOfEducation, int yearsOfExperience) {
        super(name, lastName, gender, birthday, phoneNumber, address, userName, password, levelOfEducation, yearsOfExperience);
    }
}
