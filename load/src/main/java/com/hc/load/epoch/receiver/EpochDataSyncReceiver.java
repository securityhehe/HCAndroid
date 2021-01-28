package com.hc.load.epoch.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hc.load.epoch.callback.ObservableObject;


public class EpochDataSyncReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        ObservableObject.getInstance().updateValue(intent);
    }
}
