package com.example.myapplication;

import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class Child {
    private int id;
    private int guardianId;
    private String fname;
    private String lname;
    private String dob;
    private String sex;
    private String profilePicture;

    public Child(int id, String fname, String lname, String dob, String sex, String profilePicture) {
        this.id = id;
        this.fname = fname;
        this.lname = lname;
        this.dob = dob;
        this.sex = sex;
        this.profilePicture = profilePicture;
    }
    public Child(int id, int guardianId, String fname, String lname, String dob, String sex, String profilePicture) {
        this.id = id;
        this.guardianId = guardianId;
        this.fname = fname;
        this.lname = lname;
        this.dob = dob;
        this.sex = sex;
        this.profilePicture = profilePicture;
    }

    // Getter and Setter methods for ID
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setGuardianId(int guardianId) { this.guardianId = guardianId; }
    public int getGuardianId() { return guardianId; }

    // Getter and Setter methods for First Name
    public String getFname() {
        return fname;
    }

    public void setFname(String fname) {
        this.fname = fname;
    }

    // Getter and Setter methods for Last Name
    public String getLname() {
        return lname;
    }

    public void setLname(String lname) {
        this.lname = lname;
    }

    // Getter and Setter methods for Date of Birth
    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    // Getter and Setter methods for Sex
    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getName() {
        return fname + " " + lname;
    }

    public String getProfilePicture() { return profilePicture; }

    // Method to calculate age from DOB
    public int getAge() {
        // Parse the DOB string to a LocalDate object
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate birthDate = LocalDate.parse(dob, formatter);
        // Calculate the age
        return Period.between(birthDate, LocalDate.now()).getYears();
    }

    // Getter for full name
    public String getFullName() {
        return fname + " " + lname;
    }
}
