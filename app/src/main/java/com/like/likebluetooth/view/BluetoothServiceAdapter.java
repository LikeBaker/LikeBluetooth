package com.like.likebluetooth.view;

import android.bluetooth.BluetoothGattCharacteristic;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import com.like.likebluetooth.MainActivity;
import com.like.likebluetooth.R;
import com.like.likebluetooth.model.ServicesModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.like.likebluetooth.MainActivity.TAG;

/**
 * Created by liuzhen on 2020/10/19.
 */

public class BluetoothServiceAdapter extends Adapter<RecyclerView.ViewHolder>{

    private static final int SERVICE = 0;
    private static final int CHARACTERISTIC = 1;

    private final MainActivity mActivity;
    private final List<ServicesModel> mData;
    private Map<String, List<BluetoothGattCharacteristic>> bluetoothItems;

    //记录title位置
    private List<Integer> servicePos;
    //按顺序记录位置
    private List<String> contents;
    private int mCount;

    public BluetoothServiceAdapter(MainActivity mainActivity, List<ServicesModel> services) {
        this.mActivity = mainActivity;
        this.mData = services;


        //todo 顺序ListOrderedMap
        bluetoothItems = new HashMap<>();
        contents = new ArrayList<>();
        mCount = 0;
        servicePos = new ArrayList<>();
        for (ServicesModel model : services) {
            bluetoothItems.put(model.getService().getUuid().toString(), model.getmCharacteristics());

//            Log.d(TAG, "service " + model.getService().getUuid().toString());
            servicePos.add(mCount);
            mCount += model.getmCharacteristics().size() + 1;
            contents.add(model.getService().getUuid().toString());
            for (BluetoothGattCharacteristic characteristic : model.getmCharacteristics()) {
//                Log.d(TAG, "characteristic " + characteristic.getUuid().toString());
                contents.add(characteristic.getUuid().toString());
            }
        }

//        Log.d(TAG, "初始化的servicePos " + servicePos);
//        Log.d(TAG, "初始化的contents长度 " + contents.size());
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == SERVICE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
            return new ServiceViewHolder(view);
        } else {
//           CHARACTERISTIC
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_characteristic, parent, false);
            return new CharacteristicViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (servicePos.contains(position)) {
            ServiceViewHolder serviceViewHolder = (ServiceViewHolder) holder;
            serviceViewHolder.service.setText(contents.get(position));

            serviceViewHolder.rootView.setOnClickListener(v -> {

//                Log.d(TAG, "点击的position " + position);

                int servicePosChangedPos = servicePos.indexOf(position);

                if (servicePosChangedPos == -1){
                    try {
                        throw new Exception("servicePos is invalid " + position);
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.e(TAG, e.toString());
                        return;
                    }
                }

                ServicesModel model = mData.get(servicePosChangedPos);
//                Log.d(TAG, model.getService().getUuid().toString());
                model.setExpanded(!model.isExpanded());

                serviceViewHolder.imgArrow.setImageResource(
                        model.isExpanded() ? R.drawable.ic_keyboard_arrow_up_black_18dp : R.drawable.ic_keyboard_arrow_down_black_18dp);

                if (model.isExpanded()) {
                    List<BluetoothGattCharacteristic> insertCharacteristics = model.getmCharacteristics();
                    int insertItemSize = insertCharacteristics.size();

                    ArrayList<String> insertCharacteristicList = new ArrayList<>();
                    for (int i=0; i<insertCharacteristics.size(); i++) {
                        insertCharacteristicList.add(insertCharacteristics.get(i).getUuid().toString());
                    }

                    //在position后一位插入，不要覆盖service id
                    contents.addAll(position+1, insertCharacteristicList);

                    for (int i=servicePosChangedPos + 1; i<servicePos.size(); i++) {
                        int pos = servicePos.get(i);
                        pos += insertItemSize;
                        servicePos.set(i, pos);
                    }

                    mCount += insertItemSize;

                    notifyItemRangeInserted(position+1, insertItemSize);
                    notifyItemRangeChanged(position+1, contents.size()-(position+1));

                } else {

                    int removeItemCount = 0;
                    List<BluetoothGattCharacteristic> bluetoothGattCharacteristics = model.getmCharacteristics();
                    if (bluetoothGattCharacteristics != null && bluetoothGattCharacteristics.size() > 0) {
                        removeItemCount = bluetoothGattCharacteristics.size();
//                        Log.d(TAG, "需要删除的characteristic数量 " + removeItemCount);
                    }

                    ArrayList<String> removeContents = new ArrayList<>();
                    for (int i = position+1; i < position+1+removeItemCount; i++) {
                        removeContents.add(contents.get(i));
                    }

                    contents.removeAll(removeContents);

                    for (int i=servicePosChangedPos + 1; i<servicePos.size(); i++) {
                        int pos = servicePos.get(i);
                        pos -= removeItemCount;
                        servicePos.set(i, pos);
                    }

//                    Log.d(TAG, "调整后的servicePos " + servicePos);
//                    for ( int  i = 0 ;i < servicePos.size();i++){
//                        System.out.println(servicePos.get(i) + ' ');
//                    }

//                    System.out.println('\n');

                    mCount -= removeContents.size();
//                    Log.d(TAG, "调整后的content长度 " + contents.size());
                    notifyItemRangeRemoved(position+1, removeItemCount);
                    notifyItemRangeChanged(position+1, contents.size()-(position+1));
                }
            });

        } else {
            CharacteristicViewHolder characteristicViewHolder = (CharacteristicViewHolder) holder;
            characteristicViewHolder.characteristic.setText(contents.get(position));
        }
    }

    @Override
    public int getItemCount() {
        // TODO: 2020/10/21 要运行多少遍 ？
//        Log.d(TAG, "getItemCount " + mCount);
        return mCount;
    }

    @Override
    public int getItemViewType(int position) {

//        Log.d(TAG, "getItemViewType");

        if (servicePos.contains(position)) {
            return SERVICE;
        } else {
            return CHARACTERISTIC;
        }
    }

    static class ServiceViewHolder extends RecyclerView.ViewHolder {

        View rootView;
        TextView service;
        ImageView imgArrow;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);

            rootView = itemView.findViewById(R.id.root_view);
            service = itemView.findViewById(R.id.tv_service);
            imgArrow = itemView.findViewById(R.id.imgArrow);

        }
    }

    static class CharacteristicViewHolder extends RecyclerView.ViewHolder {

        TextView characteristic;

        public CharacteristicViewHolder(@NonNull View itemView) {
            super(itemView);

            characteristic = itemView.findViewById(R.id.tv_characteristic);
        }
    }
}
