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
        String hourStr = "";
        String minuteStr = "";
        if (hour < 10) {
            hourStr = "0"+String.valueOf(hour);
        } else {
            hourStr = String.valueOf(hour);
        }
        if (minute < 10) {
            minuteStr = "0"+String.valueOf(minute);
        } else {
            minuteStr = String.valueOf(minute);
        }
        return hourStr+":"+minuteStr;//+" "+timezone;
    }
}
