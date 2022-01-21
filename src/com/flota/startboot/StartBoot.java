package com.flota.startboot;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.flota.actividades.StartAppBANCARD;

public class StartBoot extends BroadcastReceiver{
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context, StartAppBANCARD.class);
        intent1.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent1);
    }
}
