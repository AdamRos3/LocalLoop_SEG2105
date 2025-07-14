package com.example.localloop.resources.datetime;

import com.example.localloop.resources.exception.InvalidDateException;

public class Date {
    private int year;
    private int month;
    private int day;
    private String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
    public Date() {
        // Empty constructor is required by Firebase
    }
    public Date(int year, int month, int day) throws InvalidDateException {
        if (year < 2025 || year > 2050) {
            throw new InvalidDateException("Invalid year");
        }
        if (month < 1 || month > 12) {
            throw new InvalidDateException("Invalid month");
        }
        if (day < 1 || day > 31) {
            throw new InvalidDateException("Invalid day");
        }
        if ((month==4 || month==6 || month==9 || month==11) && day > 31) {
            throw new InvalidDateException("Invalid day");
        }
        if ((month==2) && day > 29) {
            throw new InvalidDateException("Invalid day");
        }
        this.year = year;
        this.month = month;
        this.day = day;
    }
    public Date(int year, String month, int day) throws Exception {
        boolean found = false;
        for (int i = 0; i < months.length; i++) {
            if (month.equals(months[i])) {
                this.month = i+1;
                found = true;
            }
        }
        if (!found) {
            throw new InvalidDateException("Invalid month");
        }
        if (year < 2025 || year > 2050) {
            throw new InvalidDateException("Invalid year");
        }
        if (this.month < 1 || this.month > 12) {
            throw new InvalidDateException("Invalid month");
        }
        if (day < 1 || day > 31) {
            throw new InvalidDateException("Invalid day");
        }
        if ((this.month==4 || this.month==6 || this.month==9 || this.month==11) && day > 31) {
            throw new InvalidDateException("Invalid day");
        }
        if ((this.month==2) && day > 29) {
            throw new InvalidDateException("Invalid day");
        }
        this.year = year;
        this.day = day;
    }
    public int getYear() {
        return year;
    }
    public int getMonth() {
        return month;
    }
    public int getDay() {
        return day;
    }
    public String toString() {
        return months[month-1]+" "+day+", "+year;
    }
}
