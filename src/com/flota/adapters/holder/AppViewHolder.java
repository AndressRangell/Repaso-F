package com.flota.adapters.holder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.flota.adapters.UAListener;
import com.flota.adapters.model.AppModel;
import com.wposs.flota.R;

public class AppViewHolder extends RecyclerView.ViewHolder {

    private final UAListener.AppListener listener;

    private TextView name;
    private CardView card;

    public AppViewHolder(View v, UAListener.AppListener listener) {
        super(v);
        try {
            name = v.findViewById(R.id.name_app);
            card = v.findViewById(R.id.card_app);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.listener = listener;
    }

    public void bind(final AppModel app) {
        try {
            name.setText(app.getName());
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(v, app);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
