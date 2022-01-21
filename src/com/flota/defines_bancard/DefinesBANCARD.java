package com.flota.defines_bancard;

public final class DefinesBANCARD {

    //***********************BANCARD************************//
    public static final String MENU_PRINCIPAL = "MENU_PRINCIPAL";
    public static final String ITEM_COMERCIO = "Comercio";
    public static final String ITEM_LEALTAD = "Lealtad";
    public static final String ITEM_COBRANZAS = "Cobranzas";
    public static final String ITEM_CONFIG = "Configuración";
    public static final String ITEM_REPORTE_MENU = "Reportes";
    public static final String ITEM_CONFIG_ACCEPTER = "00";
    public static final String ITEM_CONFIG_COMERCIO = "Configuración comercio";
    public static final String ITEM_CONFIG_TECNICO = "Configuración técnico";
    public static final String ITEM_INICIALIZACION = "Inicialización";
    public static final String PREF_AMOUNT = "Amount";
    public static final String PREF_TRACE = "TraceNro";
    public static final String PREF_CARGO = "NroCargo";
    public static final String PREF_METODO = "Metodo";
    public static final String PREF_DATE = "Date";
    public static final String PREF_TIME = "Time";
    //COMERCIO
    public static final String ITEM_VENTA = "Venta";
    public static final String ITEM_VENTA_CUOTAS = "Venta Cuotas";
    public static final String ITEM_RECARGAS = "Recargas";
    public static final String ITEM_VENTA_MINUTOS = "Venta minutos";
    public static final String ITEM_REPORTE = "Reporte";
    public static final String ITEM_CIERRE = "Cierre";
    public static final String ITEM_CONSULTA_SALDO = "Consulta de saldo";
    public static final String ITEM_ANNULMENT = "Anulación";
    public static final String ITEM_DEBITO = "Débito";
    public static final String ITEM_CREDITO = "Crédito";
    public static final String ITEM_TECNICAS = "tecnicas";
    public static final String ITEM_REIMPRESION = "Reimpresión";
    //VENTA
    public static final String ITEM_VENTA_TARJETA = "Tarjeta";
    public static final String ITEM_VENTA_SIN_TARJETA = "Sin tarjeta";
    public static final String ITEM_VENTA_SIN_CONTACTO = "Sin contacto";
    public static final String ITEM_VENTA_CON_VUELTO = "Con vuelto";
    //CUOTAS
    public static final String ITEM_CUOTAS_TARJETA = "Tarjeta ";
    public static final String ITEM_CUOTAS_SIN_TARJETA = "Sin tarjeta ";
    public static final String ITEM_CUOTAS_SERVICIOS = "Servicios ";
    public static final String ITEM_CUOTAS_SIN_CONTACTO = "Sin contacto ";
    //SIN_TARJETA
    public static final String ITEM_SIN_TARJETA_PAGO_MOVIL = "Pago movil";
    public static final String ITEM_BILLETERAS = "Billeteras";
    public static final String ITEM_SIN_TARJETA_CUENTA_ST = "Cuenta sin tarjeta";
    //SIN_TARJETA CUOTAS
    public static final String ITEM_C_SIN_TARJETA_PAGO_MOVIL = "Pago movil ";
    public static final String ITEM_C_SIN_TARJETA_ZIMPLE = "Zimple ";
    public static final String ITEM_C_SIN_TARJETA_CUENTA_ST = "Cuenta sin tarjeta ";
    public static final String ITEM_C_SIN_TARJETA_EXT_ZIMPLE = "Extracción zimple ";
    //Recargas
    public static final String ITEM_RECARGA_CARGA_ZIMPLE = "Carga zimple";
    public static final String ITEM_RECARGA_GIRO_ZIMPLE = "Giro zimple";
    public static final String ITEM_RECARGA_EXTRACCION_ZIMPLE = "Extracción zimple";
    //LEALTAD
    public static final String ITEM_VALE = "Vale";
    public static final String ITEM_CONSULTA = "Consulta";
    public static final String ITEM_CANJE = "Canje";
    public static final String ITEM_PROMO = "Promo";
    //TipoIngreso
    public static final String INGRESO_MONTO = "MONTO";
    public static final String INGRESO_TELEFONO = "TELEFONO";
    public static final String INGRESO_BILLETERAS = "BILLETERAS";
    public static final String INGRESO_CODIGO = "CODIGO";
    public static final String INGRESO_VUELTO = "VUELTO";
    public static final String INGRESO_PIN = "PIN";
    public static final String INGRESO_CUOTAS = "CUOTAS";
    //TipoVenta
    public static final String VENTA_CUOTAS = "CUOTAS";
    public static final String VENTA_SALDO = "VENTA DE SALDO";
    public static final String VENTA_NORMAL = "VENTA";
    //Configuitacion
    public static final String CONFI_PARAMETROS_ADQUIRIENTE = "Parámetros del Adquiriente";
    public static final String CONFI_INFO = "Información Configuración";
    public static final String CONFI_LLAVES = "Configuración de llaves";
    public static final String INFORMACION = "Informacion";
    //Contrasenha
    public static final String PASS_COMERCIO = "PassComercio";
    public static final String PASS_TECNICO = "PassTécnico";
    //llaves
    public static final String DUKPT = "Inyección DUKPT";
    public static final String MK = "Inyección Master Key";
    //TiposDeCuenta
    public static final String TIPO_AHORROS = "CAhorros";
    public static final String TIPO_CORRIENTE = "CCorriente";
    public static final String TIPO_CREDITO = "Credito";
    //POLARIS CLOUD
    public static final String ITEM_POLARIS = "INICIALIZACIÓN \n POLARIS";
    public static final String MSG_SETTLE = "CIERRE LOTE \n PARA CONTINUAR";
    /**
     * ********** POLARIS_NAME_TX_
     * Definicion NOMBRES DE TRANSACCIONES configuradas en Polaris
     * Estos nombres deben estar sincronizados en codigo fuente y base de datos
     * Estos valores NO DEBEN SER MODIFICADOS - en solo uno de los lados, POS, Base de datos
     */
    public static final String POLARIS_NAME_TX_VTA_CUOTA = "venta cuotas";
    public static final String POLARIS_NAME_TX_BILLETERAS = "venta zimple";
    public static final String POLARIS_NAME_TX_SIN_TARJETA = "operaciones sin tarjetas";
    public static final String POLARIS_NAME_TX_VTA_DEBITO_FORZADO = "debito forzado";
    public static final String POLARIS_NAME_TX_VUELTO = "Vuelto";
    public static final String POLARIS_NAME_TX_VTA_SALDO = "venta saldo";
    public static final String POLARIS_APP_NAME = "FLOTA";
    //Menu tarjeta
    public static final String OPC_VENTA_CUOTA = "VENTA_CUOTA";
    public static final String OPC_VENTA_SALDO = "VENTA_SALDO";
    public static final String OPC_VENTA_DC = "VENTA_DC";
    public static final String OPC_VENTA_ST = "VENTA_ST";
    public static final int COD_VENTA_CUOTA = 1231;
    public static final int COD_VENTA_SALDO = 1232;
    public static final int COD_BILLETERAS = 1233;
    public static final int COD_VENTA_DEBITO_CREDITO = 1234;
    public static final int COD_CUENTA_SIN_TARJETA = 1235;
    public static final int DATO_NO_HABILITADO = 0;
    public static final int DATO_HABILITADO = 1;
    public static final int DATO_NO_REQUERIDO = 2;
    public static final String ITEM_CONFIG_RED = "Configuración de red";
    public static final String ITEM_ECHO_TEST = "Test de comunicación";
    public static final String ITEM_WIFI = "WIFI";
    public static final String ITEM_ELIMINAR_LLAVES = "Eliminar llaves";
    public static final String ITEM_CONFIG_WIFI = "Configuración de Wifi";
    public static final String ITEM_DETALLE_INICIALIZACION = "Detalles inicialización";
    public static final String ITEM_LIMPIAR_DATOS = "Limpiar datos";
    public static final String ITEM_REVERSAL = "Enviar reversa pendiente";
    public static final String ITEM_ETHERNET = "ETHERNET";
    public static final String ITEM_CONFIG_ETHERNET = "Configuración Ethernet";
    //================Conection Types================//
    public static final String TYPE_3G = "0";
    public static final String TYPE_WIFI = "1";
    public static final String TYPE_ETHERNET = "2";
    public static final String DATO_MENU = "MENU";

    //=======================Medianet=======================//
    public static final String ITEM_BORRAR_LOTE = "BORRAR LOTE";
    public static final String ITEM_BORRAR_REVERSO = "BORRAR REVERSO";
    public static final String ITEM_REPORTE_DETALLADO = "REPORTE DETALLADO";
    public static final String TIPO_LAYOUT_LINEAR = "LINEAR";
    public static final String ITEM_DIFERIDO = "DIFERIDO";
    public static final String ITEM_TEST = "TEST";
    public static final String MSG_PAPER = "INSERTE PAPEL \n PARA CONTINUAR";
    public static final String LOTE_VACIO = "LOTE VACÍO";
    public static final String ITEM_COMUNICACION = "COMUNICACION";
    public static final String ITEM_REPORTE_EMV = "REPORTE EMV";
    public static final String ITEM_REPORTE_TERMINAL = "REPORTE TERMINAL";
    public static final String ITEM_MENU_OPERARIO = "MENU OPERARIO";
    public static final String NAME_FOLDER_CTL_FILES = "CTL_Files";
    public static final String NAME_FOLDER_CTL_CAPKS = "CTL_Cakps";
    public static final String ENTRY_POINT = "ENTRY_POINT";
    public static final String PROCESSING = "PROCESSING";
    public static final String TERMINAL = "TERMINAL";
    public static final String CAKEY = "CAKEY";
    public static final String REVOK = "REVOK";
    public static final String MENU_REPORTE_REIMPRESION = "RE-IMPRESION";
    public static final String MENU_REPORTE_TESTPOS = "TEST DE POS";
    public static final String ITEM_PARAMETROS = "PARAMETROS";
    public static final String ITEM_DEFERED = "MENU DIFERIDOS";
    public static final String SETTING_ADMINISTRATIVAS = "ADMINISTRATIVAS";
    public static final String SETTING_CONFIGURACIONES = "CONFIGURACIONES";
    public static final String SETTING_TEST = "TEST";
    public static final String SETTING_OTRAS_OPCIONES = "OTRAS OPCIONES";
    public static final String DETALLES_COMERCIOS = "COMERCIOS";
    public static final String DETALLES_DEVICE = "DEVICE";
    // PREFERENCIA DE GUARDAR LA FECHA DE INCIALIZACION
    public static final String FECHA_INICIALIZACION = "FechaInicializacion";
    public static final String FECHA_INCIALIZACION = "fecha-incializacion";
    public static final String CASTEO_APLICACION = "CasteoAplicacion";
    public static final String EVENTO_TAREAS = "EventoTareas";
    public static final String TAREA_REALIZA = "TareaRealiza";
    public static final String MENSAJE_ERROR_INYECCION_LLAVES = "MensajeErrorInyeccionLLaves";
    public static final String HABILITARMENUS = "HabilitarMenusInicializacion";

    private DefinesBANCARD() {
    }
}
