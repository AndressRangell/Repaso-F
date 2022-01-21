package com.flota.printer;

import static com.flota.defines_bancard.DefinesBANCARD.ITEM_ANNULMENT;
import static com.flota.defines_bancard.DefinesBANCARD.ITEM_BORRAR_LOTE;
import static com.flota.defines_bancard.DefinesBANCARD.ITEM_REPORTE_DETALLADO;
import static com.flota.defines_bancard.DefinesBANCARD.ITEM_REPORTE_EMV;
import static com.flota.defines_bancard.DefinesBANCARD.ITEM_REPORTE_TERMINAL;
import static com.flota.defines_bancard.DefinesBANCARD.ITEM_TEST;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.webkit.WebView;
import android.widget.TextView;

import com.newpos.libpay.device.printer.PrintManager;
import com.wposs.flota.R;

public class PrintParameter extends AppCompatActivity {

    private static boolean printTotals;
    @SuppressLint("HandlerLeak")
    private final Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            finish();
        }
    };
    TextView txt;
    TextView tvTitle;
    private PrintManager manager = null;

    public static boolean isPrintTotals() {
        return printTotals;
    }

    public static void setPrintTotals(boolean printTotal) {
        printTotals = printTotal;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_init);
        txt = findViewById(R.id.output);
        txt.setText(R.string.printing);
        loading();

        manager = PrintManager.getmInstance(this, null);

        String typeReport;
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                typeReport = null;
            } else {
                typeReport = extras.getString("typeReport");
            }
        } else {
            typeReport = (String) savedInstanceState.getSerializable("typeReport");
        }

        tvTitle = findViewById(R.id.textView_titleToolbar);
        tvTitle.setText(Html.fromHtml("<h4>" + typeReport + "</h4>"));
        printAll(typeReport);
    }

    private void printAll(final String typeReport) {
        new Thread() {
            @Override
            public void run() {
                switch (typeReport) {
                    case ITEM_TEST:
                        manager.printParamInit();
                        break;
                    case ITEM_REPORTE_EMV:
                        manager.printEMVAppCfg();
                        break;
                    case ITEM_REPORTE_TERMINAL:
                        manager.printConfigTerminal();
                        break;
                    case ITEM_ANNULMENT:
                        manager.printVoidMedianet(false, false);
                        break;
                    case ITEM_REPORTE_DETALLADO:
                        manager.printReportMedianet(isPrintTotals());
                        break;
                    case ITEM_BORRAR_LOTE:
                        manager.printReportMedianet(false, true, isPrintTotals(), true);
                        break;

                    default:
                        break;
                }
                mhandler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void loading() {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                WebView wv = findViewById(R.id.wb_init_stis);
                wv.loadDataWithBaseURL(null, "<HTML><body bgcolor='#FFF'><div align=center>" +
                        "<img width=\"128\" height=\"128\" src='file:///android_asset/gif/loader.gif'/></div></body></html>", "text/html", "UTF-8", null);
            }
        });

    }
}
