package com.flota.adapters.holder;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.flota.adapters.UAFactory;
import com.flota.adapters.UAListener;
import com.flota.adapters.model.SettingModel;
import com.wposs.flota.R;

public class SettingViewHolder extends RecyclerView.ViewHolder {

    private final UAListener.ButtonListener listener;
    private final Context context;

    private TextView title;
    private RecyclerView recycler;
    private View separator;

    public SettingViewHolder(View v, Context context, UAListener.ButtonListener listener) {
        super(v);
        try {
            title = v.findViewById(R.id.title_setting);
            recycler = v.findViewById(R.id.recycler_buttons_setting);
            separator = v.findViewById(R.id.separator_setting);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.context = context;
        this.listener = listener;
    }

    public void bind(final SettingModel setting, final boolean isLastItem) {
        try {
            title.setText(setting.getTitle());
            recycler.setLayoutManager(new GridLayoutManager(context, 3));
            recycler.setHasFixedSize(true);
            recycler.setNestedScrollingEnabled(false);
            recycler.setAdapter(UAFactory.adapterButtons(setting.getButtons(), listener));

            if (isLastItem) separator.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
