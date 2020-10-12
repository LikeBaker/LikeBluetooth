package com.like.likebluetooth;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.transition.Transition;
import android.transition.TransitionListenerAdapter;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.transition.platform.MaterialContainerTransform;
import com.like.likebluetooth.view.BluetoothDevicesAdapter;
import com.like.likebluetooth.view.IMainView;
import com.like.likebluetooth.viewmodel.BluetoothViewModel;
import com.like.likebluetooth.viewmodel.ScanListModel;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements IMainView {

    public static final String TAG = "_bluetooth";

    protected TextView text;
    private BluetoothDevicesAdapter mBluetoothDevicesAdapter;
    private ArrayList<ScanListModel> mBluetoothDevices;
    private Bluetooth mBluetoothUtil;
    private ExtendedFloatingActionButton optBtn;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        optBtn = findViewById(R.id.opt_float_btn);
        optBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (mBluetoothUtil.isScan()) {
//                    Log.d(TAG, "click stop scan");
//                    mBluetoothUtil.stopScan();
//                } else {
//                    Log.d(TAG, "click scan");
//                    mBluetoothUtil.scan();
//                }
//
//                scanStateChanged(mBluetoothUtil.isScan());

                showScanPanel();

//                showMenu(v);
            }
        });

        text = findViewById(R.id.text);

        RecyclerView rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this));
        mBluetoothDevices = new ArrayList<>();
        mBluetoothDevicesAdapter = new BluetoothDevicesAdapter(this, mBluetoothDevices);
        rv.setAdapter(mBluetoothDevicesAdapter);

        //创建ViewModel
        ViewModelProvider.AndroidViewModelFactory androidViewModelFactory = new ViewModelProvider.AndroidViewModelFactory(getApplication());
        BluetoothViewModel bluetoothModel = androidViewModelFactory.create(BluetoothViewModel.class);
//        bluetoothModel.getBraceletLiveData().setValue();
        bluetoothModel.getBraceletLiveData().observe(this, scanListModel -> {
            //新增的bluetoothDevice
            if (scanListModel.getBluetoothDevice().getName() != null) {
                String msg = scanListModel.getBluetoothDevice().getAddress() + " " + scanListModel.getBluetoothDevice().getName();
                Log.d("MainActivity", msg);
                text.setText(msg);

            } else {
                Log.d("MainActivity", scanListModel.getBluetoothDevice().getAddress());
                text.setText(scanListModel.getBluetoothDevice().getAddress());
            }

            if (!mBluetoothDevices.contains(scanListModel)) {

                mBluetoothDevices.add(scanListModel);
                mBluetoothDevicesAdapter.notifyDataSetChanged();
            } else {
                Log.d("MainActivity", "already have");
            }
        });

        MutableLiveData<BluetoothGatt> braceletGatt = bluetoothModel.getBraceletGatt();
        braceletGatt.observe(this, new Observer<BluetoothGatt>() {
            @Override
            public void onChanged(BluetoothGatt bluetoothGatt) {
                Log.d(TAG, "gatt changes");
            }
        });

        mBluetoothUtil = new BluetoothImpl(this, new LikeHandler(this), bluetoothModel);


    }

    public void connectDevice(BluetoothDevice bluetoothDevice) {
        mBluetoothUtil.connectDevice(bluetoothDevice);
    }

    @Override
    public void scanStateChanged(boolean isScan) {
        optBtn.setText(isScan ? R.string.stopScan : R.string.scan);
        /*if (isScan) {
            optBtn.extend();
        } else {
            optBtn.shrink();//收缩到只有icon的状态
        }*/
    }

    /**
     * 初始化菜单 title右上角出现的按钮
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.like_menu, menu);
        return true;
    }

    private void showScanPanel() {
        ConstraintLayout container = findViewById(R.id.container);
        CardView mTvTransform = findViewById(R.id.view_transform_end);

//        MaterialFadeThrough materialFadeThrough = new MaterialFadeThrough();
        MaterialContainerTransform materialContainerTransform = new MaterialContainerTransform();
        materialContainerTransform.setStartView(optBtn);
        materialContainerTransform.setEndView(mTvTransform);
        materialContainerTransform.addTarget(mTvTransform);
        materialContainerTransform.setDuration(500);
        materialContainerTransform.setScrimColor(Color.TRANSPARENT);
        materialContainerTransform.setContainerColor(Color.TRANSPARENT);

        materialContainerTransform.addListener(new TransitionListenerAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                super.onTransitionEnd(transition);

                optBtn.setVisibility(View.GONE);

                Log.d(TAG, "scan");
                mBluetoothUtil.scan();

            }
        });
        TransitionManager.beginDelayedTransition(container, materialContainerTransform);

        mTvTransform.setVisibility(View.VISIBLE);
//        showMenu(optBtn);
    }

    private void closeScanPanel(){
        ConstraintLayout container = findViewById(R.id.container);
        CardView mTvTransform = findViewById(R.id.view_transform_end);

//        MaterialFadeThrough materialFadeThrough = new MaterialFadeThrough();
        MaterialContainerTransform materialContainerTransform = new MaterialContainerTransform();
        materialContainerTransform.setStartView(mTvTransform);
        materialContainerTransform.setEndView(optBtn);
        materialContainerTransform.addTarget(optBtn);
        materialContainerTransform.setDuration(500);
        materialContainerTransform.setScrimColor(Color.TRANSPARENT);

        materialContainerTransform.addListener(new TransitionListenerAdapter() {
            @Override
            public void onTransitionEnd(Transition transition) {
                super.onTransitionEnd(transition);
                Log.d(TAG, "onTransitionEnd");
                Log.d(TAG, transition.getName());
                Log.d(TAG, "transition.getDuration():" + transition.getDuration());

                mTvTransform.setVisibility(View.GONE);
                optBtn.setVisibility(View.VISIBLE);
            }
        });

        TransitionManager.beginDelayedTransition(container, materialContainerTransform);
    }

    public void showMenu(View anchor) {
        PopupMenu popup = new PopupMenu(this, anchor, Gravity.END);
        popup.getMenuInflater().inflate(R.menu.like_menu, popup.getMenu());
        popup.show();
    }

    public void closeScanPanel(View view) {
        closeScanPanel();
    }

    private static class LikeHandler extends Handler {
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