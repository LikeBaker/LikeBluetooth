package com.like.likebluetooth.viewmodel;

import android.bluetooth.BluetoothDevice;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created by liuzhen on 2020/9/22.
 */

public class BluetoothModel extends ViewModel {
    private MutableLiveData<BluetoothDevice> braceletLiveData = new MutableLiveData<>();

    public BluetoothModel() {
    }

    public MutableLiveData<BluetoothDevice> getBraceletLiveData() {
        return braceletLiveData;
    }
}
