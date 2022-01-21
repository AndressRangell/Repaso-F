package cn.desert.newpos.payui.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.newpos.libpay.Logger;
import com.newpos.libpay.PaySdk;
import com.newpos.libpay.PaySdkException;
import com.newpos.libpay.PaySdkListener;
import com.pos.device.printer.Printer;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zhouqiang on 2017/7/3.
 */

public class PayApplication extends Application {

    private static final String APP_RUN = "app_run";
    private static final String APP_DEK = "app_des";
    private static PayApplication app;
    private final List<Activity> mList = new LinkedList<>();
    String clase = "PayApplication.java";
    private SharedPreferences runPreferences;
    private SharedPreferences.Editor runEditor;
    private SharedPreferences dekPreferences;
    private SharedPreferences.Editor dekEditor;

    public static PayApplication getInstance() {
        return app;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        app = this;
        initPaysdk();
    }

    private void initPaysdk() {
        runPreferences = getSharedPreferences(APP_RUN, MODE_PRIVATE);
        runEditor = runPreferences.edit();
        dekPreferences = getSharedPreferences(APP_DEK, MODE_PRIVATE);
        dekEditor = dekPreferences.edit();
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PaySdk.getInstance().init(app, new PaySdkListener() {
                        @Override
                        public void success() {
                            Logger.debug("sdk init ok");
                            int status = Printer.getInstance().getStatus();
                            Logger.debug("print sta:" + status);
                        }
                    });
                } catch (PaySdkException e) {
                    e.printStackTrace();
                      Logger.exception(clase, e);
                }
            }
        }).start();

    }

    public void addActivity(Activity activity) {
        mList.add(activity);
    }

    public void exit() {
        PaySdk.getInstance().exit();
        //结束栈中
        try {
            for (Activity activity : mList) {
                if (activity != null) {
                    activity.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.exception(clase, e);
        } finally {
            System.exit(0);
            System.gc();
        }
    }

    public void setRunned() {
        runEditor.clear().commit();
        runEditor.putBoolean(APP_RUN, true).commit();
    }

    public boolean isRunned() {
        return runPreferences.getBoolean(APP_RUN, false);
    }

    /**
     * 经典桌面与简约桌面
     *
     * @return
     */
    public boolean isClassical() {
        return dekPreferences.getBoolean(APP_DEK, false);
    }

    public void setClassical(boolean classical) {
        dekEditor.clear().commit();
        dekEditor.putBoolean(APP_DEK, classical).commit();
    }

    public boolean isAppInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        List<String> pName = new ArrayList<>();
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                pName.add(pn);
            }
        }
        return pName.contains(packageName);
    }
}
