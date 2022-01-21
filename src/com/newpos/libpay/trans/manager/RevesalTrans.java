package com.newpos.libpay.trans.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.flota.inicializacion.configuracioncomercio.APLICACIONES;
import com.flota.transactions.echotest.EchoTest;
import com.newpos.libpay.Logger;
import com.newpos.libpay.device.pinpad.PinpadManager;
import com.newpos.libpay.presenter.TransUI;
import com.newpos.libpay.trans.Tcode;
import com.newpos.libpay.trans.Trans;
import com.newpos.libpay.trans.TransInputPara;
import com.newpos.libpay.trans.translog.TransLog;
import com.newpos.libpay.trans.translog.TransLogData;
import com.newpos.libpay.utils.ISOUtil;
import com.newpos.libpay.utils.PAYUtils;
import com.wposs.flota.R;

import static android.content.Context.MODE_PRIVATE;
import static cn.desert.newpos.payui.master.MasterControl.mcontext;
import static com.flota.defines_bancard.DefinesBANCARD.POLARIS_APP_NAME;
import static com.flota.defines_bancard.DefinesBANCARD.PREF_AMOUNT;
import static com.flota.defines_bancard.DefinesBANCARD.PREF_CARGO;
import static com.flota.defines_bancard.DefinesBANCARD.PREF_DATE;
import static com.flota.defines_bancard.DefinesBANCARD.PREF_METODO;
import static com.flota.defines_bancard.DefinesBANCARD.PREF_TIME;
import static com.flota.defines_bancard.DefinesBANCARD.PREF_TRACE;

/**
 * 冲正交易实体类
 *
 * @author zhouqiang
 */
public class RevesalTrans extends Trans {

    String clase = "RevesalTrans.java";
    private final boolean reversoDespuesdeTrans;

    int timeOut;

    public RevesalTrans(Context ctx, String transEname, int timeOut) {
        super(ctx, transEname);
        isUseOrgVal = true; // 使用原交易的60.1 60.3
        iso8583.setHasMac(false);
        isTraceNoInc = false; // 冲正不需要自增流水号
        this.timeOut = timeOut;

        /* reversoDespuesdeTrans :
            true : DespuesDeTransaccion
            false: AntesDeTransaccion
        */

        Logger.comunicacion( clase, "CLASE REVERSAL TRANS ---");

        /*Reverso enviado justo despues de enviar la transaccion y no obtener respuesta*/
        // reversoDespuesdeTrans = true;//ISOUtil.stringToBoolean(comercios.getREVERSO());
        APLICACIONES aplicaciones = APLICACIONES.getSingletonInstanceAppActual(POLARIS_APP_NAME);

        Logger.comunicacion( clase, "APLICACIONES.getSingletonInstanceAppActua " + aplicaciones);

        if (aplicaciones != null) {
            reversoDespuesdeTrans = aplicaciones.isReverso();
            Logger.comunicacion( clase, "APLICACIONES.getSingletonInstanceAppActua if " +
                    "reversoDespuesdeTrans :\n" +
                    "            true : DespuesDeTransaccion\n" +
                    "            false: AntesDeTransaccion tipo Reverso Habilitado = >" + reversoDespuesdeTrans);
        } else {
            Logger.comunicacion( clase, "APLICACIONES.getSingletonInstanceAppActua  else ");
            reversoDespuesdeTrans = true;
        }
    }

    private void setFields(TransLogData data) {

        iso8583.setField(0, "0400");

        if (data.getProcCode() != null) {
            iso8583.setField(3, data.getProcCode());
        }
        if (data.getTraceNo() != null) {
            iso8583.setField(11, data.getTraceNo());
        }
        if (data.getLocalTime() != null) {
            iso8583.setField(12, data.getLocalTime());
        }
        if (data.getLocalDate() != null) {
            iso8583.setField(13, data.getLocalDate());
        }
        if (data.getEntryMode() != null) {
            iso8583.setField(22, data.getEntryMode());
        }
        if (data.getNii() != null) {
            iso8583.setField(24, data.getNii());
        }
        if (data.getTrack2() != null) {
            iso8583.setField(35, PinpadManager.getInstance().encryptTrack2(data.getTrack2()));
        }
        if (data.getTermID() != null) {
            iso8583.setField(41, data.getTermID());
        }
        if (data.getMerchID() != null) {
            iso8583.setField(42, data.getMerchID());
        }
        if (data.getCurrencyCode() != null) {
            iso8583.setField(49, data.getCurrencyCode());
        }
        if (data.getPIN() != null) {
            iso8583.setField(52, data.getPIN());
        }
        if (data.getField61() != null) {
            iso8583.setField(61, data.getField61());
        }
        if (data.getField62() != null) {
            String hexCargo = ISOUtil.asciiToHex(data.getField62());
            iso8583.setField(62, hexCargo);
        }
    }


    private int sendReversal(TransUI transUI) {
        int rtn = Tcode.T_reversal_fail;
        TransLogData data = TransLog.getReversal();

        if (data != null) {
            String procCodeReversal = data.getProcCode();
            Logger.debug(clase, "sendReversal: procCodeReversal --> " + procCodeReversal);
            switch (procCodeReversal) {
                case "920000": // REVERSO NORMAL
                default:
                    setFields(data);
                    break;
            }
            //setFieldsSale(data);
            this.transUI = transUI;
            rtn = OnLineTrans();
            switch (rtn) {
                case Tcode.T_success:
                    RspCode = iso8583.getfield(39);
                    switch (RspCode) {
                        case "00":
                        case "12":
                        case "25":
                            return rtn;
                        default:
                            data.setRspCode("06");
                            TransLog.saveReversal(data);
                            return Tcode.T_receive_refuse;
                    }
                case Tcode.T_package_mac_err:
                    Logger.comunicacion( clase, "T_package_mac_err ");
                    data.setRspCode("A0");
                    TransLog.saveReversal(data);
                    break;
                case Tcode.T_receive_err:
                    Logger.comunicacion( clase, "T_receive_err");
                    data.setRspCode("08");
                    TransLog.saveReversal(data);
                    break;
                case Tcode.T_package_illegal:
                    Logger.comunicacion( clase, "T_package_illegal");
                    data.setRspCode("08");
                    TransLog.saveReversal(data);
                    break;
                default:
                    Logger.debug("Revesal result :" + rtn);
                    Logger.comunicacion( clase, "Revesal result :" + rtn);
                    break;
            }
        }

        return rtn;
    }

    public static boolean isReversalPending(String metodo) {
        TransLogData revesalData = TransLog.getReversal();
        if (revesalData != null) {
            Logger.reversal("ReversalTrans", "Existe revesa pendiente", metodo + "-isReversalPending");
            return true;
        }
        return false;
    }


    /**
     * @return : true/false
     */
    private boolean EchoTestReverso(TransUI transUI) {
        TransInputPara paraEcho = new TransInputPara();
        paraEcho.setTransUI(transUI);
        paraEcho.setTransType(Trans.Type.ECHO_TEST);
        paraEcho.setNeedOnline(true);
        paraEcho.setNeedPrint(false);
        paraEcho.setNeedPass(false);
        paraEcho.setEmvAll(false);

        EchoTest echoTest = new EchoTest(context, Trans.Type.ECHO_TEST, paraEcho, false);
        int rta = echoTest.sendEchoTest();
        Logger.comunicacion( clase, "sendEchoTest : " + rta);
        if (rta == 0) {
            return true;
        }
        return false;
    }


    /**
     * sendReversal_retries : Asegurarse que exista reverso antes de llamar este metodo.
     *
     * @param transUI : vistas
     * @return - 0 : Reverso evacuado exitosamente
     * - T_envio_fallido_reverso_fail
     * - T_socket_err
     * - T_send_err
     * - T_reversal_fail
     * - T_reversal_fail_EchoOK : Cuando hay comunicacion (comprobada con Echo Test) y no se obtiene respuesta del reverso
     */
    private int sendReversal_retries(TransUI transUI) {
        boolean IsPending = isReversalPending("ReversalTrans-sendReversal_retries");
        int reintentos = 2;
        int contEcho = 0;

        Logger.comunicacion( clase, "isReversalPending :" + IsPending);

        if (IsPending) {

            Logger.comunicacion( clase, "sendReversal_retries : Pendiente");

            for (int i = 0; i < reintentos; i++) {

                transUI.handling(timeOut, "REVERSANDO TRANSACCIÓN", "ENVIANDO REVERSA [ Int " + (i + 1) + " ]");

                Logger.comunicacion( clase, "sendReversal_retries : Pendiente");

                int rtn = sendReversal(transUI);

                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Logger.exception(clase, e);
                    e.printStackTrace();
                }

                switch (rtn) {
                    case Tcode.T_success:
                        return rtn;

                    default:// Tcode.T_socket_err - Tcode.T_send_err: - Tcode.T_receive_err:, y otros...
                        if (transUI != null) {
                            switch (rtn) {
                                case Tcode.T_package_illegal:
                                case Tcode.T_receive_refuse:
                                    break;
                                default:
                                    transUI.toasTrans(rtn, true, true);
                                    break;
                            }

                        }
                        if (EchoTestReverso(transUI)) {
                            Logger.comunicacion( clase, "Echo Test OK");
                            contEcho++;
                        }
                        if (verificadoIntento(contEcho, reintentos)) {
                            Logger.debug("Verificando reverso con ECHO TEST --> " + rtn);
                            return Tcode.T_reversal_fail_EchoOK;
                        }

                        break;
                }
            }

        }

        return Tcode.T_envio_fallido_reverso_fail;
    }

    boolean verificadoIntento(int contEcho, int reintentos) {
        if (contEcho == reintentos) {
            Logger.debug("verificadoIntento intento Echotest --- " + contEcho + " TransReversal -- " + reintentos);
            Logger.comunicacion( clase, "verificadoIntento intento Echotest " + contEcho + " TransReversal -- " + reintentos);
            return true;
        }
        return false;
    }

    public int analizarReversoDespuesDe(TransUI transUI,String called) {
        boolean IsPending = isReversalPending( clase+"-"+called+"-analizarReversoDespuesDe");
        Logger.comunicacion( clase,  called+"-analizarReversoDespuesDe : ");
        if (IsPending) {
            Logger.reversal(clase, "Hay reversa pendiente", called + "-analizarReversoDespuesDe");
            if (!analisisReversoVSImpresion(TransLog.getReversal(),transUI, called+"-analizarReversoDespuesDe")) {
                //Se debe retornar mensaje de confirmacion para reimprimir voucher
                return Tcode.T_NO_REVERSE;
            }
            Logger.debug("Reverso Pendiente ");

            if (reversoDespuesdeTrans) {// Analisis despues de enviar transaccion
                Logger.comunicacion( clase, "reversoDespuesdeTrans == true");
                Logger.debug("reversoDespuesdeTrans == true");
                try {
                    int rtn = sendReversal_retries(transUI);
                    Logger.comunicacion( clase, "sendReversal_retries : " + rtn);

                    switch (rtn) {
                        case Tcode.T_success:
                            saveReversalInfo(TransLog.getReversal(), called+"-analizarReversoDespuesDe");
                            TransLog.clearReveral();
                            Logger.comunicacion( clase, "sendReversal_retries  if : " + rtn);
                            transUI.showError(timeout, "REVERSA APROBADA", Tcode.T_envio_fallido_reverso_ok, false, true);
                            return Tcode.T_success;


                        default:
                            if (transUI != null) {
                                switch (rtn) {
                                    case Tcode.T_envio_fallido_reverso_fail:
                                    case Tcode.T_reversal_fail_EchoOK:
                                        break;
                                    default:
                                        transUI.toasTrans(rtn, true, true);
                                        break;
                                }

                            }
                            Logger.comunicacion( clase, "sendReversal_retries else : " + rtn);
                            transUI.showError(timeout, "REVERSA PENDIENTE", Tcode.T_envio_fallido_reverso_fail, false, false);
                            return Tcode.T_socket_err;

                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Logger.exception(clase, e);
                    Logger.error("Exception ", e);
                    return Tcode.T_envio_fallido_reverso_fail;
                }


            } else {
                transUI.showError(timeout, "REVERSA PENDIENTE", Tcode.T_envio_fallido_reverso_fail, false, false);
                return Tcode.T_envio_fallido_reverso_fail;
            }//else : NO ENVIAR, configurado como antes de Trans
        }// else : NO Hay Reverso Pendiente

        return Tcode.T_success;
    }

    /**
     * @param logData Contiene los datos de la transaccion, tales como
     *                el numero de cargo, numero de boleta, etc...
     */
    private void saveReversalInfo(TransLogData logData, String metodo) {
        try {
            String revAmount = logData.getAmount().toString();
            String revTraceNro = logData.getTraceNo();
            String revNCargo = logData.getField62();

            SharedPreferences.Editor editor = context.getApplicationContext()
                    .getSharedPreferences("reversalInfo", MODE_PRIVATE).edit();
            editor.putString(PREF_AMOUNT, revAmount);
            editor.putString(PREF_TRACE, revTraceNro);
            editor.putString(PREF_CARGO, revNCargo);
            editor.putString(PREF_METODO, metodo);
            editor.putString(PREF_DATE, PAYUtils.getLocalDate());
            editor.putString(PREF_TIME, PAYUtils.getLocalTime());
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
            Logger.exception("saveReversalInfo " ,e);
            Logger.reversal("Exception : " , e, "saveReversalInfo");
        }
    }

    /**
     * Se valida la base de datos transaccional (tabla Voucher) para comprobar que la transaccion
     * que se intenta reversar no haya emitido voucher.
     * En caso de existir conflicto se elimina el reverso.
     *
     * @return true=No hay inconvenientes con impresion + reverso, continua flujo normal
     * false= ERROR el reversao pendiente corresponde a una transaccion ya impresa
     */
    private boolean analisisReversoVSImpresion(TransLogData logData, TransUI transUI, String metodo) {
        boolean res = true;
        try {
            SharedPreferences transInfo = mcontext.getSharedPreferences("transInfo", MODE_PRIVATE);
            String impAmount = transInfo.getString(PREF_AMOUNT, "");
            String impTraceNro = transInfo.getString(PREF_TRACE, "");
            String impNCargo = transInfo.getString(PREF_CARGO, "");
            String impMetodo = transInfo.getString(PREF_METODO, "");
            String date = transInfo.getString(PREF_DATE, "");
            String time = transInfo.getString(PREF_TIME, "");
            String nroBoleta = transInfo.getString("nroBoleta", "");


            String revAmount = logData.getAmount().toString();
            String revTraceNro = logData.getTraceNo();
            String revNCargo = logData.getField62();
            Logger.error(clase, "printSaleBancard:\n " + revAmount + " " + revTraceNro + " " + revNCargo);
            if (impAmount.equals(revAmount) && impTraceNro.equals(revTraceNro) && impNCargo.equals(revNCargo)) {
                Logger.reversal(clase, "La transaccion que se intenta imprimir ya fue Reversada" +
                        "\nFecha reversa: " + date + " Hora reversa: " + time +
                        "\nMetodo Reversa: " + impMetodo +
                        "\nDato: reverso-impresion" +
                        "\nMonto: " + revAmount + "-" + impAmount +
                        "\nTraceNro: " + revTraceNro + "-" + impTraceNro +
                        "\nNroCargo: " + revNCargo + "-" + impNCargo, metodo);
                showMessage("analisisReversoVSImpresion", nroBoleta, transUI);
                TransLog.clearReveral();
                Logger.copyLogsFile();
                res = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
          Logger.exception(clase, "analisisImpresionVSReverso " + e,metodo);
            Logger.reversal(clase, "Exception : " + e, "analisisReversoVSImpresion");
            Logger.error("analisisImpresionVSReverso: " ,e);
        }
        return res;
    }

    private void showMessage(String metodo, String nroBoleta, TransUI transUI) {
        String titulo = "Importante";
        String mensaje = "Si tuvo inconveniente con la impresión de su voucher" +
                " puede consultarlo en impresion de ultimo ticket";
        if (!nroBoleta.isEmpty())
            mensaje = "Si tuvo inconveniente con la impresión de se voucher " +
                    "puede consultarlo en reporte de ticket por boleta con el numero " + nroBoleta + ", " +
                    "o en impresión de ultimo ticket";
        Logger.error(clase, "metodo: " + metodo + " mensaje: " + mensaje);
        transUI.showMsgInfo(60000, titulo, mensaje, R.drawable.ic_important, metodo);
    }

    /**
     * @param transUI : vistas
     * @return - 0 :
     * Reverso evacuado exitosamente
     * No Existe reverso para evacuacion
     * - T_envio_fallido_reverso_fail
     * - T_socket_err
     * - T_send_err
     * - T_reversal_fail
     */
    public int analizarReversoAntesDe(TransUI transUI,String called) {

        boolean IsPending = isReversalPending(clase+"-"+called+"-analizarReversoAntesDe");
        Logger.comunicacion( clase, called+"-analizarReversoAntesDe : ");
        Logger.debug("analizarReversoAntesDe : ");

        if (IsPending) {
            Logger.reversal(clase, "Hay reversa pendiente", called + "-analizarReversoDespuesDe");
            if (!analisisReversoVSImpresion(TransLog.getReversal(), transUI, called+"-analizarReversoDespuesDe")) {
                //Se debe retornar mensaje de confirmacion para reimprimir voucher
                return Tcode.T_NO_REVERSE;
            }
            Logger.comunicacion( clase, "Reverso Pendiente ");
            Logger.debug("Reverso Pendiente ");
            //RevesalTrans reversalTrans = new RevesalTrans(context, "REVERSAL");
            try {
                int rtn = sendReversal_retries(transUI);

                switch (rtn) {
                    case Tcode.T_success:
                        saveReversalInfo(TransLog.getReversal(),"analizarReversoAntesDe");
                        Logger.comunicacion( clase, "sendReversal : if");
                        TransLog.clearReveral();
                        transUI.toasTrans(Tcode.Status.rev_receive_ok, true, false);
                        break;

                    case Tcode.T_reversal_fail_EchoOK:
                        Logger.debug("rtn :" + rtn);
                        Logger.comunicacion( clase, "rtn : " + rtn);
                        deleteReversal_by_EchoTestOk(transUI);
                        break;

                    default:
                        if (transUI != null && rtn != Tcode.T_envio_fallido_reverso_fail) {
                            transUI.toasTrans(rtn, true, true);
                        }
                        return rtn;
                }
            } catch (Exception e) {
                e.printStackTrace();
                Logger.exception(clase, e);
                Logger.error("Exception ", e);
                return Tcode.T_envio_fallido_reverso_fail;
            }

        }
        return Tcode.T_success;
    }


    /**
     * deleteReversal_by_EchoTestOk
     *
     * @param transUI : Vistas
     * @return : Tcode.T_success
     */
    private void deleteReversal_by_EchoTestOk(TransUI transUI) {

        Logger.comunicacion( clase, "deleteReversal_by_EchoTestOk");
        Logger.debug("deleteReversal_by_EchoTestOk");
        transUI.toasTrans(Tcode.Status.revEerso_borrado_localmente, true, false);
        TransLog.clearReveral();
    }


}
