package com.flota.transactions.Billeteras;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wposs.flota.R;

import java.util.List;

public class AdaptadorBilleteras extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Billeteras> billeterasList;
    private Context ctx;
    private OnItemClickListener mOnItemClickListener;

    public AdaptadorBilleteras(Context ctx, List<Billeteras> billeterasList) {
        this.ctx = ctx;
        this.billeterasList = billeterasList;
    }

    public interface OnItemClickListener {
        void onItemClick(Billeteras billeteras);
    }

    public void setOnItemClickListener(OnItemClickListener mItemClickListener) {
        this.mOnItemClickListener = mItemClickListener;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_billeteras, parent, false);
        vh = new OriginalViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (holder instanceof OriginalViewHolder) {
            final OriginalViewHolder view = (OriginalViewHolder) holder;

            Billeteras p = billeterasList.get(position);
            view.tx_titulomenu.setText(p.getNombreBilletera());
            if (p.getLogoBilletera() != null) {
                view.imageView.setImageDrawable(p.getLogoBilletera());
                view.tx_titulomenu.setVisibility(View.GONE);
            } else {
                view.imageView.setImageDrawable(null);
                view.tx_titulomenu.setVisibility(View.VISIBLE);
            }

            view.cv_menu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnItemClickListener != null) {
                        mOnItemClickListener.onItemClick(billeterasList.get(position));
                    }
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return billeterasList.size();
    }


    public class OriginalViewHolder extends RecyclerView.ViewHolder {

        public TextView tx_titulomenu;
        public ImageView imageView;
        public LinearLayout cv_menu;


        public OriginalViewHolder(View v) {
            super(v);
            tx_titulomenu = v.findViewById(R.id.tx_titulomenu);
            imageView = v.findViewById(R.id.imageView);
            cv_menu = v.findViewById(R.id.cv_menu);
        }
    }
}

