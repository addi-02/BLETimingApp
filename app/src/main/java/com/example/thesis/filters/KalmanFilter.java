package com.example.thesis.filters;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class KalmanFilter {
    /** Values during first successful test
    private double q = 0.1;
    private double r = 2;
    private double p = 0.75;
    private double k = 0.1;
    **/
    private double q = 0.1;
    private double r = 2;
    private double p = 0.75;
    private double k = 0.1;

    private ArrayList<Double> recentResultsStart;
    private ArrayList<Double> recentResultsFinish;
    public KalmanFilter() {
    }

    public ArrayList<Double> filterData(ArrayList<Double> data, Boolean isStart) {
        double x = data.get(0);
        ArrayList<Double> filteredData = new ArrayList<>();
        for(int i = 0; i < data.size(); i++) {
            p = p + q;
            k = p / (p + r);
            p = (1 - k) * p;
            x = x + k * (data.get(i) - x);
            filteredData.add(x);
        }

        if(isStart) {
            recentResultsStart = filteredData;
        } else {
            recentResultsFinish = filteredData;
        }
        return filteredData;
    }

    public ArrayList<Double> getRecentResultsStart() {
        return recentResultsStart;
    }
    public ArrayList<Double> getRecentResultsFinish() {
        return recentResultsFinish;
    }
}
