package com.example.localloop.resources.datetime;

public class Time {
    // 24-hour time
    private int hour;
    private int minute;
    private String timezone;
    public Time(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
        this.timezone = "EST";
    }
    public Time(int hour, int minute, String timezone) {
        this.hour = hour;
        this.minute = minute;
        this.timezone = timezone;
    }
    public int getHour() {
        return hour;
    }
    public int getMinute() {
        return minute;
    }
    public String toString() {
        return hour+":"+minute+" "+timezone;
    }
}
