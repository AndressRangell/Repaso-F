package com.flota.setting;

import static android.content.Context.WIFI_SERVICE;
import static com.flota.actividades.StartAppBANCARD.tablaComercios;

import android.content.Context;
import android.net.wifi.WifiManager;

import com.flota.adapters.model.ButtonModel;
import com.flota.adapters.model.CategoryModel;
import com.flota.adapters.model.InfoModel;
import com.flota.adapters.model.SettingModel;
import com.flota.defines_bancard.DefinesBANCARD;
import com.flota.inicializacion.configuracioncomercio.Device;
import com.flota.inicializacion.trans_init.trans.Tools;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.trans.translog.TransLogData;
import com.pos.device.net.eth.EthernetManager;
import com.wposs.flota.R;

import java.util.ArrayList;
import java.util.List;

public class ListSetting {

    private static List<SettingModel> settings;
    private static List<SettingModel> listadoInicializacion;
    private static List<CategoryModel> listInitDetails = new ArrayList<>();
    private static List<ButtonModel> modePlansList;
    private static List<ButtonModel> modeListTecnico;

    private ListSetting() {
    }

    public static List<SettingModel> getInstanceListMenus(Context context) {
        settings = new ArrayList<>();
        settings.add(new SettingModel("01", DefinesBANCARD.SETTING_ADMINISTRATIVAS, armarMenuAdministrativas(context)));
        settings.add(new SettingModel("02", DefinesBANCARD.SETTING_OTRAS_OPCIONES, armarMenuOtrasOpciones(context)));
        settings.add(new SettingModel("03", DefinesBANCARD.SETTING_CONFIGURACIONES, armarMenuConfiguraciones(context)));
        settings.add(new SettingModel("04", DefinesBANCARD.SETTING_TEST, armarMenuTest(context)));
        return settings;
    }

    public static List<ButtonModel> getInstanceListReportes(Context context) {
        if (modePlansList == null) {
            modePlansList = new ArrayList<>();
            modePlansList.add(new ButtonModel("1", "Reporte detallado", context.getDrawable(R.drawable.ic_menu_report)));
            modePlansList.add(new ButtonModel("2", "Reporte de total", context.getDrawable(R.drawable.ic_menu_report)));
            modePlansList.add(new ButtonModel("3", "Reportes de cierres", context.getDrawable(R.drawable.ic_menu_report)));
        }
        return modePlansList;
    }

    public static List<ButtonModel> getInstanceListadoTecnico(Context context) {
        if (modeListTecnico == null) {
            modeListTecnico = new ArrayList<>();
            modeListTecnico.add(new ButtonModel("6", DefinesBANCARD.ITEM_LIMPIAR_DATOS, context.getDrawable(R.drawable.ic_menu_config_clean_data)));
        }
        return modeListTecnico;
    }

    private static List<ButtonModel> armarMenuTest(Context context) {
        List<ButtonModel> modePlansList = new ArrayList<>();
        modePlansList.add(new ButtonModel("8", DefinesBANCARD.ITEM_ECHO_TEST, context.getDrawable(R.drawable.ic_echo)));
        if (Device.getConexion().equals(DefinesBANCARD.TYPE_WIFI)) {
            WifiManager manager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
            modePlansList.add(new ButtonModel("9", DefinesBANCARD.ITEM_WIFI,
                    !manager.isWifiEnabled() ? context.getDrawable(R.drawable.ic_menu_wifi_off) :
                            context.getDrawable(R.drawable.ic_menu_wifi_on)));
        }
        if (Device.getConexion().equals(DefinesBANCARD.TYPE_ETHERNET) && Tools.isEthernetFirmware()) {
            modePlansList.add(new ButtonModel("10", DefinesBANCARD.ITEM_ETHERNET,
                    !EthernetManager.getInstance().isEtherentEnabled() ? context.getDrawable(R.drawable.ic_menu_ethernet_off) :
                            context.getDrawable(R.drawable.ic_menu_ethernet_on)));
        }
        return modePlansList;
    }

    private static List<ButtonModel> armarMenuAdministrativas(Context context) {
        List<ButtonModel> modePlansList = new ArrayList<>();
        modePlansList.add(new ButtonModel("1", DefinesBANCARD.ITEM_CIERRE, context.getDrawable(R.drawable.ic_menu_closure)));
        modePlansList.add(new ButtonModel("2", DefinesBANCARD.ITEM_REIMPRESION, context.getDrawable(R.drawable.ic_menu_reprint)));
        modePlansList.add(new ButtonModel("3", DefinesBANCARD.ITEM_REPORTE_MENU, context.getDrawable(R.drawable.ic_menu_report)));
        return modePlansList;
    }

    private static List<ButtonModel> armarMenuOtrasOpciones(Context context) {
        List<ButtonModel> modePlansList = new ArrayList<>();
        modePlansList.add(new ButtonModel("4", DefinesBANCARD.ITEM_ANNULMENT, context.getDrawable(R.drawable.ic_menu_annulment)));
        TransLogData revesalData = TransLog.getReversal();
        if (revesalData != null) {
            modePlansList.add(new ButtonModel("11", DefinesBANCARD.ITEM_REVERSAL, context.getDrawable(R.drawable.ic_menu_reversal)));
        }
        return modePlansList;
    }

    private static List<ButtonModel> armarMenuConfiguraciones(Context context) {
        List<ButtonModel> modePlansList = new ArrayList<>();
        modePlansList.add(new ButtonModel("6", DefinesBANCARD.ITEM_CONFIG_TECNICO, context.getDrawable(R.drawable.ic_menu_config)));
        modePlansList.add(new ButtonModel("7", DefinesBANCARD.ITEM_INICIALIZACION, context.getDrawable(R.drawable.ic_menu_init)));
        return modePlansList;
    }

    public static List<SettingModel> getInstanceListDetalles() {
        if (listadoInicializacion == null) {
            listadoInicializacion = new ArrayList<>();
            listadoInicializacion.add(new SettingModel("01", DefinesBANCARD.DETALLES_COMERCIOS, armaComercios()));
            listadoInicializacion.add(new SettingModel("02", DefinesBANCARD.DETALLES_DEVICE, armarDevices()));
        }
        return listadoInicializacion;
    }

    public static List<CategoryModel> getInstanceListInitDetails() {
        if (listInitDetails == null || listInitDetails.isEmpty()) {
            listInitDetails = new ArrayList<>();
            listInitDetails.add(new CategoryModel(DefinesBANCARD.DETALLES_COMERCIOS, genInfoCommerce()));
            listInitDetails.add(new CategoryModel(DefinesBANCARD.DETALLES_DEVICE, genInfoCommerceDevice()));
        }
        return listInitDetails;
    }

    private static List<InfoModel> genInfoCommerce() {
        List<InfoModel> list = new ArrayList<>();
        list.add(new InfoModel("CATEGORIA :", tablaComercios.getCategoria()));
        list.add(new InfoModel("DESCRIPCIÓN :", tablaComercios.getMerchantDescription()));
        list.add(new InfoModel("HABILITA_FIRMA :", String.valueOf(tablaComercios.isHabilitaFirma())));
        list.add(new InfoModel("PERFIL :", tablaComercios.getPerfil()));
        list.add(new InfoModel("TIPO :", tablaComercios.getTipo()));
        list.add(new InfoModel("FECHA HORA_ALTA :", tablaComercios.getFechaHoraAlta()));
        list.add(new InfoModel("FECHA HORA \n ÚLTIMA ACTUALIZACIÓN :", tablaComercios.getFechaHoraUltimaActualizacion()));
        list.add(new InfoModel("USUARIO ÚLTIMA \n ACTUALIZACIÓN :", tablaComercios.getUsuarioUltimaActualizacion()));
        return list;
    }

    private static List<InfoModel> genInfoCommerceDevice() {
        List<InfoModel> list = new ArrayList<>();
        list.add(new InfoModel("IDENTIFICADOR :", Device.getDeviceIdentifier()));
        list.add(new InfoModel("DESCRIPCIÓN :", Device.getDeviceDescription()));
        list.add(new InfoModel("CAJA :", String.valueOf(Device.getCajaPOS())));
        list.add(new InfoModel("NÚMERO CAJA :", Device.getNumeroCajas()));
        list.add(new InfoModel("NÚMERO SERIAL :", Device.getNumSerial()));
        list.add(new InfoModel("PRIORIDAD :", Device.getPrioridad()));
        list.add(new InfoModel("ESTADO :", Device.getEstado()));
        list.add(new InfoModel("VERSIÓN SOFTWARE :", Device.getVersionSoftware()));
        list.add(new InfoModel("FECHA ÚLTIMO \nECHO :", Device.getFechaUltimoEcho()));
        list.add(new InfoModel("FECHA ÚLTIMA \nTRANSACCIÓN :", Device.getFechaUltimaTransaccion()));
        list.add(new InfoModel("FECHA ÚLTIMO \nCIERRE :", Device.getFechaUltimoCierre()));
        list.add(new InfoModel("GRUPO :", Device.getGrupo()));
        list.add(new InfoModel("FECHA ALTA :", Device.getFechaAlta()));
        list.add(new InfoModel("FECHA ÚLTIMA \nACTUALIZACIÓN :", Device.getFechaUltimaActualizacion()));
        list.add(new InfoModel("USUARIO ÚLTIMA \nACTUALIZACIÓN :", Device.getUsuarioUltimoActualizacion()));
        return list;
    }

    private static List<ButtonModel> armarDevices() {
        List<ButtonModel> listadoDevices = new ArrayList<>();
        listadoDevices.add(new ButtonModel("IDENTIFICADOR :", Device.getDeviceIdentifier()));
        listadoDevices.add(new ButtonModel("DESCRIPCIÓN :", Device.getDeviceDescription()));
        listadoDevices.add(new ButtonModel("CAJA :", String.valueOf(Device.getCajaPOS())));
        listadoDevices.add(new ButtonModel("NÚMERO CAJA :", Device.getNumeroCajas()));
        listadoDevices.add(new ButtonModel("NÚMERO SERIAL :", Device.getNumSerial()));
        listadoDevices.add(new ButtonModel("PRIORIDAD :", Device.getPrioridad()));
        listadoDevices.add(new ButtonModel("ESTADO :", Device.getEstado()));
        listadoDevices.add(new ButtonModel("VERSIÓN SOFTWARE :", Device.getVersionSoftware()));
        listadoDevices.add(new ButtonModel("FECHA ÚLTIMO \nECHO :", Device.getFechaUltimoEcho()));
        listadoDevices.add(new ButtonModel("FECHA ÚLTIMA \nTRANSACCIÓN :", Device.getFechaUltimaTransaccion()));
        listadoDevices.add(new ButtonModel("FECHA ÚLTIMO \nCIERRE :", Device.getFechaUltimoCierre()));
        listadoDevices.add(new ButtonModel("GRUPO :", Device.getGrupo()));
        listadoDevices.add(new ButtonModel("FECHA ALTA :", Device.getFechaAlta()));
        listadoDevices.add(new ButtonModel("FECHA ÚLTIMA \nACTUALIZACIÓN :", Device.getFechaUltimaActualizacion()));
        listadoDevices.add(new ButtonModel("USUARIO ÚLTIMA \nACTUALIZACIÓN :", Device.getUsuarioUltimoActualizacion()));
        return listadoDevices;
    }

    private static List<ButtonModel> armaComercios() {
        List<ButtonModel> listadoComercios = new ArrayList<>();
        listadoComercios.add(new ButtonModel("CATEGORIA :", tablaComercios.getCategoria()));
        listadoComercios.add(new ButtonModel("DESCRIPCIÓN :", tablaComercios.getMerchantDescription()));
        listadoComercios.add(new ButtonModel("HABILITA_FIRMA :", String.valueOf(tablaComercios.isHabilitaFirma())));
        listadoComercios.add(new ButtonModel("PERFIL :", tablaComercios.getPerfil()));
        listadoComercios.add(new ButtonModel("TIPO :", tablaComercios.getTipo()));
        listadoComercios.add(new ButtonModel("FECHA HORA_ALTA :", tablaComercios.getFechaHoraAlta()));
        listadoComercios.add(new ButtonModel("FECHA HORA \n ÚLTIMA ACTUALIZACIÓN :", tablaComercios.getFechaHoraUltimaActualizacion()));
        listadoComercios.add(new ButtonModel("USUARIO ÚLTIMA \n ACTUALIZACIÓN :", tablaComercios.getUsuarioUltimaActualizacion()));
        return listadoComercios;
    }

    public static void setModelSettingsNull() {
        ListSetting.settings = null;
        ListSetting.listadoInicializacion = null;
        ListSetting.modePlansList = null;
        ListSetting.modeListTecnico = null;
    }
}
