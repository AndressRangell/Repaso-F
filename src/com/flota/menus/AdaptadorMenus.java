package com.flota.menus;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.flota.adaptadores.ModeloMenusOpciones;
import com.wposs.flota.R;

import java.util.List;

public class AdaptadorMenus extends RecyclerView.Adapter<AdaptadorMenus.ViewHolder> {

    List<ModeloMenusOpciones> menuList;
    Context context;
    MenusCallback callback;

    public AdaptadorMenus(Context context, List<ModeloMenusOpciones> menuList) {
        this.context = context;
        this.menuList = menuList;
    }

    public void setCallback(MenusCallback callback) {
        this.callback = callback;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_imgbutton, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        if (holder != null) {
            if (menuList.get(position).isHABILITAR_TRANSACCION()) {
                holder.menuOption.setImageDrawable(menuList.get(position).getIcono());
                holder.menuOption.setVisibility(View.VISIBLE);
                holder.menuOption.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        callback.onMenuClick(menuList.get(position));
                    }
                });
            } else {
                holder.menuOption.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return menuList.size();
    }

    public interface MenusCallback {
        void onMenuClick(ModeloMenusOpciones option);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton menuOption;

        public ViewHolder(View itemView) {
            super(itemView);
            menuOption = itemView.findViewById(R.id.menuOpcion);
        }
    }

}

