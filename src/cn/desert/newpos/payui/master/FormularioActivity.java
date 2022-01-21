package cn.desert.newpos.payui.master;

import static cn.desert.newpos.payui.UIUtils.formatSerial;

import android.content.Context;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flota.screen.utils.ScreenToolbar;
import com.newpos.libpay.Logger;
import com.wposs.flota.BuildConfig;
import com.wposs.flota.R;

import java.io.File;

import cn.desert.newpos.payui.UIUtils;

public class FormularioActivity extends AppCompatActivity {

    public static final String TAG_FORM = "FormActivity";

    protected int leftButton = R.id.iv_close;
    protected int rightButton = R.id.iv_Info;
    TextView tvVersion;
    TextView tvSerial;

    protected void initScreen(@LayoutRes int layoutResID, ScreenToolbar screenToolbar) {
        try {
            setContentView(layoutResID);
            showSerialVersion();
            initToolbar(screenToolbar);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initToolbar(ScreenToolbar toolbar) {
        // Button Left
        ImageView left = findViewById(leftButton);
        if (toolbar.getListenerLeft() != null) {
            if (toolbar.getIconLeft() != 0)
                left.setImageDrawable(getDrawable(toolbar.getIconLeft()));
            left.setOnClickListener(toolbar.getListenerLeft());
            left.setVisibility(View.VISIBLE);
        } else left.setVisibility(View.VISIBLE);
        // Button Right
        ImageView right = findViewById(rightButton);
        if (toolbar.getListenerRight() != null) {
            if (toolbar.getIconRight() != 0)
                right.setImageDrawable(getDrawable(toolbar.getIconRight()));
            right.setOnClickListener(toolbar.getListenerLeft());
            right.setVisibility(View.VISIBLE);
        } else right.setVisibility(View.GONE);
    }

    protected void initView(mToolbar res, onToolbarClick leftButton, onToolbarClick rightButton) {
        setContentView(res.getLayout());
        showSerialVersion();
        initToolbar(leftButton, rightButton, res);
    }

    protected void toast(String msg) {
        if (msg != null && !msg.trim().isEmpty())
            try {
                UIUtils.toast(this, R.drawable.redinfonet, msg, Toast.LENGTH_SHORT);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG_FORM, "error show toast: msg=" + msg);
            }
    }

    protected void tone() {
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        toneG.startTone(ToneGenerator.TONE_CDMA_PIP, 500);
    }

    protected void genRcy(@IdRes int id, Adapter<?> adapter) {
        try {
            RecyclerView recycler = findViewById(id);
            recycler.setAdapter(adapter);
            recycler.setHasFixedSize(true);
            recycler.setNestedScrollingEnabled(false);
            recycler.setLayoutManager(new GridLayoutManager(this, 3));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void genRcySettings(@IdRes int id, Adapter<?> adapter) {
        try {
            RecyclerView recycler = findViewById(id);
            recycler.setAdapter(adapter);
            recycler.setHasFixedSize(true);
            recycler.setLayoutManager(new LinearLayoutManager(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initView(@LayoutRes int layoutResID) {
        initView(new mToolbar(layoutResID, 0, 0), null, null);
    }

    protected void showVersionSerial(TextView tvVersion, TextView tvSerial) {
        tvVersion.setText(BuildConfig.VERSION_NAME);
        tvSerial.setText(formatSerial());
    }

    protected void showVersionSerial() {
        try {
            showVersionSerial((TextView) findViewById(R.id.tvVersion),
                    (TextView) findViewById(R.id.tvSerial));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initToolbar(final onToolbarClick leftButtonClick, final onToolbarClick rightButtonClick, mToolbar res) {
        int closeButton = res.getLeftIcon();
        int infoButton = res.getRightIcon();

        //RIGHT BUTTON
        ImageView ivClose = findViewById(leftButton);
        ivClose.setImageDrawable(getDrawable(closeButton));
        ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leftButtonClick.onClick();
            }
        });

        //LEFT BUTTON
        ImageView ivInfo = findViewById(rightButton);
        ivInfo.setImageDrawable(getDrawable(infoButton));
        ivInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rightButtonClick.onClick();
            }
        });

        ivClose.setVisibility(getVisibility(leftButtonClick));

        ivInfo.setVisibility(getVisibility(rightButtonClick));
    }

    protected void setButtonVisibility(@IdRes int res, boolean visibility) {
        ImageView b = findViewById(res);
        b.setVisibility(visibility ? View.VISIBLE : View.INVISIBLE);
    }

    private int getVisibility(onToolbarClick button) {
        int visibility = View.INVISIBLE;
        if (button != null) {
            visibility = View.VISIBLE;
        }
        return visibility;
    }

    protected void showSerialVersion() {
        tvSerial = findViewById(R.id.tvSerial);
        tvVersion = findViewById(R.id.tvVersion);
        tvVersion.setText(BuildConfig.VERSION_NAME);
        tvSerial.setText(formatSerial());
    }

    protected void inicializarRecyclerViewLinear(Context context, RecyclerView recyclerView) {
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
    }

    protected void inicializarRecyclerViewGrid(Context context, RecyclerView recyclerView,
                                               int spanCount) {
        recyclerView.setLayoutManager(new GridLayoutManager(context, spanCount));
        recyclerView.setHasFixedSize(true);
        recyclerView.setNestedScrollingEnabled(false);
    }

    protected void eliminarCache(Context context, String clase) {
        try {
            File dir = context.getCacheDir();
            if (dir.exists()) {
                deleteDir(dir);
            }
        } catch (Exception e) {
            Logger.exception(clase, e);
            e.printStackTrace();
        }
    }

    private boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (String child : children) {
                boolean success = deleteDir(new File(dir, child));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

    protected void eliminarDatos(File fileOrDirectory, String clase) {
        try {
            if (fileOrDirectory.exists()) {
                if (fileOrDirectory.isDirectory())
                    for (File child : fileOrDirectory.listFiles())
                        eliminarDatos(child, clase);
                fileOrDirectory.delete();
            }
        } catch (Exception e) {
            Logger.exception(clase, e);
            e.printStackTrace();
        }
    }
}
