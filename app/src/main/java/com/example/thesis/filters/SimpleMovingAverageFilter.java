package com.example.thesis.filters;

import java.util.ArrayList;

public class SimpleMovingAverageFilter {

    public SimpleMovingAverageFilter() {

    }

    public ArrayList<Double> filterData(ArrayList<Double> data) {
        ArrayList<Double> filteredData = new ArrayList<Double>();
        int n = 3;
        for(int i = 0; i < data.size(); i++) {
            if(i+1 < n) {
                continue;
            }
            double newRSSI = ((double) 1 /n)*(data.get(i - 2) + data.get(i - 1) + data.get(i));
            filteredData.add(newRSSI);
        }
        return filteredData;
    }
}
