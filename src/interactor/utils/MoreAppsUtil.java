package interactor.utils;

import com.flota.adapters.model.AppModel;
import com.flota.inicializacion.configuracioncomercio.APLICACIONES;

import java.util.ArrayList;
import java.util.List;

public interface MoreAppsUtil {

    List<AppModel> getListApps(ArrayList<APLICACIONES> applications);

    boolean appIsInstalled(String packageFaceId);
}
