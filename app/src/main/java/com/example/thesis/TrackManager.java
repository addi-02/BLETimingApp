package com.example.thesis;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.lang.reflect.Array;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class TrackManager {
    private ArrayList<Track> tracks;
    private ArrayList<Integer> unsortedRSSI;
    private ArrayList<String> unsortedRSSIAddresses;
    private ArrayList<String> unsortedRSSITimestamps;
    private Map<String, Map<String, ArrayList<Double>>> trackResults;
    private Map<String, Map<String, ArrayList<String>>> timestamps;
    private Context context;

    public TrackManager(Context context) {
        this.tracks = new ArrayList<>();
        this.trackResults = new HashMap<>();
        this.unsortedRSSI = new ArrayList<>();
        this.unsortedRSSIAddresses = new ArrayList<>();
        this.unsortedRSSITimestamps = new ArrayList<>();
        this.timestamps = new HashMap<>();
        this.context = context;
        loadTracks();
        for(Track track : tracks ) {
            track.setContext(context);
            track.loadMacList();
        }
    }

    public ArrayList<Track> getTracks() {
        return this.tracks;
    }

    // Loads the saved tracks from media storage
    public void loadTracks(){
        SharedPreferences prefs = context.getSharedPreferences("track_prefs", Context.MODE_PRIVATE);
        String tracks = prefs.getString("track_names", "");
        for(String name : Arrays.asList(tracks.split(","))) {
            this.tracks.add(new Track(name, new ArrayList<>(), new ArrayList<>()));
        }
    }

    // Calculates the elapsed time from the provided filtered (or unfiltered) RSSI values
    public HashMap<String, String> getElapsedTime(Track track, ArrayList<Double> filteredStart, ArrayList<Double> filteredFinish) {
        double closestStart = Collections.max(filteredStart);
        double closestFinish = Collections.max(filteredFinish);
        int closestValueStart = filteredStart.indexOf(closestStart);
        int closestValueFinish = filteredFinish.indexOf(closestFinish);

        ArrayList<String> startTimes = this.getTrackTimestamps(track, "start");
        ArrayList<String> finishTimes = this.getTrackTimestamps(track, "finish");

        Timestamp start = Timestamp.valueOf(startTimes.get(closestValueStart));
        Timestamp finish = Timestamp.valueOf(finishTimes.get(closestValueFinish));
        long elapsedTime = finish.getTime() - start.getTime();
        long minute = TimeUnit.MILLISECONDS.toMinutes(elapsedTime);
        long second = TimeUnit.MILLISECONDS.toSeconds(elapsedTime) - TimeUnit.MINUTES.toSeconds(minute);
        long millisecond = elapsedTime % 1000;
        HashMap<String, String> times = new HashMap<>();
        String formattedTime = String.format("%d:%02d.%03d", minute, second, millisecond);
        times.put("elapsed", formattedTime);
        times.put("start", start.toString());
        times.put("finish", finish.toString());
        return times;
    }

    // Initializes the HashMaps holding all results
    public void initializeTracks() {
        for(Track track : tracks) {
            try {
                Map<String, ArrayList<Double>> initialValues = new HashMap<>();
                initialValues.put("start", new ArrayList<Double>());
                initialValues.put("finish", new ArrayList<Double>());

                Map<String, ArrayList<String>> initialTimestamps = new HashMap<>();
                initialTimestamps.put("start", new ArrayList<String>());
                initialTimestamps.put("finish", new ArrayList<String>());

                trackResults.put(track.getTrackName(), initialValues);
                timestamps.put(track.getTrackName(), initialTimestamps);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

        }
    }

    // Adds the signal's RSSI value and timestamp to the correct Track and checkpoint
    public void addResult(Track track, String checkpoint, int result, String timestamp) {
        trackResults.get(track.getTrackName()).get(checkpoint).add((double)result);
        timestamps.get(track.getTrackName()).get(checkpoint).add(timestamp);
    }

    // Associates the unsorted results with their respective Track and checkpoint
    public void sortResults() {
        for(int i = 0; i < unsortedRSSIAddresses.size(); i++) {
            String address = unsortedRSSIAddresses.get(i);
            for(Track track : tracks) {
                if(track.getAddressStrings("start").contains(address)) {
                    addResult(track, "start", unsortedRSSI.get(i), unsortedRSSITimestamps.get(i));

                } else if(track.getAddressStrings("finish").contains(address)){
                    addResult(track, "finish", unsortedRSSI.get(i), unsortedRSSITimestamps.get(i));

                }
            }
        }

    }
    public ArrayList<Double> getTrackResult(Track track, String checkpoint) {
        return trackResults.get(track.getTrackName()).get(checkpoint);
    }
    public ArrayList<String> getTrackTimestamps(Track track, String checkpoint) {
        return timestamps.get(track.getTrackName()).get(checkpoint);
    }

    // Adds the results to their own lists to be associated with a Track and
    // checkpoint later, so that the scanCallback doesn't drop any signals
    // due to prolonged execution
    public void addUnsortedRSSI(String address, int value) {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        this.unsortedRSSI.add(value);
        this.unsortedRSSIAddresses.add(address);
        this.unsortedRSSITimestamps.add(timestamp.toString());
    }
}
