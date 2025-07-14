package com.example.localloop.resources.datetime;

import com.example.localloop.resources.exception.InvalidTimeException;

public class Time {
    // 24-hour time
    private int hour;
    private int minute;
    private String timezone;
    public Time() {
        // Empty constructor is required by Firebase
    }
    public Time(int hour, int minute) throws InvalidTimeException {
        if (hour < 0 || hour > 23) {
            throw new InvalidTimeException("Invalid hour");
        }
        if (minute < 0 || minute > 59) {
            throw new InvalidTimeException("Invalid minute");
        }
        this.hour = hour;
        this.minute = minute;
        this.timezone = "EST";
    }
    public Time(int hour, int minute, String timezone) throws InvalidTimeException {
        if (hour < 0 || hour > 23) {
            throw new InvalidTimeException("Invalid hour");
        }
        if (minute < 0 || minute > 59) {
            throw new InvalidTimeException("Invalid minute");
        }
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
    public String getTimezone() { return timezone; }
    public String toString() {
        return hour+":"+minute+" "+timezone;
    }
}
