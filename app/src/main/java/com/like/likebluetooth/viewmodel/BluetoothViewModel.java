package com.like.likebluetooth.viewmodel;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created by liuzhen on 2020/9/22.
 */

public class BluetoothViewModel extends ViewModel {
    private MutableLiveData<ScanListModel> braceletLiveData = new MutableLiveData<>();
    private MutableLiveData<BluetoothGatt> braceletGatt = new MutableLiveData<>();

    public BluetoothViewModel() {
    }

    public MutableLiveData<ScanListModel> getBraceletLiveData() {
        return braceletLiveData;
    }

    public MutableLiveData<BluetoothGatt> getBraceletGatt() {
        return braceletGatt;
    }
}
