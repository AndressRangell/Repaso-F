package com.flota.inicializacion.tools;

import static com.flota.actividades.StartAppBANCARD.listadoIps;
import static com.flota.actividades.StartAppBANCARD.listadoPlanes;
import static com.flota.actividades.StartAppBANCARD.listadoTransacciones;
import static com.flota.actividades.StartAppBANCARD.tablaCards;
import static com.flota.actividades.StartAppBANCARD.tablaComercios;
import static com.flota.actividades.StartAppBANCARD.tablaDevice;
import static com.flota.actividades.StartAppBANCARD.tablaHost;
import static com.flota.actividades.StartAppBANCARD.tablaIp;
import static com.flota.actividades.StartAppBANCARD.tablaTransacciones;
import static com.flota.actividades.StartAppBANCARD.transActive;
import static com.flota.inicializacion.trans_init.Init.NAME_DB;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.flota.actividades.StartAppBANCARD;
import com.flota.inicializacion.configuracioncomercio.APLICACIONES;
import com.flota.inicializacion.configuracioncomercio.CARDS;
import com.flota.inicializacion.configuracioncomercio.COMERCIOS;
import com.flota.inicializacion.configuracioncomercio.ChequeoIPs;
import com.flota.inicializacion.configuracioncomercio.Device;
import com.flota.inicializacion.configuracioncomercio.HOST;
import com.flota.inicializacion.configuracioncomercio.IPS;
import com.flota.inicializacion.configuracioncomercio.Red;
import com.flota.inicializacion.configuracioncomercio.TRANSACCIONES;
import com.flota.inicializacion.configuracioncomercio.Tareas;
import com.flota.inicializacion.configuracioncomercio.TransActive;
import com.flota.inicializacion.trans_init.trans.DbHelper;
import com.flota.setting.ListSetting;
import com.flota.setting.ListTransacciones;
import com.flota.transactions.Billeteras.Billeteras;
import com.newpos.libpay.Logger;
import com.wposs.flota.R;

import java.util.ArrayList;

import cn.desert.newpos.payui.UIUtils;

public class PolarisUtil {

    private static final String TAG = "PolarisUtil.java";

    public PolarisUtil() {
        // Contructor vacío
    }

    /**
     * isInitPolaris check Stis Table
     *
     * @param context - Activity's context
     * @return true or false
     * @author Francisco Mahecha
     * @version 1.0
     */
    public static boolean isInitPolaris(Context context) {
        int countRow;
        int counterTables = 1;

        boolean APLICACIONES = false;
        boolean CAPKS = false;
        boolean CARDS = false;
        boolean COMERCIOS = false;
        boolean DEVICE = false;
        boolean HOST = false;
        boolean IPS = false;
        boolean PLANES = false;
        boolean RED = false;
        boolean SUCURSAL = false;
        boolean TRANSACCIONES = false;
        boolean EMVAPPS = false;
        boolean BILLETERAS = false;
        boolean emvappsdebug = false;
        boolean tareas = false;

        //Read packages
        DbHelper databaseAccess = new DbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);


        String sql = consultaSQL();

        try {

            Logger.error(TAG, "SQL ---- " + sql);
            Cursor cursor = databaseAccess.rawQuery(sql);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                countRow = cursor.getInt(0);

                switch (counterTables) {
                    case 1:
                        APLICACIONES = verificacionTabla(context, countRow, "APLICACIONES");
                        break;
                    case 2:
                        CAPKS = verificacionTabla(context, countRow, "CAPKS");
                        break;
                    case 3:
                        CARDS = verificacionTabla(context, countRow, "CARDS");
                        break;
                    case 4:
                        COMERCIOS = verificacionTabla(context, countRow, "COMERCIOS");
                        break;
                    case 5:
                        DEVICE = verificacionTabla(context, countRow, "DEVICE");
                        break;
                    case 6:
                        HOST = verificacionTabla(context, countRow, "HOST");
                        break;
                    case 7:
                        IPS = verificacionTabla(context, countRow, "IPS");
                        break;
                    case 8:
                        PLANES = verificacionTabla(context, countRow, "PLANES");
                        break;
                    case 9:
                        RED = verificacionTabla(context, countRow, "RED");
                        break;
                    case 10:
                        SUCURSAL = verificacionTabla(context, countRow, "SUCURSAL");
                        break;
                    case 11:
                        TRANSACCIONES = verificacionTabla(context, countRow, "TRANSACCIONES");
                        break;
                    case 12:
                        EMVAPPS = verificacionTabla(context, countRow, "EMVAPPS");
                        break;
                    case 13:
                        BILLETERAS = verificacionTabla(context, countRow, "BILLETERAS");
                        break;
                    case 14:
                        if (countRow == 0) {
                            tareas = false;
                        } else {
                            tareas = true;
                        }
                        break;
                    case 15:
                        emvappsdebug = true;
                        break;
                    default:
                        break;
                }

                counterTables = counterTables + 1;
                cursor.moveToNext();
            }
            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.error(TAG, e);
            Logger.exception(TAG, e);
        }
        databaseAccess.closeDb();

        Logger.error(TAG, "counterTables " + counterTables);
        Logger.error(TAG, "APLICACIONES " + APLICACIONES);
        Logger.error(TAG, "CAPKS " + CAPKS);
        Logger.error(TAG, "CARDS " + CARDS);
        Logger.error(TAG, "COMERCIOS " + COMERCIOS);
        Logger.error(TAG, "DEVICE " + DEVICE);
        Logger.error(TAG, "HOST " + HOST);
        Logger.error(TAG, "IPS " + IPS);
        Logger.error(TAG, "PLANES " + PLANES);
        Logger.error(TAG, "RED " + RED);
        Logger.error(TAG, "SUCURSAL " + SUCURSAL);
        Logger.error(TAG, "TRANSACCIONES " + TRANSACCIONES);
        Logger.error(TAG, "EMVAPPS " + EMVAPPS);
        Logger.error(TAG, "BILLETERAS " + BILLETERAS);
        Logger.error(TAG, "emvappsdebug " + emvappsdebug);
        Logger.error(TAG, "tareas " + tareas);

        return counterTables == 15 && APLICACIONES && CAPKS && CARDS && COMERCIOS && DEVICE &&
                HOST && IPS && PLANES && RED && SUCURSAL && TRANSACCIONES && EMVAPPS && BILLETERAS;
    }

    public static boolean verificacionTabla(Context context, int countRow, String tabla) {
        if (countRow == 0) {
            showMensaje(context, "Tabla " + tabla + " vacía ");
            return false;
        } else {
            return true;
        }
    }

    private static String consultaSQL() {
        StringBuilder sql = new StringBuilder();
        final String UNION_ALL = "union all ";
        sql.append("select count (*) from APLICACIONES ");
        sql.append(UNION_ALL);
        sql.append("select count (*) from CAPKS ");
        sql.append(UNION_ALL);
        sql.append("select  count (*) from CARDS ");
        sql.append(UNION_ALL);
        sql.append("select  count (*) from COMERCIOS ");
        sql.append(UNION_ALL);
        sql.append("select  count (*) from DEVICE ");
        sql.append(UNION_ALL);
        sql.append("select  count (*) from HOST ");
        sql.append(UNION_ALL);
        sql.append("select  count (*) from IPS ");
        sql.append(UNION_ALL);
        sql.append("select count (*) from PLANES ");
        sql.append(UNION_ALL);
        sql.append("select count (*) from RED ");
        sql.append(UNION_ALL);
        sql.append("select  count (*) from SUCURSAL ");
        sql.append(UNION_ALL);
        sql.append("select  count (*) from TRANSACCIONES ");
        sql.append(UNION_ALL);
        sql.append("select  count (*) from EMVAPPS ");
        sql.append(UNION_ALL);
        sql.append("select  count (*) from billeteras ");
        sql.append(UNION_ALL);
        sql.append("select  count (*) from tareas");

        return sql.toString();
    }

    private static void showMensaje(Context context, String mensaje) {
        if (context != null)
            UIUtils.toast((Activity) context, R.drawable.ic_redinfonet, mensaje, Toast.LENGTH_LONG);
        else
            Logger.debug("Info", "showMensaje: " + "context == null");
    }

    /**
     * Mostrar en pantalla si falla si falla query de lectura de alguna
     * tabla de la BD
     *
     * @param nameTable
     */
    private void showErrMsg(String nameTable, Context context) {
        StartAppBANCARD.isInit = false;
        UIUtils.toast((Activity) context, R.drawable.ic_redinfonet, "Error al leer tabla ," + nameTable + "\n Por favor Inicialice nuevamente", Toast.LENGTH_LONG);
    }

    /**
     * Permite tener toda la informacion pertinente al comercio antes de
     * mostrar el menu principal
     *
     * @param context
     */
    public void leerBaseDatos(Context context) {
        StartAppBANCARD.isInit = PolarisUtil.isInitPolaris(context);
        if (StartAppBANCARD.isInit && Device.selectTConfig(context) &&
                tablaComercios.selectComercios(context) && tablaHost.selectHostConfi(context)) {
            if (!Billeteras.getInstance(false, context).inicializandoComponentes(context)) {
                showErrMsg("BILLETERAS", context);
            }

            Tareas.getInstance(false).inicializandoComponentes(context);
            if (!Red.getInstance(false).inicializandoComponentes(context)) {
                showErrMsg("RED", context);
            }
            if (!APLICACIONES.checksAppsActive(context)) {
                showErrMsg(APLICACIONES.NAME_TABLE, context);
            }
            if ((listadoTransacciones = tablaTransacciones.selectTRANSACCIONES(context)) != null) {
                transActive.verificateTransActive(listadoTransacciones);
            } else {
                showErrMsg(TRANSACCIONES.NAME_TABLE, context);
            }
            if ((listadoIps = ChequeoIPs.selectIP(context)) == null) {
                showErrMsg(IPS.NAME_TABLE, context);
            }
        }
    }

    /**
     * Instancia todos los objetos necesarios para el manejo de la
     * inicializacion del PSTIS
     */
    public void initObjetPSTIS(Context context) {


        APLICACIONES.setAplicacionesNull();
        ListTransacciones.setModeloMenusOpcionesNull();
        ListSetting.setModelSettingsNull();

        //----------- Init Bancard-----------
        if (tablaHost == null) {
            tablaHost = HOST.getSingletonInstance();
        }

        if (listadoIps == null) {
            listadoIps = new ArrayList<>();
        }

        if (tablaIp == null) {
            tablaIp = new IPS(context);
        }

        if (tablaCards == null) {
            tablaCards = CARDS.getSingletonInstance(context);
        }

        if (tablaTransacciones == null) {
            tablaTransacciones = TRANSACCIONES.getSingletonInstance(context);
        }

        if (tablaDevice == null) {
            tablaDevice = Device.getSingletonInstance(context);
        }

        if (tablaComercios == null) {
            tablaComercios = COMERCIOS.getSingletonInstance(context);
        }

        APLICACIONES.getSingletonInstance();

        Billeteras.getInstance(true, context);

        Red.getInstance(true);

        Tareas.getInstance(true);


        if (listadoTransacciones == null) {
            listadoTransacciones = new ArrayList<>();
        }

        if (listadoPlanes == null) {
            listadoPlanes = new ArrayList<>();
        }

        if (transActive == null) {
            transActive = new TransActive();
        }

        //--------- limpiar datos----------
        if (tablaDevice != null) {
            Device.clearTConf();
        }

        if (tablaComercios != null) {
            tablaComercios.clearCOMERCIOS();
        }


        if (listadoTransacciones != null) {
            listadoTransacciones.clear();
        }

        if (tablaHost != null) {
            tablaHost.clearHostConfi();
        }

        if (listadoIps != null) {
            listadoIps.clear();
        }

        if (listadoPlanes != null) {
            listadoPlanes.clear();
        }
    }
}
