package cn.desert.newpos.payui.master;

import static com.flota.menus.MenuAction.callBackSeatle;
import static java.lang.Thread.sleep;

import android.content.Intent;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flota.actividades.MainActivity;
import com.newpos.libpay.Logger;
import com.pos.device.icc.IccReader;
import com.pos.device.icc.SlotType;
import com.wposs.flota.R;

import cn.desert.newpos.payui.UIUtils;

/**
 * Created by zhouqiang on 2016/11/12.
 */
public class ResultControl extends FormularioActivity {

    static String className = "ResultControl.java";
    Button confirm;
    ImageView removeCard;
    IccReader iccReader0;
    Thread process = null;
    private boolean back = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Logger.debug("ResultControl.java", "onCreate activity");
        setContentView(R.layout.activity_remove_card);
        mostrarSerialvsVersion();
        removeCard2();
    }

    private void mostrarSerialvsVersion() {
        TextView tvSerial = findViewById(R.id.tvSerial);
        TextView tvVersion = findViewById(R.id.tvVersion);
        showVersionSerial(tvVersion, tvSerial);
    }

    private void removeCard2() {
        new Thread(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iccReader0 = IccReader.getInstance(SlotType.USER_CARD);
                        if (iccReader0.isCardPresent()) {
                            setContentView(R.layout.activity_remove_card);
                            mostrarSerialvsVersion();
                        }
                    }
                });
                validarICC();
                UIUtils.startView(ResultControl.this, MainActivity.class, "");
                if (callBackSeatle != null)
                    callBackSeatle.getRspSeatleReport(0);
            }
        }).start();
    }

    private void validarICC() {
        iccReader0 = IccReader.getInstance(SlotType.USER_CARD);
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        while (iccReader0.isCardPresent()) {
            toneG.startTone(ToneGenerator.TONE_PROP_BEEP2, 2000);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Logger.exception(className, e);
            } finally {
                Thread.currentThread().interrupt();
            }
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            over();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void over() {
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeCard();
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            if (!back) {
                backToMainMenu();
            }

            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    private void removeCard() {
        if (process == null) {
            process = new Thread(new Runnable() {
                public void run() {

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            iccReader0 = IccReader.getInstance(SlotType.USER_CARD);

                            if (iccReader0.isCardPresent()) {
                                setContentView(R.layout.activity_remove_card);
                                mostrarSerialvsVersion();
                                removeCard = (ImageView) findViewById(R.id.iv_remove__card);
                                removeCard.setImageResource(R.drawable.ic_retire_la_tarjeta);
                            }
                        }
                    });

                    checkCard();
                    finish();
                    backToMainMenu();
                    if (callBackSeatle != null)
                        callBackSeatle.getRspSeatleReport(0);
                }
            });
            process.start();
        }
    }

    private void checkCard() {
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        process = null;
        while (iccReader0.isCardPresent()) {
            try {
                toneG.startTone(ToneGenerator.TONE_PROP_BEEP2, 2000);
                sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
                Logger.exception(className, e);
                Logger.error("Exception" , e);
                if (e instanceof InterruptedException) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        back = false;
    }

    public void backToMainMenu() {
        Intent intent = new Intent();
        intent.setClass(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
