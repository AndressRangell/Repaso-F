package com.flota.logscierres;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.widget.ListView;
import android.widget.TextView;

import com.flota.basedatos.implementaciones.CierreDetalladoDAOImpl;
import com.flota.basedatos.implementaciones.CierreTotalDAOImpl;
import com.flota.basedatos.interfaces.CierreDetalladoDAO;
import com.flota.basedatos.interfaces.CierreTotalDAO;
import com.newpos.libpay.device.printer.PrintManager;
import com.newpos.libpay.trans.translog.TransLogData;
import com.wposs.flota.R;

import java.util.List;

import cn.desert.newpos.payui.master.FormularioActivity;
import cn.desert.newpos.payui.master.mToolbar;
import cn.desert.newpos.payui.master.onToolbarClick;

public class ReportesCierresActivity extends FormularioActivity implements CierresLogsAdapter.OnItemReprintClick {

    CierresLogsAdapter adapter;
    ListView lvCierres;
    List<TransLogData> logsCierredetallado = null;
    private PrintManager manager = null;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.initView(new mToolbar(R.layout.activity_reportes_cierres, R.drawable.ic__back, 0), new onToolbarClick() {
            @Override
            public void onClick() {
                onBackPressed();
            }
        }, null);
        CierreTotalDAO sql = new CierreTotalDAOImpl(this);
        List<LogsCierresModelo> cierres = sql.getAllLogsCierre();

        manager = PrintManager.getmInstance(this, null);

        adapter = new CierresLogsAdapter(this, this);
        adapter.setList(cierres);
        adapter.notifyDataSetChanged();
        lvCierres = findViewById(R.id.lvCierres);
        lvCierres.setAdapter(adapter);
        mostrarSerialvsVersion();
    }

    private void mostrarSerialvsVersion() {
        TextView tvSerial = findViewById(R.id.tvSerial);
        TextView tvVersion = findViewById(R.id.tvVersion);
        showVersionSerial(tvVersion, tvSerial);
    }

    @Override
    public void onItemClick(final LogsCierresModelo logsCierresModelo, final boolean isDetallado) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentReporteCierre fragment = FragmentReporteCierre.newInstance();
        if (isDetallado) {
            CierreDetalladoDAO sqlLogsCierres = new CierreDetalladoDAOImpl(this);
            logsCierredetallado = sqlLogsCierres.getLogsCierreDetallado(ReportesCierresActivity.this, logsCierresModelo.getNumLote());
            if (logsCierredetallado != null && logsCierredetallado.size() > 0)
                fragment.setList(logsCierredetallado);
        } else {
            fragment.setModelo(logsCierresModelo);
        }
        fragment.setDetalle(isDetallado);
        fragment.setContext(ReportesCierresActivity.this);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.add(android.R.id.content, fragment).addToBackStack(null).commit();
        fragment.setListener(new FragmentReporteCierre.OnClickFragmentListener() {
            @Override
            public void onClickFragment() {
                imprimirCopiaCierre(logsCierresModelo, isDetallado, logsCierredetallado);
            }
        });
    }

    private void imprimirCopiaCierre(final LogsCierresModelo logsCierresModelo, final boolean isDetallado, final List<TransLogData> logsCierredetallado) {
        final ProgressDialog progressDialog = new ProgressDialog(ReportesCierresActivity.this);
        progressDialog.setMessage("Imprimiendo...");
        progressDialog.show();
        new Thread() {
            @Override
            public void run() {
                if (isDetallado) {
                    reporteDetallado(logsCierredetallado);
                } else {
                    manager.imprimirCierre(logsCierresModelo, false);
                }
                mHandler.sendEmptyMessage(0);
                progressDialog.dismiss();
            }
        }.start();
    }

    private void reporteDetallado(List<TransLogData> logsCierredetallado) {
        if (logsCierredetallado != null) {
            manager.imprimirReporteDetallado(logsCierredetallado);
        }
    }
}
