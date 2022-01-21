package com.flota.basedatos;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ConexionSQLite extends SQLiteOpenHelper {

    protected static final String DATABASE_NAME = "Bancard.db";
    private static final int DATABASE_VERSION = 3;
    protected SQLiteDatabase conexion;
    protected static final String TABLE_LOGS_CIERRES = "logs_cierres";
    protected static final String COLUMN_LOGS_ID = "logs_id";
    protected static final String COLUMN_LOGS_TIPO_CIERRE = "logs_tipoCierre";
    protected static final String COLUMN_LOGS_NUM_LOTE = "logs_numLote";
    protected static final String COLUMN_LOGS_FECHA_ULTIMO_CIERRE = "logs_fechaUltimoCierre";
    protected static final String COLUMN_LOGS_FECHA_CIERRE = "logs_fechaCierre";
    protected static final String COLUMN_LOGS_DISCRIMINADO_COMERCIOS = "logs_discriminadoComercios";
    protected static final String COLUMN_LOGS_CANT_CREDITO = "logs_cantCredito";
    protected static final String COLUMN_LOGS_TOTAL_CREDITO = "logs_totalCredito";
    protected static final String COLUMN_LOGS_CANT_DEBITO = "logs_cantDebito";
    protected static final String COLUMN_LOGS_TOTAL_DEBITO = "logs_totalDebito";
    protected static final String COLUMN_LOGS_CANT_MOVIL = "logs_cantMovil";
    protected static final String COLUMN_LOGS_TOTAL_MOVIL = "logs_totalMovil";
    protected static final String COLUMN_LOGS_CANT_ANULAR = "logs_cantAnular";
    protected static final String COLUMN_LOGS_TOTAL_ANULAR = "logs_totalAnular";
    protected static final String COLUMN_LOGS_CANT_VUELTO = "logs_cantVuelto";
    protected static final String COLUMN_LOGS_TOTAL_VUELTO = "logs_totalVuelto";
    protected static final String COLUMN_LOGS_CANT_SALDO = "logs_cantSaldo";
    protected static final String COLUMN_LOGS_TOTAL_SALDO = "logs_totalSaldo";
    protected static final String COLUMN_LOGS_CANT_GENERAL = "logs_cantGeneral";
    protected static final String COLUMN_LOGS_TOTAL_GENERAL = "logs_totalGeneral";
    protected static final String TABLE_LOGS_CIERRES_DETALLADO = "logs_cierres_detallado";
    protected static final String COLUMN_LOGS_CARGO = "logs_cargo";
    protected static final String COLUMN_LOGS_NUM_BOLETA = "logs_boleta";
    protected static final String COLUMN_LOGS_MONTO = "logs_monto";
    protected static final String COLUMN_LOGS_FECHA = "logs_fecha";
    protected static final String COLUMN_LOGS_HORA = "logs_hora";
    protected static final String COLUMN_LOGS_TRANS = "logs_trans";
    protected static final String COLUMN_LOGS_TARJETA = "logs_tarjeta";
    protected static final String COLUMN_LOGS_TIPO_TARJETA = "logs_tipoTarjeta";
    protected static final String COLUMN_LOGS_ID_CIERRE = "logs_idCierre";
    protected static final String TABLE_TM_CONFIG = "TMConfig";
    protected static final String COLUMN_IP_PRIMARIA = "Ip_Primaria";
    protected static final String COLUMN_PUERTO_PRIMARIA = "Puerto_Primaria";
    protected static final String COLUMN_IP_SECUNDARIA = "Ip_Secundaria";
    protected static final String COLUMN_PUERTO_SECUNDARIA = "Puerto_Secundaria";
    protected static final String COLUMN_TIMEOUT = "Timeout";
    protected static final String COLUMN_TIMEOUT_DATA = "TimeoutData";
    protected static final String COLUMN_NII = "Nii";
    protected static final String TABLE_VOUCHER = "Voucher";
    protected static final String COLUMN_PAN = "PAN";
    protected static final String COLUMN_NRO_BOLETA = "NroBoleta";
    protected static final String COLUMN_NRO_CARGO = "NroCargo";
    protected static final String COLUMN_MONTO = "Monto";
    protected static final String COLUMN_TIPO_VENTA = "TipoVenta";
    protected static final String COLUMN_FECHA = "Fecha";
    protected static final String COLUMN_IMG_VOUCHER = "ImgVoucher";
    Context context;

    private static final String CREATE_TABLE = "CREATE TABLE ";
    private static final String TEXT = " TEXT, ";

    public ConexionSQLite(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(tablaConfiguracionIP());
        db.execSQL(tablaConfiguracionCierres());
        db.execSQL(tablaConfiguracionReporteDetallado());
        db.execSQL(tablaVoucherReimpresion());
    }

    private String tablaVoucherReimpresion() {
        return CREATE_TABLE + TABLE_VOUCHER + " (" +
                COLUMN_NRO_CARGO + TEXT +
                COLUMN_PAN + TEXT +
                COLUMN_NRO_BOLETA + TEXT +
                COLUMN_MONTO + TEXT +
                COLUMN_TIPO_VENTA + TEXT +
                COLUMN_FECHA + TEXT +
                COLUMN_IMG_VOUCHER + " BLOB " +
                ")";
    }

    private String tablaConfiguracionReporteDetallado() {
        return CREATE_TABLE + TABLE_LOGS_CIERRES_DETALLADO + " (" +
                COLUMN_LOGS_TARJETA + TEXT +
                COLUMN_LOGS_CARGO + TEXT +
                COLUMN_LOGS_NUM_BOLETA + TEXT +
                COLUMN_LOGS_MONTO + TEXT +
                COLUMN_LOGS_FECHA + TEXT +
                COLUMN_LOGS_HORA + TEXT +
                COLUMN_LOGS_TRANS + TEXT +
                COLUMN_LOGS_TIPO_TARJETA + TEXT +
                COLUMN_LOGS_ID_CIERRE + " TEXT NOT NULL " +
                ")";
    }

    private String tablaConfiguracionCierres() {
        return CREATE_TABLE + TABLE_LOGS_CIERRES + " (" +
                COLUMN_LOGS_ID + " TEXT NOT NULL, " +
                COLUMN_LOGS_TIPO_CIERRE + " TEXT NOT NULL, " +
                COLUMN_LOGS_NUM_LOTE + TEXT +
                COLUMN_LOGS_FECHA_ULTIMO_CIERRE + TEXT +
                COLUMN_LOGS_FECHA_CIERRE + TEXT +
                COLUMN_LOGS_DISCRIMINADO_COMERCIOS + TEXT +
                COLUMN_LOGS_CANT_CREDITO + TEXT +
                COLUMN_LOGS_TOTAL_CREDITO + TEXT +
                COLUMN_LOGS_CANT_DEBITO + TEXT +
                COLUMN_LOGS_TOTAL_DEBITO + TEXT +
                COLUMN_LOGS_CANT_MOVIL + TEXT +
                COLUMN_LOGS_TOTAL_MOVIL + TEXT +
                COLUMN_LOGS_CANT_ANULAR + TEXT +
                COLUMN_LOGS_TOTAL_ANULAR + TEXT +
                COLUMN_LOGS_CANT_VUELTO + TEXT +
                COLUMN_LOGS_TOTAL_VUELTO + TEXT +
                COLUMN_LOGS_CANT_SALDO + TEXT +
                COLUMN_LOGS_TOTAL_SALDO + TEXT +
                COLUMN_LOGS_CANT_GENERAL + TEXT +
                COLUMN_LOGS_TOTAL_GENERAL + " TEXT" +
                ")";
    }

    private String tablaConfiguracionIP() {
        return CREATE_TABLE + TABLE_TM_CONFIG + " (" +
                COLUMN_IP_PRIMARIA + TEXT +
                COLUMN_PUERTO_PRIMARIA + TEXT +
                COLUMN_IP_SECUNDARIA + TEXT +
                COLUMN_PUERTO_SECUNDARIA + TEXT +
                COLUMN_TIMEOUT + TEXT +
                COLUMN_TIMEOUT_DATA + TEXT +
                COLUMN_NII + " TEXT NOT NULL " +
                ")";
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Do nothing because of X and Y.
    }
}
