package com.example.thesis;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Track implements Serializable {

    private Map<String, Map<String, ArrayList<MacAddress>>> track = new HashMap<>();
    private Context context;

    public Track(String name, ArrayList<MacAddress> start, ArrayList<MacAddress> finish) {
        Map<String, ArrayList<MacAddress>> trackBeacons = new HashMap<>();
        trackBeacons.put("start", start);
        trackBeacons.put("finish", finish);
        this.track.put(name, trackBeacons);
    }

    public String getTrackName() {
        return this.track.keySet().iterator().next();
    }

    // Used to get the MAC address class instances
    public ArrayList<MacAddress> getAddresses(String checkpoint) {
        return this.track.get(getTrackName()).get(checkpoint);
    }

    // Used to get the actual addresses from each MAC address in String form
    public ArrayList<String> getAddressStrings(String checkpoint) {
        ArrayList<String> addresses = new ArrayList<>();
        for(MacAddress address : Objects.requireNonNull(this.track.get(getTrackName()).get(checkpoint))) {
            addresses.add(address.getAddress());
        }
        return addresses;
    }

    // The functions below save/load the MAC address Strings to/from the phone's storage
    public void saveMacListStart(ArrayList<MacAddress> newAddresses) {
        SharedPreferences prefs = context.getSharedPreferences("mac_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        ArrayList<String> macs = new ArrayList<>();
        for (MacAddress item : newAddresses) {
            macs.add(item.getAddress());
        }
        editor.putString(getTrackName() + "_start", TextUtils.join(",", macs));
        editor.apply();
    }

    public void saveMacListFinish(ArrayList<MacAddress> newAddresses) {
        SharedPreferences prefs = context.getSharedPreferences("mac_prefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        ArrayList<String> macs = new ArrayList<>();
        for (MacAddress item : newAddresses) {
            macs.add(item.getAddress());
        }
        editor.putString(getTrackName() + "_finish", TextUtils.join(",", macs));
        editor.apply();
    }

    public void loadMacList() {
        SharedPreferences prefs = context.getSharedPreferences("mac_prefs", Context.MODE_PRIVATE);
        String start = prefs.getString(getTrackName()+"_start", "");
        String finish = prefs.getString(getTrackName()+"_finish", "");
        ArrayList<MacAddress> startAddresses = new ArrayList<>();
        ArrayList<MacAddress> finishAddresses = new ArrayList<>();
        if (!start.isEmpty()) {
            for (String mac : start.split(",")) {
                startAddresses.add(new MacAddress(mac));
            }
        }
        if (!finish.isEmpty()) {
            for (String mac : finish.split(",")) {
                finishAddresses.add(new MacAddress(mac));
            }
        }

        Objects.requireNonNull(this.track.get(getTrackName())).put("start", startAddresses);
        Objects.requireNonNull(this.track.get(getTrackName())).put("finish", finishAddresses);
    }
    public void setContext(Context context) {
        this.context = context;
    }
}
