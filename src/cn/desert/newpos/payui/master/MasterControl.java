package cn.desert.newpos.payui.master;

import static com.flota.actividades.MainActivity.modoCaja;
import static com.flota.adapters.UAFactory.getDescriptionOfCodeProduct;
import static com.flota.defines_bancard.DefinesBANCARD.INGRESO_BILLETERAS;
import static com.flota.defines_bancard.DefinesBANCARD.INGRESO_TELEFONO;
import static com.flota.menus.MenuAction.callBackSeatle;
import static com.flota.menus.menus.FALLBACK;
import static com.flota.menus.menus.contFallback;
import static com.flota.menus.menus.idAcquirer;
import static com.flota.screen.inputs.methods.FormatInput.DATE;
import static com.newpos.libpay.presenter.TransUIImpl.SCREEN_TAG;
import static com.newpos.libpay.trans.Trans.idLote;
import static com.newpos.libpay.trans.finace.FinanceTrans.LOCAL;
import static java.lang.Thread.sleep;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.IdRes;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.desert.keyboard.InputManager;
import com.flota.actividades.MainActivity;
import com.flota.adaptadores.ModeloMensajeConfirmacion;
import com.flota.adaptadores.ModeloMenusOpciones;
import com.flota.adapters.UAFactory;
import com.flota.adapters.UAListener;
import com.flota.adapters.UniversalAdapter;
import com.flota.adapters.model.ButtonModel;
import com.flota.adapters.model.ProductModel;
import com.flota.defines_bancard.DefinesBANCARD;
import com.flota.inicializacion.configuracioncomercio.CARDS;
import com.flota.inicializacion.init_emv.CapkRow;
import com.flota.inicializacion.init_emv.EmvAppRow;
import com.flota.logscierres.LogsCierresModelo;
import com.flota.menus.AdaptadorMenus;
import com.flota.menus.menuItemsModelo;
import com.flota.screen.inputs.ScreenCard;
import com.flota.screen.inputs.ScreenEnterNumericalData;
import com.flota.screen.inputs.ScreenSelectFromTwoOptions;
import com.flota.screen.inputs.ScreenSelectProduct;
import com.flota.screen.inputs.methods.FormatInput;
import com.flota.screen.inputs.methods.NumericalData;
import com.flota.screen.result.ScreenAccountBalance;
import com.flota.screen.utils.ScreenToolbar;
import com.flota.setting.ListTransacciones;
import com.flota.timertransacciones.TimerTrans;
import com.flota.tools.MenuApplicationsList;
import com.flota.tools.PaperStatus;
import com.flota.tools.WaitSelectApplicationsList;
import com.flota.transactions.Billeteras.AdaptadorBilleteras;
import com.flota.transactions.Billeteras.Billeteras;
import com.flota.transactions.DataAdicional.DataAdicional;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.gson.Gson;
import com.newpos.libpay.Logger;
import com.newpos.libpay.PaySdk;
import com.newpos.libpay.PaySdkException;
import com.newpos.libpay.device.card.CardManager;
import com.newpos.libpay.device.printer.PrintRes;
import com.newpos.libpay.device.user.OnUserResultListener;
import com.newpos.libpay.presenter.TransView;
import com.newpos.libpay.trans.Trans;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;
import com.pos.device.icc.IccReader;
import com.pos.device.icc.SlotType;
import com.pos.device.printer.Printer;
import com.wposs.flota.R;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import cn.desert.newpos.payui.UIUtils;
import cn.desert.newpos.payui.base.PayApplication;

/**
 * Created by zhouqiang on 2017/7/3.
 */

public class MasterControl extends FormularioActivity implements TransView, View.OnClickListener {

    public static final String TAG = "MasterControl";

    public static final String TRANS_KEY = "TRANS_KEY";
    public static final String TIPO_VENTA = "TIPO_VENTA";
    public static final String CAJAS = "CAJAS";

    // Multiple Response
    public static final String KEY_CODE_PRODUCT = "code";
    public static final String KEY_QUANTITY_PRODUCT = "quantity";
    public static final String KEY_DESCRIPTION_PRODUCT = "description";

    // Two Options Response
    public static final String SELECT_OPTION_1 = "select option 1";
    public static final String SELECT_OPTION_2 = "select option 2";

    // Animations
    public static final boolean ANIM_ACTIVE = true;

    public static boolean ctlSign;
    public static String holderName;
    public static Context mcontext;
    public static String field22;
    // Keyboard
    @IdRes
    private final int[] keysKeyboard = {
            R.id.number_1_keyboard, R.id.number_2_keyboard, R.id.number_3_keyboard,
            R.id.number_4_keyboard, R.id.number_5_keyboard, R.id.number_6_keyboard,
            R.id.number_7_keyboard, R.id.number_8_keyboard, R.id.number_9_keyboard,
            R.id.number_0_keyboard, R.id.number_delete_keyboard, R.id.clear_keyboard
    };
    // Multiple Response
    private final Map<String, String> multiResp = new HashMap<>();
    android.widget.Button btnConfirm;
    android.widget.Button btnCancel;
    EditText editCardNO;
    EditText transInfo;
    //Toolbar
    ImageView close;
    ImageView menu;
    TextView etTitle;
    //Type Account
    RadioButton rbMon1;
    RadioButton rbMon2;
    android.widget.Button btnCancelTypeCoin;
    android.widget.Button btnAcceptTypeCoin;
    String typeCoin = "1";
    //Input user
    TextView btnCancelInputUser;
    TextView btnAcceptInputUser;
    EditText etInputUser;
    TextView tvInputUser;
    int minEtInputUser;
    int maxEtInputUser;
    //Show message info
    android.widget.Button btnCancelMsg;
    android.widget.Button btnConfirmMsg;
    EditText etMsgInfo;
    OnUserResultListener listener;
    String className = "MasterControl.java";
    String inputContent = "";
    //Firma
    boolean isSignature;
    boolean isOnSignature;
    //prompt
    EditText inputData;
    android.widget.Button btnCancelPrompt;
    android.widget.Button btnAcceptPrompt;
    //Tarjeta manual
    FloatingActionButton btnTarjetaManual;
    ProgressBar progressBar;
    StringBuilder builder;
    EditText editText;
    int longitudMaxima = 0;
    String tipoIngreso;
    List<menuItemsModelo> itemMenu;
    // Ingreso numero
    IccReader iccReader0;
    boolean isCajas = false;
    private int quantityValue = 0;
    private SignaturePad mSignaturePad;
    private android.widget.Button mClearButton;
    private android.widget.Button mSaveButton;
    private Gson gson;
    // Keyboard
    private EditText inputTextKeyboard;
    private FormatInput formatInput;

    public static String ch2en(String ch) {
        String[] chs = PrintRes.TRANSCH;
        int index = 0;
        for (int i = 0; i < chs.length; i++) {
            if (chs[i].equals(ch)) {
                index = i;
            }
        }
        return PrintRes.TRANSEN[index];
    }

    public static String en2ch(String en) {
        String[] chs = PrintRes.TRANSEN;
        int index = 0;
        for (int i = 0; i < chs.length; i++) {
            if (chs[i].equals(en)) {
                index = i;
            }
        }
        return PrintRes.TRANSCH[index];
    }

    /**
     * Check card exist in table.
     *
     * @param cardNum Numero Tarjeta
     * @return return
     */
    public static boolean incardTable(String cardNum, String tipoTrans) {

        if (cardNum == null)
            return false;

        if (cardNum.length() < 10)
            return false;

        if (!CARDS.inCardTable(cardNum, mcontext)) {
            Logger.info(TAG, "No se encontraron parametros");
            return false;
        }

        return true;
    }

    public static void setMcontext(Context mcontext) {
        MasterControl.mcontext = mcontext;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gson = new Gson();

        PayApplication.getInstance().addActivity(this);

        ctlSign = false;
        holderName = "";
        String type = getIntent().getStringExtra(TRANS_KEY);
        String auxMonto = getIntent().getStringExtra(TIPO_VENTA);
        isCajas = getIntent().getBooleanExtra(CAJAS, false);

        if (modoCaja && isCajas) {
            encenderPantalla(MasterControl.this);
        }

        if (contFallback != FALLBACK) {


            switch (type) {
                case Trans.Type.SETTLE:
                case Trans.Type.ECHO_TEST:
                case Trans.Type.AUTO_SETTLE:
                case Trans.Type.INYECCION:
                    break;

                default:
                    //--------PENDIENTE REVISAR INIT EMV----------------
                    EmvAppRow emvappRow = null;
                    emvappRow = EmvAppRow.getSingletonInstance();
                    emvappRow.selectEmvAppRow(MasterControl.this);

                    CapkRow capkRow = null;
                    capkRow = CapkRow.getSingletonInstance();
                    capkRow.selectCapkRow(MasterControl.this);

                    break;
            }

            idAcquirer = idLote;

            if (type.equals(Trans.Type.AUTO_SETTLE)) {
                startTrans(type, null, MasterControl.this, isCajas);
            } else {
                startTrans(type, auxMonto, MasterControl.this, isCajas);
            }
        } else {
            if (idAcquirer != null)
                startTrans(type, auxMonto, MasterControl.this, isCajas);
        }
    }

    // Screen > Card

    @SuppressWarnings("deprecation")
    @Override
    public void screenCardProcessing(final ScreenCard screen, OnUserResultListener l) {
        Log.i(SCREEN_TAG, "screenCardProcessing: " + screen);
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initScreen(screen.getLayout(), new ScreenToolbar().setButtonLeft(R.drawable.ic_close, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        listener.cancel();
                    }
                }));

                if (screen.getTitle() != null)
                    ((TextView) findViewById(R.id.title_card_processing)).setText(screen.getTitle().toUpperCase(Locale.ROOT));

                if (screen.getMessage() != null)
                    ((TextView) findViewById(R.id.message_card_processing)).setText(Html.fromHtml(screen.getMessage()));

                ImageView image = findViewById(R.id.image_card_processing);
                int mode = screen.getMode();
                if ((mode & CardManager.INMODE_MAG) != 0) {
                    if ((mode & CardManager.INMODE_IC) != 0) {
                        if ((mode & CardManager.INMODE_NFC) == 0) {
                            image.setImageDrawable(getDrawable(R.drawable.ic_pos_mag_ic));
                        }
                    } else {
                        if (!ANIM_ACTIVE) {
                            image.setImageDrawable(getDrawable(R.drawable.ic_pos_mag));
                        } else {
                            image.setImageDrawable(getDrawable(R.drawable.ic_pos_mag_anim_1));

                            ImageView imageAnim = findViewById(R.id.image_anim_card_processing);
                            imageAnim.setImageDrawable(getDrawable(R.drawable.ic_pos_mag_anim_2));
                            imageAnim.setVisibility(View.VISIBLE);
                            imageAnim.setAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.card_mag));
                        }
                    }
                } else if ((mode & CardManager.INMODE_IC) != 0) {
                    image.setImageDrawable(getDrawable(R.drawable.ic_pos_ic));
                }

                deleteTimer();
            }
        });
    }

    // Screen > Input

    @SuppressWarnings("deprecation")
    @Override
    public void screenEnterNumericalData(final ScreenEnterNumericalData screen, OnUserResultListener l) {
        Log.i(SCREEN_TAG, "screenEnterNumericalData: " + screen);
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initScreen(screen.getLayout(), screen.getToolbar() != null ? screen.getToolbar()
                                : new ScreenToolbar().setButtonLeft(R.drawable.ic_close, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteTimer();
                                listener.cancel();
                            }
                        })
                );

                if (screen.getTitle() != null)
                    ((TextView) findViewById(R.id.title_view)).setText(screen.getTitle().toUpperCase(Locale.ROOT));

                if (screen.getMessage() != null)
                    ((TextView) findViewById(R.id.msg_view)).setText(Html.fromHtml(screen.getMessage()));

                if (screen.getInput() instanceof NumericalData)
                    initInputNumericalData(screen.getInput());

                if (screen.getImage() != null) {
                    ImageView image = findViewById(R.id.image_SEND);
                    image.setVisibility(View.VISIBLE);
                    image.setImageDrawable(screen.getImage());
                }

                counterDownTimer(screen.getTimeout(), getString(R.string.waiting_time_over),
                        true, "showEnterNumericalData");
            }
        });
    }

    @Override
    public void screenSelectProduct(final ScreenSelectProduct screen, final OnUserResultListener l) {
        Log.i(SCREEN_TAG, "screenEnterNumericalData: " + screen + " l:" + l);
        this.listener = l;
        runOnUiThread(new Runnable() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public void run() {
                initScreen(screen.getLayout(), new ScreenToolbar().setButtonLeft(R.drawable.ic_close, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteTimer();
                        listener.cancel();
                    }
                }));

                final Button code = findViewById(R.id.code_product_select_product);
                if (multiResp.get(KEY_CODE_PRODUCT) != null)
                    code.setText(multiResp.get(KEY_CODE_PRODUCT));
                final TextView quantity = findViewById(R.id.quantity_select_product);

                genRcy(R.id.recycler_screen_select_product, UAFactory.adapterProducts(screen.getProducts(),
                        multiResp.get(KEY_CODE_PRODUCT),
                        new UAListener.ProductListener() {
                            @Override
                            public void onClick(View view, ProductModel product) {
                                multiResp.put(KEY_CODE_PRODUCT, product.getCode());
                                multiResp.put(KEY_DESCRIPTION_PRODUCT, product.getDescription());
                                code.setText(product.getCode());
                            }
                        }));
                if (multiResp.get(KEY_CODE_PRODUCT) != null) {
                    multiResp.put(KEY_DESCRIPTION_PRODUCT,
                            getDescriptionOfCodeProduct(screen.getProducts(), multiResp.get(KEY_CODE_PRODUCT)));
                }

                quantity.setText(quantityValue <= 0 ? "-" : FormatInput.formatNumber(String.valueOf(quantityValue)));

                findViewById(R.id.manual_code_product_select_product).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        screenCodeProduct(screen, l);
                    }
                });
                findViewById(R.id.manual_quantity_select_product).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        screenQuantityProduct(screen, l);
                    }
                });
                findViewById(R.id.continue_select_product).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (multiResp.get(KEY_CODE_PRODUCT) == null) {
                            toast("Seleccione un producto o ingrese el código.");
                            return;
                        }
                        if (quantityValue <= 0) {
                            toast("Ingrese una cantidad.");
                            return;
                        }
                        multiResp.put(KEY_QUANTITY_PRODUCT, String.valueOf(quantityValue));
                        deleteTimer();
                        inputContent = gson.toJson(multiResp);
                        quantityValue = 0;
                        multiResp.clear();
                        listener.confirm(InputManager.Style.COMMONINPUT);
                    }
                });

                counterDownTimer(screen.getTimeout(), getString(R.string.waiting_time_over),
                        true, "screenSelectProduct");
            }
        });
    }

    @Override
    public void screenSelectFromTwoOptions(final ScreenSelectFromTwoOptions screen, final OnUserResultListener l) {
        listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initScreen(screen.getLayout(), screen.getToolbar() != null ? screen.getToolbar()
                                : new ScreenToolbar().setButtonLeft(R.drawable.ic_close, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                deleteTimer();
                                l.cancel();
                            }
                        })
                );

                if (screen.getTitle() != null)
                    ((TextView) findViewById(R.id.title_SFTO)).setText(screen.getTitle().toUpperCase(Locale.ROOT));

                if (screen.getQuestion() != null)
                    ((TextView) findViewById(R.id.question_SFTO)).setText(screen.getQuestion());

                if (screen.getData() != null)
                    ((TextView) findViewById(R.id.data_SFWO_s2)).setText(screen.getData());

                Button option1 = findViewById(R.id.option_1_SFTO);
                if (screen.getTextOption1() != null)
                    option1.setText(screen.getTextOption1());
                option1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteTimer();
                        inputContent = SELECT_OPTION_1;
                        l.confirm(InputManager.Style.COMMONINPUT);
                    }
                });

                Button option2 = findViewById(R.id.option_2_SFTO);
                if (screen.getTextOption2() != null)
                    option2.setText(screen.getTextOption2());
                option2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteTimer();
                        inputContent = SELECT_OPTION_2;
                        l.confirm(InputManager.Style.COMMONINPUT);
                    }
                });

                counterDownTimer(screen.getTimeout(), getString(R.string.waiting_time_over),
                        true, "screenSelectFromTwoOptions");
            }
        });
    }

    // Screen > Result

    @Override
    public void screenAccountBalance(final ScreenAccountBalance screen) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                initScreen(screen.getLayout(), new ScreenToolbar().setButtonLeft(R.drawable.ic_close, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        removeCard();
                    }
                }));

                String s = "Gs. " + FormatInput.formatNumber(String.valueOf(screen.getAccountBalance()));
                ((TextView) findViewById(R.id.account_balance_result)).setText(s);

                counterDownTimer(screen.getTimeout(), getString(R.string.waiting_time_over),
                        true, "screenAccountBalance");
            }
        });
    }

    // Common functionalities

    private void screenCodeProduct(final ScreenSelectProduct screen, final OnUserResultListener l) {
        screenEnterNumericalData(
                new ScreenEnterNumericalData(
                        screen.getTimeout(),
                        new ScreenToolbar().setButtonLeft(R.drawable.ic__back, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                screenSelectProduct(screen, l);
                            }
                        }))
                        .setTitle("Ingresar el código")
                        .setMessage("Ingresar el <b>código</b> y presiona <b>OK</b> para continuar")
                        .setInput(new NumericalData(FormatInput.CODE, 7)
                                .setTextAlignment(NumericalData.TextAlignment.CENTER)
                                .setHint("código de producto")
                        ),

                new OnUserResultListener() {
                    @Override
                    public void confirm(InputManager.Style type) {
                        Log.d(SCREEN_TAG, "listener: confirm screenCodeProduct");
                        if (builder.length() > 0) try {
                            multiResp.put(KEY_CODE_PRODUCT, builder.toString());
                            screenSelectProduct(screen, l);
                            return;
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        toast("ingrese el valor correctamente");
                    }

                    @Override
                    public void cancel() {
                        Log.d(SCREEN_TAG, "listener: cancel go select product");
                        screenSelectProduct(screen, l); // NO call
                    }

                    @Override
                    public void confirm(int applistselect) {
                        Log.d(SCREEN_TAG, "listener: confirm 2");
                        // NO Call
                    }
                });
    }

    private void screenQuantityProduct(final ScreenSelectProduct screen, final OnUserResultListener l) {
        screenEnterNumericalData(
                new ScreenEnterNumericalData(
                        screen.getTimeout(),
                        new ScreenToolbar().setButtonLeft(R.drawable.ic__back, new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                screenSelectProduct(screen, l);
                            }
                        }))
                        .setTitle("Ingresar la cantidad")
                        .setMessage("Ingresar la <b>cantidad</b> y presiona <b>OK</b> para continuar")
                        .setInput(new NumericalData(FormatInput.NUMBER, 0, 5)
                                .setTextAlignment(NumericalData.TextAlignment.CENTER)
                                .setHint("cantidad")
                        ),

                new OnUserResultListener() {
                    @Override
                    public void confirm(InputManager.Style type) {
                        Log.d(SCREEN_TAG, "listener: confirm");
                        if (builder.length() > 0) try {
                            quantityValue = Integer.parseInt(builder.toString());
                            screenSelectProduct(screen, l);
                            return;
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                        toast("ingrese el valor correctamente");
                    }

                    @Override
                    public void cancel() {
                        Log.d(SCREEN_TAG, "listener: cancel");
                        screenSelectProduct(screen, l); // NO call
                    }

                    @Override
                    public void confirm(int applistselect) {
                        Log.d(SCREEN_TAG, "listener: confirm 2");
                        // NO Call
                    }
                });
    }

    private void initInputNumericalData(final NumericalData numerical) {
        builder = new StringBuilder(numerical.getText());
        formatInput = numerical.getFormatInput();
        longitudMaxima = numerical.getMaxLength();

        inputTextKeyboard = findViewById(R.id.input_text_view);
        inputTextKeyboard.setText(numerical.getText());
        inputTextKeyboard.setHint(numerical.getHint());
        inputTextKeyboard.setGravity(numerical.getTextAlignment());

        if (numerical.getSuffix() != null && !numerical.getSuffix().trim().isEmpty()) {
            TextView suffix = findViewById(R.id.suffix_view);
            suffix.setText(numerical.getSuffix());
            suffix.setVisibility(View.VISIBLE);
        }

        if (numerical.getIcon() != null) {
            ImageView icon = findViewById(R.id.icon_view);
            icon.setImageDrawable(numerical.getIcon());
            icon.setVisibility(View.VISIBLE);
        }

        // Keyboard
        initKeyboard(numerical.isKeyboardWith000());
        findViewById(R.id.ok_keyboard).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (builder.length() >= numerical.getMinLength()) {
                    if (formatInput == DATE && !FormatInput.isValidDate(builder.toString())) {
                        toast("El formato de la fecha es incorrecto");
                        return;
                    }

                    deleteTimer();
                    inputContent = builder.toString();
                    listener.confirm(InputManager.Style.COMMONINPUT);
                } else {
                    toast(numerical.getMsgErrorValidation().trim().isEmpty() ?
                            "Ingrese el campo" : numerical.getMsgErrorValidation());
                }
            }
        });
    }

    // Keyboard

    private void initKeyboard(boolean isKeyboardWith000) {
        ((LinearLayout) findViewById(R.id.box_keyboard_view)).addView(getLayoutInflater().inflate(
                isKeyboardWith000 ? R.layout.view_keyboard : R.layout.view_keyboard_2,
                (ViewGroup) findViewById(R.id.keyboard_linear)
        ));

        View.OnClickListener keyboardListener = new View.OnClickListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.number_delete_keyboard:
                        deleteLastCharacterKeyboard();
                        break;
                    case R.id.clear_keyboard:
                        builder = new StringBuilder();
                        updateTextKeyboard();
                        break;
                    default:
                        keyboardPress(((Button) v).getText().toString());
                        break;
                }
            }
        };

        for (@IdRes int id : keysKeyboard) {
            findViewById(id).setOnClickListener(keyboardListener);
        }
        if (isKeyboardWith000)
            findViewById(R.id.number_000_keyboard).setOnClickListener(keyboardListener);
    }

    private void deleteLastCharacterKeyboard() {
        if (builder != null) {
            int len = builder.length();
            if (len != 0) {
                builder.deleteCharAt(len - 1);
                updateTextKeyboard();
            }
        }
    }

    private void keyboardPress(String val) {
        if (builder.length() < longitudMaxima) {
            if (val.contains("0") && builder.toString().isEmpty()
                    && (formatInput.equals(FormatInput.NUMBER)
                    || formatInput.equals(FormatInput.CARD))) return;

            if (val.equals("000") && builder.length() + 3 > longitudMaxima) {
                val = val.substring(0, longitudMaxima - builder.length());
            }

            builder.append(val);
            updateTextKeyboard();
        }
    }

    private void updateTextKeyboard() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                inputTextKeyboard.setText(formatInput.applyFormat(builder.toString()));
            }
        });
    }

    @Override
    public void onClick(View view) {
        if (tipoIngreso != null) {
            switch (tipoIngreso) {
                case DefinesBANCARD.INGRESO_MONTO:
                case INGRESO_TELEFONO:
                case INGRESO_BILLETERAS:
                case DefinesBANCARD.INGRESO_VUELTO:
                case DefinesBANCARD.INGRESO_PIN:
                case DefinesBANCARD.INGRESO_CUOTAS:
                case DefinesBANCARD.INGRESO_CODIGO:
                    switch (view.getId()) {
                        case R.id.number0:
                            if (tipoIngreso.equals(DefinesBANCARD.INGRESO_MONTO)
                                    || tipoIngreso.equals(DefinesBANCARD.INGRESO_VUELTO)
                                    || tipoIngreso.equals(DefinesBANCARD.INGRESO_CUOTAS)) {
                                if (builder.length() != 0) {
                                    builder.append("0");
                                }
                            } else {
                                if (!builder.toString().equals("0") || !builder.toString().equals("000")) {
                                    builder.append("0");
                                }
                            }
                            mostrarEnTextView();
                            break;
                        case R.id.number1:
                            builder.append("1");
                            mostrarEnTextView();
                            break;
                        case R.id.number2:
                            builder.append("2");
                            mostrarEnTextView();
                            break;
                        case R.id.number3:
                            builder.append("3");
                            mostrarEnTextView();
                            break;
                        case R.id.number4:
                            builder.append("4");
                            mostrarEnTextView();
                            break;
                        case R.id.number5:
                            builder.append("5");
                            mostrarEnTextView();
                            break;
                        case R.id.number6:
                            builder.append("6");
                            mostrarEnTextView();
                            break;
                        case R.id.number7:
                            builder.append("7");
                            mostrarEnTextView();
                            break;
                        case R.id.number8:
                            builder.append("8");
                            mostrarEnTextView();
                            break;
                        case R.id.number9:
                            builder.append("9");
                            mostrarEnTextView();
                            break;
                        case R.id.number000:
                            if (builder.length() != 0) {
                                builder.append("000");
                            }
                            mostrarEnTextView();
                            break;
                        case R.id.btnClear:
                        case R.id.numberAC:
                            builder.setLength(0);
                            mostrarEnTextView();
                            break;
                        case R.id.numberDelete:
                            eliminarUltimoCaracter();
                            break;
                        default:
                            throw new IllegalStateException(getString(R.string.unexpected_value) + view.getId());
                    }
                    break;
                default:
                    throw new IllegalStateException(getString(R.string.unexpected_value) + tipoIngreso);
            }
        }

        if (view.equals(close)) {
            listener.cancel();
        }

        if (view.equals(btnCancel)) {
            listener.cancel();
        }
        if (view.equals(btnConfirm)) {
            listener.confirm(InputManager.Style.COMMONINPUT);
        }

        //Type Account
        if (view.equals(btnCancelTypeCoin)) {
            listener.cancel();
        }
        if (view.equals(btnAcceptTypeCoin)) {
            inputContent = typeCoin;
            listener.confirm(InputManager.Style.COMMONINPUT);
        }
        if (view.equals(rbMon1)) {
            rbMon1.setChecked(true);
            rbMon2.setChecked(false);
            typeCoin = "1";
        }
        if (view.equals(rbMon2)) {
            rbMon1.setChecked(false);
            rbMon2.setChecked(true);
            typeCoin = "2";
        }

        //ingreso de datos
        if (view.equals(btnAcceptInputUser)) {

            if (etInputUser.getText().toString().equals(""))
                UIUtils.toast(MasterControl.this, R.drawable.ic_redinfonet, getString(R.string.ingrese_dato), Toast.LENGTH_SHORT);
            else {
                if (etInputUser.length() < minEtInputUser) {
                    UIUtils.toast(MasterControl.this, R.drawable.ic_redinfonet, getString(R.string.longitud_invalida), Toast.LENGTH_SHORT);
                } else {
                    hideKeyBoard(etInputUser.getWindowToken());
                    inputContent = etInputUser.getText().toString();
                    listener.confirm(InputManager.Style.COMMONINPUT);
                }
            }
        }
        if (view.equals(btnCancelInputUser)) {
            listener.cancel();
        }

        //Show Message
        if (view.equals(btnConfirmMsg)) {
            listener.confirm(InputManager.Style.COMMONINPUT);
        }
        if (view.equals(btnCancelMsg)) {
            listener.cancel();
        }

        //prompt
        if (view.equals(btnAcceptPrompt) && inputData.length() == 0) {
            UIUtils.toast(MasterControl.this, R.drawable.ic_redinfonet,
                    getString(R.string.ingrese_dato), Toast.LENGTH_SHORT);
        }
        if (view.equals(btnCancelPrompt)) {
            listener.cancel();
        }

        //Tarjeta Manual
        if (view.equals(btnTarjetaManual)) {
            Snackbar.make(view, "Tarjeta Manual", Snackbar.LENGTH_LONG).show();
            inputContent = "MANUAL";
            listener.confirm(InputManager.Style.COMMONINPUT);
        }
    }

    @Override
    public void showCardView(final String msg, final int timeout, final int mode, final String title,
                             final long amount, final boolean opciones, OnUserResultListener l) {
        this.listener = l;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.vista_insertar_tarjeta);
                TextView tvSerial = findViewById(R.id.tvSerial);
                TextView tvVersion = findViewById(R.id.tvVersion);
                showVersionSerial(tvVersion, tvSerial);
                boolean validaTexto = false;
                String mensaje = "";
                String recargaSaldo = DataAdicional.getField(82);
                String ventaCuotaPlan = DataAdicional.getField(14);
                String ventaCuotaCuotas = DataAdicional.getField(45);
                if (recargaSaldo != null) {
                    mensaje = "Venta de saldo para: \n" +
                            PAYUtils.FormarPhonePyg(ISOUtil.hex2AsciiStr(recargaSaldo), tipoIngreso);
                    validaTexto = true;
                }

                if (ventaCuotaPlan != null && ventaCuotaCuotas != null) {
                    mensaje = "Venta Cuotas\nPlan " + Integer.parseInt(ISOUtil.hex2AsciiStr(ventaCuotaPlan)) +
                            " - " + Integer.parseInt(ISOUtil.hex2AsciiStr(DataAdicional.getField(45))) + " Cuotas";
                    validaTexto = true;
                }

                if (validaTexto) {
                    TextView tvConfirmarNumero = findViewById(R.id.tvConfirmarNumero);
                    tvConfirmarNumero.setVisibility(View.VISIBLE);
                    tvConfirmarNumero.setText(mensaje);
                }

                TextView tvMensaje = findViewById(R.id.tvMensaje);
                if (msg != null && !msg.equals("")) {
                    tvMensaje.setText(msg);
                }

                TextView tv1 = findViewById(R.id.tv1);
                String monto = String.valueOf(amount);
                int len = monto.length();
                if (len > 2) {
                    monto = monto.substring(0, len - 2);
                }
                if (amount == 0) {
                    tv1.setVisibility(View.GONE);
                }
                tv1.setText(formatearValor(monto));

                close = findViewById(R.id.iv_close);
                close.setVisibility(View.VISIBLE);

                ImageView imgPos = findViewById(R.id.imgPos);

                int inmodeMag = 0x02;
                int inmodeIc = 0x08;
                int inmodeNfc = 0x10;

                if ((mode & inmodeMag) != 0) {
                    if ((mode & inmodeIc) != 0) {
                        if ((mode & inmodeNfc) == 0) {
                            imgPos.setImageDrawable(getDrawable(R.drawable.ic_pos_mag_ic));
                        }
                    } else {
                        imgPos.setImageDrawable(getDrawable(R.drawable.ic_pos_mag));
                    }
                } else if ((mode & inmodeIc) != 0) {
                    imgPos.setImageDrawable(getDrawable(R.drawable.ic_pos_ic));
                }

                LinearLayout linearOpciones = findViewById(R.id.linearOpciones);
                LinearLayout linearOpt = findViewById(R.id.linearOpt);
                if (isCajas) {
                    linearOpt.setPaddingRelative(0, 0, 0, 80);
                } else if (opciones) {
                    linearOpt.setPaddingRelative(0, 0, 0, 0);
                }
                linearOpciones.setVisibility(View.GONE);
                if (opciones) {
                    linearOpciones.setVisibility(View.VISIBLE);

                    ArrayList<ModeloMenusOpciones> menuList = new ArrayList<>(
                            ListTransacciones.obtenerOpcionesMenu(MasterControl.this));
                    if (isCajas) linearOpciones.setVisibility(View.GONE);

                    AdaptadorMenus adaptadorMenus = new AdaptadorMenus(MasterControl.this, menuList);
                    RecyclerView rvMenus = findViewById(R.id.rvMenus);
                    inicializarRecyclerViewGrid(MasterControl.this, rvMenus, 3);
                    rvMenus.setAdapter(adaptadorMenus);

                    adaptadorMenus.setCallback(new AdaptadorMenus.MenusCallback() {
                        @Override
                        public void onMenuClick(ModeloMenusOpciones option) {
                            inputContent = option.getInputContent();
                            listener.confirm(InputManager.Style.COMMONINPUT);
                        }
                    });
                }
                close.setOnClickListener(MasterControl.this);
                deleteTimer();
            }
        });
    }

    @Override
    public void showCardNo(final int timeout, final String pan, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.trans_show_cardno);
                showConfirmCardNO(pan);

                deleteTimer();
            }
        });
    }

    @Override
    public void showMessageInfo(final String title, final String msg, final String btnCancel, final String btnConfirm, final int timeout, OnUserResultListener l) {
        this.listener = l;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.trans_show_cardno);

                close = findViewById(R.id.iv_close);
                etTitle = findViewById(R.id.textView_titleToolbar);
                etMsgInfo = findViewById(R.id.cardno_display_area);
                btnCancelMsg = findViewById(R.id.cardno_cancel);
                btnConfirmMsg = findViewById(R.id.cardno_confirm);

                close.setVisibility(View.VISIBLE);
                etTitle.setText(title);
                etMsgInfo.setText(msg);
                btnCancelMsg.setText(btnCancel);
                btnConfirmMsg.setText(btnConfirm);

                close.setOnClickListener(MasterControl.this);
                btnCancelMsg.setOnClickListener(MasterControl.this);
                btnConfirmMsg.setOnClickListener(MasterControl.this);

                counterDownTimer(timeout, getString(R.string.timeout), true, "showMessageInfo");
            }
        });
    }

    @Override
    public void showMessageImpresion(final String title, final String msg, final String btnCancel, final String btnConfirm, final int timeout, OnUserResultListener l) {
        this.listener = l;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.trans_show_cardno);

                close = findViewById(R.id.iv_close);
                etTitle = findViewById(R.id.textView_titleToolbar);
                etMsgInfo = findViewById(R.id.cardno_display_area);
                btnCancelMsg = findViewById(R.id.cardno_cancel);
                btnConfirmMsg = findViewById(R.id.cardno_confirm);

                close.setVisibility(View.VISIBLE);
                etTitle.setText(title);
                etMsgInfo.setText(msg);
                btnCancelMsg.setText(btnCancel);
                btnConfirmMsg.setText(btnConfirm);

                close.setOnClickListener(MasterControl.this);
                btnCancelMsg.setOnClickListener(MasterControl.this);
                btnConfirmMsg.setOnClickListener(MasterControl.this);

                counterDownTimer(timeout, "", false, "showMessageImpresion");
            }
        });
    }

    @Override
    public String getInput(InputManager.Mode type) {
        return inputContent;
    }

    @Override
    public void showTransInfoView(final int timeout, final TransLogData data, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.trans_show_transinfo);
                TextView tv_title = findViewById(R.id.title_find_result);
                showOrignalTransInfo(data);
                counterDownTimer(timeout, "Tiempo de espera de confirmacion de datos agotado", true, "showTransInfoView");
            }
        });
    }

    @Override
    public void showCardAppListView(int timeout, final String[] apps, OnUserResultListener l) {
        this.listener = l;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {

                MenuApplicationsList applicationsList = new MenuApplicationsList(MasterControl.this);
                applicationsList.menuApplicationsList(apps, new WaitSelectApplicationsList() {
                    @Override
                    public void getAppListSelect(int idApp) {
                        listener.confirm(idApp);
                    }
                });
            }
        });
    }

    @Override
    public void showMultiLangView(int timeout, String[] langs, OnUserResultListener l) {
        this.listener = l;
    }

    @Override
    public void showSuccess(int timeout, final String info) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UIUtils.startResult(MasterControl.this, true, info);
                deleteTimer();
            }
        });
    }

    @Override
    public void showError(int timeout, final String err) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                UIUtils.startResult(MasterControl.this, false, err);
                deleteTimer();
            }
        });
    }

    @Override
    public void showMsgInfo(final int timeout, final String status, final boolean transaccion, final boolean withClose) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.trans_handling);
                TextView tvSerial = findViewById(R.id.tvSerial);
                TextView tvVersion = findViewById(R.id.tvVersion);
                showVersionSerial(tvVersion, tvSerial);
                close = findViewById(R.id.iv_close);
                if (withClose) {
                    close.setVisibility(View.VISIBLE);
                }
                close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showError(timeout, "");

                    }
                });
                progressBar = findViewById(R.id.handling_loading);
                if (transaccion) {
                    progressBar.setVisibility(View.INVISIBLE);
                }
                showHanding(status);

                deleteTimer();
            }
        });
    }

    @Override
    public void showMsgInfo(final int timeout, final String status, final String title, final boolean transaccion) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.trans_handling);
                TextView tvSerial = findViewById(R.id.tvSerial);
                TextView tvVersion = findViewById(R.id.tvVersion);
                showVersionSerial(tvVersion, tvSerial);
                close = findViewById(R.id.iv_close);
                close.setOnClickListener(MasterControl.this);
                etTitle = findViewById(R.id.tvDataInfo);
                progressBar = findViewById(R.id.handling_loading);

                if (transaccion) {
                    progressBar.setVisibility(View.INVISIBLE);
                }

                try {
                    etTitle.setVisibility(View.VISIBLE);
                    etTitle.setText(title.replace("_", " "));
                } catch (Exception e) {
                    Logger.exception(className, e);
                    etTitle.setText(title);
                }

                showHanding(status);

                deleteTimer();
            }
        });
    }

    @Override
    public void showTypeCoinView(final int timeout, final String title, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_menu_tipo_moneda);
                rbMon1 = findViewById(R.id.rb_moneda1);
                rbMon2 = findViewById(R.id.rb_moneda2);
                btnCancelTypeCoin = findViewById(R.id.btn_cancel_mon);
                btnAcceptTypeCoin = findViewById(R.id.btn_conf_mon);

                rbMon1.setOnClickListener(MasterControl.this);
                rbMon2.setOnClickListener(MasterControl.this);
                btnCancelTypeCoin.setOnClickListener(MasterControl.this);
                btnAcceptTypeCoin.setOnClickListener(MasterControl.this);

                rbMon1.setChecked(true);
                setToolbar(title);
            }
        });
    }

    @Override
    public void showInputUser(final int timeout, final String title, final String label, final int min, final int max, OnUserResultListener l) {
        this.listener = l;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_input_user);

                close = findViewById(R.id.iv_close);
                etTitle = findViewById(R.id.textView_titleToolbar);
                close.setVisibility(View.VISIBLE);
                try {
                    etTitle.setText(title.replace("_", " "));
                } catch (Exception e) {
                    Logger.exception(className, e);
                    etTitle.setText("");
                }

                minEtInputUser = min;
                maxEtInputUser = max;

                etInputUser = findViewById(R.id.editText_input);
                etInputUser.setFilters(new InputFilter[]{new InputFilter.LengthFilter(max)});
                tvInputUser = findViewById(R.id.textView_title);
                btnCancelInputUser = findViewById(R.id.last4_cancel);
                btnAcceptInputUser = findViewById(R.id.last4_confirm);

                close.setOnClickListener(MasterControl.this);
                btnAcceptInputUser.setOnClickListener(MasterControl.this);
                btnCancelInputUser.setOnClickListener(MasterControl.this);

                tvInputUser.setText(label);

                counterDownTimer(timeout, getString(R.string.waiting_time_over), true, "showInputUser");
            }
        });
    }

    @Override
    public void toasTransView(final String errcode, final boolean sound) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (sound) {
                    ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
                    toneG.startTone(ToneGenerator.TONE_PROP_BEEP2, 2000);
                    toneG.stopTone();
                }
                UIUtils.toast(MasterControl.this, R.drawable.ic_redinfonet, errcode, Toast.LENGTH_SHORT);
            }
        });

    }

    @Override
    public void showConfirmAmountView(final int timeout, final String title, final String label, final String amnt, final boolean isHTML, OnUserResultListener l) {
        this.listener = l;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_confirm_amount);

                close = findViewById(R.id.iv_close);
                etTitle = findViewById(R.id.textView_titleToolbar);
                btnCancel = findViewById(R.id.btn_cancel_mon);
                btnConfirm = findViewById(R.id.btn_conf_mon);

                close.setVisibility(View.VISIBLE);
                try {
                    etTitle.setText(title.replace("_", " "));
                } catch (Exception e) {
                    Logger.exception(className, e);
                    etTitle.setText("");
                }
                close.setOnClickListener(MasterControl.this);
                btnCancel.setOnClickListener(MasterControl.this);
                btnConfirm.setOnClickListener(MasterControl.this);

                EditText total = findViewById(R.id.monto_display_area);

                if (isHTML)
                    total.setText(Html.fromHtml(label + " " + amnt));
                else
                    total.setText(label + " " + amnt);

                counterDownTimer(timeout, getString(R.string.waiting_time_over), true, "showConfirmAmountView");
            }
        });
    }

    @Override
    public void showSignatureView(final int timeout, OnUserResultListener l, final String title, final String transType) {
        this.listener = l;
        isSignature = true;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_signature);
                final TextView textViewTitle = findViewById(R.id.textView_cont);
                final EditText editText_cedula = findViewById(R.id.editText_cedula);
                final EditText editText_telefono = findViewById(R.id.editText_telefono);

                mSignaturePad = findViewById(R.id.signature_pad);
                mSignaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
                    @Override
                    public void onStartSigning() {
                        isOnSignature = true;
                    }

                    @Override
                    public void onSigned() {
                        mClearButton.setEnabled(true);
                    }

                    @Override
                    public void onClear() {
                        mClearButton.setEnabled(false);
                        isOnSignature = false;
                    }
                });
                mClearButton = findViewById(R.id.clear_button);
                mSaveButton = findViewById(R.id.save_button);
                mSaveButton.setEnabled(true);
                mClearButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        mSignaturePad.clear();
                    }
                });
                mSaveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (editText_cedula.getText().toString().trim().length() > 5 && isOnSignature) {
                            Bitmap signatureBitmap = mSignaturePad.getSignatureBitmap();
                            saveImage(signatureBitmap);
                            inputContent = editText_cedula.getText().toString() + ";" + editText_telefono.getText().toString();
                            listener.confirm(InputManager.Style.COMMONINPUT);
                        } else if (!isOnSignature) {
                            UIUtils.showAlertDialog("Informacion", "Debe ingresar firma", MasterControl.this);
                        } else if (editText_cedula.getText().toString().trim().length() <= 5) {
                            UIUtils.showAlertDialog("Informacion", "Debe ingresar cédula", MasterControl.this);
                        }
                    }
                });


            }
        });

    }

    final void saveImage(Bitmap signature) {

        String root = Environment.getExternalStorageDirectory().toString();

        // the directory where the signature will be saved
        File myDir = new File(root + "/saved_signature");

        // make the directory if it does not exist yet
        if (!myDir.exists()) {
            myDir.mkdirs();
        }

        // set the file name of your choice
        String fname = "signature.png";

        // in our case, we delete the previous file, you can remove this
        File file = new File(myDir, fname);
        if (file.exists()) {
            file.delete();
        }

        try {

            // save the signature
            FileOutputStream out = new FileOutputStream(file);
            signature.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            Logger.exception(className, e);
            e.printStackTrace();
        }
    }

    @Override
    public void showListView(final int timeout, OnUserResultListener l, final String title, final String transType, final ArrayList<String> listMenu, final int id) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.frag_show_list);
                close = findViewById(R.id.iv_close);
                etTitle = findViewById(R.id.textView_titleToolbar);
                menu = findViewById(R.id.iv_menus);

                close.setVisibility(View.VISIBLE);
                menu.setImageResource(id);

                try {
                    etTitle.setText(title.replace("_", " "));
                } catch (Exception e) {
                    Logger.exception(className, e);
                    etTitle.setText("");
                }

                initList(transType, listMenu);

                close.setOnClickListener(MasterControl.this);

                counterDownTimer(timeout, getString(R.string.waiting_time_over), true, "showListView");
            }
        });

    }

    @Override
    public void showVentaCuotasView(final int timeout, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_venta_cuotas);

                final String[] planes = {"Plan 1", "Plan 2", "Plan 3", "Plan 4", "Plan 5", "Plan 6", "Plan 7", "Plan 8", "Plan 9"};

                final TextView tvPlanes = findViewById(R.id.tvPlanes);
                tvPlanes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(MasterControl.this);
                        dialog.setContentView(R.layout.dialog_number_picker);
                        android.widget.Button dialogButtonAceptar = dialog.findViewById(R.id.btnAccept);
                        android.widget.Button dialogButtonCancelar = dialog.findViewById(R.id.btnCancel);
                        final String old = tvPlanes.getText().toString();

                        NumberPicker np = dialog.findViewById(R.id.numberPicker);
                        np.setMinValue(0);
                        np.setMaxValue(planes.length - 1);
                        np.setDisplayedValues(planes);
                        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                                tvPlanes.setText("" + planes[numberPicker.getValue()]);
                            }
                        });

                        dialogButtonAceptar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialogButtonCancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                tvPlanes.setText(old);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });

                final TextView tvCuotas = findViewById(R.id.tvCuotas);
                tvCuotas.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        final Dialog dialog = new Dialog(MasterControl.this);
                        dialog.setContentView(R.layout.dialog_number_picker);
                        final String old = tvCuotas.getText().toString();
                        android.widget.Button dialogButtonAceptar = dialog.findViewById(R.id.btnAccept);
                        android.widget.Button dialogButtonCancelar = dialog.findViewById(R.id.btnCancel);

                        NumberPicker np = dialog.findViewById(R.id.numberPicker);

                        np.setMinValue(2);
                        np.setMaxValue(99);
                        np.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker numberPicker, int i, int i1) {
                                tvCuotas.setText("" + numberPicker.getValue());
                            }
                        });

                        dialogButtonAceptar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialogButtonCancelar.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                tvCuotas.setText(old);
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                    }
                });

                android.widget.Button aceptar;
                aceptar = findViewById(R.id.btnAccept);
                android.widget.Button cancelar;
                cancelar = findViewById(R.id.btnCancel);

                aceptar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        int plan = 0;
                        for (int i = 0; i < planes.length; i++) {
                            if (planes[i].equals(tvPlanes.getText().toString())) {
                                plan = i + 1;
                                break;
                            }
                        }
                        inputContent = plan + "@" + tvCuotas.getText().toString();
                        listener.confirm(InputManager.Style.COMMONINPUT);
                    }
                });

                cancelar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.cancel();
                    }
                });


                counterDownTimer(timeout, getString(R.string.waiting_time_over), true, "showVentaCuotasView");


            }
        });
    }

    @Override
    public void showPlanVentaButtonView(int timeout, final ButtonModel botones[], final String monto, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_venta_cuotas_list_plan);
                TextView tvSerial = findViewById(R.id.tvSerial);
                TextView tvVersion = findViewById(R.id.tvVersion);
                showVersionSerial(tvVersion, tvSerial);
                View view = findViewById(R.id.viewCotas);
                view.setVisibility(View.VISIBLE);
                TextView textView = findViewById(R.id.tvTransName);
                textView.setVisibility(View.GONE);
                textView = findViewById(R.id.tvSelecPlan);
                textView.setVisibility(View.VISIBLE);
                textView = findViewById(R.id.tv1);
                textView.setVisibility(View.VISIBLE);

                int len = monto.length();
                if (len > 2) {
                    String monto2 = monto.substring(0, len - 2);
                    textView.setText(formatearValor(monto2));

                } else {
                    textView.setText(formatearValor(monto));
                }

                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                inicializarRecyclerViewGrid(MasterControl.this, recyclerView, 3);


                List<ButtonModel> modePlansList = new ArrayList<>();
                for (int i = 0; i < botones.length; i++) {
                    ButtonModel model = new ButtonModel();
                    model.setCode(botones[i].getCode());
                    model.setName(botones[i].getName());
                    modePlansList.add(model);
                }

                ImageView ivClose = findViewById(R.id.iv_close);
                ivClose.setVisibility(View.VISIBLE);
                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.cancel();
                    }
                });

                // TODO: NO ITEM SUPPORTED
                UniversalAdapter adapter = UAFactory.adapterButtons(modePlansList, new UAListener.ButtonListener() {
                    @Override
                    public void onClick(View view, ButtonModel button) {
                        inputContent = button.getCode() + "@" + button.getName();
                        listener.confirm(InputManager.Style.COMMONINPUT);
                    }
                });
                recyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public void showBotonesView(final int timeout, final String titulo, final ArrayList<ButtonModel> buttons, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_venta_cuotas_list_plan);
                TextView tvSerial = findViewById(R.id.tvSerial);
                TextView tvVersion = findViewById(R.id.tvVersion);
                showVersionSerial(tvVersion, tvSerial);
                TextView tvTransName = findViewById(R.id.tvTransName);
                tvTransName.setText(titulo);


                counterDownTimer(timeout, getString(R.string.waiting_time_over), true, "showBotonesView");

                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                inicializarRecyclerViewLinear(MasterControl.this, recyclerView);

                ImageView ivClose = findViewById(R.id.iv_close);
                ivClose.setVisibility(View.VISIBLE);
                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteTimer();
                        inputContent = getString(R.string.cancel_);
                        listener.confirm(InputManager.Style.COMMONINPUT);
                    }
                });

                recyclerView.setAdapter(UAFactory.adapterButtons(buttons, new UAListener.ButtonListener() {
                    @Override
                    public void onClick(View view, ButtonModel button) {
                        deleteTimer();
                        inputContent = button.getCode();
                        listener.confirm(InputManager.Style.COMMONINPUT);
                    }
                }));
            }
        });
    }

    @Override
    public void showMensajeConfirmacionView(final int timeout, final ModeloMensajeConfirmacion modelo, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.vista_mensaje_confirmacion);

                TextView tvSerial = findViewById(R.id.tvSerial);
                TextView tvVersion = findViewById(R.id.tvVersion);
                showVersionSerial(tvVersion, tvSerial);

                counterDownTimer(timeout, getString(R.string.waiting_time_over), false, "showMensajeConfirmacionView");

                ImageView ivClose = findViewById(R.id.iv_close);
                ivClose.setVisibility(View.VISIBLE);
                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.cancel();
                    }
                });

                RelativeLayout logoToolbar = findViewById(R.id.logo_toolbar);
                TextView tvBanner = findViewById(R.id.tvBannerBancard);
                TextView tvtitle = findViewById(R.id.tvTitulo);
                ImageView imgData = findViewById(R.id.imgData);
                TextView message = findViewById(R.id.mensaje);
                android.widget.Button btnSi = findViewById(R.id.btnSi);
                android.widget.Button btnNo = findViewById(R.id.btnNo);
                TextView tvSubMensaje = findViewById(R.id.tvSubMensaje);

                if (modelo.getBanner() != null) {
                    logoToolbar.setVisibility(View.GONE);
                    tvBanner.setVisibility(View.VISIBLE);
                    tvBanner.setText(modelo.getBanner());
                }

                if (modelo.getTitulo() != null) {
                    tvtitle.setText(modelo.getTitulo());
                }

                if (modelo.getDrawable() != null) {
                    imgData.setVisibility(View.VISIBLE);
                    imgData.setImageDrawable(modelo.getDrawable());
                }

                if (modelo.getMensaje() != null) {
                    message.setText(modelo.getMensaje());
                }

                if (modelo.getMsgBtnAceptar() != null) {
                    btnSi.setVisibility(View.VISIBLE);
                    btnSi.setText(modelo.getMsgBtnAceptar());
                }

                if (modelo.getMsgBtnCancelar() != null) {
                    btnNo.setVisibility(View.VISIBLE);
                    btnNo.setText(modelo.getMsgBtnCancelar());
                }

                if (modelo.getSubMensaje() != null) {
                    tvSubMensaje.setText(modelo.getSubMensaje());
                }

                btnSi.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteTimer();
                        inputContent = "si";
                        listener.confirm(InputManager.Style.COMMONINPUT);
                    }
                });

                btnNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteTimer();
                        inputContent = "no";
                        listener.confirm(InputManager.Style.COMMONINPUT);
                    }
                });

            }
        });
    }

    @Override
    public void showVerSaldoCuentaView(int timeout, final long saldo) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.vista_mostrar_monto_cuenta);

                TextView tvSerial = findViewById(R.id.tvSerial);
                TextView tvVersion = findViewById(R.id.tvVersion);
                showVersionSerial(tvVersion, tvSerial);

                ImageView ivClose = findViewById(R.id.iv_close);
                ivClose.setVisibility(View.VISIBLE);
                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeCard();
                    }
                });

                final TextView tvMensajeHost = findViewById(R.id.tvMensajeHost);

                final FloatingActionButton buttonF = findViewById(R.id.buttonF);
                buttonF.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String strSaldo = String.valueOf(saldo);
                        strSaldo = PAYUtils.FormatPyg(strSaldo);
                        tvMensajeHost.setText("Saldo: Gs. " + strSaldo);
                        buttonF.setImageDrawable(getDrawable(R.drawable.ic_close_white));
                        buttonF.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                removeCard();
                            }
                        });

                    }
                });
            }
        });
    }

    @Override
    public void showResultCierreView(final int timeout, final LogsCierresModelo cierresModelo, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.vista_result_settle);
                TextView tvSerial = findViewById(R.id.tvSerial);
                TextView tvVersion = findViewById(R.id.tvVersion);
                showVersionSerial(tvVersion, tvSerial);
                deleteTimer();
                counterDownTimer(timeout, getString(R.string.waiting_time_over), true, "showResultCierreView");

                ImageView ivClose = findViewById(R.id.iv_close);
                ivClose.setVisibility(View.VISIBLE);
                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteTimer();
                        listener.cancel();
                    }
                });

                TextView tvMensaje = findViewById(R.id.tvMensaje);
                String voucher = "CIERRE DE LOTE - LOTE " +
                        cierresModelo.getNumLote() +
                        "\n" +
                        "ENTRE " +
                        cierresModelo.getFechaUltimoCierre() +
                        "\n" +
                        "HASTA " +
                        cierresModelo.getFechaCierre() +
                        "\n" +
                        "TOTAL CREDITO - " +
                        checkNullZero(cierresModelo.getCantCredito()) +
                        getString(R.string.gs) +
                        PAYUtils.FormatPyg(cierresModelo.getTotalCredito()) +
                        "\n" +
                        "TOTAL DEBITO - " +
                        checkNullZero(cierresModelo.getCantDebito()) +
                        getString(R.string.gs) +
                        PAYUtils.FormatPyg(cierresModelo.getTotalDebito()) +
                        "\n" +
                        "TOTAL MOVIL - " +
                        checkNullZero(cierresModelo.getCantMovil()) +
                        getString(R.string.gs) +
                        PAYUtils.FormatPyg(cierresModelo.getTotalMovil()) +
                        "\n" +
                        "TOTAL ANULAR - " +
                        checkNullZero(cierresModelo.getCantAnular()) +
                        getString(R.string.gs) +
                        PAYUtils.FormatPyg(cierresModelo.getTotalAnular()) +
                        "\n" +
                        "TOTAL VUELTO - " +
                        checkNullZero(cierresModelo.getCantVuelto()) +
                        getString(R.string.gs) +
                        PAYUtils.FormatPyg(cierresModelo.getTotalVuelto()) +
                        "\n" +
                        "TOTAL VENTA SALDO - " +
                        checkNullZero(cierresModelo.getCantSaldo()) +
                        getString(R.string.gs) +
                        PAYUtils.FormatPyg(cierresModelo.getTotalSaldo()) +
                        "\n";
                tvMensaje.setText(voucher);

                TextView tvTotalGeneral = findViewById(R.id.tvTotalGeneral);
                tvTotalGeneral.setText(new StringBuilder().append("TOTAL GENERAL - ")
                        .append(cierresModelo.getCantGeneral()).append(getString(R.string.gs))
                        .append(PAYUtils.FormatPyg(cierresModelo.getTotalGeneral())).toString());

                android.widget.Button btnImprimir = findViewById(R.id.btnImprimir);
                android.widget.Button btnNoImprimir = findViewById(R.id.btnNoImprmir);

                btnImprimir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteTimer();
                        listener.confirm(InputManager.Style.COMMONINPUT);
                    }
                });

                btnNoImprimir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteTimer();
                        listener.cancel();
                    }
                });

            }
        });
    }

    private String checkNullZero(String data) {
        if (data == null || data.trim().equals("")) {
            return "0";
        } else {
            return data;
        }
    }

    @Override
    public void showContacLessInfoView(final boolean finish) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.vista_remove_ctl);
                TextView tvSerial = findViewById(R.id.tvSerial);
                TextView tvVersion = findViewById(R.id.tvVersion);
                showVersionSerial(tvVersion, tvSerial);
                if (!finish) {
                    TextView tvTitulo = findViewById(R.id.tvTitulo);
                    TextView tvSubTitulo = findViewById(R.id.content_info);
                    tvSubTitulo.setVisibility(View.VISIBLE);
                    tvTitulo.setText("MANTENGA LA TARJETA");
                    ImageView imageView = findViewById(R.id.iv_remove__card);
                    imageView.setImageDrawable(getDrawable(R.drawable.nfc_acerca));
                }

            }
        });
    }

    @Override
    public void showListBilleteras(final int timeout, final String titulo, final List<Billeteras> billeterasList, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_venta_cuotas_list_plan);
                TextView tvSerial = findViewById(R.id.tvSerial);
                TextView tvVersion = findViewById(R.id.tvVersion);
                showVersionSerial(tvVersion, tvSerial);
                TextView tvTransName = findViewById(R.id.tvTransName);
                tvTransName.setText(titulo);


                counterDownTimer(timeout, getString(R.string.waiting_time_over), true, "showBotonesView");

                RecyclerView recyclerView = findViewById(R.id.recyclerView);
                inicializarRecyclerViewGrid(MasterControl.this, recyclerView, 3);

                ImageView ivClose = findViewById(R.id.iv_close);
                ivClose.setVisibility(View.VISIBLE);
                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteTimer();
                        inputContent = "CANCEL";
                        listener.cancel();
                    }
                });

                AdaptadorBilleteras adapter = new AdaptadorBilleteras(MasterControl.this, billeterasList);
                recyclerView.setAdapter(adapter);
                adapter.setOnItemClickListener(new AdaptadorBilleteras.OnItemClickListener() {
                    @Override
                    public void onItemClick(Billeteras billeteras) {
                        deleteTimer();
                        inputContent = billeteras.getCodBilletera() + " @ " + billeteras.getNombreBilletera();
                        listener.confirm(InputManager.Style.COMMONINPUT);
                    }
                });


            }
        });
    }

    @Override
    public void showRetireTarjeta() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.activity_remove_card);
                TextView tvSerial = findViewById(R.id.tvSerial);
                TextView tvVersion = findViewById(R.id.tvVersion);
                showVersionSerial(tvVersion, tvSerial);
            }
        });
    }

    @Override
    public void showInfoMessage(final String title, final String message, final int timeOut, final int ico, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.vista_result_trans);
                TextView tvSerial = findViewById(R.id.tvSerial);
                TextView tvVersion = findViewById(R.id.tvVersion);
                showVersionSerial(tvVersion, tvSerial);
                deleteTimer();
                counterDownTimer(timeOut, getString(R.string.waiting_time_over), true, "showInfoMessage");

                ImageView imgView = findViewById(R.id.imgView);
                LinearLayout linearOpciones = findViewById(R.id.linearOpciones);
                linearOpciones.setVisibility(View.VISIBLE);
                if (!title.isEmpty()) {
                    linearOpciones.setVisibility(View.GONE);
                    TextView tvTitulo = findViewById(R.id.tvTitulo);
                    tvTitulo.setText(title);
                    imgView.setImageDrawable(getDrawable(ico));
                }

                FloatingActionButton buttonF = findViewById(R.id.buttonF);

                linearOpciones.setVisibility(View.GONE);
                buttonF.setVisibility(View.VISIBLE);

                ImageView iv_close = findViewById(R.id.iv_close);
                iv_close.setVisibility(View.VISIBLE);
                iv_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteTimer();
                        removeCard();
                        listener.cancel();
                    }
                });

                buttonF.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteTimer();
                        removeCard();
                    }
                });
                TextView tvMensajeHost = findViewById(R.id.tvMensajeHost);
                if (message != null) {
                    tvMensajeHost.setText(message.trim());
                }
            }
        });
    }

    @Override
    public void showIngresoCuota(int timeout, final String valor, final String titulo, final String subtitulo, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.vista_ingreso_cuotas);
                TextView tvSerial = findViewById(R.id.tvSerial);
                TextView tvVersion = findViewById(R.id.tvVersion);
                showVersionSerial(tvVersion, tvSerial);
                String[] strings = valor.split("~");

                TextView tv1 = findViewById(R.id.tv1);
                tv1.setText("Plan " + strings[0]);
                TextView tv2 = findViewById(R.id.tv2);
                tv2.setText("Cuotas " + strings[1]);

                ImageView ivClose = findViewById(R.id.iv_close);
                ivClose.setVisibility(View.VISIBLE);
                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.cancel();
                    }
                });

                tipoIngreso = DefinesBANCARD.INGRESO_CUOTAS;
                builder = new StringBuilder();
                editText = findViewById(R.id.editText);
                longitudMaxima = 2;

                TextView tvTransName = findViewById(R.id.tvTransName);
                tvTransName.setText(titulo);
                TextView tvAmount = findViewById(R.id.tvAmount);
                String amount = String.valueOf(subtitulo);
                int len = amount.length();
                if (len > 2) {
                    amount = amount.substring(0, len - 2);
                }
                tvAmount.setText(formatearValor(amount));

                ImageButton btnOk = findViewById(R.id.btnOk);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (builder.length() != 0 && !builder.toString().equals("1")) {
                            inputContent = builder.toString();
                            listener.confirm(InputManager.Style.COMMONINPUT);
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(), "Solo se aceptan valores entre 2 y 99", Toast.LENGTH_SHORT);
                            toast.setGravity(Gravity.CENTER, 0, 0);
                            toast.show();
                        }
                    }
                });

            }
        });
    }

    @Override
    public void showIngresoDataNumericoView(final int timeout, final String tipo, final String mensajeSecundaio,
                                            final String title, final int longitud,
                                            final String trx, final long amount, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.vista_ingreso_numerico);
                TextView tvSerial = findViewById(R.id.tvSerial);
                TextView tvVersion = findViewById(R.id.tvVersion);
                showVersionSerial(tvVersion, tvSerial);

                ImageView ivClose = findViewById(R.id.iv_close);
                ivClose.setVisibility(View.VISIBLE);
                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteTimer();
                        if (tipoIngreso.equals(DefinesBANCARD.INGRESO_PIN)) {
                            inputContent = "CANCEL";
                            listener.confirm(InputManager.Style.COMMONINPUT);
                        } else {
                            listener.cancel();
                        }
                    }
                });

                tipoIngreso = tipo;
                builder = new StringBuilder();
                editText = findViewById(R.id.editText);
                switch (tipoIngreso) {
                    case DefinesBANCARD.INGRESO_MONTO:
                    case DefinesBANCARD.INGRESO_VUELTO:
                        editText.setHint("Gs. 0");
                        editText.setGravity(Gravity.CENTER);
                        break;
                    case INGRESO_TELEFONO:
                        break;
                    case DefinesBANCARD.INGRESO_CODIGO:
                        break;
                    case DefinesBANCARD.INGRESO_PIN:
                        editText.setInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_VARIATION_PASSWORD);
                        editText.setHint("****");
                        break;
                    default:
                        throw new IllegalStateException(getString(R.string.unexpected_value) + tipoIngreso);
                }

                TextView tvTransName = findViewById(R.id.tvTransName);
                tvTransName.setText(trx);
                final TextView tvMensajeSecundaio = findViewById(R.id.tvMensaje02);

                if (mensajeSecundaio != null && !mensajeSecundaio.isEmpty()) {
                    tvMensajeSecundaio.setVisibility(View.VISIBLE);
                    tvMensajeSecundaio.setText(mensajeSecundaio);
                }

                TextView tvAmount = findViewById(R.id.tvAmount);
                if (tipoIngreso.equals(DefinesBANCARD.INGRESO_MONTO)) {
                    tvAmount.setText("Ingreso de monto");
                } else if (tipoIngreso.equals(DefinesBANCARD.INGRESO_VUELTO)) {
                    tvAmount.setText("Ingreso de vuelto");
                } else {
                    String monto = String.valueOf(amount);
                    int len = monto.length();
                    monto = monto.substring(0, len - 2);
                    tvAmount.setText(formatearValor(monto));
                }

                TextView tvMensaje = findViewById(R.id.tvMensaje);
                tvMensaje.setText(title);

                longitudMaxima = longitud;

                ImageButton btnOk = findViewById(R.id.btnOk);
                btnOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (builder.length() != 0) {

                            if (tipoIngreso.equals(INGRESO_TELEFONO) || tipoIngreso.equals(INGRESO_BILLETERAS)) {
                                if ((tipoIngreso.equals(INGRESO_TELEFONO) && builder.length() == longitudMaxima) || (tipoIngreso.equals(INGRESO_BILLETERAS) && builder.length() >= 2)) {
                                    deleteTimer();
                                    inputContent = builder.toString();
                                    listener.confirm(InputManager.Style.COMMONINPUT);
                                } else {
                                    UIUtils.toast(MasterControl.this, R.drawable.redinfonet, title + " no valido ", Toast.LENGTH_SHORT);
                                }
                            } else {
                                deleteTimer();
                                inputContent = builder.toString();
                                listener.confirm(InputManager.Style.COMMONINPUT);
                            }
                        } else {
                            UIUtils.toast(MasterControl.this, R.drawable.redinfonet, "Ingrese " + title, Toast.LENGTH_SHORT);
                        }
                    }
                });

                counterDownTimer(timeout, getString(R.string.waiting_time_over), true, "showIngresoDataNumericoView");
            }
        });
    }

    @Override
    public void showSeleccionTipoDeCuentaView(int timeout, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.vista_seleccion_tipo_cuenta);

                android.widget.Button btn_cancel_mon = findViewById(R.id.btn_cancel_mon);
                android.widget.Button btn_conf_mon = findViewById(R.id.btn_conf_mon);
                final RadioButton cuentaAhorros = findViewById(R.id.rbCAhorros);
                final RadioButton cuentaCorriente = findViewById(R.id.rbCCorriente);
                final RadioButton credito = findViewById(R.id.rbCredito);

                btn_conf_mon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String cuenta = null;
                        if (cuentaAhorros.isChecked()) {
                            cuenta = DefinesBANCARD.TIPO_AHORROS;
                        }
                        if (cuentaCorriente.isChecked()) {
                            cuenta = DefinesBANCARD.TIPO_CORRIENTE;
                        }
                        if (credito.isChecked()) {
                            cuenta = DefinesBANCARD.TIPO_CREDITO;
                        }
                        if (cuenta != null) {
                            inputContent = cuenta;
                            listener.confirm(InputManager.Style.COMMONINPUT);
                        } else {
                            Toast.makeText(MasterControl.this, "Selecciona el tipo de cuenta.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                btn_cancel_mon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listener.cancel();
                    }
                });

            }
        });
    }

    @Override
    public void showImprimiendoView(int timeout) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.vista_imprimiendo);
                TextView tvSerial = findViewById(R.id.tvSerial);
                TextView tvVersion = findViewById(R.id.tvVersion);
                showVersionSerial(tvVersion, tvSerial);
            }
        });
    }

    @Override
    public void showResultView(final int timeout, final boolean aprobada, final boolean isIconoWifi, final boolean opciones, final String mensajeHost, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.vista_result_trans);
                TextView tvSerial = findViewById(R.id.tvSerial);
                TextView tvVersion = findViewById(R.id.tvVersion);
                showVersionSerial(tvVersion, tvSerial);
                deleteTimer();
                counterDownTimer(timeout, getString(R.string.waiting_time_over), true, "showResultView");

                ImageView imgView = findViewById(R.id.imgView);
                LinearLayout linearOpciones = findViewById(R.id.linearOpciones);
                linearOpciones.setVisibility(View.VISIBLE);
                if (!aprobada) {
                    if (isIconoWifi && field22 == null) {
                        linearOpciones.setVisibility(View.GONE);
                        TextView tvTitulo = findViewById(R.id.tvTitulo);
                        tvTitulo.setText("ERROR DE CONEXIÓN");
                        imgView.setImageDrawable(getDrawable(R.drawable.ic_icono_bancard));
                    } else {
                        linearOpciones.setVisibility(View.GONE);
                        TextView tvTitulo = findViewById(R.id.tvTitulo);
                        tvTitulo.setText("RECHAZADA");
                        imgView.setImageDrawable(getDrawable(R.drawable.transaccion_fallida));
                    }

                }

                FloatingActionButton buttonF = findViewById(R.id.buttonF);

                if (!opciones) {
                    linearOpciones.setVisibility(View.GONE);
                    buttonF.setVisibility(View.VISIBLE);
                }

                ImageView iv_close = findViewById(R.id.iv_close);
                iv_close.setVisibility(View.VISIBLE);
                iv_close.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteTimer();
                        removeCard();
                        listener.cancel();
                    }
                });

                buttonF.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteTimer();
                        removeCard();
                    }
                });

                TextView tvMensajeHost = findViewById(R.id.tvMensajeHost);
                if (field22 != null) {
                    tvMensajeHost.setText(field22.trim());
                } else if (mensajeHost != null) {
                    tvMensajeHost.setText(mensajeHost.trim());
                }


                android.widget.Button btnImprimir = findViewById(R.id.btnImprimir);
                android.widget.Button btnNoImprimir = findViewById(R.id.btnNoImprmir);

                btnImprimir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteTimer();
                        listener.confirm(InputManager.Style.COMMONINPUT);
                    }
                });

                btnNoImprimir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteTimer();
                        removeCard();
                        listener.cancel();
                    }
                });

            }
        });
    }

    @Override
    public void showResultView(final int timeout, final String emcabezado, final boolean aprobada, final boolean isIconoWifi, final boolean opciones, final String mensajeHost, final OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.vista_result_trans);
                TextView tvSerial = findViewById(R.id.tvSerial);
                TextView tvVersion = findViewById(R.id.tvVersion);
                showVersionSerial(tvVersion, tvSerial);
                deleteTimer();
                counterDownTimer(timeout, getString(R.string.waiting_time_over), true, "showResultView");

                ImageView imgView = findViewById(R.id.imgView);
                LinearLayout linearOpciones = findViewById(R.id.linearOpciones);

                TextView tvTitulo = findViewById(R.id.tvTitulo);
                tvTitulo.setText(emcabezado);

                linearOpciones.setVisibility(View.VISIBLE);
                if (!aprobada) {
                    if (isIconoWifi && field22 == null) {
                        linearOpciones.setVisibility(View.GONE);
                        imgView.setImageDrawable(getDrawable(R.drawable.ic_icono_bancard));
                    } else {
                        linearOpciones.setVisibility(View.GONE);
                        imgView.setImageDrawable(getDrawable(R.drawable.transaccion_fallida));
                    }

                }

                FloatingActionButton buttonF = findViewById(R.id.buttonF);

                if (!opciones) {
                    linearOpciones.setVisibility(View.GONE);
                    buttonF.setVisibility(View.VISIBLE);
                }

                ImageView ivClose = findViewById(R.id.iv_close);
                ivClose.setVisibility(View.VISIBLE);
                ivClose.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteTimer();
                        removeCard();
                    }
                });

                buttonF.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteTimer();
                        removeCard();
                    }
                });

                TextView tvMensajeHost = findViewById(R.id.tvMensajeHost);
                if (field22 != null) {
                    tvMensajeHost.setText(field22.trim());
                } else if (mensajeHost != null) {
                    tvMensajeHost.setText(mensajeHost.trim());
                }


                android.widget.Button btnImprimir = findViewById(R.id.btnImprimir);
                android.widget.Button btnNoImprimir = findViewById(R.id.btnNoImprmir);

                btnImprimir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteTimer();
                        listener.confirm(InputManager.Style.COMMONINPUT);
                    }
                });

                btnNoImprimir.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        deleteTimer();
                        removeCard();
                    }
                });

            }
        });
    }

    @Override
    public void showFinishView() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                removeCard();
            }
        });
    }

    private void removeCard() {
        field22 = null;
        Thread proceso = null;
        proceso = new Thread(new Runnable() {
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        iccReader0 = IccReader.getInstance(SlotType.USER_CARD);
                        if (iccReader0.isCardPresent()) {
                            setContentView(R.layout.activity_remove_card);
                            TextView tvSerial = findViewById(R.id.tvSerial);
                            TextView tvVersion = findViewById(R.id.tvVersion);
                            showVersionSerial(tvVersion, tvSerial);
                        }
                    }
                });
                if (validarICC()) {
                    UIUtils.startView(MasterControl.this, MainActivity.class, "");
                    if (callBackSeatle != null)
                        callBackSeatle.getRspSeatleReport(0);
                }
            }
        });
        proceso.start();
    }

    private boolean validarICC() {
        iccReader0 = IccReader.getInstance(SlotType.USER_CARD);
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
        while (true) {
            try {
                if (iccReader0.isCardPresent()) {
                    toneG.startTone(ToneGenerator.TONE_PROP_BEEP2, 2000);
                    sleep(2000);
                } else {
                    return true;
                }
            } catch (Exception e) {
                Logger.exception(className, e);
                Thread.currentThread().interrupt();
                return false;
            }
        }
    }

    private void eliminarUltimoCaracter() {
        int len = builder.length();
        if (len != 0) {
            builder.deleteCharAt(len - 1);
        }
        mostrarEnTextView();
    }

    private void mostrarEnTextView() {
        int len = builder.length();
        if (len <= longitudMaxima) {
            switch (tipoIngreso) {
                case DefinesBANCARD.INGRESO_MONTO:
                case DefinesBANCARD.INGRESO_VUELTO:
                    editText.setText(formatearValor(builder.toString()).replace("Monto compra: ", ""));
                    break;
                case INGRESO_TELEFONO:
                case INGRESO_BILLETERAS:
                    editText.setText(PAYUtils.FormarPhonePyg(builder.toString(), tipoIngreso));
                    break;
                case DefinesBANCARD.INGRESO_CUOTAS:
                    editText.setText(builder.toString());
                    break;
                case DefinesBANCARD.INGRESO_CODIGO:
                case DefinesBANCARD.INGRESO_PIN:
                    setSizeText(len);
                    editText.setText(builder.toString());
                    break;
                default:
                    throw new IllegalStateException(getString(R.string.unexpected_value) + tipoIngreso);
            }
        } else {
            eliminarUltimoCaracter();
        }

    }

    private void setSizeText(int len) {
        if (len > 13 && len < 18) {
            editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
        }
        if (len > 18 && len < 28) {
            editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        }
        if (len > 28 && len < 33) {
            editText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        }
    }

    private String formatearValor(String dato) {

        dato = dato.replace(".", "");


        if (dato.equals("")) {
            return "Monto compra: Gs. 0";
        }

        StringBuilder dato1 = new StringBuilder();
        char[] aux1 = dato.toCharArray();
        for (char c : aux1) {
            dato1.append(c);
        }

        String str = dato1.toString();
        String salida = "";
        int longitud = str.length();
        if (longitud < 4) {
            return "Monto compra: Gs. " + dato1.toString();
        }

        if (longitud == 4) {
            String sub2 = str.substring(0, 1);
            String sub1 = str.substring(1, 4);
            salida = sub2 + "." + sub1;
        }
        if (longitud == 5) {
            String sub2 = str.substring(0, 2);
            String sub1 = str.substring(2, 5);
            salida = sub2 + "." + sub1;
        }
        if (longitud == 6) {
            String sub2 = str.substring(0, 3);
            String sub1 = str.substring(3, 6);
            salida = sub2 + "." + sub1;
        }
        if (longitud == 7) {
            String sub2 = str.substring(0, 1);
            String sub1 = str.substring(1, 4);
            String sub0 = str.substring(4, 7);
            salida = sub2 + "." + sub1 + "." + sub0;
        }
        if (longitud == 8) {
            String sub2 = str.substring(0, 2);
            String sub1 = str.substring(2, 5);
            String sub0 = str.substring(5, 8);
            salida = sub2 + "." + sub1 + "." + sub0;
        }
        if (longitud == 9) {
            String sub2 = str.substring(0, 3);
            String sub1 = str.substring(3, 6);
            String sub0 = str.substring(6, 9);
            salida = sub2 + "." + sub1 + "." + sub0;
        }
        if (longitud == 10) {
            String sub2 = str.substring(0, 1);
            String sub1 = str.substring(1, 4);
            String sub0 = str.substring(4, 7);
            String sub = str.substring(7, 10);
            salida = sub2 + "." + sub1 + "." + sub0 + "." + sub;
        }
        if (longitud == 11) {
            String sub2 = str.substring(0, 2);
            String sub1 = str.substring(2, 5);
            String sub0 = str.substring(5, 8);
            String sub = str.substring(8, 11);
            salida = sub2 + "." + sub1 + "." + sub0 + "." + sub;
        }
        if (longitud == 12) {
            String sub2 = str.substring(0, 3);
            String sub1 = str.substring(3, 6);
            String sub0 = str.substring(6, 9);
            String sub = str.substring(9, 12);
            salida = sub2 + "." + sub1 + "." + sub0 + "." + sub;
        }
        if (longitud == 13) {
            String sub2 = str.substring(0, 1);
            String sub1 = str.substring(1, 4);
            String sub0 = str.substring(4, 7);
            String sub = str.substring(7, 10);
            String su = str.substring(10, 13);
            salida = sub2 + "." + sub1 + "." + sub0 + "." + sub + "." + su;
        }

        return "Monto compra: Gs. " + salida;
    }

    private void initList(String transType, final ArrayList<String> listMenu) {
        final ListView listview = findViewById(R.id.simpleListView);

        final StableArrayAdapter adapter = new StableArrayAdapter(MasterControl.this, android.R.layout.simple_list_item_1, listMenu);
        listview.setAdapter(adapter);
        Logger.info(TAG, "TransType: " + transType);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                final String item = String.valueOf(parent.getItemIdAtPosition(position));
                view.animate().setDuration(500).alpha(0)
                        .withEndAction(new Runnable() {
                            @Override
                            public void run() {

                                if (!item.equals("")) {
                                    inputContent = item;
                                    listener.confirm(InputManager.Style.COMMONINPUT);
                                }

                            }
                        });
            }

        });
    }

    private void startTrans(String type, String auxMonto, Context ctx, boolean isCajas) {
        try {
            Logger.info(TAG, "Type: " + type);
            if (PaperStatus.getInstance().getRet() == Printer.PRINTER_STATUS_PAPER_LACK && (!type.equals(Trans.Type.ECHO_TEST))) {
                UIUtils.toast(MasterControl.this, R.drawable.ic_redinfonet, DefinesBANCARD.MSG_PAPER, Toast.LENGTH_SHORT);
                finish();
            } else {
                PaySdk.getInstance().startTrans(type, auxMonto, this, ctx);
            }
        } catch (PaySdkException e) {
            Logger.exception(className, e);
            Logger.error("Exception", e);
        }
    }

    private void showConfirmCardNO(String pan) {
        btnConfirm = findViewById(R.id.cardno_confirm);
        btnCancel = findViewById(R.id.cardno_cancel);
        editCardNO = findViewById(R.id.cardno_display_area);
        btnCancel.setOnClickListener(MasterControl.this);
        btnConfirm.setOnClickListener(MasterControl.this);
        editCardNO.setText(pan);
    }

    private void showOrignalTransInfo(TransLogData data) {
        btnConfirm = findViewById(R.id.transinfo_confirm);
        btnCancel = findViewById(R.id.transinfo_cancel);
        btnCancel.setOnClickListener(MasterControl.this);
        btnConfirm.setOnClickListener(MasterControl.this);
        transInfo = findViewById(R.id.transinfo_display_area);

        StringBuilder info = new StringBuilder();

        info.append(getString(R.string.b)).append(getString(R.string.void_original_trans)).append(getString(R.string.br)).append(" ");
        info.append(data.getEName().replace("_", " "));
        info.append(getString(R.string.br));
        info.append(getString(R.string.b)).append(getString(R.string.void_card_no)).append(getString(R.string.br));
        info.append(" ");
        info.append(data.getPan()).append(getString(R.string.br));
        info.append(getString(R.string.b)).append(getString(R.string.void_trace_no)).append(getString(R.string.br));
        info.append(" ");
        info.append(data.getTraceNo()).append(getString(R.string.br));
        if (!PAYUtils.isNullWithTrim(data.getAuthCode())) {
            info.append(getString(R.string.b)).append(getString(R.string.void_auth_code)).append(getString(R.string.br));
            info.append(" ");
            info.append(data.getAuthCode()).append(getString(R.string.br));
        }
        info.append(getString(R.string.b)).append(getString(R.string.void_batch_no)).append(getString(R.string.br));
        info.append(" ");
        info.append(data.getBatchNo()).append(getString(R.string.br));

        if (data.getTypeCoin().equals(LOCAL)) {
            info.append(getString(R.string.b)).append(getString(R.string.void_amount)).append(getString(R.string.br));
            info.append(" $. ");
            info.append(PAYUtils.getStrAmount(data.getAmount())).append(getString(R.string.br));
        } else {
            info.append(getString(R.string.b) + getString(R.string.void_amount) + getString(R.string.br));
            info.append(" $ ");
            info.append(PAYUtils.getStrAmount(data.getAmount())).append(getString(R.string.br));
        }
        info.append(getString(R.string.b)).append(getString(R.string.void_time)).append(getString(R.string.br));
        info.append(" ");
        info.append(PAYUtils.printStr(data.getLocalDate(), data.getLocalTime()));

        transInfo.setText(Html.fromHtml(info.toString()));
    }

    private void showHanding(String msg) {
        TextView tv = findViewById(R.id.txt_handing_info_msg);
        tv.setText(msg);
    }

    private void setToolbar(String titleToolbar) {
        final Toolbar toolbar = findViewById(R.id.toolbarBancard);
        toolbar.setTitleTextColor(Color.WHITE);
        String title = "<h4>" + titleToolbar + "</h4>";
        toolbar.setTitle(Html.fromHtml(title));
        toolbar.setLogo(R.drawable.ic_redinfonet);
        toolbar.postDelayed(new Runnable() {
            @Override
            public void run() {
                setSupportActionBar(toolbar);
            }
        }, 0);
    }

    private void hideKeyBoard(IBinder windowToken) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(windowToken, 0);
    }

    @Override
    public void onBackPressed() {
        //Método no implementado
    }

    @Override
    public void showCardViewImg(final String img, OnUserResultListener l) {
        this.listener = l;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                setContentView(R.layout.trans_show_card_image);
                loadWebGifImg(img);
            }
        });
    }

    private void loadWebGifImg(String nameCard) {

        ImageView wvInsert = findViewById(R.id.webview_card_img);

        switch (nameCard.trim()) {
            case "0"://visa
                wvInsert.setImageResource(R.drawable.visa);
                break;
            case "1"://Master
                wvInsert.setImageResource(R.drawable.mastercard);
                break;
            case "2"://Amex
                wvInsert.setImageResource(R.drawable.amex);
                break;
            case "3"://Diners
                wvInsert.setImageResource(R.drawable.diners);
                break;
            case "4"://Visa Electron
                wvInsert.setImageResource(R.drawable.electron);
                break;
            case "5"://Maestro
                wvInsert.setImageResource(R.drawable.maestro);
                break;
            case "6"://
                wvInsert.setImageResource(R.drawable.infonet_other);
                break;
            case "payclub"://PAYCLUB
                wvInsert.setImageResource(R.drawable.payclub);
                break;
            case "wallet"://PAYBLUE
                wvInsert.setImageResource(R.drawable.payblue);
                break;
            default:
                break;
        }
    }

    /**
     * se agrega booleano para la pantalla de imprimir copia no utilice
     * el Resultcontrol y finalice la actividad
     */
    private void counterDownTimer(int timeout, final String mensaje, final boolean usarStar, final String metodo) {
        TimerTrans.getInstanceTimerTrans(timeout, mensaje, metodo, new TimerTrans.OnResultTimer() {
            @Override
            public void rsp2Timer() {
                if (usarStar) {
                    Logger.error("onFinish", "startResult - " + metodo);
                    UIUtils.startResult(MasterControl.this, false, mensaje);
                    listener.cancel();
                } else {
                    Logger.error("onFinish", "confirm - " + metodo);
                    listener.confirm(0);
                }
            }
        });
    }

    private void deleteTimer() {
        TimerTrans.deleteTimer();
    }

    private void encenderPantalla(final Activity activity) {
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        HashMap<String, Integer> mIdMap = new HashMap<>();

        StableArrayAdapter(Context context, int textViewResourceId,
                           List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }
}
