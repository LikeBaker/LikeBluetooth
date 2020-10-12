package com.like.likebluetooth.viewmodel;

import android.bluetooth.BluetoothDevice;

/**
 * Created by 刘振 on 2020/10/12.
 */

public class ScanListModel {
    private BluetoothDevice bluetoothDevice;
    //信号强度
    private String rssi;

    public ScanListModel(BluetoothDevice bluetoothDevice, String rssi) {
        this.bluetoothDevice = bluetoothDevice;
        this.rssi = rssi;
    }

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public String getRssi() {
        return rssi;
    }

    public void setRssi(String rssi) {
        this.rssi = rssi;
    }
}
