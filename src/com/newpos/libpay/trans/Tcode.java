package com.newpos.libpay.trans;

/**
 * Created by zhouqiang on 2017/3/26.
 * @author zhouqiang
 * 交易代码集合
 */

public class Tcode {
    public static final int T_success = 0 ;//成功
    public static final int T_socket_err = 101 ;//Socket连接失败、999999999999
    public static final int T_send_err = 102 ;//发送数据失败
    public static final int T_receive_err = 103 ;//接收数据失败
    public static final int T_user_cancel_input = 104 ;//用户取消输入
    public static final int T_invoke_para_err = 105 ;//调用传参错误
    public static final int T_wait_timeout = 106 ;//等待超时
    public static final int T_search_card_err = 107 ;//寻卡错误
    public static final int T_sdk_err = 107 ;//SDK异常
    public static final int T_ic_power_err = 108 ;//IC卡上电错误
    public static final int T_ic_not_exist_err = 109 ;//IC卡不在位
    public static final int T_ped_card_err = 110 ;//计算PINBlock卡号错误
    public static final int T_user_cancel_pin_err = 111 ;//用户取消PIn输入
    public static final int T_print_no_log_err = 112 ;//无此交易日志
    public static final int T_terminal_no_aid = 113 ;//终端无AID
    public static final int T_not_find_trans = 114 ;//未查询到改交易
    public static final int T_trans_is_voided = 115 ;//此交易已撤销
    public static final int T_user_cancel_operation = 116 ;//用户取消操作
    public static final int T_original_trans_can_not_void = 117 ;//原交易不能撤销
    public static final int T_void_card_not_same = 118 ;//撤销交易非原交易卡
    public static final int T_package_mac_err = 119 ;//返回报文MAC校验错误
    public static final int T_package_illegal = 120 ;//返回报文非法处理
    public static final int T_receive_refuse = 121 ;//拒绝
    public static final int T_qpboc_read_err = 122 ;//非接QPBOC读卡信息失败
    public static final int T_sale_tc_err = 123 ;//联机消费不允许脱机
    public static final int T_select_app_err = 124 ;//选择应用失败
    public static final int T_read_app_data_err = 125 ;//读取卡片应用数据失败
    public static final int T_offline_dataauth_err = 126 ;//脱机数据认证失败
    public static final int T_card_holder_auth_err = 127 ;//持卡人认证失败
    public static final int T_terminal_action_ana_err = 128 ;//终端行为分析失败
    public static final int T_must_online = 129 ;//此交易必须联机
    public static final int T_read_ec_amount_err = 130 ;//读取电子现金余额失败
    public static final int T_search_card_err_mag = 131 ;//读取电子现金余额失败
    public static final int T_quick_pass_first_offline = 132 ;//快速消费应优先选择脱机
    public static final int T_batch_no_trans = 133 ;//本批次暂无任何需要上送的交易
    public static final int T_pboc_refuse = 134 ;//PBOC流程拒绝
    public static final int T_ic_not_allow_swipe = 135 ;//IC卡不允许降级交易
    public static final int T_master_pass_err = 136 ;//主管密码错误
    public static final int T_settle_tc_send_err = 137; //结算交易明细上送失败
    public static final int T_reversal_fail = 138; //冲正失败，联系管理员
    public static final int T_qpboc_errno = 139; //非接读卡异常
    public static final int T_amount_not_same = 140; //金额与原交易金额不一致
    public static final int T_user_cancel = 141; //用户取消操作
    public static final int T_refund_amount_beyond = 142; //退货金额超限
    public static final int T_printer_exception = 143; //打印机异常
    public static final int T_scanner_user_exit = 144; //用户退出扫描
    public static final int T_scanner_timeout = 145; //扫描超时
    public static final int T_gen_2_ac_fail = 146 ; //二次授权失败
    public static final int T_orignal_comp = 147 ; //原交易已完成
    public static final int T_exp_date_card = 148 ; //tarjeta expirada
    public static final int T_err_last_4 = 149 ; //Incorrecto
    public static final int T_not_allow = 150 ; //Transaccion no permitida
    public static final int T_unsupport_card = 151 ; //Tarjeta no soportada
    public static final int T_not_list = 152 ; //No existe lista de pagos varios
    public static final int T_msg_err_gas = 153 ; //Transaccion no permitida, intente con otra tarjeta
    public static final int T_err_ingrese_cuotas = 154 ; //Ingrese cuotas
    public static final int T_err_ingrese_monto = 155 ; //Ingrese monto
    public static final int T_err_invalid_amnt = 156 ; //Cantidad de propina inválida
    public static final int T_err_invalid_tip = 157 ; //Ingrese propina
    public static final int T_err_read_reserved_acq = 158 ; //No se pudo obtener reservado de la tabla ACQUIRER
    public static final int T_err_ingrese_celular = 159 ; //Ingrese numero de celular
    public static final int T_err_ingrese_pin = 160 ; //Ingrese clave pin
    public static final int T_err_invalid_len = 161 ; //Longitud invalida
    public static final int T_err_invalid_same_num = 162 ; //Longitud invalida
    public static final int T_err_fallback = 163 ; //contFallback
    public static final int T_err_no_trans = 164 ; //Lote vacio
    public static final int T_err_batch_full = 165 ; //Realice cierre para continuar
    public static final int T_err_exp_date_app = 166 ; //Aplicacion expirada
    public static final int T_err_pin_null = 167 ; //No ingreso PIN
    public static final int T_err_trans_anul = 168 ; //Transaccion Anulada
    public static final int T_err_read_file_fideliza = 169 ; //Error al leer archivo FIDELIZA
    public static final int T_err_amount_cashback_exceed = 170 ; //El monto excede el valor permitido
    public static final int T_err_detect_card_failed = 171 ;//Fallo al detectar la tarjeta
    public static final int T_err_not_auth = 172 ;//Realice autenticacion
    public static final int T_err_exceeds_amount = 173 ;//monto excedido para contactless
    public static final int T_err_not_file_terminal = 174 ;//No existe el archivo TERMINAL
    public static final int T_err_not_file_processing = 175 ;//No existe el archivo PROCESSING
    public static final int T_err_not_file_entry_point = 176 ;//No existe el archivo ENTRY POINT
    public static final int T_err_not_allow = 177;//Ingreso manual NO permitido
    public static final int T_err_prevoucher_already_paid = 178;//Prevoucher ya pagado
    public static final int T_err_amount_cash_over = 179;//El monto debe ser multiplo de 10
    public static final int T_err_void_not_allow = 180;//Anulacion no permitida
    public static final int T_err_cod = 181;//Codigo No Coincide
    public static final int T_err_timeout = 182;//Tiempo de espera agotado
    public static final int T_insert_card = 183;//Inserte Tarjeta
    public static final int T_not_list_pe = 184 ; //No existe lista de pagos con codigo
    public static final int T_err_send_rev = 185 ; //ERROR ENVIANDO REVERSO
    public static final int T_err_def_not_allow = 186 ; //DIFERIDO NO PERMITIDO
    public static final int T_err_def_declined = 187 ;
    public static final int T_rspCode_no_llego = 188;
    public static final int T_usar_chip = 189;
    public static final int T_aplicacion_no_encontrada = 190;
    public static final int T_envio_fallido_reverso_ok = 191;
    public static final int T_envio_fallido_reverso_fail = 192;
    public static final int T_reversal_fail_EchoOK = 193; //Error en reverso-> borrado localmente
    public static final int T_monto_invalido = 194; // Monto Invalido
    public static final int T_CardBlock = 195; // Tarjeta Bloqueada
    public static final int T_sin_billeteras = 3160; // No hay Billeteras
    public static final int T_no_payment_confirm=3162;
    public static final int T_NO_IPS_LAN_DISPONIBLES = 3164; // No hay Ips disponibles lan
    public static final int T_NO_IPS_WIFI_DISPONIBLES = 3165; // No hay Ips disponibles wifi
    public static final int T_NO_IPS_3G_DISPONIBLES = 3166; // No hay Ips disponibles 3g
    public static final int T_NO_REVERSE=3167; // Transaccion NO REVERSADA por conflicto o sospechas de impresion.

    /**
     * Errores propios aplicativo Bancard inician con 600
     */
    public static final int T_no_datos_cuentas = 600; //Error en reverso-> borrado localmente


    public static final int T_unknow_err = 999 ;//未知错误
    public static final int T_blocked_aplication = 2063; //AC++
    public static final int T_decline_offline = 2069; //AC++

    public interface Status{
        public int downing_capk = 1 ;
        public int downing_aid = 2 ;
        public int downing_succ = 3 ;
        public int terminal_logon = 4 ;
        public int terminal_logonout = 5 ;
        public int connecting_center = 6 ;
        public int printing_recept = 7 ;
        public int printing_details = 20 ;
        public int terminal_reversal = 21 ;
        public int printer_lack_paper = 22 ;
        public int sale_succ = 8 ;
        public int enquiry_succ = 9 ;
        public int void_succ = 10 ;
        public int ecenquiry_succ = 11 ;
        public int quickpass_succ = 12 ;
        public int logon_succ = 13 ;
        public int logonout_succ = 14 ;
        public int handling = 15 ;
        public int settling_start = 16 ;
        public int settling_send_trans = 17 ;
        public int settling_over = 18 ;
        public int settling_succ = 19 ;
        public int settle_send_shell = 23 ;
        public int settle_send_reversal = 24 ;
        public int logon_down_succ = 25 ;
        public int send_over_2_recv = 26 ;
        public int pre_auth_success = 27 ;
        public int pre_auth_comp_success = 28 ;
        public int pre_auth_comp_void_success = 29 ;
        public int pre_auth_void_success = 30 ;
        public int refund_success = 31 ;
        public int scan_pay_success = 32 ;
        public int send_data_2_server = 33 ;
        public int scan_void_success = 34 ;
        public int scan_refund_success = 35 ;
        public int scan_success = 36 ;
        public int echo_test = 37 ;
        public int echo_test_success = 38 ;
        public int venta_success = 39 ;
        public int pago_electronico_exitoso = 40 ;
        public int diferido_exitoso = 41 ;
        public int settle_error = 42 ;
        public int preauto_exitosa = 43 ;
        public int ampliacion = 44 ;
        public int confirmacion = 45 ;
        public int process_trans = 46 ;
        public int reprint_exitosa = 47 ;
        public int pago_prevoucher_exitoso = 48;
        public int prevoucher_exitoso = 49;
        public int cash_over_succ = 50;
        public int pago_vario_succ = 51;
        public int msg_cod_diners = 52;
        public int msg_retry = 53;
        public int rev_receive_ok = 54;
        public int consulta_saldo_exitosa = 55;
        public int revEerso_borrado_localmente = 56;
        public int inyeccion_llaves = 57;
        public int inyeccion_llaves_exitoso = 58;
        public int inyeccion_llaves_fallida = 59;

    }
}
