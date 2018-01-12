package com.example.myapplication;

import java.util.Calendar;

/**
 * Created by 曜多 on 2017/11/26.
 */

public class Unit{
    double rssi1;
    double rssi2;
    double rssi3;
    int hour;
    int min;
    int sec;
    String x;
    String y;
    Unit(){
        Calendar time = Calendar.getInstance();
        hour = time.get(Calendar.HOUR);
        min = time.get(Calendar.MINUTE);
        sec = time.get(Calendar.SECOND);
        rssi1 = 0;
        rssi2 = 0;
        rssi3 = 0;
    }

    public double getRssi1() {

        return rssi1;
    }

    public double getRssi2() {
        return rssi2;
    }

    public double getRssi3() {
        return rssi3;
    }

    public int getHour() {
        return hour;
    }

    public int getMin() {
        return min;
    }

    public int getSec() {
        return sec;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }
}