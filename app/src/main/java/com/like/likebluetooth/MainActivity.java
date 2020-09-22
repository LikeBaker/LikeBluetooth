package com.like.likebluetooth;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import com.like.likebluetooth.view.BluetoothDevicesAdapter;
import com.like.likebluetooth.viewmodel.BluetoothModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    protected TextView text;
    private RecyclerView rv;
    private BluetoothDevicesAdapter mBluetoothDevicesAdapter;
    private ArrayList<BluetoothDevice> mBluetoothDevices;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        text = findViewById(R.id.text);
        rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        mBluetoothDevices = new ArrayList<>();
        mBluetoothDevicesAdapter = new BluetoothDevicesAdapter(this, mBluetoothDevices);
        rv.setAdapter(mBluetoothDevicesAdapter);

        //创建ViewModel
        ViewModelProvider.AndroidViewModelFactory androidViewModelFactory = new ViewModelProvider.AndroidViewModelFactory(getApplication());
        BluetoothModel bluetoothModel = androidViewModelFactory.create(BluetoothModel.class);
//        bluetoothModel.getBraceletLiveData().setValue();
        bluetoothModel.getBraceletLiveData().observe(this, new Observer<BluetoothDevice>() {
            @Override
            public void onChanged(BluetoothDevice bluetoothDevice) {
                //新增的bluetoothDevice
                if (bluetoothDevice.getName() != null){
                    String msg = bluetoothDevice.getAddress() + " " + bluetoothDevice.getName();
                    Log.d("MainActivity", msg);
                    text.setText(msg);

                } else {
                    Log.d("MainActivity", bluetoothDevice.getAddress());
                    text.setText(bluetoothDevice.getAddress());
                }

                mBluetoothDevices.add(bluetoothDevice);
                mBluetoothDevicesAdapter.notifyDataSetChanged();
            }
        });

        Bluetooth bluetoothUtil = new BluetoothImpl(this, new LikeHandler(this), bluetoothModel);
        bluetoothUtil.scan();

    }

    private static class LikeHandler extends Handler{
        WeakReference<MainActivity> mActivity;

        public LikeHandler(MainActivity activity) {
            this.mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            BluetoothDevice bluetoothDevice = msg.getData().getParcelable("BluetoothScanDevice");
            if (bluetoothDevice != null && bluetoothDevice.getAddress() != null) {
                if (bluetoothDevice.getName() != null) {
                    Log.d("MainActivity", bluetoothDevice.getName() + " " + bluetoothDevice.getAddress());
                    String text = bluetoothDevice.getName() + " " + bluetoothDevice.getAddress();
                    mActivity.get().text.setText(text);
                } else {
                    Log.d("MainActivity", bluetoothDevice.getAddress());
                    mActivity.get().text.setText(bluetoothDevice.getAddress());
                }

            }
        }
    }
}