package com.like.likebluetooth;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.like.likebluetooth.viewmodel.BluetoothViewModel;

import java.util.List;

import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static com.like.likebluetooth.MainActivity.TAG;

/**
 * Created by like on 2020/9/16.
 */

public class BluetoothImpl implements Bluetooth {

    private final Context mContext;
    private final BluetoothAdapter mBluetoothAdapter;
    private final BluetoothLeScanner mBluetoothLeScanner;
    private final BluetoothViewModel mBluetoothDeviceModel;
    private ScanCallback mScanCallback;
    private BluetoothGattCallback mBluetoothGattCallback;
    private Handler mDataHandler;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)//todo
    public BluetoothImpl(Context context, Handler handler, BluetoothViewModel bluetoothModel) {
        this.mContext = context;
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        mDataHandler = handler;
        this.mBluetoothDeviceModel = bluetoothModel;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void scan() {
        if (mScanCallback == null) {
            mScanCallback = new ScanCallBack(mDataHandler, mBluetoothDeviceModel);
        }

        mBluetoothLeScanner.startScan(mScanCallback);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void stopScan() {
        if (mScanCallback == null && mDataHandler != null) {
            mScanCallback = new ScanCallBack(mDataHandler);
        }

        mBluetoothLeScanner.stopScan(mScanCallback);
    }

    @Override
    public void connectDevice(BluetoothDevice bluetoothDevice) {
        //todo 从优化角度考虑，context如何选择
        if (mBluetoothGattCallback == null) {
            mBluetoothGattCallback = new BluetoothGattCallBack(mBluetoothDeviceModel);
        }

        Log.d(TAG, "connectGatt");
        bluetoothDevice.connectGatt(mContext, false, mBluetoothGattCallback);
    }

    @Override
    public void disConnectDevice() {

    }

    @Override
    public void readData() {

    }

    @Override
    public void writeData() {

    }

    @Override
    public void openBluetooth() {

    }

    @Override
    public void closeBluetooth() {

    }

    @Override
    public void isBluetoothEnable() {

    }
}

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
class ScanCallBack extends ScanCallback {

    private BluetoothViewModel mViewModel;
    private Handler mHandler;

    public ScanCallBack(Handler mHandler) {
        this.mHandler = mHandler;
    }

    public ScanCallBack(Handler mDataHandler, BluetoothViewModel mViewModel) {
        this.mViewModel = mViewModel;
        this.mHandler = mDataHandler;
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {
        super.onScanResult(callbackType, result);

        BluetoothDevice device = result.getDevice();
        if (device!=null){

            //handler 方式
//            Bundle bundle = new Bundle();
//            bundle.putParcelable("BluetoothScanDevice", device);
//            Message message = new Message();
//            message.setData(bundle);
//            mHandler.sendMessage(message);

            //ViewModel 方式
            mViewModel.getBraceletLiveData().setValue(device);
        }
    }

    @Override
    public void onScanFailed(int errorCode) {
        super.onScanFailed(errorCode);
    }
}

class BluetoothGattCallBack extends BluetoothGattCallback {
    private final BluetoothViewModel mBluetoothDeviceModel;

    public BluetoothGattCallBack(BluetoothViewModel bluetoothDeviceModel) {
        this.mBluetoothDeviceModel = bluetoothDeviceModel;
    }

    @Override
    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
        super.onConnectionStateChange(gatt, status, newState);

        Log.d("BluetoothGattCallBack", "onConnectionStateChange");

        if (newState == STATE_CONNECTED) {
            gatt.discoverServices();
        }
    }

    @Override
    public void onServicesDiscovered(BluetoothGatt gatt, int status) {
        super.onServicesDiscovered(gatt, status);

        Log.d("BluetoothGattCallBack", "onServicesDiscovered");

//        List<BluetoothGattService> services = gatt.getServices();
//            mBluetoothDeviceModel.getBraceletGatt().setValue(gatt);//报错Cannot invoke setValue on a background thread
            Log.d(TAG, "post value");
            mBluetoothDeviceModel.getBraceletGatt().postValue(gatt);

    }

    @Override
    public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicRead(gatt, characteristic, status);
    }

    @Override
    public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
        super.onCharacteristicWrite(gatt, characteristic, status);
    }
}





