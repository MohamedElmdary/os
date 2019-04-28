package com.example.osproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.OutputStream;
import java.util.List;

import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

public class MainActivity extends AppCompatActivity {

    private SmoothBluetooth mSmoothBluetooth;
    private TextView d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        d = findViewById(R.id.debug);

        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                d.setText(
                        "isBluetoothAvailable: " + mSmoothBluetooth.isBluetoothAvailable() + "\n"
                                + "isServiceAvailable: " + mSmoothBluetooth.isServiceAvailable() + "\n"
                                + "isConnected: " + mSmoothBluetooth.isConnected() + "\n"
                );
                mSmoothBluetooth.send("d");
            }
        });


        mSmoothBluetooth = new SmoothBluetooth(
                this,
                SmoothBluetooth.ConnectionTo.OTHER_DEVICE,
                SmoothBluetooth.Connection.INSECURE,
                new SmoothBluetooth.Listener() {
                    @Override
                    public void onBluetoothNotSupported() {
                        Toast.makeText(MainActivity.this, "Your device doesn't support blueTooth", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onBluetoothNotEnabled() {
                        Toast.makeText(MainActivity.this, "Please enable your blueTooth", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onConnecting(Device device) {}

                    @Override
                    public void onConnected(final Device device) {
                        d.setText("connected to " + device.getName());


                    }

                    @Override
                    public void onDisconnected() {

                    }

                    @Override
                    public void onConnectionFailed(Device device) {

                    }

                    @Override
                    public void onDiscoveryStarted() {

                    }

                    @Override
                    public void onDiscoveryFinished() {

                    }

                    @Override
                    public void onNoDevicesFound() {

                    }

                    @Override
                    public void onDevicesFound(List<Device> deviceList, SmoothBluetooth.ConnectionCallback connectionCallback) {
                        for (int i = 0; i < deviceList.size(); i++) {
//                            Log.d("Device " + i + " mac: " , deviceList.get(i).getAddress());
                                d.setText(d.getText() + "\nDevice " + i + " mac: " + deviceList.get(i).getName() + "  " +  deviceList.get(i).getAddress());
//                                d.setText(d.getText() + "\n" + deviceList.get(i).getAddress() + ": " + (deviceList.get(i).getAddress() == "98:D3:37:90:F1:70"));
                                if (deviceList.get(i).getAddress().equals("98:D3:37:90:F1:70")) {
                                    d.setText("Found device and start connecting");
                                 connectionCallback.connectTo(deviceList.get(i));
                                 break;
                                }
                        }
                    }

                    @Override
                    public void onDataReceived(int data) {

                    }
                });

        mSmoothBluetooth.tryConnection();
        mSmoothBluetooth.doDiscovery();


    }
}
