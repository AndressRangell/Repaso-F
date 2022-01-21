package cn.desert.newpos.payui.master;

import com.wposs.flota.R;

public class mToolbar {
    int layout = 0;
    int leftIcon = 0;
    int rightIcon = 0;

    public mToolbar(int layout, int leftIcon, int rightIcon) {
        this.layout = layout;
        this.leftIcon = leftIcon;
        this.rightIcon = rightIcon;
    }

    public mToolbar(int layout) {
        this.layout = layout;
    }

    public int getLayout() {
        return layout;
    }

    public int getLeftIcon() {
        int res = R.drawable.ic_close;
        if (leftIcon != 0) {
            res = leftIcon;
        }
        return res;
    }

    public int getRightIcon() {
        int res = R.drawable.ic_acerca_de;
        if (rightIcon != 0) {
            res = rightIcon;
        }
        return res;
    }
}
