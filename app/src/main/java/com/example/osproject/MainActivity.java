package com.example.osproject;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

public class MainActivity extends AppCompatActivity {

    private final String mAddress = "20:15:11:23:82:28";
    private SmoothBluetooth mSmoothBluetooth;
    private class MoveButton {
        public Button btn;
        public String action;
        public MoveButton(int btn, String action) {
            this.btn = findViewById(btn);
            this.action = action;
        }
    }
    private MoveButton[] btns = new MoveButton[4];
    private ConstraintLayout load, controller;
    private TextView loading, error;
    private Button enablebt, again;


    private enum LoadingTypes {
        LOADING,
        SEARCHING,
        CONNECTING,
        ENABLE_BLUETOOTH
    }
    private void showLoad(LoadingTypes event) {
        String txt;
        switch (event) {
            case LOADING:
                txt = "Loading...";
                break;
            case SEARCHING:
                txt = "Searching...";
                break;
            case CONNECTING:
                txt = "Connecting...";
                break;
            case ENABLE_BLUETOOTH:
                txt = "Enabling Bluetooth...";
                break;
            default:
                return;
        }
            loading.setText(txt);
            load.setVisibility(View.VISIBLE);
    }

    private  void addEventListener(final MoveButton instance) {
        instance.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmoothBluetooth.send(instance.action);
            }
        });
    }

    private enum ErrorTypes {
        ERROR,
        WARNING
    }
    private void showError(String txt, ErrorTypes type) {
        switch (type) {
            case ERROR:
                txt = "Error: " + txt;
                error.setTextColor(Color.parseColor("#ff3d00"));
                break;
            case WARNING:
                txt = "Warning: " + txt;
                error.setTextColor(Color.parseColor("#fbc02d"));
                break;
            default:
                return;
        }
        error.setVisibility(View.VISIBLE);
        error.setText(txt);
    }


    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();

            if (action.equals(BluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE,
                        BluetoothAdapter.ERROR);
                switch (state) {

                    case BluetoothAdapter.STATE_OFF:
                        hideAll();
                        showError("Please enable your blueTooth.", ErrorTypes.WARNING);
                        break;
                    case BluetoothAdapter.STATE_ON:
                        error.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mSmoothBluetooth.tryConnection();
                                mSmoothBluetooth.doDiscovery();
                            }
                        }, 1000);
                        break;
                    case BluetoothAdapter.STATE_TURNING_ON:
                        hideAll();
                        showLoad(LoadingTypes.ENABLE_BLUETOOTH);
                        break;
                }
            }
        }
    };

    private void hideAll() {
        load.setVisibility(View.INVISIBLE);
        controller.setVisibility(View.INVISIBLE);
        error.setVisibility(View.INVISIBLE);
        again.setVisibility(View.INVISIBLE);
        again.setOnClickListener(null);
        enablebt.setVisibility(View.INVISIBLE);
        enablebt.setOnClickListener(null);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        IntentFilter filter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(mReceiver, filter);
        btns[0] = new MoveButton(R.id.front, "f");
        btns[1] = new MoveButton(R.id.back, "b");
        btns[2] = new MoveButton(R.id.left, "l");
        btns[3] = new MoveButton(R.id.right, "r");
        load = findViewById(R.id.load);
        controller = findViewById(R.id.controller);
        loading = findViewById(R.id.loading);
        error = findViewById(R.id.error);
        again = findViewById(R.id.again);
        enablebt = findViewById(R.id.enablebt);


        mSmoothBluetooth = new SmoothBluetooth(
                this,
                SmoothBluetooth.ConnectionTo.OTHER_DEVICE,
                SmoothBluetooth.Connection.INSECURE,
                new SmoothBluetooth.Listener() {
                    @Override
                    public void onBluetoothNotSupported() {
                        hideAll();
                        showError("Your device doesn't support blueTooth.", ErrorTypes.ERROR);
                    }

                    @Override
                    public void onBluetoothNotEnabled() {
                        hideAll();
                        showError("Please enable your blueTooth.", ErrorTypes.WARNING);
                        enablebt.setVisibility(View.VISIBLE);
                        enablebt.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                                mBluetoothAdapter.enable();
                            }
                        });
                    }

                    @Override
                    public void onConnecting(Device device) {
                        hideAll();
                        showLoad(LoadingTypes.CONNECTING);
                    }


                    @Override
                    public void onConnected(final Device device) {
                        hideAll();
                        Toast.makeText(MainActivity.this, "Connected!", Toast.LENGTH_SHORT).show();
                        controller.setVisibility(View.VISIBLE);
                        for (int i = 0, max = btns.length; i < max; addEventListener(btns[i++]));
                    }

                    @Override
                    public void onDisconnected() {
                        hideAll();
                        for (int i = 0, max = btns.length; i < max; btns[i++].btn.setOnClickListener(null));
                        Toast.makeText(MainActivity.this, "Disconnected!", Toast.LENGTH_SHORT).show();
                        showLoad(LoadingTypes.CONNECTING);
                        mSmoothBluetooth.tryConnection();
                        mSmoothBluetooth.doDiscovery();
                    }

                    @Override
                    public void onConnectionFailed(Device device) {
                        hideAll();
                        if (device == null) {
                            onNoDevicesFound();
                        } else {
                            showError("Some thing went wrong while connecting", ErrorTypes.ERROR);
                        }
                    }

                    @Override
                    public void onDiscoveryStarted() {
                        hideAll();
                        showLoad(LoadingTypes.SEARCHING);
                    }

                    @Override
                    public void onDiscoveryFinished() {
                        hideAll();
                        Toast.makeText(MainActivity.this, "Searching completed!", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onNoDevicesFound() {
                        hideAll();
                        showError("No device was found!", ErrorTypes.WARNING);
                        again.setVisibility(View.VISIBLE);
                        again.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                    mSmoothBluetooth.tryConnection();
                                    mSmoothBluetooth.doDiscovery();
                                } catch (Exception e) {
                                    onNoDevicesFound();
                                    showError("Something went wrong while reconnecting!", ErrorTypes.ERROR);
                                }
                            }
                        });
                    }

                    @Override
                    public void onDevicesFound(List<Device> deviceList, SmoothBluetooth.ConnectionCallback connectionCallback) {
                        hideAll();
                        for (int i = 0; i < deviceList.size(); i++) {
                                if (deviceList.get(i).getAddress().equals(mAddress)) {
                                     connectionCallback.connectTo(deviceList.get(i));
                                     break;
                                }
                        }
                    }

                    @Override
                    public void onDataReceived(int data) { /* won't happen on our case! */ }
                });

        mSmoothBluetooth.tryConnection();
        mSmoothBluetooth.doDiscovery();


    }
}
