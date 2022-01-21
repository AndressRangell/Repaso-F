package com.flota.basedatos.implementaciones;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.flota.basedatos.ConexionSQLite;
import com.flota.basedatos.ModeloVoucherReimpresion;
import com.flota.basedatos.interfaces.ReimpresionVoucherDAO;
import com.newpos.libpay.Logger;

public class ReimpresionVoucherDAOImpl extends ConexionSQLite implements ReimpresionVoucherDAO {

    String clase = "ReimpresionVoucherDAOImpl.java";

    public ReimpresionVoucherDAOImpl(Context context) {
        super(context);
    }

    @Override
    public boolean ingresarRegistro(ModeloVoucherReimpresion ObjectReimpresion) {

        boolean ret = false;
        conexion = this.getWritableDatabase();
        try {
            String sql = "INSERT INTO " + TABLE_VOUCHER + "  " +
                    "(" + COLUMN_NRO_CARGO + ", " +
                    "" + COLUMN_PAN + ", " +
                    "" + COLUMN_NRO_BOLETA + ", " +
                    "" + COLUMN_MONTO + ", " +
                    "" + COLUMN_TIPO_VENTA + ", " +
                    "" + COLUMN_FECHA + ", " +
                    "" + COLUMN_IMG_VOUCHER + " " +
                    ")" +
                    "" +
                    "VALUES(?,?,?,?,?,?,?)";
            SQLiteStatement insert = conexion.compileStatement(sql);
            insert.clearBindings();
            insert.bindString(1, ObjectReimpresion.getNroCargo());
            if (ObjectReimpresion.getPan() != null) {
                insert.bindString(2, ObjectReimpresion.getPan());
            }
            insert.bindString(3, ObjectReimpresion.getNroBoleta());
            insert.bindString(4, ObjectReimpresion.getMonto());
            insert.bindString(5, ObjectReimpresion.getTipoVenta());
            insert.bindString(6, ObjectReimpresion.getFecha());
            insert.bindBlob(7, ObjectReimpresion.getVoucher());
            insert.executeInsert();
            conexion.close();
            ret = true;
        } catch (Exception e) {
            Logger.exception(clase, e);
            Logger.error("Error al insertar voucher " , e);
            e.printStackTrace();
        }
        return ret;
    }

    @Override
    public Bitmap obtenerVoucher(String nroCargo) {
        Bitmap bitmap = null;
        try {
            conexion = this.getWritableDatabase();
            String sql = "SELECT ImgVoucher FROM Voucher WHERE NroCargo = '" + nroCargo + "'";
            Cursor cursor = conexion.rawQuery(sql, new String[]{});
            if (cursor.moveToFirst()) {
                byte[] blob = cursor.getBlob(cursor.getColumnIndex("ImgVoucher"));
                cursor.close();
                bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);
                if (bitmap != null) {
                    Logger.info(clase, "El bitmap tiene algo y es esto :" + bitmap);
                }
            }
            if (!cursor.isClosed()) {
                cursor.close();
            }
            conexion.close();
        } catch (Exception e) {
            Logger.exception(clase, e);
            Logger.error( "No se pudo obtener la imagen :" ,e);
            e.printStackTrace();
        }
        return bitmap;
    }
}
