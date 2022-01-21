package com.flota.adapters.holder;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.flota.adapters.UAListener;
import com.flota.adapters.model.ProductModel;
import com.wposs.flota.R;

public class ProductViewHolder extends RecyclerView.ViewHolder {

    private final UAListener.ProductListener listener;
    private final UAListener.UAL ual;

    private TextView code;
    private TextView description;
    private CardView card;

    public ProductViewHolder(View v, UAListener.ProductListener listener, UAListener.UAL ual) {
        super(v);
        try {
            code = v.findViewById(R.id.code_product);
            description = v.findViewById(R.id.description_product);
            card = v.findViewById(R.id.card_product);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.listener = listener;
        this.ual = ual;
    }

    public void bind(final ProductModel product, final int checkedPosition) {
        try {
            if (checkedPosition == getAdapterPosition()) {
                card.setCardBackgroundColor(0xFFBDCCE0);
            } else {
                card.setCardBackgroundColor(0xFFF0F5FF);
            }

            code.setText(product.getCode());
            description.setText(product.getDescription());
            card.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    card.setCardBackgroundColor(0xFFBDCCE0);
                    ual.updateLastItem(getAdapterPosition());
                    listener.onClick(v, product);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
