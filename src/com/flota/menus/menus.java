package com.flota.menus;

import static com.flota.actividades.StartAppBANCARD.isInit;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.flota.actividades.MainActivity;
import com.flota.defines_bancard.DefinesBANCARD;
import com.flota.inicializacion.tools.PolarisUtil;
import com.newpos.libpay.Logger;
import com.wposs.flota.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class menus extends AppCompatActivity {

    public static final int FALLBACK = 3;
    public static final int NO_FALLBACK = 0;
    public static final int TOTAL_BATCH = 200;
    public static final String TAG = "menus.java";
    public static String idAcquirer;
    public static int contFallback = 0;
    boolean isClose = true;
    boolean isDisplay;
    CountDownTimer countDownTimerMenus;
    CountDownTimer countDownTimerDisplay;
    TextView tvCardReader;
    ViewPager viewPager;
    private String menu;
    private int positionpager = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        setContentView(R.layout.activity_menu);
        tvCardReader = findViewById(R.id.tvCardReader);
        tvCardReader.setVisibility(View.GONE);

        if (extras != null) {
            showMenu(Objects.requireNonNull(extras.getString(DefinesBANCARD.DATO_MENU)));
            menu = Objects.requireNonNull(extras.getString(DefinesBANCARD.DATO_MENU));
        }
    }

    private void showMenu(String typeMenu) {
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        RecyclerViewAdaptadorMenu recyclerViewAdaptadorMenu;
        if (typeMenu.equals(DefinesBANCARD.MENU_PRINCIPAL)) {
            recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
        }
        recyclerViewAdaptadorMenu = new RecyclerViewAdaptadorMenu(getItems(typeMenu), this, DefinesBANCARD.TIPO_LAYOUT_LINEAR);
        recyclerViewAdaptadorMenu.setTipoMenu(typeMenu);
        recyclerView.setAdapter(recyclerViewAdaptadorMenu);
    }

    public List<menuItemsModelo> getItems(String typeMenu) {
        List<menuItemsModelo> itemMenu = new ArrayList<>();
        switch (typeMenu) {
            case DefinesBANCARD.MENU_PRINCIPAL:
                counterDownTimerMenus();
                deleteTimerMenus();
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_CONFIG, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_REPORTE_MENU, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_POLARIS, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_ECHO_TEST, R.drawable.mainitemunclick));
                break;
            case DefinesBANCARD.ITEM_COMERCIO:
                counterDownTimerMenus();
                deleteTimerMenus();
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_VENTA, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_VENTA_CUOTAS, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_RECARGAS, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_VENTA_MINUTOS, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_REPORTE, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_CIERRE, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_CONSULTA_SALDO, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_DEBITO, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_CREDITO, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_ANNULMENT, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_TECNICAS, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_ECHO_TEST, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_CONFIG, R.drawable.mainitemunclick));
                break;
            case DefinesBANCARD.ITEM_LEALTAD:
                counterDownTimerMenus();
                deleteTimerMenus();
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_VALE, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_CONSULTA, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_CANJE, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_PROMO, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_REPORTE, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_CIERRE, R.drawable.mainitemunclick));
                break;


            case DefinesBANCARD.CONFI_LLAVES:
                counterDownTimerMenus();
                deleteTimerMenus();
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.DUKPT, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.MK, R.drawable.mainitemunclick));
                break;
            case DefinesBANCARD.ITEM_VENTA:
                counterDownTimerMenus();
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_VENTA_TARJETA, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_VENTA_SIN_TARJETA, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_VENTA_CON_VUELTO, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_VENTA_SIN_CONTACTO, R.drawable.mainitemunclick));
                break;
            case DefinesBANCARD.ITEM_VENTA_CUOTAS:
                counterDownTimerMenus();
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_CUOTAS_TARJETA, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_CUOTAS_SIN_TARJETA, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_VENTA_SIN_TARJETA, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_CUOTAS_SIN_CONTACTO, R.drawable.mainitemunclick));
                break;
            case DefinesBANCARD.ITEM_VENTA_SIN_TARJETA:
                counterDownTimerMenus();
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_SIN_TARJETA_PAGO_MOVIL, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_BILLETERAS, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_SIN_TARJETA_CUENTA_ST, R.drawable.mainitemunclick));
                break;
            case DefinesBANCARD.ITEM_CUOTAS_SIN_TARJETA:
                counterDownTimerMenus();
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_C_SIN_TARJETA_PAGO_MOVIL, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_C_SIN_TARJETA_ZIMPLE, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_C_SIN_TARJETA_CUENTA_ST, R.drawable.mainitemunclick));
                break;
            case DefinesBANCARD.ITEM_RECARGAS:
                counterDownTimerMenus();
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_RECARGA_CARGA_ZIMPLE, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_RECARGA_GIRO_ZIMPLE, R.drawable.mainitemunclick));
                itemMenu.add(new menuItemsModelo(DefinesBANCARD.ITEM_RECARGA_EXTRACCION_ZIMPLE, R.drawable.mainitemunclick));
                break;
            default:
                break;
        }
        return itemMenu;
    }


    private void deleteTimerDisplay() {
        if (countDownTimerDisplay != null) {
            countDownTimerDisplay.cancel();
            countDownTimerDisplay = null;
        }
    }

    private void counterDownTimerMenus() {
        if (countDownTimerMenus != null) {
            countDownTimerMenus.cancel();
            countDownTimerMenus = null;
        }
        countDownTimerMenus = new CountDownTimer(30000, 5000) {
            public void onTick(long millisUntilFinished) {
                Logger.info("onTick", "init onTick countDownTimer Home");
            }

            public void onFinish() {
                Logger.info("onTick", "finish onTick countDownTimer Homet");
                deleteTimerMenus();
                deleteTimerDisplay();
                finish();
            }
        }.start();
    }

    private void deleteTimerMenus() {
        if (countDownTimerMenus != null) {
            countDownTimerMenus.cancel();
            countDownTimerMenus = null;
        }
    }

    @Override
    public void onBackPressed() {
        if (menu.equals(DefinesBANCARD.ITEM_CONFIG_ACCEPTER)) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        if (menu.equals(DefinesBANCARD.ITEM_COMERCIO) && positionpager != 0 && viewPager != null) {
            viewPager.setCurrentItem(0);
            positionpager = 0;
            return;
        }
        if (!menu.equals(DefinesBANCARD.MENU_PRINCIPAL)) {
            deleteTimerDisplay();
            super.onBackPressed();

            if (isClose && !isDisplay)
                finish();
            else {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        deleteTimerMenus();
                        deleteTimerDisplay();
                    }
                });
            }
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        contFallback = 0;
        isInit = PolarisUtil.isInitPolaris(menus.this);
    }

}

