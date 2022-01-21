package com.flota.adapters.holder;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.flota.adapters.model.InfoModel;
import com.wposs.flota.R;

public class InfoViewHolder extends RecyclerView.ViewHolder {

    private TextView subtitle;
    private TextView content;
    private View separator;

    public InfoViewHolder(View v) {
        super(v);
        try {
            subtitle = v.findViewById(R.id.subtitle_info);
            content = v.findViewById(R.id.content_info);
            separator = v.findViewById(R.id.separator_info);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void bind(final InfoModel info, final boolean isLastItem) {
        try {
            subtitle.setText(info.getSubtitle());
            content.setText(info.getContent());
            if (isLastItem) separator.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
