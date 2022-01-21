package com.flota.transactions.venta;

import static com.flota.menus.menus.idAcquirer;
import static com.newpos.libpay.device.printer.PrintManager.printExtras;
import static cn.desert.newpos.payui.master.MasterControl.KEY_CODE_PRODUCT;
import static cn.desert.newpos.payui.master.MasterControl.KEY_DESCRIPTION_PRODUCT;
import static cn.desert.newpos.payui.master.MasterControl.KEY_QUANTITY_PRODUCT;

import android.content.Context;

import com.android.desert.keyboard.InputInfo;
import com.flota.adapters.model.ProductModel;
import com.flota.screen.inputs.ScreenEnterNumericalData;
import com.flota.screen.inputs.ScreenSelectFromTwoOptions;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.desert.newpos.payui.master.MasterControl;

public class Venta extends FinanceTrans implements TransPresenter {

    private static final String TAG = "Venta";

    /**
     * 金融交易类构造
     *
     * @param ctx        Context
     * @param transEname Nombre Transaccion
     * @param p          Parametros
     */
    public Venta(Context ctx, String transEname, TransInputPara p) {
        super(ctx, transEname);
        para = p;
        transUI = para.getTransUI();
        isReversal = true;
        isProcPreTrans = true;
        isSaveLog = true;
        isDebit = true;
        typeCoin = CommonFunctionalities.tipoMoneda()[1];
        TransEName = transEname;
        host_id = idAcquirer;
    }

    @Override
    public ISO8583 getISO8583() {
        return null;
    }

    @Override
    public void start() {

        Logger.flujo(TAG, "Inicio transaccion Venta");

        if (!checkBatchAndSettle(true, false))
            return;

        Logger.flujo(TAG, " Solicitud de tarjeta");

        int cardProcess = cardProcess(INMODE_MAG, "VENTA FLOTA",
                "Deslice tarjeta cliente", false);
        if (cardProcess == 1) {
            Logger.flujo(TAG, "Lectura de tarjeta exitosa, cardProcess = " + cardProcess);
            prepareOnline();
        }

    }

    private void prepareOnline() {
        int res = checkIps(context);

        ProcCode = "800000";

        Logger.error(TAG, "prepareOnline: res " + res);
        if (res != Tcode.T_success) {
            transUI.showError(timeout, res, true);
            return;
        }

        if (!getClientPassword("Venta Flota", TAG)) {
            return;
        }

        final String playeroPassword = getPlayeroPassword("Venta Flota", TAG);
        if (playeroPassword != null) {
            DataAdicional.addOrUpdate(1, playeroPassword);
        } else {
            return;
        }

        if (NroBoleta != null) {
            DataAdicional.addOrUpdate(9, NroBoleta);
        }

        String kilometres = getKilometres();
        if (kilometres != null) {
            DataAdicional.addOrUpdate(2, kilometres);
        } else {
            return;
        }

        Map<String, String> products = getProduct(getProducts1());
        if (products.size() != 0) {
            DataAdicional.addOrUpdate(3, products.get(KEY_CODE_PRODUCT));
            DataAdicional.addOrUpdate(4, products.get(KEY_QUANTITY_PRODUCT));
            printExtras.put(KEY_DESCRIPTION_PRODUCT + "1", products.get(KEY_DESCRIPTION_PRODUCT));
        } else {
            return;
        }

        final String optionSelect = selectAnotherProduct();
        if (optionSelect == null)
            return;
        if (optionSelect.equals(MasterControl.SELECT_OPTION_1)) {
            products = getProduct(getProducts2());
            if (products.size() != 0) {
                DataAdicional.addOrUpdate(5, products.get(KEY_CODE_PRODUCT));
                DataAdicional.addOrUpdate(6, products.get(KEY_QUANTITY_PRODUCT));
                printExtras.put(KEY_DESCRIPTION_PRODUCT + "2", products.get(KEY_DESCRIPTION_PRODUCT));
            } else {
                return;
            }
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
        printExtras.clear();
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

    private String selectAnotherProduct() {
        InputInfo info = transUI.showScreenInput(new ScreenSelectFromTwoOptions(timeout)
                .setTitle("Nuevo producto")
                .setQuestion("¿Seleccionar segundo\nproducto?")
                .setTextOption1("sí")
                .setTextOption2("no")
        );
        if (info.isResultFlag()) {
            final String optionSelect = info.getResult();
            Logger.debug(TAG, "start: selectAnotherProduct: optionSelect=" + optionSelect);
            return optionSelect;
        } else {
            transUI.showFinish();
        }
        return null;
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

    private List<ProductModel> getProducts2() {
        List<ProductModel> products = new ArrayList<>();
        products.add(new ProductModel("4001813", "DIESEL PODIUM S-11"));
        products.add(new ProductModel("4002147", "DIESEL EURO 6 S-11"));
        products.add(new ProductModel("4002148", "DIESEL EURO 5 S-51"));
        products.add(new ProductModel("4002142", "NAFTA TURBO AD"));
        products.add(new ProductModel("4002141", "NAFTA SUPER AD"));
        products.add(new ProductModel("4002146", "NAFTA GRID 95"));
        products.add(new ProductModel("4002145", "NAFTA PRIX 91"));
        products.add(new ProductModel("4001810", "ETANOL HIDRATADO"));
        products.add(new ProductModel("4001826", "KEROSENE"));
        products.add(new ProductModel("4201543", "GLP AUTOMOTRIZ"));
        products.add(new ProductModel("4002149", "NAFTA PODIUM 100"));
        return products;
    }

}
