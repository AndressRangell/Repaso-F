package com.flota.adapters.holder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.flota.adapters.UAListener;
import com.flota.adapters.model.ButtonModel;
import com.wposs.flota.R;

public class ButtonViewHolder extends RecyclerView.ViewHolder {

    private final UAListener.ButtonListener listener;

    private ImageView icon;
    private TextView name;
    private TextView code;
    private CardView card;

    public ButtonViewHolder(View v, UAListener.ButtonListener listener) {
        super(v);
        try {
            icon = v.findViewById(R.id.icon_button);
            name = v.findViewById(R.id.name_button);
            code = v.findViewById(R.id.code_button);
            card = v.findViewById(R.id.card_button);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.listener = listener;
    }

    public void bind(final ButtonModel button) {
        try {
            name.setText(button.getName());
            if (button.getIcon() != null) {
                icon.setImageDrawable(button.getIcon());
            } else {
                code.setText(button.getCode().trim());
            }
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onClick(v, button);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
