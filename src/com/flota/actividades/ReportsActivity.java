package com.flota.actividades;

import static com.flota.defines_bancard.DefinesBANCARD.LOTE_VACIO;
import static com.flota.defines_bancard.DefinesBANCARD.MSG_PAPER;
import static com.flota.menus.menus.idAcquirer;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.flota.adapters.UAFactory;
import com.flota.adapters.UAListener;
import com.flota.adapters.model.ButtonModel;
import com.flota.logscierres.ReportesCierresActivity;
import com.flota.setting.ListSetting;
import com.flota.tools.PaperStatus;
import com.flota.tools_bacth.ToolsBatch;
import com.pos.device.printer.Printer;
import com.wposs.flota.R;

import java.util.List;

import cn.desert.newpos.payui.master.FormularioActivity;
import cn.desert.newpos.payui.master.mToolbar;
import cn.desert.newpos.payui.master.onToolbarClick;
import cn.desert.newpos.payui.transrecord.HistoryTrans;

public class ReportsActivity extends FormularioActivity {

    public static final String TAG = "ReportsActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.initView(new mToolbar(R.layout.activity_reports, R.drawable.ic__back, 0), new onToolbarClick() {
            @Override
            public void onClick() {
                onBackPressed();
                overridePendingTransition(0, 0);
            }
        }, null);
        genRecyclerButtons();
    }

    private void genRecyclerButtons() {
        List<ButtonModel> buttons = ListSetting.getInstanceListReportes(ReportsActivity.this);
        genRcy(R.id.recycler_buttons_reports,
                UAFactory.adapterButtons(buttons, new UAListener.ButtonListener() {
                    @Override
                    public void onClick(View view, ButtonModel button) {
                        processButtonClick(button);
                    }
                })
        );
    }

    private void processButtonClick(ButtonModel button) {
        Intent i = null;

        switch (button.getCode()) {
            case "1":
                if (PaperStatus.getInstance().getRet() == Printer.PRINTER_STATUS_PAPER_LACK) {
                    toast(MSG_PAPER);
                } else {
                    if (ToolsBatch.statusTrans(idAcquirer)) {
                        i = new Intent(ReportsActivity.this, HistoryTrans.class);
                        i.putExtra(HistoryTrans.EVENTS, HistoryTrans.REPORTE_DETALLADO);
                    } else {
                        toast(LOTE_VACIO);
                    }
                }
                break;
            case "2":
                if (PaperStatus.getInstance().getRet() == Printer.PRINTER_STATUS_PAPER_LACK) {
                    toast(MSG_PAPER);
                } else {
                    if (ToolsBatch.statusTrans(idAcquirer)) {
                        i = new Intent(ReportsActivity.this, HistoryTrans.class);
                        i.putExtra(HistoryTrans.EVENTS, HistoryTrans.REPORTE_TOTAL);
                    } else {
                        toast(LOTE_VACIO);
                    }
                }
                break;
            case "3":
                i = new Intent(ReportsActivity.this, ReportesCierresActivity.class);
                break;
            default:
                toast("otra opcion");
                break;
        }

        if (i != null) {
            startActivity(i);
            overridePendingTransition(0, 0);
        }
    }
}
