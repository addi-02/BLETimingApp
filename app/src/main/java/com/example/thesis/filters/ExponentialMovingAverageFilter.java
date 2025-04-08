package com.example.thesis.filters;

import java.util.ArrayList;

public class ExponentialMovingAverageFilter {

    public ExponentialMovingAverageFilter() {

    }

    public ArrayList<Double> filterData(ArrayList<Double> data) {
        int n = 5;
        double a = (double) 2 /(n+1);
        double prevRSSI = data.get(0);
        double newRSSI = 0;
        ArrayList<Double> filteredData = new ArrayList<>();
        for(int i = 0; i < data.size(); i++) {
            if(i < 1) {
                filteredData.add(data.get(0));
                continue;
            }
            newRSSI = a* data.get(i) +(1-a)*prevRSSI;

            filteredData.add(newRSSI);
            prevRSSI = newRSSI;
        }
        return filteredData;
    }
}
