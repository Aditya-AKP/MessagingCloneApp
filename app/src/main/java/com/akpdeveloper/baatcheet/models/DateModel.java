package com.akpdeveloper.baatcheet.models;

import com.google.firebase.Timestamp;

import java.util.Date;

public class DateModel {
    Long seconds;
    int nanoseconds;

    public DateModel() {
    }

    public DateModel(Long seconds, int nanoseconds) {
        this.seconds = seconds;
        this.nanoseconds = nanoseconds;
    }

    public static DateModel now() {
        Timestamp ts = Timestamp.now();
        return new DateModel(ts.getSeconds(),ts.getNanoseconds());
    }

    public Date toDate() {
        return new Timestamp(seconds,nanoseconds).toDate();
    }

    public Long getSeconds() {
        return seconds;
    }

    public void setSeconds(Long seconds) {
        this.seconds = seconds;
    }

    public int getNanoseconds() {
        return nanoseconds;
    }

    public void setNanoseconds(int nanoseconds) {
        this.nanoseconds = nanoseconds;
    }


}
