package com.flota.transactions.venta;

import static cn.desert.newpos.payui.master.MasterControl.KEY_CODE_PRODUCT;
import static cn.desert.newpos.payui.master.MasterControl.KEY_QUANTITY_PRODUCT;

import android.content.Context;
import android.util.Log;

import com.android.desert.keyboard.InputInfo;
import com.flota.adapters.model.ProductModel;
import com.flota.screen.inputs.ScreenEnterNumericalData;
import com.flota.screen.inputs.ScreenSelectProduct;
import com.flota.screen.inputs.methods.FormatInput;
import com.flota.screen.inputs.methods.NumericalData;
import com.flota.transactions.DataAdicional.DataAdicional;
import com.flota.transactions.common.CommonFunctionalities;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.newpos.libpay.Logger;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.trans.finace.FinanceTrans;
import com.wposs.flota.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VentaManual extends FinanceTrans implements TransPresenter {

    private static final String TAG = "VentaManual";

    /**
     * 金融交易类构造
     *
     * @param ctx        Context
     * @param transEname Nombre Transaccion
     * @param p          Parametros
     */
    public VentaManual(Context ctx, String transEname, TransInputPara p) {
        super(ctx, transEname);
        para = p;
        TransEName = transEname;
        isReversal = true;
        isProcPreTrans = true;
        isSaveLog = true;
        isDebit = true;
        typeCoin = CommonFunctionalities.tipoMoneda()[1];
        transUI = para.getTransUI();
    }

    @Override
    public ISO8583 getISO8583() {
        return null;
    }

    @Override
    public void start() {

        Logger.flujo(TAG, "Inicio transaccion Venta Manual");

        if (!checkBatchAndSettle(true, false))
            return;

        Logger.flujo(TAG, " Solicitud de tarjeta");

        int cardProcess = cardProcess(INMODE_MAG, "VENTA MANUAL",
                "Deslice tarjeta supervisor", false);
        if (cardProcess == 1) {
            Logger.flujo(TAG, "Lectura de tarjeta exitosa, cardProcess = " + cardProcess);
            prepareOnline();
        }
    }

    private void prepareOnline() {
        int res = checkIps(context);

        ProcCode = "810000";

        Logger.error(TAG, "prepareOnline: res " + res);
        if (res != Tcode.T_success) {
            transUI.showError(timeout, res, true);
            return;
        }
        String kilometres = getKilometres();
        if (kilometres != null) {
            DataAdicional.addOrUpdate(2, kilometres);
        } else {
            return;
        }

        String numberCard = getNumberCard();
        if (numberCard != null) {
            Pan = numberCard;
        } else {
            return;
        }

        Map<String, String> products = getProduct(getProducts1());
        if (products.size() != 0) {
            DataAdicional.addOrUpdate(3, products.get(KEY_CODE_PRODUCT));
            DataAdicional.addOrUpdate(4, products.get(KEY_QUANTITY_PRODUCT));
        } else {
            return;
        }

        final String supervisedPassword = getSupervisedPassword("Venta Manual", TAG);
        if (supervisedPassword != null) {
            DataAdicional.addOrUpdate(7, supervisedPassword);
        } else {
            return;
        }

        String date = getDate();
        if (date != null) {
            DataAdicional.addOrUpdate(8, date);
        } else {
            return;
        }

        setDatas(inputMode);
        if (inputMode == ENTRY_MODE_MAG) {
            retVal = OnlineTrans(emv);
        } else {
            retVal = OnlineTrans(null);
        }

        clearPan();

        if (retVal == 0) {
            transUI.showResult(timeout, true, false, true, DataAdicional.getField(22));
        } else {
            transUI.showError(timeout, retVal, true);
        }

    }

    private String getKilometres() {
        InputInfo info = transUI.showScreenInput(new ScreenEnterNumericalData(timeout)
                .setTitle("Kilometraje del vehículo")
                .setMessage("Ingresar <b>kilómetros</b> y presiona <b>OK</b> para continuar")
                .setInput(new NumericalData(FormatInput.NUMBER, 1, 15)
                        .setHint("Kilómetros")
                        .setSuffix("Km")
                )
        );

        if (info.isResultFlag()) {
            String kilometres = info.getResult();
            Logger.debug(TAG, "start: kilometres=" + kilometres);
            return kilometres;
        } else {
            transUI.showFinish();
        }
        return null;
    }

    private Map<String, String> getProduct(List<ProductModel> products) {
        InputInfo info = transUI.showScreenInput(new ScreenSelectProduct(timeout, products));

        if (info.isResultFlag()) {
            String mapStr = info.getResult();
            Logger.debug(TAG, "start: mapStr=" + mapStr);
            try {
                return new Gson().fromJson(mapStr, new TypeToken<Map<String, String>>() {
                }.getType());
            } catch (JsonSyntaxException e) {
                e.printStackTrace();
            }
        } else {
            transUI.showFinish();
        }
        return new HashMap<>();
    }

    private List<ProductModel> getProducts1() {
        List<ProductModel> products = new ArrayList<>();
        products.add(new ProductModel("4001812", "DIESEL PODIUM S-10"));
        products.add(new ProductModel("4002146", "DIESEL EURO 6 S-10"));
        products.add(new ProductModel("4002147", "DIESEL EURO 5 S-50"));
        products.add(new ProductModel("4002141", "NAFTA TURBO AD.AUT"));
        products.add(new ProductModel("4002140", "NAFTA SUPER AD.AUT"));
        products.add(new ProductModel("4002145", "NAFTA GRID 94"));
        products.add(new ProductModel("4002144", "NAFTA PRIX 90"));
        products.add(new ProductModel("4001809", "ETANOL HIDRATADO PNA"));
        products.add(new ProductModel("4001825", "KEROSENE COMUN"));
        products.add(new ProductModel("4201542", "GLP AUTOMOTRIZ COMER"));
        products.add(new ProductModel("4002148", "NAFTA PODIUM 100 AD"));
        return products;
    }

    private String getNumberCard() {
        InputInfo info = transUI.showScreenInput(new ScreenEnterNumericalData(timeout)
                .setTitle("Numero de tarjeta")
                .setMessage("Ingresar el <b>número de tarjeta</b> y presiona <b>OK</b> para continuar")
                .setInput(new NumericalData(FormatInput.CARD, 16)
                        .setMsgErrorValidation("Ingrese el número de tarjeta.")
                        .setIcon(context.getDrawable(R.drawable.ic_card))
                        .setKeyboardWith000(false)
                        .setHint("####-####-####-####")
                )
        );

        if (info.isResultFlag()) {
            String numberCard = info.getResult();
            Log.d(TAG, "start: numberCard=" + numberCard);
            return numberCard;
        } else {
            transUI.showFinish();
        }
        return null;
    }

    private String getDate() {
        InputInfo info = transUI.showScreenInput(new ScreenEnterNumericalData(timeout)
                .setTitle("Fecha")
                .setMessage("Ingresar la <b>fecha</b> y presiona <b>OK</b> para continuar")
                .setInput(new NumericalData(FormatInput.DATE, 6)
                        .setMsgErrorValidation("Ingrese la fecha.")
                        .setKeyboardWith000(false)
                        .setHint("DD/MM/AA")
                )
        );
        if (info.isResultFlag()) {
            String numberCard = info.getResult();
            Log.d(TAG, "start: numberCard=" + numberCard);
            return numberCard;
        } else {
            transUI.showFinish();
        }
        return null;
    }

}
