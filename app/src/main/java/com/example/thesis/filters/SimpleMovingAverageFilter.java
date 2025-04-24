package com.example.thesis.filters;

import java.util.ArrayList;

public class SimpleMovingAverageFilter {
    private ArrayList<Double> recentResultsStart;
    private ArrayList<Double> recentResultsFinish;
    public SimpleMovingAverageFilter() {

    }

    public ArrayList<Double> filterData(ArrayList<Double> data, Boolean isStart) {
        ArrayList<Double> filteredData = new ArrayList<Double>();
        int n = 3;
        for (int i = 0; i < data.size(); i++) {
            if (i + 1 < n) {
                continue;
            }
            double newRSSI = ((double) 1 / n) * (data.get(i - 2) + data.get(i - 1) + data.get(i));
            filteredData.add(newRSSI);
        }
        if (isStart) {
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
