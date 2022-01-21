package com.flota.tools;

import android.annotation.SuppressLint;
import android.app.Instrumentation;
import android.content.Context;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.desert.keyboard.DesertboardListener;
import com.android.desert.keyboard.InputInfo;
import com.android.desert.keyboard.InputListener;
import com.android.desert.keyboard.InputManager;
import com.newpos.libpay.Logger;


public class InputManager2 extends InputManager {

    private static final String TAG = "InputManager2.java";
    private static final String PAY_EN = "Please chose pay style";
    private static final String PAY_CH = "请选择付款方式";
    private final Context context;
    private final InputManager instance;
    private final WindowManager mWindowManager;
    private LinearLayout container;
    private ImageView alipayView;
    private ImageView wetchatView;
    private EditText input;
    private String mTitle = InputManager.class.getSimpleName();
    private InputManager.Mode mInputMode;
    private InputManager.Lang mLang;
    private boolean mDisorder;
    private boolean mAddEdit;
    private boolean mAddKeyboard;
    private boolean mAddStyles;
    private boolean useOnce;
    private int mLenData;
    private InputListener mListener;

    @SuppressLint("WrongConstant")
    public InputManager2(Context c) {
        super(c);
        this.mInputMode = InputManager.Mode.AMOUNT;
        this.mLang = InputManager.Lang.EN;
        this.mDisorder = false;
        this.mAddEdit = true;
        this.mAddKeyboard = true;
        this.mAddStyles = false;
        this.useOnce = false;
        this.context = c;
        this.instance = this;
        this.mLenData = 8;
        this.mWindowManager = (WindowManager) this.context.getSystemService("window");
    }

    private static void sendKeyCode(final int keyCode) {
        (new Thread() {
            @Override
            public void run() {
                try {
                    Instrumentation inst = new Instrumentation();
                    inst.sendKeyDownUpSync(keyCode);
                } catch (Exception var2) {
                    var2.printStackTrace();
                    Logger.exception(TAG, var2);
                    Logger.error("Exception" + var2.toString());
                }

            }
        }).start();
    }

    private static String padleft(String s, int len, char c) {
        s = s.trim();
        if (s.length() > len) {
            return null;
        } else {
            StringBuilder d = new StringBuilder(len);
            int var4 = len - s.length();

            while (var4-- > 0) {
                d.append(c);
            }

            d.append(s);
            return d.toString();
        }
    }

    @Override
    public InputManager setTitle(String title) {
        this.mTitle = title;
        return this.instance;
    }

    @Override
    public InputManager setTitle(int rid) {
        this.mTitle = this.context.getResources().getString(rid);
        return this.instance;
    }

    @Override
    public InputManager setLang(InputManager.Lang l) {
        this.mLang = l;
        return this.instance;
    }

    @Override
    public InputManager setUseOnce(boolean useOnce) {
        this.useOnce = useOnce;
        return this.instance;
    }

    @Override
    public InputManager addEdit(InputManager.Mode mode) {
        this.mAddEdit = true;
        this.mInputMode = mode;
        return this.instance;
    }

    public InputManager addEdit(InputManager.Mode mode, int lenData) {
        this.mAddEdit = true;
        this.mInputMode = mode;
        this.mLenData = lenData;
        return this.instance;
    }

    @Override
    public InputManager addKeyboard(boolean disorder) {
        this.mAddKeyboard = true;
        this.mDisorder = disorder;
        return this.instance;
    }

    @Override
    public InputManager addStyles() {
        this.mAddStyles = true;
        return this.instance;
    }

    @Override
    public InputManager setListener(InputListener l) {
        this.mListener = l;
        return this.instance;
    }

    @SuppressLint("WrongConstant")
    private void handleInputData1(InputManager.Style style) {
        String in = this.input.getText().toString();
        InputInfo inputInfo = new InputInfo();
        inputInfo.setNextStyle(style);
        if (style == InputManager.Style.COMMONINPUT && this.mAddStyles) {
            if (this.mLang == InputManager.Lang.CH) {
                Toast.makeText(this.context, PAY_CH, 0).show();
            } else {
                Toast.makeText(this.context, PAY_EN, 0).show();
            }
        } else {
            inputInfo.setResultFlag(true);
            if (InputManager.Mode.AMOUNT == this.mInputMode) {
                in = in.replaceAll("(\\.)?", "");
            }

            if (InputManager.Mode.VOUCHER == this.mInputMode && in.length() < 6) {
                in = padleft(in, 6, '0');
            }

            if (InputManager.Mode.DATETIME == this.mInputMode && in != null && in.length() < 8) {
                if (in.length() == 6) {
                    in = padleft(in, 7, '0');
                }

                if (in != null) {
                    in = padleft(in, 8, '2');
                }

            }

            inputInfo.setResult(in);
            this.mListener.callback(inputInfo);
            if (this.useOnce) {
                this.container.removeAllViews();
            }
        }

    }

    public enum Mode {
        AMOUNT(1),
        PASSWORD(2),
        VOUCHER(3),
        AUTHCODE(4),
        DATETIME(5),
        REFERENCE(6);

        private int val;

        private Mode(int value) {
            this.val = value;
        }

        protected int getVal() {
            return this.val;
        }
    }

    public enum Lang {
        CH(1),
        EN(2);

        private int val;

        private Lang(int value) {
            this.val = value;
        }

        protected int getVal() {
            return this.val;
        }
    }

    public enum Style {
        ALIPAY(0),
        WETCHATPAY(1),
        UNIONPAY(2),
        COMMONINPUT(3);

        private int val;

        private Style(int value) {
            this.val = value;
        }

        protected int getVal() {
            return this.val;
        }
    }

    final class UserInputListener implements DesertboardListener {
        UserInputListener() {
        }

        public void onVibrate(int ms) {
            //Método no implementado
        }

        public void onChar() {
            //Método no implementado
        }

        public void onInputKey(int key) {
            switch (key) {
                case 0:
                    InputManager2.sendKeyCode(7);
                    break;
                case 1:
                    InputManager2.sendKeyCode(8);
                    break;
                case 2:
                    InputManager2.sendKeyCode(9);
                    break;
                case 3:
                    InputManager2.sendKeyCode(10);
                    break;
                case 4:
                    InputManager2.sendKeyCode(11);
                    break;
                case 5:
                    InputManager2.sendKeyCode(12);
                    break;
                case 6:
                    InputManager2.sendKeyCode(13);
                    break;
                case 7:
                    InputManager2.sendKeyCode(14);
                    break;
                case 8:
                    InputManager2.sendKeyCode(15);
                    break;
                case 9:
                    InputManager2.sendKeyCode(16);
                case 128:
                default:
                    break;
                case 129:
                    if (InputManager2.this.mInputMode == InputManager.Mode.AMOUNT ||
                            InputManager2.this.mInputMode == InputManager.Mode.PASSWORD ||
                            InputManager2.this.mInputMode == InputManager.Mode.VOUCHER ||
                            InputManager2.this.mInputMode == InputManager.Mode.REFERENCE) {
                        InputManager2.this.handleInputData1(InputManager.Style.UNIONPAY);
                    } else {
                        InputManager2.this.handleInputData1(InputManager.Style.COMMONINPUT);
                    }
                    break;
                case 130:
                    InputManager2.sendKeyCode(67);
            }

        }

        public void onClr() {
            //Método no implementado
        }

        public void onCannel() {
            //Método no implementado
        }

        public void onEnter(String encpin) {
            //Método no implementado
        }
    }
}
