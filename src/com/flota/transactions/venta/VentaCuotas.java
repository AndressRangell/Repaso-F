package com.flota.transactions.venta;

import android.content.Context;

import com.android.desert.keyboard.InputInfo;
import com.flota.defines_bancard.DefinesBANCARD;
import com.flota.transactions.DataAdicional.DataAdicional;
import com.flota.transactions.common.CommonFunctionalities;
import com.newpos.libpay.Logger;
import com.newpos.libpay.helper.iso8583.ISO8583;
import com.newpos.libpay.presenter.TransPresenter;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.trans.finace.FinanceTrans;

import static com.flota.defines_bancard.DefinesBANCARD.INGRESO_CODIGO;
import static com.flota.defines_bancard.DefinesBANCARD.INGRESO_MONTO;
import static com.flota.menus.menus.idAcquirer;

public class VentaCuotas extends FinanceTrans implements TransPresenter {

    private String tipoVenta;
    private String contrapartida;

    public VentaCuotas(Context ctx, String transEname, String tipoVenta, TransInputPara p) {
        super(ctx, transEname);
        init(transEname, p);
        this.tipoVenta = tipoVenta;
    }

    private void init(String transEname, TransInputPara p) {
        para = p;
        transUI = para.getTransUI();
        isReversal = true;
        isProcPreTrans = true;
        isSaveLog = true;
        isDebit = false;
        TransEName = transEname;
        host_id = idAcquirer;
    }

    @Override
    public void start() {
        if (!checkBatchAndSettle(true, false))
            return;
        ProcCode = "000000";
        switch (tipoVenta) {
            case DefinesBANCARD.ITEM_CUOTAS_TARJETA:
                if (cardProcess(INMODE_IC | INMODE_MAG, false) == 0)
                    return;

                if (getPlanCuotas()) {
                    if (!prepareOnline())
                        return;
                }
                break;
            case DefinesBANCARD.ITEM_CUOTAS_SERVICIOS:
                contrapartida = getContrapartida(INGRESO_CODIGO, "Codigo:", 33);
                if (!contrapartida.equals("null")) {
                    //Almacenar codigo en campo desconocido
                    //Envia transaccion... codigo de proceso desconocido.
                    if (prepareOnline()) {

                    }
                }
                break;
            case DefinesBANCARD.ITEM_CUOTAS_SIN_CONTACTO:
                if (getPlanCuotas()) {
                    if (getMonto()) {
                        if (cardProcess(INMODE_IC | INMODE_MAG | INMODE_NFC, false) == 0)
                            return;

                        if (!prepareOnline())
                            return;
                    }
                }
                break;
            case DefinesBANCARD.ITEM_C_SIN_TARJETA_PAGO_MOVIL:

                break;
            case DefinesBANCARD.ITEM_C_SIN_TARJETA_ZIMPLE:

                break;
            case DefinesBANCARD.ITEM_C_SIN_TARJETA_CUENTA_ST:

                break;
            case DefinesBANCARD.ITEM_C_SIN_TARJETA_EXT_ZIMPLE:

                break;
        }

    }

    private boolean getPlanCuotas() {
        boolean ret = false;
        InputInfo inputInfo = transUI.showVentaCuotas(timeout);
        if (inputInfo.isResultFlag()) {
            String[] data = inputInfo.getResult().split("@");
            if (!data[0].equals("")) {
                if (!data[1].equals("")) {
                    String sPlanCuotas = data[0];
                    String sNumCuotas = data[1];
                    DataAdicional.addOrUpdate(14, sPlanCuotas);
                    DataAdicional.addOrUpdate(45, sNumCuotas);
                    ret = true;
                }
            }
        } else {
            transUI.showError(timeout, inputInfo.getErrno(), false);
        }
        return ret;
    }

    @Override
    public ISO8583 getISO8583() {
        return null;
    }

    private String getContrapartida(String tipoIngreso, String titulo, int longitud) {
        String ret = "null";
        InputInfo inputInfo = transUI.showIngresoDataNumerico(timeout, tipoIngreso, titulo, longitud, "", 0);
        if (inputInfo.isResultFlag()) {
            ret = inputInfo.getResult();
        } else {
            transUI.showError(timeout, inputInfo.getErrno(), false);
        }
        return ret;
    }

    private boolean getMonto() {
        boolean ret = false;
        InputInfo inputInfo = transUI.showIngresoDataNumerico(timeout, INGRESO_MONTO, "Monto:", 9, "", 0);
        if (inputInfo.isResultFlag()) {
            Amount = Long.parseLong(inputInfo.getResult());
            para.setAmount(Amount);
            para.setOtherAmount(0);
            ret = true;

        } else {
            transUI.showError(timeout, inputInfo.getErrno(), false);
        }
        return ret;
    }

    /**
     * 准备联机
     */
    private boolean prepareOnline() {

        Amount = para.getAmount();
        if (Amount == 0) {
            if (!getMonto()) {
                transUI.showError(timeout, Tcode.T_user_cancel_input, false);
                return false;
            }
        }

        ProcCode = CommonFunctionalities.getProCode();

        if (!requestPin()) {
            return false;
        }

        if (retVal == 0) {

            transUI.handling(timeout, Tcode.Status.connecting_center, TransEName);
            setDatas(inputMode);
            if (inputMode == ENTRY_MODE_ICC || inputMode == ENTRY_MODE_NFC) {
                retVal = OnlineTrans(emv);
            } else {
                retVal = OnlineTrans(null);
            }
            Logger.debug("SaleTrans>>OnlineTrans=" + retVal);
            clearPan();
            if (retVal == 0) {
                String campo22 = DataAdicional.getField(22);

                if (campo22 != null) {
                    transUI.trannSuccess(timeout, Tcode.Status.sale_succ, campo22.trim());
                    DataAdicional.addOrUpdate(22, null);
                } else {
                    transUI.trannSuccess(timeout, Tcode.Status.sale_succ, "");
                }

                return true;

            } else {
                transUI.showError(timeout, retVal, true);
                return false;
            }

        } else {
            transUI.showError(timeout, retVal, true);
            return false;
        }
    }
}
