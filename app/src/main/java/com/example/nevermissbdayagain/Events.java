package com.example.nevermissbdayagain;

//Класс сущности бд
public class Events {
    String PERSON_NAME,PERSON_AGE,DATE,MONTH,YEAR;

    public Events(String PERSON_NAME, String PERSON_AGE, String DATE, String MONTH, String YEAR) {
        this.PERSON_NAME = PERSON_NAME;
        this.PERSON_AGE = PERSON_AGE;
        this.DATE = DATE;
        this.MONTH = MONTH;
        this.YEAR = YEAR;
    }

    //Авто-генерация
    public String getPERSON_NAME() {
        return PERSON_NAME;
    }

    public void setPERSON_NAME(String PERSON_NAME) {
        this.PERSON_NAME = PERSON_NAME;
    }

    public String getPERSON_AGE() {
        return PERSON_AGE;
    }

    public void setPERSON_AGE(String PERSON_AGE) {
        this.PERSON_AGE = PERSON_AGE;
    }

    public String getDATE() {
        return DATE;
    }

    public void setDATE(String DATE) {
        this.DATE = DATE;
    }

    public String getMONTH() {
        return MONTH;
    }

    public void setMONTH(String MONTH) {
        this.MONTH = MONTH;
    }

    public String getYEAR() {
        return YEAR;
    }

    public void setYEAR(String YEAR) {
        this.YEAR = YEAR;
    }
}
