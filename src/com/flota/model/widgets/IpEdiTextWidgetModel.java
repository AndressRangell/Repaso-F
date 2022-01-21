package com.flota.model.widgets;

import android.content.Context;
import android.view.View;

import com.wposs.flota.R;

import cn.desert.newpos.payui.setting.view.IPEditText;

public class IpEdiTextWidgetModel extends WidgetModel implements ItemWidget {

    IPEditText ipEditText;

    public IpEdiTextWidgetModel(String title, int type, boolean isEnabled, Context context) {
        super(title, type, context);
        ipEditText = new IPEditText(context, null);
        ipEditText.setLiveOrDeath(isEnabled);
    }


    public void setIPHint(String ip) {
        oldValue = ip;
        String[] ipValor = ip.replace(".", "-").split("-");
        if (ipValor.length == 4) {
            ipEditText.setIpHint(ipValor);
        }
    }

    public void setText(String ip) {
        oldValue = ip;
        String[] ipValor = ip.replace(".", "-").split("-");
        if (ipValor.length == 4) {
            ipEditText.setIPText(ipValor);
        }
    }

    public void setDesign(int i) {
        switch (i) {
            case 0:
                ipEditText.setLayoutParams(lp);
                break;
            case 1:
                ipEditText.setLayoutParams(lp);
                ipEditText.setBackground(R.drawable.fondo_editext, context);
                ipEditText.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        ipEditText.setIPText(new String[]{"0", "0", "0", "0"});
                        return false;
                    }
                });
            default:
        }
    }

    @Override
    public IPEditText getWidget() {
        return ipEditText;
    }

    @Override
    public String getTituloItem() {
        return title;
    }

    @Override
    public View getWidgetItem() {
        return ipEditText;
    }

    @Override
    public String getResultado() {
        return ipEditText.getIPTextOrHint();
    }

    @Override
    public boolean isNewValor() {
        return !oldValue.equals(ipEditText.getIPTextOrHint());
    }
}
