package com.flota.adapters;

import static com.flota.adapters.UAListener.AppListener;
import static com.flota.adapters.UAListener.ButtonListener;
import static com.flota.adapters.UAListener.ProductListener;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.flota.adapters.holder.AppViewHolder;
import com.flota.adapters.holder.ButtonViewHolder;
import com.flota.adapters.holder.CategoryViewHolder;
import com.flota.adapters.holder.InfoViewHolder;
import com.flota.adapters.holder.ProductViewHolder;
import com.flota.adapters.holder.SettingViewHolder;
import com.flota.adapters.model.AppModel;
import com.flota.adapters.model.ButtonModel;
import com.flota.adapters.model.CategoryModel;
import com.flota.adapters.model.InfoModel;
import com.flota.adapters.model.ProductModel;
import com.flota.adapters.model.SettingModel;
import com.flota.adapters.model.UAItem;
import com.wposs.flota.R;

import java.util.List;

public class UniversalAdapter extends Adapter<ViewHolder> {

    public static final String TAG = "UniversalAdapter";

    private final int layout;
    private Context context;
    private List<UAItem> items;
    private UAListener.Listener listener;

    // Options
    private boolean inCategory = false;

    // Select Item
    private int checkedPosition = -1;

    public UniversalAdapter(Context context, @LayoutRes int layout, boolean inCategory,
                            int checkedPosition, List<UAItem> items, UAListener.Listener listener) {
        this.context = context;
        this.layout = layout;
        this.inCategory = inCategory;
        this.checkedPosition = checkedPosition;
        this.items = items;
        this.listener = listener;
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(
                inCategory ? R.layout.item_category : layout, parent, false);
        try {
            if (inCategory) return new CategoryViewHolder(v, context, layout);

            switch (layout) {
                case R.layout.item_button:
                    return new ButtonViewHolder(v, (ButtonListener) listener);
                case R.layout.item_setting:
                    return new SettingViewHolder(v, context, (ButtonListener) listener);
                case R.layout.item_app:
                    return new AppViewHolder(v, (AppListener) listener);
                case R.layout.item_product:
                    return new ProductViewHolder(v, (ProductListener) listener, new UAListener.UAL() {
                        @Override
                        public void updateLastItem(int adapterPosition) {
                            if (checkedPosition != adapterPosition) {
                                notifyItemChanged(checkedPosition);
                                checkedPosition = adapterPosition;
                            }
                        }
                    });
                case R.layout.item_info:
                case R.layout.item_info_config:
                    return new InfoViewHolder(v);
                default:
                    Log.e(TAG, "onCreateViewHolder: item missing");
                    return new EmptyViewHolder(v);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new EmptyViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(ViewHolder h, int position) {
        final boolean isLastItem = position == items.size() - 1;

        if (inCategory)
            ((CategoryViewHolder) h).bind((CategoryModel) items.get(position), isLastItem);

        if (h instanceof ButtonViewHolder) {
            ((ButtonViewHolder) h).bind((ButtonModel) items.get(position));
        } else if (h instanceof SettingViewHolder) {
            ((SettingViewHolder) h).bind((SettingModel) items.get(position), isLastItem);
        } else if (h instanceof AppViewHolder) {
            ((AppViewHolder) h).bind((AppModel) items.get(position));
        } else if (h instanceof ProductViewHolder) {
            ((ProductViewHolder) h).bind((ProductModel) items.get(position), checkedPosition);
        } else if (h instanceof InfoViewHolder) {
            ((InfoViewHolder) h).bind((InfoModel) items.get(position), isLastItem);
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class EmptyViewHolder extends ViewHolder {
        public EmptyViewHolder(View v) {
            super(v);
        }
    }
}
