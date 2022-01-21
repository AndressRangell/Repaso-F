package com.newpos.libpay.presenter;

import com.android.desert.keyboard.InputInfo;
import com.android.desert.keyboard.InputManager;
import com.flota.adaptadores.ModeloMensajeConfirmacion;
import com.flota.adapters.model.ButtonModel;
import com.flota.logscierres.LogsCierresModelo;
import com.flota.screen.inputs.ScreenCard;
import com.flota.screen.inputs.ScreenPin;
import com.flota.screen.inputs.ScreenSelectFromTwoOptions;
import com.flota.screen.inputs.base.ScreenInput;
import com.flota.screen.result.ScreenAccountBalance;
import com.flota.screen.result.base.ScreenResult;
import com.flota.transactions.Billeteras.Billeteras;
import com.newpos.libpay.device.card.CardInfo;
import com.newpos.libpay.device.pinpad.PinInfo;
import com.newpos.libpay.trans.translog.TransLogData;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhouqiang on 2017/3/15.
 *
 * @author zhouqiang
 * 交易UI接口类
 */

public interface TransUI {

    /**
     * Obtenga la interfaz de IU de entrada externa(solicite al usuario que ingrese la tarjeta),
     * Muestra en pantalla una vista que retorne informaci&oacute;n en el modelo {@link CardInfo CardInfo}.
     * <blockquote>
     * <br/> Los siguientes son {@link ScreenCard.TypeScreen tipos de vista} provenientes del enum:
     * <br/> &bull; CARD_PROCESSING,
     * <br/> &bull; CARD_HANDLING,
     * <br/> &bull; CARD_FALLBACK
     * </blockquote>
     *
     * @param screen Modelo de clase {@link ScreenCard ScreenCard}.
     * @return CardInfo
     * @author Esneider
     */
    CardInfo showScreenCard(ScreenCard screen);

    /**
     * Obtenga la interfaz de IU de entrada externa(solicite al usuario que ingreso de PIN),
     * Muestra en pantalla una vista que retorne informaci&oacute;n en el modelo {@link PinInfo PinInfo}.
     * <blockquote>
     * <br/> Los siguientes son {@link ScreenPin.TypeScreen tipos de vista} provenientes del enum:
     * <br/> &bull; OFFLINE,
     * <br/> &bull; ONLINE,
     * <br/> &bull; ONLINE_DUKPT
     * </blockquote>
     *
     * @param screen Modelo de clase {@link ScreenPin ScreenPin}.
     * @return PinInfo
     * @author Esneider
     */
    PinInfo showScreenPin(ScreenPin screen);

    /**
     * Obtenga la interfaz de IU de entrada externa(solicite al usuario que ingrese informaci&oacute;n),
     * Muestra en pantalla una vista que retorne informaci&oacute;n en el modelo {@link InputInfo InputInfo}.
     * <blockquote>
     * <br/> Clases disponibles:
     * <br/> &bull; {@link com.flota.screen.inputs.ScreenEnterNumericalData ScreenEnterNumericalData}.
     * <br/> &bull; {@link com.flota.screen.inputs.ScreenSelectProduct ScreenSelectProduct}.
     * <br/> &bull; {@link ScreenSelectFromTwoOptions ScreenSelectTwoOptions}.
     * </blockquote>
     *
     * @param screen Modelo de clase que herede la clase abstracta {@link ScreenInput}.
     * @return InputInfo
     * @author Esneider
     */
    InputInfo showScreenInput(ScreenInput screen);

    /**
     * Obtenga la interfaz de IU (muestre información o resultado),
     * <blockquote>
     * <br/> Clases disponibles:
     * <br/> • {@link ScreenAccountBalance ScreenResultAccountBalance}.
     * </blockquote>
     *
     * @param screen Modelo de clase que herede la clase abstracta {@link ScreenResult}.
     * @author Esneider
     */
    void showScreenResult(ScreenResult screen);

    /**
     * 获取外界输入UI接口(提示用户输入信息)
     *
     * @return return
     */
    InputInfo getOutsideInput(int timeout, InputManager.Mode type, String title, String trx, long amount);

    /**
     * 人机交互显示UI接口(卡号确认)
     *
     * @param cn 卡号
     */
    int showCardConfirm(int timeout, String cn);

    /**
     * @param msg        Mensaje
     * @param btnCancel  Boton cancel
     * @param btnConfirm boton confirmar
     * @return return
     */
    InputInfo showMessageInfo(String title, String msg, String btnCancel, String btnConfirm, int timeout);

    /**
     * @param msg        Mensaje
     * @param btnCancel  Boton cancel
     * @param btnConfirm boton confirmar
     * @return return
     */
    InputInfo showMessageImpresion(String title, String msg, String btnCancel, String btnConfirm, int timeout);

    /**
     * 人机交互显示UI接口(多应用卡片选择)
     *
     * @param timeout Timeout
     * @param list    Lista
     * @return return
     */
    int showCardApplist(int timeout, String[] list);

    /**
     * 人机交互显示UI接口（多语言选择接口）
     *
     * @param timeout Timeout
     * @param langs   Lenguajes
     * @return return
     */
    int showMultiLangs(int timeout, String[] langs);

    /**
     * 人机交互显示UI接口(耗时处理操作)
     *
     * @param timeout Timeout
     * @param status  TransStatus 状态标志以获取详细错误信息
     */
    void handling(int timeout, int status);

    /**
     * 人机交互显示UI接口(耗时处理操作)
     *
     * @param timeout Timeout
     * @param status  TransStatus 状态标志以获取详细错误信息
     */
    void handling(int timeout, int status, String title);

    /**
     * 人机交互显示UI接口(耗时处理操作)
     *
     * @param timeout Timeout
     * @param mensaje TransStatus 状态标志以获取详细错误信息
     */
    void handling(int timeout, String mensaje, String title);

    /**
     * 人机交互显示UI接口
     *
     * @param timeout Timeout
     * @param logData 详细交易日志
     */
    int showTransInfo(int timeout, TransLogData logData);

    /**
     * 交易成功处理结果
     *
     * @param code Codigo
     */
    void trannSuccess(int timeout, int code, String... args);

    /**
     * 人机交互显示UI接口(显示交易出错错误信息)
     *
     * @param errcode 实际代码错误返回码
     */
    void showError(int timeout, int errcode, boolean isIconoWfi);


    /**
     * 人机交互显示UI接口(显示交易出错错误信息)
     *
     * @param errcode 实际代码错误返回码
     */
    void showError(int timeout, String encabezado, int errcode, boolean isIconoWfi, boolean aprobado);

    /**
     * 人机交互显示UI接口(显示交易出错错误信息)
     *
     * @param errcode 实际代码错误返回码
     */
    void showError(int timeout, String encabezado, String errcode, boolean isIconoWfi, boolean aprobado);

    /**
     * @param timeout Timeout
     * @param title   Titulo
     * @return return
     */
    InputInfo showTypeCoin(int timeout, final String title);

    /**
     * @param timeout Timeout
     * @param title   Titulo
     * @return return
     */
    InputInfo showInputUser(int timeout, final String title, final String label2, int min, int max);

    /**
     * @param errcode Error code
     */
    void toasTrans(int errcode, boolean sound, boolean isErr);

    void toasTrans(String errcode, boolean sound, boolean isErr);

    /**
     * @param timeout Timeout
     * @param title   Titulo
     * @param label   Mensaje
     * @return return
     */
    InputInfo showConfirmAmount(int timeout, final String title, final String label, String amnt, boolean isHTML);

    /**
     * @param message Mensaje
     */
    void showMessage(String message, boolean transaccion);

    /**
     * @param img Imagen
     */
    void showCardImg(String img);

    /**
     * @param timeout   Timeout
     * @param title     Titulo
     * @param transType Tipo Transaccion
     * @return return
     */
    InputInfo showSignature(int timeout, String title, String transType);

    /**
     * @param timeout   Timeout
     * @param title     Titulo
     * @param transType Tipo Transaccion
     * @return return
     */
    InputInfo showList(int timeout, String title, String transType, final ArrayList<String> listMenu, int id);

    InputInfo showVentaCuotas(int timeout);

    InputInfo showIngresoDataNumerico(int timeout, String tipoIngreso, String title, int longitudMaxima, String trx, long amount);

    InputInfo showIngresoDataNumerico(int timeout, String tipoIngreso, String mensaje, String title, int longitudMaxima, String trx, long amount);

    InputInfo showSeleccionTipoDeCuenta(int timeout);

    void showImprimiendo(int timeout);

    InputInfo showResult(int timeout, boolean aprobada, boolean isIconoWifi, boolean opciones, String mensajeHost);

    InputInfo showPlanVentaButtonView(int timeout, ButtonModel[] botones, String monto);

    InputInfo showIngresoCuota(int timeout, String valor, String titulo, String subtitulo);

    void showFinish();

    InputInfo showBotones(int timeout, String titulo, ArrayList<ButtonModel> cuentas);

    InputInfo showMensajeConfirmacion(int timeout, ModeloMensajeConfirmacion modelo);

    void showVerSaldoCuenta(int timeout, long saldo);

    InputInfo showResultCierre(int timeout, LogsCierresModelo cierresModelo);

    void showContacLessInfo(boolean finish);

    InputInfo showListBilleteras(int timeout, final String titulo, final List<Billeteras> billeterasList);

    /**
     * 人机交互显示UI接口(显示交易出错错误信息)
     *
     * @param timeout Tiempo de duracion de la vista
     * @param title   Header del la vista
     * @param message Contenido de la vista
     * @param ico     Icono a mostar
     * @param metodo  Nombre del metodo que llama la vista
     */
    void showMsgInfo(int timeout, String title, String message, int ico, String metodo);
}
