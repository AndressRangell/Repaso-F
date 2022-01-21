package com.flota.adapters.holder;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.flota.adapters.UAFactory;
import com.flota.adapters.model.CategoryModel;
import com.wposs.flota.R;

public class CategoryViewHolder extends RecyclerView.ViewHolder {

    private final Context context;
    @LayoutRes
    private final int layout;

    private TextView title;
    private RecyclerView recycler;
    private View separator;

    public CategoryViewHolder(View v, Context context, int layout) {
        super(v);
        try {
            title = v.findViewById(R.id.title_category);
            recycler = v.findViewById(R.id.recycler_info_category);
            separator = v.findViewById(R.id.separator_category);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.context = context;
        this.layout = layout;
    }

    public void bind(final CategoryModel category, final boolean isLastItem) {
        try {
            if (category.getTitle().isEmpty()) {
                title.setVisibility(View.GONE);
            } else {
                title.setText(category.getTitle());
            }
            recycler.setHasFixedSize(true);
            recycler.setLayoutManager(new LinearLayoutManager(context));
            recycler.setAdapter(UAFactory.adapterItem(context, layout, category.getInfoListItems()));

            if (isLastItem) separator.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
