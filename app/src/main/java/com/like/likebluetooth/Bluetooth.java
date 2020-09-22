package com.like.likebluetooth;

import android.bluetooth.BluetoothDevice;

/**
 * 蓝牙相关操作
 * Created by like on 2020/9/16.
 */

interface Bluetooth {

    /**
     * 搜索蓝牙设备（BLE）
     */
    void scan();

    /**
     * 停止搜索
     */
    void stopScan();

    /**
     * 连接蓝牙设备
     */
    void connectDevice(BluetoothDevice bluetoothDevice);

    /**
     * 断开连接
     */
    void disConnectDevice();

    /**
     * 从蓝牙中读取数据
     */
    void readData();

    /**
     * 像蓝牙中写数据
     */
    void writeData();

    /**
     * 打开蓝牙
     */
    void openBluetooth();

    /**
     * 关闭蓝牙
     */
    void closeBluetooth();

    /**
     * 蓝牙是否打开
     */
    void isBluetoothEnable();
}
