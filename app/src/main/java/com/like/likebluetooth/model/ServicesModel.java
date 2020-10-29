package com.like.likebluetooth.model;

import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;

import java.util.List;

/**
 * Created by liuzhen on 2020/10/19.
 */

public class ServicesModel {

    private BluetoothGattService mService;
    private List<BluetoothGattCharacteristic> mCharacteristics;

    private boolean expanded = true;

    public ServicesModel(BluetoothGattService service) {
        this.mService = service;
        this.mCharacteristics = service.getCharacteristics();
    }

    public boolean isExpanded() {
        return expanded;
    }

    public void setExpanded(boolean expanded) {
        this.expanded = expanded;
    }

    public BluetoothGattService getService() {
        return mService;
    }

    public void setmService(BluetoothGattService mService) {
        this.mService = mService;
    }

    public List<BluetoothGattCharacteristic> getCharacteristics() {
        return mCharacteristics;
    }

    public void setmCharacteristics(List<BluetoothGattCharacteristic> mCharacteristics) {
        this.mCharacteristics = mCharacteristics;
    }
}
