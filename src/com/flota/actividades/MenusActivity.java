package com.flota.actividades;

import static com.flota.defines_bancard.DefinesBANCARD.HABILITARMENUS;
import static com.flota.defines_bancard.DefinesBANCARD.ITEM_ANNULMENT;
import static com.flota.defines_bancard.DefinesBANCARD.ITEM_CIERRE;
import static com.flota.defines_bancard.DefinesBANCARD.ITEM_CONFIG_COMERCIO;
import static com.flota.defines_bancard.DefinesBANCARD.ITEM_CONFIG_TECNICO;
import static com.flota.defines_bancard.DefinesBANCARD.ITEM_ECHO_TEST;
import static com.flota.defines_bancard.DefinesBANCARD.ITEM_ETHERNET;
import static com.flota.defines_bancard.DefinesBANCARD.ITEM_INICIALIZACION;
import static com.flota.defines_bancard.DefinesBANCARD.ITEM_REIMPRESION;
import static com.flota.defines_bancard.DefinesBANCARD.ITEM_REPORTE_MENU;
import static com.flota.defines_bancard.DefinesBANCARD.ITEM_REVERSAL;
import static com.flota.defines_bancard.DefinesBANCARD.ITEM_WIFI;
import static com.flota.defines_bancard.DefinesBANCARD.LOTE_VACIO;
import static com.flota.defines_bancard.DefinesBANCARD.MENSAJE_ERROR_INYECCION_LLAVES;
import static com.flota.defines_bancard.DefinesBANCARD.MSG_PAPER;
import static com.flota.menus.menus.idAcquirer;
import static com.newpos.libpay.trans.Trans.idLote;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.flota.adapters.UAFactory;
import com.flota.adapters.UAListener;
import com.flota.adapters.model.ButtonModel;
import com.flota.adapters.model.SettingModel;
import com.flota.inicializacion.configuracioncomercio.Red;
import com.flota.inicializacion.trans_init.Init;
import com.flota.setting.ListSetting;
import com.flota.tools.PaperStatus;
import com.flota.tools_bacth.ToolsBatch;
import com.newpos.libpay.device.printer.PrintRes;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.trans.translog.TransLogData;
import com.pos.device.net.eth.EthernetManager;
import com.pos.device.printer.Printer;
import com.wposs.flota.R;

import java.util.List;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.master.FormularioActivity;
import cn.desert.newpos.payui.master.MasterControl;
import cn.desert.newpos.payui.master.mToolbar;
import cn.desert.newpos.payui.master.onToolbarClick;
import cn.desert.newpos.payui.transrecord.HistoryTrans;

public class MenusActivity extends FormularioActivity {

    public static final String TAG = "MenusActivity.java";

    private Bundle bundle;
    private Dialog dialog;
    private boolean buttonPress = false;
    private static boolean manualOff = false; //Booleano que valida si el usuario desactivo manualmente el medio principal


    public static boolean isManualOff() {
        return manualOff;
    }

    public static void setManualOff(boolean manualOff) {
        MenusActivity.manualOff = manualOff;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.initView(new mToolbar(R.layout.activity_menus, R.drawable.ic__back, R.drawable.ic_acerca_de), new onToolbarClick() {
            @Override
            public void onClick() {
                if (!verificacionIncializacion(MenusActivity.this, true)) {
                    onBackPressed();
                }
            }
        }, new onToolbarClick() {
            @Override
            public void onClick() {
                startActivity(new Intent(MenusActivity.this, InfoActivity.class));
                overridePendingTransition(0, 0);
            }
        });
        verificacionErrorInyeccionLLaves();
        genRecyclerSettings();
        idAcquirer = idLote;
    }

    private void verificacionErrorInyeccionLLaves() {
        bundle = getIntent().getExtras();
        if (bundle != null) {
            toast(bundle.getString(MENSAJE_ERROR_INYECCION_LLAVES, ""));
        }
    }

    private void genRecyclerSettings() {
        List<SettingModel> settings = ListSetting.getInstanceListMenus(this);
        genRcySettings(R.id.recycler_settings_menus,
                UAFactory.adapterSettings(this, settings, new UAListener.ButtonListener() {
                    @Override
                    public void onClick(View view, ButtonModel button) {
                        processButtonClick(view, button);
                    }
                })
        );
    }

    private void processButtonClick(View view, ButtonModel button) {
        if (!buttonPress) { // Si el botón no está siendo presionado se pueden usar
            buttonPress = true; // Se define su uso actual

            Intent i = null;
            switch (button.getName()) {
                case ITEM_CIERRE:
                    if (!verificacionIncializacion(MenusActivity.this, false)) {
                        if (PaperStatus.getInstance().getRet() == Printer.PRINTER_STATUS_PAPER_LACK) {
                            toast(MSG_PAPER);
                        } else {
                            idAcquirer = idLote;
                            if (ToolsBatch.statusTrans(idAcquirer)) {
                                i = new Intent(MenusActivity.this, MasterControl.class);
                                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                i.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[5]);
                            } else {
                                toast(LOTE_VACIO);
                                tone();
                            }
                        }
                    }
                    break;
                case ITEM_REIMPRESION:
                    if (!verificacionIncializacion(MenusActivity.this, false)) {
                        if (PaperStatus.getInstance().getRet() == Printer.PRINTER_STATUS_PAPER_LACK) {
                            toast(MSG_PAPER);
                        } else {
                            idAcquirer = idLote;
                            if (ToolsBatch.statusTrans(idAcquirer)) {
                                i = new Intent(MenusActivity.this, HistoryTrans.class);
                                i.putExtra(HistoryTrans.EVENTS, HistoryTrans.BUSQUEDA_BOLETA);
                            } else {
                                toast(LOTE_VACIO);
                                tone();
                            }
                        }
                    }
                    break;
                case ITEM_REPORTE_MENU:
                    if (!verificacionIncializacion(MenusActivity.this, false)) {
                        if (PaperStatus.getInstance().getRet() == Printer.PRINTER_STATUS_PAPER_LACK) {
                            toast(MSG_PAPER);
                        } else {
                            i = new Intent(MenusActivity.this, ReportsActivity.class);
                        }
                    }
                    break;
                case ITEM_ANNULMENT:
                    if (!verificacionIncializacion(MenusActivity.this, false)) {
                        i = new Intent(MenusActivity.this, MasterControl.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[23]);
                    }
                    break;
                case ITEM_REVERSAL:
                    TransLogData reversal = TransLog.getReversal();
                    if (reversal != null) {
                        i = new Intent(MenusActivity.this, MasterControl.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[36]);
                    }
                    break;
                case ITEM_CONFIG_TECNICO:
                    requestPasswordTechnical();
                    break;
                case ITEM_INICIALIZACION:
                    i = new Intent(MenusActivity.this, Init.class);
                    i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    break;
                case ITEM_ECHO_TEST:
                    if (!verificacionIncializacion(MenusActivity.this, false)) {
                        i = new Intent(MenusActivity.this, MasterControl.class);
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        i.putExtra(MasterControl.TRANS_KEY, PrintRes.TRANSEN[20]);
                    }
                    break;
                case ITEM_WIFI:
                    changeWifiEnable(view);
                    break;
                case ITEM_ETHERNET:
                    changeEthernetEnabled(view);
                    break;
                default:
                    toast("otra opcion");
                    buttonPress = false;
                    break;
            }

            if (i != null) {
                startActivity(i);
                overridePendingTransition(0, 0);
            }

            // Si no es configuración comercio o técnico se reactiva el uso del botón.
            // Los botones configuración comercio y técnico se reactivan al finalizar se alert dialog
            if (!ITEM_CONFIG_COMERCIO.equals(button.getName())
                    && !ITEM_CONFIG_TECNICO.equals(button.getName())) {
                buttonPress = false;
            }
        }
    }

    private boolean verificacionIncializacion(Context context, boolean isIntentInicializaar) {
        if (bundle != null && bundle.getBoolean(HABILITARMENUS, false)) {
            if (isIntentInicializaar) {
                Intent i = new Intent();
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                i.setClass(MenusActivity.this, Init.class);
                startActivity(i);
                overridePendingTransition(0, 0);
            } else {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(context);
                dialogo1.setTitle("Importante");
                dialogo1.setMessage("Es necesario realizar la inicialización\ndel POS.");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogo1, int id) {
                        dialogo1.dismiss();
                    }
                });
                dialogo1.show();
            }
            return true;
        }
        return false;
    }

    public void requestPasswordTechnical() {
        dialog = UIUtils.centerDialog(MenusActivity.this, R.layout.setting_home_pass, R.id.setting_pass_layout);
        final EditText etPassword = dialog.findViewById(R.id.editText);
        final ImageView close = dialog.findViewById(R.id.setting_pass_close);
        final ImageButton accept = dialog.findViewById(R.id.btnAceptar);
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        etPassword.setHint("Contraseña del técnico");

        // Al cerrar el dialog se reactivan el uso de botones
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                buttonPress = false;
            }
        });

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                validatePassword(etPassword.getText().toString());
            }
        });

        etPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_ENDCALL) {
                    dialog.dismiss();
                    validatePassword(etPassword.getText().toString());
                }
                return true;
            }
        });
        etPassword.requestFocus();
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    private void validatePassword(String pass) {
        String passwordTechnical = Red.getInstance(false).getClaveTecnico();
        if (passwordTechnical != null && !passwordTechnical.isEmpty()) {
            if (pass.equals(passwordTechnical.trim())) {
                startActivity(new Intent(MenusActivity.this, ConfigTechnicianActivity.class));
                overridePendingTransition(0, 0);
            } else {
                toast("Contraseña incorrecta");
            }
        } else {
            toast("Clave no establecida");
        }
    }

    private void changeWifiEnable(View viewButton) {
        WifiManager manager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        boolean wifiEnabled = !manager.isWifiEnabled();
        manager.setWifiEnabled(wifiEnabled);
        ButtonModel.setImage(viewButton,
                getDrawable(wifiEnabled ? R.drawable.ic_menu_wifi_on : R.drawable.ic_menu_wifi_off));
        toast(wifiEnabled ? "Wifi Activado" : "Wifi Desactivado");
    }

    private void changeEthernetEnabled(View viewButton) {
        boolean ethernetEnabled = !EthernetManager.getInstance().isEtherentEnabled();
        EthernetManager.getInstance().setEtherentEnabled(ethernetEnabled);
        ButtonModel.setImage(viewButton,
                getDrawable(ethernetEnabled ? R.drawable.ic_menu_ethernet_on : R.drawable.ic_menu_ethernet_off));
        toast(ethernetEnabled ? "Ethernet Activado" : "Ethernet Desactivado");
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, MainActivity.class));
        overridePendingTransition(0, 0);
    }
}
