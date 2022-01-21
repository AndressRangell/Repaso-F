package com.flota.actividades;

import static android.provider.Settings.Global.AIRPLANE_MODE_ON;
import static com.flota.actividades.StartAppBANCARD.tablaComercios;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.flota.adapters.UAFactory;
import com.flota.adapters.model.CategoryModel;
import com.flota.adapters.model.InfoModel;
import com.flota.defines_bancard.DefinesBANCARD;
import com.flota.inicializacion.configuracioncomercio.ChequeoIPs;
import com.flota.inicializacion.configuracioncomercio.IPS;
import com.flota.inicializacion.trans_init.trans.Tools;
import com.newpos.libpay.Logger;
import com.wposs.flota.BuildConfig;
import com.wposs.flota.R;

import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cn.desert.newpos.payui.master.FormularioActivity;
import cn.desert.newpos.payui.master.mToolbar;
import cn.desert.newpos.payui.master.onToolbarClick;

public class InfoActivity extends FormularioActivity {

    public static final String TAG = "InfoActivity.java";

    public static String getMacAddr() {
        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface nif : all) {
                String nifName = nif.getName();
                if (!nifName.equalsIgnoreCase("wlan0") && !nifName.equalsIgnoreCase("eth0")) {
                    continue;
                }
                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }
                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02x:", b));
                }
                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }
                return res1.toString();
            }
        } catch (Exception ex) {
            Logger.error("Error getting mac address ", ex);
            ex.printStackTrace();
        }
        return "02:00:00:00:00:00";
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.initView(new mToolbar(R.layout.activity_info, R.drawable.ic__back, 0), new onToolbarClick() {
            @Override
            public void onClick() {
                onBackPressed();
                overridePendingTransition(0, 0);
            }
        }, null);

        String menu = getIntent().getStringExtra("menu");
        genRcyMenu(menu == null ? " " : menu);
    }

    private void genRcyMenu(String menu) {
        final List<CategoryModel> list;
        @LayoutRes final int layout;

        if (menu.equals(DefinesBANCARD.CONFI_INFO)) {
            list = getItemsConfigInfo();
            layout = R.layout.item_info_config;
            findViewById(R.id.logo_info).setVisibility(View.GONE);
            TextView title = findViewById(R.id.title_info);
            title.setVisibility(View.VISIBLE);
            title.setText(menu);
        } else {
            list = getItemsInfo();
            layout = R.layout.item_info;
        }

        RecyclerView recycler = findViewById(R.id.recycler_info);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        recycler.setAdapter(UAFactory.adapterCategory(this, layout, list));
    }

    private List<CategoryModel> getItemsConfigInfo() {
        List<CategoryModel> list = new ArrayList<>();
        IPS ip;
        for (int i = 0; i < ChequeoIPs.getLengIps(); i++) {
            ip = ChequeoIPs.seleccioneIP(i);
            List<InfoModel> listInfo = new ArrayList<>();
            listInfo.add(new InfoModel("ID", ip.getIdIp()));
            listInfo.add(new InfoModel("Nombre", ip.getIdIp()));
            listInfo.add(new InfoModel("Ip Host", ip.getIp()));
            listInfo.add(new InfoModel("Puerto", ip.getPuerto()));
            listInfo.add(new InfoModel("TLS", String.valueOf(ip.isTls())));
            listInfo.add(new InfoModel("Cliente", String.valueOf(ip.isAutenticarCliente())));
            list.add(new CategoryModel("IP " + i, listInfo));
        }
        return list;
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("HardwareIds")
    private List<CategoryModel> getItemsInfo() {
        List<InfoModel> listInfo;
        List<CategoryModel> list = new ArrayList<>();

        // Información del comercio
        String nameCommerce = tablaComercios.sucursal.getDescripcion();
        list.add(new CategoryModel("Información del comercio", InfoModel.justSubtitle(nameCommerce)));

        // Dispositivo
        listInfo = new ArrayList<>();
        listInfo.add(new InfoModel("IP", IPS.getIPAddress(true)));
        try {
            String address = getMacAddr();
            Log.i(TAG, "getInfo: MAC ADD: " + address);
            listInfo.add(new InfoModel("MAC", address));
            TelephonyManager manager = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
            listInfo.add(new InfoModel("Operador", manager.getNetworkOperatorName()));
            listInfo.add(new InfoModel("IMEI", manager.getDeviceId()));
        } catch (Exception e) {
            Logger.exception(TAG, e);
            e.printStackTrace();
        }
        listInfo.add(new InfoModel("Modo avión", isAirplaneModeOn()));
        listInfo.add(new InfoModel("Android", Build.VERSION.RELEASE));
        listInfo.add(new InfoModel("Serial", Tools.getSerial()));
        list.add(new CategoryModel("Dispositivo", listInfo));

        // Información de app
        listInfo = new ArrayList<>();
        listInfo.add(new InfoModel("Nombre", getString(R.string.app_name)));
        listInfo.add(new InfoModel("Versión", BuildConfig.VERSION_NAME));
        listInfo.add(new InfoModel("Tipo de app", BuildConfig.BUILD_TYPE));
        StringBuilder databases = new StringBuilder("");
        boolean separator = false;
        for (String db : getApplicationContext().databaseList()) {
            if (separator) databases.append(", ");
            else separator = true;
            databases.append(db);
        }
        listInfo.add(new InfoModel("Nombre db", databases.toString()));
        list.add(new CategoryModel("Información de app", listInfo));
        return list;
    }

    private String isAirplaneModeOn() {
        if (Settings.System.getInt(this.getContentResolver(), AIRPLANE_MODE_ON, 0) != 0) {
            return "Si";
        } else {
            return "No";
        }
    }
}
