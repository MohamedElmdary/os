package com.example.osproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.List;

import io.palaima.smoothbluetooth.Device;
import io.palaima.smoothbluetooth.SmoothBluetooth;

public class MainActivity extends AppCompatActivity {

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

    private  void addEventListener(final MoveButton instance) {
        instance.btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmoothBluetooth.send(instance.action);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btns[0] = new MoveButton(R.id.front, "f");
        btns[1] = new MoveButton(R.id.back, "b");
        btns[2] = new MoveButton(R.id.left, "l");
        btns[3] = new MoveButton(R.id.right, "r");


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
                        for (int i = 0; i < 4; addEventListener(btns[i++]));
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
                                if (deviceList.get(i).getAddress().equals("20:15:11:23:82:28")) {
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
