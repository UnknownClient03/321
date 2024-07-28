package com.example.myapplication;

public class Immunisation {

    private String vaccineName;
    private String dateDue;

    public Immunisation(String vaccineName, String dateDue) {
        this.vaccineName = vaccineName;
        this.dateDue = dateDue;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public String getDateDue() {
        return dateDue;
    }
}