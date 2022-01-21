package interactor.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;

public class SecurityImpl {

    private SecurityImpl() {
    }

    @SuppressWarnings("deprecation")
    public static boolean isNamedProcessRunning(Context ctx, String processName) {
        ActivityManager manager = (ActivityManager) ctx.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            String serv = service.service.getClassName();
            if (serv.contains(processName)) {
                return true;
            }
            Log.i("SecurityImpl", "Process " + serv);
        }
        return false;
    }

    public static boolean isSafeMode(Context context) {
        PackageManager pm = context.getApplicationContext().getPackageManager();
        return pm.isSafeMode();
    }
}
