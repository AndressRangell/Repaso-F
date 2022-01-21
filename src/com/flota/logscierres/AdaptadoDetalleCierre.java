package com.flota.logscierres;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.flota.adapters.model.ButtonModel;
import com.newpos.libpay.Logger;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.PAYUtils;
import com.wposs.flota.R;

import java.util.List;

public class AdaptadoDetalleCierre extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final String TAG = "AdaptadoDetalleCierre";

    private final List<TransLogData> items;
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public AdaptadoDetalleCierre(Context ctx, List<TransLogData> items) {
        this.ctx = ctx;
        this.items = items;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.itemdetallecierre, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;

            try {
                final TransLogData data = items.get(position);

                String cargo = data.getNroCargo().substring(2);
                String card = "****";
                if (data.getPan() != null) {
                    card += data.getPan().substring(data.getPan().length() - 4);
                }
                String monto = String.valueOf(data.getAmount());
                monto = monto.substring(0, monto.length() - 2);
                monto = PAYUtils.formatMontoGs(monto);
                if (monto.equals("G. nu")) {
                    monto = "G. 0";
                }
                view.tcRef.setText(cargo);
                view.tvNumeroTarjeta.setText(card);
                view.tvTipoMonto.setText(monto + "   " + data.getTipoTarjeta());

            } catch (Exception e) {
                Logger.exception(TAG, e);
                e.printStackTrace();
            }

        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public interface OnItemClickListener {
        void onItemClick(View view, ButtonModel obj, int position);
    }

    public static class OriginalViewHolder extends RecyclerView.ViewHolder {

        private final TextView tcRef;
        private final TextView tvNumeroTarjeta;
        private final TextView tvTipoMonto;

        public OriginalViewHolder(View v) {
            super(v);
            tcRef = v.findViewById(R.id.tcRef);
            tvNumeroTarjeta = v.findViewById(R.id.tvNumeroTarjeta);
            tvTipoMonto = v.findViewById(R.id.tvTipoMonto);

        }
    }
}
