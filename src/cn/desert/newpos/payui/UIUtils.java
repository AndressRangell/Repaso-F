package cn.desert.newpos.payui;

import static java.lang.Thread.sleep;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flota.defines_bancard.DefinesBANCARD;
import com.flota.inicializacion.trans_init.trans.Tools;
import com.newpos.libpay.Logger;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.SDKException;
import com.pos.device.rtc.RealTimeClock;
import com.wposs.flota.BuildConfig;
import com.wposs.flota.R;

import java.io.File;

import cn.desert.newpos.payui.master.ResultControl;
import me.drakeet.support.toast.BadTokenListener;
import me.drakeet.support.toast.ToastCompat;

/**
 * @author zhouqiang
 * @email wy1376359644@163.com
 */
public class UIUtils {

    private static final String CLASS_NAME = "UIUtils.java";

    private UIUtils(){

    }

    /**
     * 显示交易结果
     *
     * @param activity
     * @param flag
     * @param info
     */
    public static void startResult(Activity activity, boolean flag, String info) {
        Intent intent = new Intent();
        intent.setClass(activity, ResultControl.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Bundle bundle = new Bundle();
        bundle.putBoolean("flag", flag);
        bundle.putString("info", info);
        intent.putExtras(bundle);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * @param activity
     * @param cls
     */
    public static void startView(Activity activity, Class<?> cls, String putExtra) {
        Intent intent = new Intent();
        intent.setClass(activity, cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(DefinesBANCARD.DATO_MENU, putExtra);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * 自定义提示信息
     *
     * @param activity
     * @param content
     */
    public static void toast(Activity activity, boolean flag, int content) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        LayoutInflater inflater3 = activity.getLayoutInflater();
        View view3 = inflater3.inflate(R.layout.app_toast,
                (ViewGroup) activity.findViewById(R.id.toast_layout));
        ImageView face = view3.findViewById(R.id.app_t_iv);
        if (flag) {
            face.setImageDrawable(activity.getDrawable(R.drawable.icon_face_laugh));
        } else {
            face.setImageDrawable(activity.getDrawable(R.drawable.icon_face_cry));
        }
        int[] gravity = {Gravity.CENTER, 0, 0};
        String str = activity.getResources().getString(content);
        showToast(activity, str, Toast.LENGTH_SHORT, view3, gravity);
    }

    /**
     * 自定义提示信息
     *
     * @param activity
     * @param str
     */
    public static void toast(Activity activity, boolean flag, String str) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        LayoutInflater inflater3 = activity.getLayoutInflater();
        View view3 = inflater3.inflate(R.layout.app_toast,
                (ViewGroup) activity.findViewById(R.id.toast_layout));
        ImageView face = view3.findViewById(R.id.app_t_iv);
        if (flag) {
            face.setImageDrawable(activity.getDrawable(R.drawable.icon_face_laugh));
        } else {
            face.setImageDrawable(activity.getDrawable(R.drawable.icon_face_cry));
        }
        int[] gravity = {Gravity.CENTER, 0, 0};
        showToast(activity, str, Toast.LENGTH_SHORT, view3, gravity);
    }

    public static void toast(Activity activity, int ico, String str, int duration) {
        if (activity == null || activity.isFinishing()) {
            return;
        }
        LayoutInflater inflater3 = activity.getLayoutInflater();
        View view3 = inflater3.inflate(R.layout.app_toast,
                (ViewGroup) activity.findViewById(R.id.toast_layout));
        ImageView face = view3.findViewById(R.id.app_t_iv);

        face.setImageDrawable(activity.getDrawable(ico));
        int[] gravity = {Gravity.CENTER, 0, 400};
        showToast(activity, str, duration, view3, gravity);
    }

    private static void showToast(Context ctx, String message, int duration, View view, int[] gravity) {
        ((TextView) view.findViewById(R.id.toast_tv)).setText(message);
        Logger.flujo( "Showing Toast: " + message);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
            ToastCompat toastCompat = ToastCompat.makeText(ctx, message, duration)
                    .setBadTokenListener(new BadTokenListener() {
                        @Override
                        public void onBadTokenCaught(@NonNull Toast toast) {
                            Logger.error("failed toast", "BadTokenException");
                            Logger.exception( getClass().getName() + " BadTokenException showToast");
                        }
                    });
            toastCompat.setView(view);
            toastCompat.setGravity(gravity[0], gravity[1], gravity[2]);
            toastCompat.setDuration(duration);
            toastCompat.show();
        } else {
            Toast toast = new Toast(ctx);
            toast.setGravity(gravity[0], gravity[1], gravity[2]);
            toast.setDuration(duration);
            toast.setView(view);
            toast.show();
        }
    }

    public static Dialog centerDialog(Context mContext, int resID, int root) {
        final Dialog pd = new Dialog(mContext, R.style.Translucent_Dialog);
        pd.setContentView(resID);
        LinearLayout layout = (LinearLayout) pd.findViewById(root);
        layout.startAnimation(AnimationUtils.loadAnimation(mContext, R.anim.up_down));
        pd.setCancelable(true);
        pd.setCanceledOnTouchOutside(true);
        pd.show();
        return pd;
    }

    /**
     * 删除文件或者文件夹
     *
     * @param file
     */
    public static void delete(File file) {
        if (!file.exists()) {
            return;
        }

        if (file.isFile()) {
            file.delete();
            return;
        }

        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }

            for (File childFile : childFiles) {
                delete(childFile);
            }
            file.delete();
        }
    }

    /**
     * 根据资源ID获取资源字符串
     *
     * @param context 上下文对象
     * @param resid   资源ID
     * @return
     */
    public static String getStringByInt(Context context, int resid) {
        return context.getResources().getString(resid);
    }

    /**
     * 根据资源ID获取资源字符串
     *
     * @param context
     * @param resid
     * @param parm
     * @return
     */
    public static String getStringByInt(Context context, int resid, String parm) {
        String sAgeFormat1 = context.getResources().getString(resid);
        return String.format(sAgeFormat1, parm);
    }

    /**
     * 根据资源ID获取资源字符串
     *
     * @param context
     * @param resid
     * @param parm1
     * @param parm2
     * @return
     */
    public static String getStringByInt(Context context, int resid,
                                        String parm1, String parm2) {
        String sAgeFormat1 = context.getResources().getString(resid);
        return String.format(sAgeFormat1, parm1, parm2);
    }

    public static void beep(int typeTone) {

        int timeOut = 2;
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);


        long start = SystemClock.uptimeMillis();
        while (true) {
            toneG.startTone(typeTone, 2000);
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Logger.exception(CLASS_NAME, e);
                Logger.error("Exception" , e);
                Thread.currentThread().interrupt();
            }

            if (SystemClock.uptimeMillis() - start > timeOut) {
                toneG.stopTone();
                break;
            }
        }
    }

    public static String labelHTML(final String label, final String value) {
        return "<b>" + label + "</b>" + " " + value;
    }

    /**
     * Crea un dialogo de alerta
     *
     * @return Nuevo dialogo
     */
    public static void showAlertDialog(String title, String msg, Context context) {
        final android.app.AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(context);

        alertDialog.setTitle(title);
        alertDialog.setMessage(msg);
        alertDialog.setIcon(R.drawable.ic_redinfonet);
        alertDialog.setCancelable(false);

        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        android.app.AlertDialog dialog = alertDialog.create();
        dialog.show();
    }


    public static void dateTime(String date, String time) {
        if (time != null && date != null && !time.isEmpty() && !date.isEmpty()) {
            StringBuilder dateTime = new StringBuilder();
            try {
                dateTime.append(PAYUtils.getYear());
                dateTime.append(date);
                dateTime.append(time);

                RealTimeClock.set(dateTime.toString());
            } catch (SDKException e) {
                Logger.exception(CLASS_NAME, e);
                e.printStackTrace();
                e.printStackTrace();
            }
        }
    }

    public static void mostrarSerialvsVersion(TextView tvVersion, TextView tvSerial) {
        tvVersion.setText(BuildConfig.VERSION_NAME);

        tvSerial.setText(formatSerial());
    }

    public static String formatSerial() {
        String serial = Tools.getSerial();
        int space = 5;
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < serial.length(); i += space) {
            if (i + space >= serial.length()) {
                result.append(serial.substring(i));
            } else {
                result.append(serial.substring(i, i + space)).append("-");
            }
        }
        return result.toString();
    }

    public static boolean verificacionBoolean(Context context, String parametro, String claseEvento) {
        if (!parametro.isEmpty()) {
            if (parametro.equals("true") || parametro.equals("1") || parametro.equalsIgnoreCase("WIFI")) {
                return true;
            } else if (parametro.equals("false") || parametro.equals("0") || parametro.equalsIgnoreCase("3G")) {
                return false;
            } else {
                ISOUtil.showMensaje("Parametro no definido \n" + claseEvento, context);
            }
        }
        return false;
    }
}
