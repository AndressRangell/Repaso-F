package com.flota.logscierres;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.newpos.libpay.utils.PAYUtils;
import com.wposs.flota.R;

import cn.desert.newpos.payui.transrecord.ListAdapter;

public class CierresLogsAdapter extends ListAdapter<LogsCierresModelo> {

    private final OnItemReprintClick click;

    public CierresLogsAdapter(Activity context, OnItemReprintClick l) {
        super(context);
        click = l;
    }

    @SuppressLint({"SetTextI18n", "InflateParams"})
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHold viewHold;
        LogsCierresModelo item = null;
        if (mList.size() > 0) {
            item = mList.get(position);
        }
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_reporte_cierre, null);
            viewHold = new ViewHold();
            viewHold.tvTitulo = convertView.findViewById(R.id.tvTitulo);
            viewHold.tvTipoCierre = convertView.findViewById(R.id.tvTipoCierre);
            viewHold.tvFechaAnterior = convertView.findViewById(R.id.tvFechaAnterior);
            viewHold.tvFechaCierre = convertView.findViewById(R.id.tvFechaCierre);
            viewHold.tvGeneral = convertView.findViewById(R.id.tvGeneral);
            viewHold.reprint = convertView.findViewById(R.id.re_print);
            viewHold.detallado = convertView.findViewById(R.id.detallado);
            convertView.setTag(viewHold);
        } else {
            viewHold = (ViewHold) convertView.getTag();
        }

        if (item != null) {

            String numLote = item.getNumLote();
            if (!PAYUtils.isNullWithTrim(numLote)) {
                viewHold.tvTitulo.setText("CIERRE DE LOTE NUMERO: " + numLote);
            }

            String tipoCierre = item.getTipoCierre();
            if (!PAYUtils.isNullWithTrim(tipoCierre)) {
                viewHold.tvTipoCierre.setText("MODO CIERRE: " + tipoCierre);
            }

            String entre = item.getFechaUltimoCierre();
            if (!PAYUtils.isNullWithTrim(entre)) {
                viewHold.tvFechaAnterior.setText("ENTRE: " + entre);
            }

            String fechaCierre = item.getFechaCierre();
            if (!PAYUtils.isNullWithTrim(fechaCierre)) {
                viewHold.tvFechaCierre.setText("HASTA: " + fechaCierre);
            }

            String cantGeneral = item.getCantGeneral();
            String totalGeneral = item.getTotalGeneral();
            if (!PAYUtils.isNullWithTrim(cantGeneral) && !PAYUtils.isNullWithTrim(totalGeneral)) {
                    viewHold.tvGeneral.setText("CANT: " + cantGeneral + " - MONTO: Gs. " + PAYUtils.FormatPyg(totalGeneral));
            }

            final LogsCierresModelo finalItem = item;
            viewHold.reprint.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (click != null) {
                        click.onItemClick(finalItem, false);
                    }
                }
            });

            viewHold.detallado.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (click != null) {
                        click.onItemClick(finalItem, true);
                    }
                }
            });

            convertView.setTag(R.id.tag_item_history_trans, item);
        }
        return convertView;
    }

    public interface OnItemReprintClick {
        void onItemClick(LogsCierresModelo logsCierresModelo, boolean isDetallado);
    }

    static final class ViewHold {
        TextView tvTitulo;
        TextView tvTipoCierre;
        TextView tvFechaAnterior;
        TextView tvFechaCierre;
        TextView tvGeneral;

        Button reprint;
        Button detallado;
    }
}
