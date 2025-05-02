package com.example.bluetooth_print;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.util.Log;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class BluetoothPrintPlugin implements FlutterPlugin, ActivityAware, MethodCallHandler {

    private static final String TAG = "BluetoothPrintPlugin";
    private MethodChannel channel;
    private EventChannel stateChannel;

    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private Activity activity;
    private Application application;

    // The new method to handle plugin attachment
    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        channel = new MethodChannel(binding.getBinaryMessenger(), "bluetooth_print");
        channel.setMethodCallHandler(this);

        stateChannel = new EventChannel(binding.getBinaryMessenger(), "bluetooth_print_state");
        stateChannel.setStreamHandler(new StreamHandler() {
            @Override
            public void onListen(Object arguments, EventSink events) {
                // Handle streaming events if needed
            }

            @Override
            public void onCancel(Object arguments) {
                // Handle cancellation of the stream
            }
        });
    }

    // The new method for when the plugin is detached
    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        channel.setMethodCallHandler(null);
    }

    // Implement ActivityAware methods
    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
        this.activity = binding.getActivity();
        this.application = (Application) binding.getActivity().getApplicationContext();
        // Initialize Bluetooth manager and adapter when activity is attached
        mBluetoothManager = (BluetoothManager) activity.getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = mBluetoothManager.getAdapter();
    }

    @Override
    public void onDetachedFromActivity() {
        // Clean up if necessary
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {
        // Handle changes if necessary
    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
        this.activity = binding.getActivity();
        this.application = (Application) binding.getActivity().getApplicationContext();
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        // Handle incoming method calls from Flutter
        if (call.method.equals("getBluetoothState")) {
            if (mBluetoothAdapter != null) {
                result.success(mBluetoothAdapter.isEnabled());
            } else {
                result.error("BLUETOOTH_ERROR", "Bluetooth is not available", null);
            }
        } else {
            result.notImplemented();
        }
    }

    // Optional: Permissions handling for Bluetooth and location permissions
    private boolean checkPermissions() {
        // Check if the required permissions are granted
        int bluetoothPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.BLUETOOTH);
        int locationPermission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);

        return bluetoothPermission == PackageManager.PERMISSION_GRANTED &&
                locationPermission == PackageManager.PERMISSION_GRANTED;
    }
}
