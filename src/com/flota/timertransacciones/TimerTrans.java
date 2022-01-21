package com.flota.timertransacciones;

import android.os.CountDownTimer;
import android.util.Log;

import com.newpos.libpay.Logger;

public class TimerTrans extends CountDownTimer {

    static TimerTrans timerTrans;
    static String nombreClase = "TimerTrans.java";
    int timeout;
    String mensaje;
    String metodo;
    static OnResultTimer resultTimer;

    public TimerTrans(int timeout, final String mensaje, final String metodo) {
        super(timeout, 5000);
        this.timeout = timeout;
        this.mensaje = mensaje;
        this.metodo = metodo;

    }

    public interface OnResultTimer {
        void rsp2Timer();
    }


    @Override
    public void onTick(long millisUntilFinished) {
        Logger.error("onTick", "init onTick countDownTimer <<< " + mensaje + ">>> Metodo -> "+metodo+" Time : " + millisUntilFinished);
    }

    @Override
    public void onFinish() {
        timerTrans.cancel();
        Logger.error("Mensaje ", "counterDownTimer: " + " Finalizado ");

        resultTimer.rsp2Timer();

    }

    public static void deleteTimer() {
        if (timerTrans != null) {
            try {
                timerTrans.finalize();
            } catch (Throwable throwable) {
                Logger.exception("TimerTrans.java", throwable);
                throwable.printStackTrace();
            }
            timerTrans.cancel();
            Logger.error(nombreClase, "<<< clase TimerTrans  >>> : Se elimna  Timer del  ->  ( Metodo : " + timerTrans.metodo + " ) se Cancela ");
        }
    }


    public static void setResultTimer(OnResultTimer resultTimer) {
        TimerTrans.resultTimer = resultTimer;
    }

    public static void getInstanceTimerTrans(int timeout, String mensaje, String metodo, OnResultTimer resultTimer) {
        setResultTimer(resultTimer);

        if (timerTrans != null) {
            timerTrans.cancel();
            Logger.error(nombreClase, "<<< clase TimerTrans  >>> : Se detecto un Timer ->  ( Metodo : " + timerTrans.metodo + " ) se Cancela ");
            timerTrans = null;
        }
        timerTrans = new TimerTrans(timeout, mensaje, metodo);
        timerTrans.start();
        Logger.error(nombreClase, "<<< clase TimerTrans >>> : Se Crea  un Nuevo Timer ->  ( Metodo : " + metodo + " ) ");
    }
}
