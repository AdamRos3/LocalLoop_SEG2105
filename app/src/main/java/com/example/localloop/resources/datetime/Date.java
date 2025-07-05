package com.example.localloop.resources.datetime;

public class Date {
    private int year;
    private int month;
    private int day;
    private String[] months = {"January","February","March","April","May","June","July","August","September","October","November","December"};
    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }
    public Date(int year, String month, int day) throws Exception {
        this.year = year;
        for (int i = 0; i < months.length; i++) {
            if (month.equals(months[i])) {
                this.month = i+1;
            }
        }
        this.day = day;
    }
    public String toString() {
        return months[month]+" "+day+", "+year;
    }
}
