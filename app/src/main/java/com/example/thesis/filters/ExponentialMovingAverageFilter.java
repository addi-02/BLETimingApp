package com.example.thesis.filters;

import java.util.ArrayList;

public class ExponentialMovingAverageFilter {
    // Used to get the results after processing when saving to media storage
    private ArrayList<Double> recentResultsStart;
    private ArrayList<Double> recentResultsFinish;
    public ExponentialMovingAverageFilter() {

    }

    public ArrayList<Double> filterData(ArrayList<Double> data, Boolean isStart) {
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
