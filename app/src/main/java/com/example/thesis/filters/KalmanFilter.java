package com.example.thesis.filters;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class KalmanFilter {

    private double q = 0.1;
    private double r = 2;
    private double p = 0.75;
    private double k = 0.1;
    public KalmanFilter() {
    }

    public ArrayList<Double> filterData(double[] data) {
        double x = data[0];
        ArrayList<Double> filteredData = new ArrayList<>();
        for(int i = 0; i < data.length; i++) {
            p = p + q;
            k = p / (p + r);
            p = (1 - k) * p;
            x = x + k * (data[i] - x);
            filteredData.add(x);
        }

        return filteredData;
    }
}
