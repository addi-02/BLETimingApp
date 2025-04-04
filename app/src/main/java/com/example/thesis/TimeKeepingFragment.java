package com.example.thesis;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.thesis.filters.ExponentialMovingAverageFilter;
import com.example.thesis.filters.KalmanFilter;
import com.example.thesis.filters.SimpleMovingAverageFilter;

import org.w3c.dom.Text;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class TimeKeepingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    /**
     * A simple {@link Fragment} subclass.
     * Use the {@link TimeKeepingFragment#newInstance} factory method to
     * create an instance of this fragment.
     */

    private KalmanFilter kmnFilter = new KalmanFilter();
    private ExponentialMovingAverageFilter emaFilter = new ExponentialMovingAverageFilter();
    private SimpleMovingAverageFilter smaFilter = new SimpleMovingAverageFilter();
    private ArrayList<Double> rssiResults = new ArrayList<Double>();
    private ArrayList<String> timestamps = new ArrayList<String>();
    private boolean isScanning = false;
    private static final int REQUEST_CODE_PERMISSIONS = 1001;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothLeScanner bluetoothLeScanner;
    private MutableLiveData<String> rssiData = new MutableLiveData<>();
    public TimeKeepingFragment() {
        // Required empty public constructor
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            String deviceAddress = result.getDevice().getAddress();
            if(Objects.equals(deviceAddress, "C4:3D:1A:F6:D3:61")) {
                rssiResults.add((double)result.getRssi());
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                timestamps.add(timestamp.toString());
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.e("BLE", "Scan failed with error code: " + errorCode);
        }
    };
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TimeKeepingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TimeKeepingFragment newInstance(String param1, String param2) {
        TimeKeepingFragment fragment = new TimeKeepingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_time_keeping, container, false);

        // Buttons for selection of the individual filtering algorithms
        Button btnKalman = v.findViewById(R.id.button_kalman);
        Button btnEMA = v.findViewById(R.id.button_ema);
        Button btnSMA = v.findViewById(R.id.button_sma);

        btnKalman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isScanning) {
                    ArrayList<Double> filtered = new ArrayList<>();

                    /**
                     * Dummy data, a noisy -x^2 plot
                     * double[] test = new double[]{
                            -22.63, -25.09, -20.26, -21.74, -18.22, -20.66, -13.64, -12.87, -10.54, -10.95,
                            -6.76, -10.21, -7.79, -7.77, -6.14, -2.63, -2.08, 2.99, -0.08, -1.78,
                            1.24, -1.86, -3.29, -1.25, -3.44, -1.37, 5.11, -3.53, 0.57, -1.13,
                            1.44, -1.80, -5.61, -1.34, -5.63, -3.48, -6.47, -7.69, -8.37, -8.71,
                            -10.85, -12.59, -11.96, -15.07, -12.42, -16.73, -16.19, -20.23, -24.16, -23.49
                    };**/

                    filtered = kmnFilter.filterData(rssiResults);
                    double closest = Collections.max(filtered);
                    int closestValue = filtered.indexOf(closest);
                    rssiData.setValue(timestamps.get(closestValue));
                    Log.w("test", filtered.toString());
                }
            }
        });

        // Initialization of the Bluetooth classes
        BluetoothManager bluetoothManager = (BluetoothManager) requireContext().getSystemService(getContext().BLUETOOTH_SERVICE);
        bluetoothAdapter = bluetoothManager.getAdapter();
        bluetoothLeScanner = bluetoothAdapter.getBluetoothLeScanner();

        Button btnStartScan = v.findViewById(R.id.button_start_scan);
        TextView textScanning = v.findViewById(R.id.text_is_scanning);
        btnStartScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isScanning) {
                    startScan();
                } else {
                    stopScan();
                }
            }
        });

        // Observer for the RSSI values so that the text displaying the current measured RSSI can
        // be updated in real-time
        final Observer<String> rssiObserver = new Observer<String>() {
            @Override
            public void onChanged(String newRssi) {
                textScanning.setText(newRssi);
            }
        };
        rssiData.observe(getViewLifecycleOwner(), rssiObserver);
        // Inflate the layout for this fragment
        return v;
    }

    private void startScan() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (bluetoothLeScanner != null) {
                // Starts the BLE scanner using the callback function to define wanted behaviour upon receiving a signal
                bluetoothLeScanner.startScan(scanCallback);
                isScanning = true;
                Log.w("TAG", "Bluetooth scanning started.");
            } else {
                Log.e("TAG", "Bluetooth LE Scanner is null.");
            }
        } else {
            requestPermissions(new String[]{
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_CODE_PERMISSIONS);
        }
    }
    private void stopScan() {
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (bluetoothLeScanner != null) {
                // Starts the BLE scanner using the callback function to define wanted behaviour upon receiving a signal
                bluetoothLeScanner.stopScan(scanCallback);
                isScanning = false;
                Log.w("TAG", "Bluetooth scanning stopped.");
            } else {
                Log.e("TAG", "Bluetooth LE Scanner is null.");
            }
        } else {
            requestPermissions(new String[]{
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.ACCESS_FINE_LOCATION
            }, REQUEST_CODE_PERMISSIONS);
        }
    }
}