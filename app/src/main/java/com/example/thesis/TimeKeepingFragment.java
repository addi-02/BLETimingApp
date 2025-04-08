package com.example.thesis;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.thesis.filters.ExponentialMovingAverageFilter;
import com.example.thesis.filters.KalmanFilter;
import com.example.thesis.filters.SimpleMovingAverageFilter;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Array;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
            if(Objects.equals(deviceAddress, "CD:D7:E9:7E:31:9E")) {
                rssiResults.add((double)result.getRssi());
                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                timestamps.add(timestamp.toString());
                rssiData.setValue(String.valueOf(result.getRssi()));

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
        TextView textTimestamps = v.findViewById(R.id.text_timestamps);
        btnKalman.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isScanning) {
                    ArrayList<Double> filtered = new ArrayList<>();

                    try {
                        filtered = kmnFilter.filterData(rssiResults);

                    } catch (Exception e) {
                        rssiData.setValue("Filtering failed");
                    }
                    saveToDownloadsWithMediaStore(requireContext(), filtered, "rssi_values");
                    try {
                        double closest = Collections.max(filtered);
                        int closestValue = filtered.indexOf(closest);
                        textTimestamps.setText(timestamps.get(closestValue));
                    } catch (Exception e) {
                        rssiData.setValue("Getting time failed" + filtered.toString());
                        textTimestamps.setText(timestamps.toString());
                    }

                }
            }
        });

        btnEMA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isScanning) {
                    ArrayList<Double> filtered = new ArrayList<>();

                    try {
                        filtered = emaFilter.filterData(rssiResults);

                    } catch (Exception e) {
                        rssiData.setValue("Filtering failed");
                    }
                    saveToDownloadsWithMediaStore(requireContext(), filtered, "rssi_values");
                    try {
                        double closest = Collections.max(filtered);
                        int closestValue = filtered.indexOf(closest);
                        textTimestamps.setText(timestamps.get(closestValue));
                    } catch (Exception e) {
                        rssiData.setValue("Getting time failed" + filtered.toString());
                        textTimestamps.setText(timestamps.toString());
                    }

                }
            }
        });

        btnSMA.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!isScanning) {
                    ArrayList<Double> filtered = new ArrayList<>();

                    try {
                        filtered = smaFilter.filterData(rssiResults);

                    } catch (Exception e) {
                        rssiData.setValue("Filtering failed");
                    }
                    saveToDownloadsWithMediaStore(requireContext(), filtered, "rssi_values");
                    try {
                        double closest = Collections.max(filtered);
                        int closestValue = filtered.indexOf(closest);
                        textTimestamps.setText(timestamps.get(closestValue));
                    } catch (Exception e) {
                        rssiData.setValue("Getting time failed" + filtered.toString());
                        textTimestamps.setText(timestamps.toString());
                    }

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

    public void saveToDownloadsWithMediaStore(Context context, ArrayList<Double> data, String filename) {
        String mimeType = "text/plain";
        String relativeLocation = Environment.DIRECTORY_DOWNLOADS;

        ContentValues contentValues = new ContentValues();
        contentValues.put(MediaStore.Downloads.DISPLAY_NAME, filename);
        contentValues.put(MediaStore.Downloads.MIME_TYPE, mimeType);
        contentValues.put(MediaStore.Downloads.RELATIVE_PATH, relativeLocation);

        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);

        if (uri != null) {
            try (OutputStream outputStream = resolver.openOutputStream(uri)) {
                for (Double value : data) {
                    String line = value.toString() + "\n";
                    outputStream.write(line.getBytes(StandardCharsets.UTF_8));
                }
                outputStream.flush();
                Toast.makeText(context, "Saved to Downloads", Toast.LENGTH_SHORT).show();
                Log.d("MediaStore", "Saved to: " + uri.toString());
            } catch (IOException e) {
                Log.e("MediaStore", "Write failed: " + e.getMessage());
            }
        } else {
            Log.e("MediaStore", "Failed to create MediaStore entry");
        }
    }

}