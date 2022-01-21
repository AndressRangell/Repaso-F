package interactor.utils;

import android.content.Context;
import android.content.pm.PackageInfo;

import com.flota.adapters.model.AppModel;
import com.flota.inicializacion.configuracioncomercio.APLICACIONES;
import com.newpos.libpay.utils.ISOUtil;

import java.util.ArrayList;
import java.util.List;

public class MoreAppsImpl implements MoreAppsUtil {

    // List Package More Apps
    public static final String PACKAGE_NAME_POLARIS = "com.downloadmanager";
    public static final String PACKAGE_NAME_CREDITO_DEBITO = "com.wposs.bancard";
    public static final String PACKAGE_NAME_LEALTAD = "com.wposs.bancard_lealtad";
    public static final String PACKAGE_NAME_COBRANZAS = "com.wposs.cobranzas";

    Context context;

    public MoreAppsImpl(Context context) {
        this.context = context;
    }

    @Override
    public List<AppModel> getListApps(ArrayList<APLICACIONES> applications) {
        List<AppModel> appList = new ArrayList<>();
        appList.add(new AppModel("POLARIS CLOUD", PACKAGE_NAME_POLARIS));
        for (APLICACIONES application : applications) {
            switch (application.getNameApp()) {
                case "CREDITO-DEBITO":
                    if (ISOUtil.stringToBoolean(application.getActive()))
                        appList.add(new AppModel(application.getNameApp(), PACKAGE_NAME_CREDITO_DEBITO));
                    break;
                case "LEALTAD":
                    if (ISOUtil.stringToBoolean(application.getActive()))
                        appList.add(new AppModel(application.getNameApp(), PACKAGE_NAME_LEALTAD));
                    break;
                case "INFONET-COBRANZAS":
                    if (ISOUtil.stringToBoolean(application.getActive()))
                        appList.add(new AppModel(application.getNameApp(), PACKAGE_NAME_COBRANZAS));
                    break;
                default:
                    break;
            }
        }
        return appList;
    }

    @Override
    public boolean appIsInstalled(String packageFaceId) {
        if (packageFaceId != null && !packageFaceId.isEmpty()) {
            for (PackageInfo info : context.getPackageManager().getInstalledPackages(0)) {
                if (info.packageName.equals(packageFaceId)) return true;
            }
        }
        return false;
    }
}
