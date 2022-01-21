package cn.desert.newpos.payui.transrecord;

import static com.flota.menus.menus.idAcquirer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.HideReturnsTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.flota.actividades.MenusActivity;
import com.flota.adaptadores.ModeloCierreTrans;
import com.flota.basedatos.implementaciones.ReimpresionVoucherDAOImpl;
import com.flota.basedatos.interfaces.ReimpresionVoucherDAO;
import com.newpos.libpay.Logger;
import com.newpos.libpay.device.printer.PrintManager;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;
import com.wposs.flota.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.base.PayApplication;
import cn.desert.newpos.payui.master.FormularioActivity;
import cn.desert.newpos.payui.master.mToolbar;
import cn.desert.newpos.payui.master.onToolbarClick;

public class HistoryTrans extends FormularioActivity implements
        HistorylogAdapter.OnItemReprintClick, View.OnClickListener {

    public static final String EVENTS = "EVENTS";
    public static final String LAST = "LAST";
    public static final String COMMON = "COMMON";
    public static final String ALL = "ALL";
    public static final String ALL_F_REDEN = "ALL_F_REDEN";
    public static final String REPORTE_TOTAL = "REPORTE_TOTAL";
    public static final String REPORTE_DETALLADO = "REPORTE_DETALLADO";
    public static final String BUSQUEDA_CARGO = "BUSQUEDA_CARGO";
    public static final String BUSQUEDA_BOLETA = "BUSQUEDA_BOLETA";
    private final String TAG = "HistoryTrans";
    ListView lvTrans;
    View viewNoData;
    View viewReprint;
    EditText searchEdit;
    ImageView search;
    LinearLayout z;
    LinearLayout root;
    ImageView close;
    boolean isLoadData = true;
    boolean isPrint = false;//SABER QUE ESTA IMPRIMIENDO
    boolean isShowAlertPrint = false;//SABER QUE YA SE MOSTRO LA PRIMERA VEZ LA ALERTA DE LA IMPRESION
    private HistorylogAdapter adapter;
    private boolean isSearch = false;
    private boolean isCommonEvents = false;
    @SuppressLint("HandlerLeak")
    private final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            viewReprint.setVisibility(View.GONE);
            lvTrans.setVisibility(View.VISIBLE);
            z.setVisibility(View.VISIBLE);
            if (!isCommonEvents) {
                finish();
            }
        }
    };
    private PrintManager manager = null;
    private String searchType = BUSQUEDA_BOLETA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.initView(new mToolbar(R.layout.activity_history, R.drawable.ic__back, 0), new onToolbarClick() {
            @Override
            public void onClick() {
                startActivity(new Intent(HistoryTrans.this, MenusActivity.class));
            }
        }, null);
        PayApplication.getInstance().addActivity(this);
        mostrarSerialvsVersion();

        lvTrans = findViewById(R.id.history_lv);
        viewNoData = findViewById(R.id.history_nodata);
        viewReprint = findViewById(R.id.reprint_process);
        searchEdit = findViewById(R.id.history_search_edit);
        search = findViewById(R.id.history_search);
        z = findViewById(R.id.history_search_layout);
        root = findViewById(R.id.transaction_details_root);
        adapter = new HistorylogAdapter(this, this);
        lvTrans.setAdapter(adapter);
        viewReprint.setVisibility(View.GONE);
        search.setOnClickListener(new SearchListener());
        manager = PrintManager.getmInstance(this, null);

        searchEdit.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            String even = bundle.getString(HistoryTrans.EVENTS);
            switch (Objects.requireNonNull(even)) {
                case LAST:
                    String nroCargo = TransLog.getInstance(idAcquirer).getLastTransLog().getNroCargo();
                    re_print(nroCargo);
                    break;
                case ALL:
                    printAll(ALL);
                    break;
                case ALL_F_REDEN:
                    printAll(ALL_F_REDEN);
                    break;
                case REPORTE_TOTAL:
                    isLoadData = false;
                    printReporte(REPORTE_TOTAL);
                    break;
                case REPORTE_DETALLADO:
                    isLoadData = false;
                    printReporteDetallado();
                    break;
                default:
                    isCommonEvents = true;
                    break;
            }
        }

        searchEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_UP) {
                    view.clearFocus();
                    new SearchListener().onClick(view);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return false;
            }
        });
    }

    private void mostrarSerialvsVersion() {
        TextView tvSerial = findViewById(R.id.tvSerial);
        TextView tvVersion = findViewById(R.id.tvVersion);
        showVersionSerial(tvVersion, tvSerial);
    }

    private void printReporteDetallado() {
        final List<TransLogData> list = TransLog.getInstance(idAcquirer).getData();
        new Thread() {
            @Override
            public void run() {
                manager.imprimirReporteDetallado(list);
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private void printReporte(final String reporte) {

        final List<TransLogData> list = TransLog.getInstance(idAcquirer).getData();

        ArrayList<String> codigosDeComercios = new ArrayList<>();
        for (TransLogData transLogData : list) {
            if (!codigosDeComercios.contains(transLogData.getCodigoDelNegocio())) {
                codigosDeComercios.add(transLogData.getCodigoDelNegocio());
            }
        }

        final ArrayList<ModeloCierreTrans> cierreTrans = new ArrayList<>();

        for (String codComercio : codigosDeComercios) {
            int cont = 0;
            long montoSuma = 0;
            for (TransLogData transLogData : list) {
                if (codComercio.equals(transLogData.getCodigoDelNegocio()) && transLogData.getAmount() != null) {
                    montoSuma += transLogData.getAmount();
                    cont++;
                }
            }
            ModeloCierreTrans transData = new ModeloCierreTrans();
            transData.setCodNegocio(codComercio);
            transData.setMontoTotal(String.valueOf(montoSuma));
            transData.setCantidadTrans(String.valueOf(cont));
            cierreTrans.add(transData);
        }

        new Thread() {
            @Override
            public void run() {
                manager.imprimirReporte(reporte, cierreTrans, list);
                handler.sendEmptyMessage(0);
            }
        }.start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isLoadData) {
            loadData();
        } else {
            finish();
        }
    }

    private void loadData() {
        List<TransLogData> list = TransLog.getInstance(idAcquirer).getData();
        List<TransLogData> temp = new ArrayList<>();
        int num = 0;
        for (int i = list.size() - 1; i >= 0; i--) {
            temp.add(num, list.get(i));
            num++;
        }
        if (!list.isEmpty()) {
            showView(false);
            adapter.setList(temp);
            adapter.notifyDataSetChanged();
            isSearch = true;
            search.setImageResource(android.R.drawable.ic_menu_search);
        } else {
            showView(true);
        }
    }

    private void showView(boolean isShow) {
        if (isShow) {
            lvTrans.setVisibility(View.GONE);
            viewNoData.setVisibility(View.VISIBLE);
        } else {
            lvTrans.setVisibility(View.VISIBLE);
            viewNoData.setVisibility(View.GONE);
        }
    }

    @Override
    public void OnItemClick(String NroCargo) {
        ReimpresionVoucherDAO reimpresionVoucherDAO = new ReimpresionVoucherDAOImpl(this);
        final Bitmap bitmap = reimpresionVoucherDAO.obtenerVoucher(NroCargo);
        if (bitmap != null) {
            if (isPrint) {
                if (!isShowAlertPrint) {
                    ISOUtil.showMensaje("Espera a que termine de imprimir.", HistoryTrans.this);
                    isShowAlertPrint = true;
                }
                return;
            }
            Logger.info(TAG, "Bitmap viene lleno y es :" + bitmap);
            isPrint = true;
            new Thread() {
                @Override
                public void run() {
                    manager.printVoucher(bitmap);

                    handler.sendEmptyMessage(0);
                    isPrint = false;
                    isShowAlertPrint = false;

                }
            }.start();
        } else {
            Logger.info(TAG, "Bitmap null");
            ISOUtil.showMensaje("No se puede imprimir recibo.", HistoryTrans.this);
        }

    }

    private void re_print(final String data) {
        new Thread() {
            @Override
            public void run() {
                manager.rePrint(data);
                handler.sendEmptyMessage(0);
                finish();
            }
        }.start();

    }

    private void printAll(final String key) {
        new Thread() {
            @Override
            public void run() {
                manager.selectPrintReport(key);
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    @Override
    public void onClick(View view) {
        if (view.equals(close)) {
            finish();
        }
    }

    private final class SearchListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            if (isSearch) {
                String edit = searchEdit.getText().toString();
                if (!PAYUtils.isNullWithTrim(edit)) {
                    TransLog transLog = TransLog.getInstance(idAcquirer);
                    TransLogData data;
                    if (searchType.equals(BUSQUEDA_BOLETA)) {
                        edit = ISOUtil.padleft(edit, 12, '0');
                        data = transLog.searchTransLogByRNN(edit);
                    } else {
                        edit = ISOUtil.padleft(edit, 6, '0');
                        data = transLog.searchTransLogByNroCargo(edit);
                    }
                    if (data != null) {
                        InputMethodManager imm = (InputMethodManager) HistoryTrans.this.getSystemService(Context.INPUT_METHOD_SERVICE);
                        Objects.requireNonNull(imm).hideSoftInputFromWindow(searchEdit.getWindowToken(), 0);
                        List<TransLogData> list = new ArrayList<>();
                        list.add(0, data);
                        adapter.setList(list);
                        adapter.notifyDataSetChanged();
                        search.setImageResource(android.R.drawable.ic_menu_revert);
                        isSearch = false;
                    } else {
                        UIUtils.toast(HistoryTrans.this, R.drawable.redinfonet, HistoryTrans.this.getResources().getString(R.string.not_any_record), Toast.LENGTH_SHORT);
                    }
                }
            } else {
                searchEdit.setText("");
                loadData();
            }
        }
    }


}
