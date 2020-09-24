package com.like.likebluetooth.view;

import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.like.likebluetooth.MainActivity;
import com.like.likebluetooth.R;
import com.like.likebluetooth.info.BluetoothDeviceInfoActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by liuzhen on 2020/9/22.
 */

public class BluetoothDevicesAdapter extends RecyclerView.Adapter {
    private final MainActivity mActivity;
    private final List<BluetoothDevice> mList;

    public BluetoothDevicesAdapter(MainActivity mainActivity, ArrayList<BluetoothDevice> bluetoothDevices) {
        this.mActivity = mainActivity;
        this.mList = bluetoothDevices;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mActivity).inflate(R.layout.item_bluetooth_device, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        ViewHolder vh = (ViewHolder) holder;

        if (position == mList.size() - 1) {
            appearAnimation(vh.itemView);
        }

        BluetoothDevice bluetoothDevice = mList.get(position);
        if (bluetoothDevice.getName() != null) {
            vh.name.setText(bluetoothDevice.getName());
        } else {
            vh.name.setText("无名字蓝牙设备");
        }

        vh.address.setText(bluetoothDevice.getAddress());

        vh.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.connectDevice(mList.get(position));
                mActivity.startActivity(new Intent(mActivity, BluetoothDeviceInfoActivity.class));
            }
        });
    }

    @Override
    public int getItemCount() {
        if (mList != null) {
            return mList.size();
        } else {
            return 0;
        }
    }

    private static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, address;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.name);
            address = itemView.findViewById(R.id.address);
        }
    }

    private void appearAnimation(View v) {
        v.setAlpha(0.f);
        v.animate().alpha(1.f).setDuration(500).start();
    }
}
