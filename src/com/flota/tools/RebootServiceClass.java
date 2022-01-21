package com.flota.tools;

import android.app.IntentService;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.flota.actividades.MenusActivity;
import com.newpos.libpay.utils.ISOUtil;
import com.wposs.flota.R;


public class RebootServiceClass extends IntentService {
    public static boolean alarma = false;

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public RebootServiceClass(String name) {
        super(name);
        startForeground(1, new Notification());
    }

    public RebootServiceClass() {
        super("RebootServiceClass");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String intentType = intent.getExtras().getString("caller");
        if (intentType == null) return;
        if (intentType.equals("RebootReceiver")) {
            Log.e("RebootServiceClass", "remove-alm\nmanualOff-false");
            UtilNetwork.removeAlarm(this);
            MenusActivity.setManualOff(false);
        }
    }

    public static boolean isAlarma() {
        return alarma;
    }

    public static void setAlarma(boolean alarma) {
        RebootServiceClass.alarma = alarma;
    }
}
