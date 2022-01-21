package com.flota.logscierres;


import static com.flota.actividades.StartAppBANCARD.tablaComercios;
import static com.flota.actividades.StartAppBANCARD.tablaDevice;
import static com.flota.inicializacion.configuracioncomercio.Device.getFechaUltimoCierre;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flota.inicializacion.configuracioncomercio.Device;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;
import com.wposs.flota.R;

import java.util.List;

import cn.desert.newpos.payui.UIUtils;

public class FragmentReporteCierre extends DialogFragment {

    private LogsCierresModelo modelo;
    private boolean isDetalle;
    private List<TransLogData> list;
    private Context context;
    private OnClickFragmentListener listener;

    public static FragmentReporteCierre newInstance() {
        return new FragmentReporteCierre();
    }

    public void setModelo(LogsCierresModelo modelo) {
        this.modelo = modelo;
    }

    public boolean isDetalle() {
        return isDetalle;
    }

    public void setDetalle(boolean detalle) {
        isDetalle = detalle;
    }

    public List<TransLogData> getList() {
        return list;
    }

    public void setList(List<TransLogData> list) {
        this.list = list;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setListener(OnClickFragmentListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = null;
        if (isDetalle()) {
            view = reporteDetallado(inflater, container);
        } else {
            view = reporteCierre(inflater, container);
        }

        mostrarSerialvsVersion(view);
        Button btnImprimir = view.findViewById(R.id.btnImprimir);
        btnImprimir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onClickFragment();
                }
                dismiss();
            }
        });

        Button btnNoImprmir = view.findViewById(R.id.btnNoImprmir);
        btnNoImprmir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


        return view;
    }

    private void mostrarSerialvsVersion(View view) {
        TextView tvVersion = view.findViewById(R.id.tvVersion);
        TextView tvSerial = view.findViewById(R.id.tvSerial);
        UIUtils.mostrarSerialvsVersion(tvVersion, tvSerial);
    }

    @SuppressLint("SetTextI18n")
    private View reporteDetallado(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.vista_reporte_detallado, container, false);

        encabezadoReportes(view);
        informacionIntermedia(view);
        RecyclerView recyclerView = view.findViewById(R.id.listadoTransacciones);
        inicializarRecyclerView(context, recyclerView);

        if (list != null) {
            AdaptadoDetalleCierre adaptadoDetalleCierre = new AdaptadoDetalleCierre(context, list);
            recyclerView.setAdapter(adaptadoDetalleCierre);

            TextView tvCantidad = view.findViewById(R.id.tvCantidad);

            tvCantidad.setText("CANTIDAD DE TRANSACCIONES:   " + list.size());
        }

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void informacionIntermedia(View view) {
        TextView tvNroLote = view.findViewById(R.id.TvNroLote);
        tvNroLote.setText("Nro de lote: " + TMConfig.getInstance().getBatchNo() + " Comercio: " + tablaComercios.sucursal.getMerchantId());

        String fechaUltimoCierre = getFechaUltimoCierre();
        String fechaActual = PAYUtils.getLocalDateFormat("dd/MM/yyyy HH:mm");

        TextView tvDesde = view.findViewById(R.id.tvDesde);
        tvDesde.setText("Desde: " + fechaUltimoCierre);


        TextView txHasta = view.findViewById(R.id.txHasta);
        txHasta.setText("Hasta: " + fechaActual);
    }

    private void encabezadoReportes(View view) {
        String nombreComercio = checkNull(tablaComercios.sucursal.getDescripcion());
        TextView tvNombreComercio = view.findViewById(R.id.tvNombreComercio);
        tvNombreComercio.setText(nombreComercio);

        String ciudadComerio = checkNull(tablaComercios.sucursal.getDireccionPrincipal());
        TextView tvDireccion = view.findViewById(R.id.tvDireccion);
        tvDireccion.setText(ciudadComerio);

        TextView tvFecha = view.findViewById(R.id.tvFecha);
        tvFecha.setText(PAYUtils.getLocalDate2());

        TextView tvHora = view.findViewById(R.id.tvHora);
        tvHora.setText(PAYUtils.getLocalTime2());

        TextView tvTerminal = view.findViewById(R.id.tvTerminal);
        tvTerminal.setText(ISOUtil.padleft(Device.getNumeroCajas(), 4, '0'));
    }

    @SuppressLint("SetTextI18n")
    private View reporteCierre(LayoutInflater inflater, ViewGroup container) {
        View view = inflater.inflate(R.layout.vista_result_settle, container, false);

        ImageView ivClose = view.findViewById(R.id.iv_close);
        ivClose.setVisibility(View.VISIBLE);
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        TextView tvTitulo = view.findViewById(R.id.tvTitulo);
        tvTitulo.setText("Informe de cierre");

        StringBuilder builder = new StringBuilder();
        builder.append("CIERRE DE LOTE - LOTE ");
        builder.append(modelo.getNumLote());
        builder.append("\n");
        builder.append("ENTRE ");
        builder.append(modelo.getFechaUltimoCierre());
        builder.append("\n");
        builder.append("HASTA ");
        builder.append(modelo.getFechaCierre());
        builder.append("\n");

        builder.append("TOTAL CREDITO - ");
        builder.append(modelo.getCantCredito());
        builder.append(" - Gs. ");
        builder.append(PAYUtils.FormatPyg(modelo.getTotalCredito()));
        builder.append("\n");

        builder.append("TOTAL DEBITO - ");
        builder.append(modelo.getCantDebito());
        builder.append(" - Gs. ");
        builder.append(PAYUtils.FormatPyg(modelo.getTotalDebito()));
        builder.append("\n");

        builder.append("TOTAL MOVIL - ");
        builder.append(modelo.getCantMovil());
        builder.append(" - Gs. ");
        builder.append(PAYUtils.FormatPyg(modelo.getTotalMovil()));
        builder.append("\n");

        builder.append("TOTAL ANULAR - ");
        builder.append(modelo.getCantAnular());
        builder.append(" - Gs. ");
        builder.append(PAYUtils.FormatPyg(modelo.getTotalAnular()));
        builder.append("\n");

        builder.append("TOTAL VUELTO - ");
        builder.append(modelo.getCantVuelto());
        builder.append(" - Gs. ");
        builder.append(PAYUtils.FormatPyg(modelo.getTotalVuelto()));
        builder.append("\n");

        builder.append("TOTAL VENTA SALDO - ");
        builder.append(modelo.getCantSaldo());
        builder.append(" - Gs. ");
        builder.append(PAYUtils.FormatPyg(modelo.getTotalSaldo()));
        builder.append("\n");

        TextView tvMensaje = view.findViewById(R.id.tvMensaje);
        tvMensaje.setText(builder.toString());

        TextView tvTotalGeneral = view.findViewById(R.id.tvTotalGeneral);
        tvTotalGeneral.setText("TOTAL GENERAL - " +
                modelo.getCantGeneral() + " - Gs. " +
                PAYUtils.FormatPyg(modelo.getTotalGeneral()));

        return view;
    }

    private String checkNull(String strText) {
        if (strText == null) {
            strText = "   ";
        }
        return strText;
    }

    private void inicializarRecyclerView(Context context, RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
    }

    public interface OnClickFragmentListener {
        void onClickFragment();
    }
}
