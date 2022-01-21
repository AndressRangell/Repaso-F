package com.flota.tools;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

public class BatteryStatus extends BroadcastReceiver {

    static BatteryStatus batteryStatus;

    int levelBattery;
    boolean isCharging = false;

    public static BatteryStatus getInstance() {
        if (batteryStatus == null) {
            batteryStatus = new BatteryStatus();
        }
        return batteryStatus;
    }

    public int getLevelBattery() {
        return levelBattery;
    }

    public boolean isCharging() {
        return isCharging;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        this.levelBattery = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        this.isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING || status == BatteryManager.BATTERY_STATUS_FULL;
    }
}
