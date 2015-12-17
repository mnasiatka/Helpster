package com.example.helpster;

import android.widget.TimePicker;

public class TimeFormatter {

    TimePicker tp;
    String time;

    public TimeFormatter() {
        this.tp = null;
    }

    public TimeFormatter(TimePicker tp) {
        this.tp = tp;
    }

    public String format(TimePicker tp) {
        this.tp = tp;
        return format();
    }

    public String format() {
        Integer hour, minute;
        String tod;
        hour = tp.getCurrentHour();
        minute = tp.getCurrentMinute();
        if (hour == 0) {
            hour = 12;
            tod = "am";
        } else if (hour > 12) {
            tod = "pm";
            hour -= 12;
        } else if (hour == 12) {
            tod = "pm";
        } else {
            tod = "am";
        }
        return String.format("%d:%d%s", hour, minute, tod);
    }

}
