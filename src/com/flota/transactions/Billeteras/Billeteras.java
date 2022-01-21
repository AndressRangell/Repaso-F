package com.flota.transactions.Billeteras;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.widget.Toast;

import com.flota.inicializacion.trans_init.trans.DbHelper;
import com.newpos.libpay.Logger;
import com.wposs.flota.R;

import java.util.ArrayList;
import java.util.regex.Pattern;

import cn.desert.newpos.payui.UIUtils;

import static com.flota.inicializacion.trans_init.Init.NAME_DB;

public class Billeteras {

    private static ArrayList<Billeteras> billeterasList = new ArrayList<>();
    private static ArrayList<Billeteras> billeterasTempList = new ArrayList<>();
    private String plantillaIdBilletera;
    private String codBilletera;
    private String nombreBilletera;
    private Drawable logoBilletera;
    private String active;

    String clase = "Billeteras.java";

    public static Billeteras billetera;

    final String tablaBaseDatosRed = "BILLETERAS";
    final String plantillaId = "plantillaid";
    final String codigo = "codigo";
    final String nombre = "nombre";
    final String activar = "activar";
    final String logo = "logo";
    Context mContext;

    String[] listadoColumnasSQL = new String[]{
            plantillaId, codigo, activar, logo, nombre
    };
    private boolean isLast = false;

    private Billeteras(Context mContext) {
        this.mContext = mContext;
    }

    public Billeteras(String codBilletera, String nombreBilletera, Drawable logoBilletera) {
        this.codBilletera = codBilletera;
        this.nombreBilletera = nombreBilletera;
        this.logoBilletera = logoBilletera;
    }

    public String getNombreBilletera() {
        return nombreBilletera;
    }

    public void setNombreBilletera(String nombreBilletera) {
        this.nombreBilletera = nombreBilletera;
    }

    public Drawable getLogoBilletera() {
        return logoBilletera;
    }

    public void setLogoBilletera(Drawable logoBilletera) {
        this.logoBilletera = logoBilletera;
    }

    public String getCodBilletera() {
        return codBilletera;
    }

    public void setCodBilletera(String codBilletera) {
        this.codBilletera = codBilletera;
    }

    public String getPlantillaIdBilletera() {
        return plantillaIdBilletera;
    }

    public void setPlantillaIdBilletera(String plantillaIdBilletera) {
        this.plantillaIdBilletera = plantillaIdBilletera;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public static ArrayList<Billeteras> getAllBilleteras() {
        Logger.info("Cantidad de Billeteras: " + billeterasList.size());
        return billeterasList;
    }


    public static Bitmap getBitmapFromBytes(byte[] bytes) {
        if (bytes != null) {
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        }
        return null;
    }

    public static Billeteras getInstance(boolean isBorrarInfo,Context context) {
        if (isBorrarInfo) {
            billetera = null;
            billeterasList.clear();
        }
        if (billetera == null) {
            billetera = new Billeteras(context);
        }
        return billetera;
    }

    public boolean inicializandoComponentes(Context context) {

        DbHelper databaseAccess = new DbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);


        String sql = consultaSQL(listadoColumnasSQL);
        Logger.info("Billeteras sql---" + sql);
        try {
            Cursor cursor = databaseAccess.rawQuery(sql, null);
            cursor.moveToFirst();
            int indexColumn;

            while (!cursor.isAfterLast()) {
                billetera = new Billeteras(context);
                indexColumn = 0;
                for (String s : listadoColumnasSQL) {
                    billetera.setAPP(s, cursor.getString(indexColumn++).trim());
                }
                addBilletera(billetera);
                cursor.moveToNext();
            }
            if (!billeterasTempList.isEmpty()) {
                billeterasList.addAll(billeterasTempList);
            }

            cursor.close();
            Logger.info("Cantidad de Billeteras1: " + billeterasList.size());
            return true;
        } catch (Exception ex) {
            Logger.exception(clase, ex);
            ex.printStackTrace();
            Logger.error("",ex);

        }
        databaseAccess.closeDb();
        return false;
    }

    private void addBilletera(Billeteras billetera) {
        if (billetera.getActive().equalsIgnoreCase("true")) {
            if (isLast) {
                billeterasTempList.add(billetera);
            } else {
                billeterasList.add(billetera);
            }
        }
    }

    private String consultaSQL(String[] listadoColumnasSQL) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");

        for (int i = 0; i < listadoColumnasSQL.length; i++) {
            sql.append(listadoColumnasSQL[i]);
            if (i < (listadoColumnasSQL.length - 1)) {
                sql.append(", ");
            }
        }
        sql.append(" FROM ");
        sql.append(tablaBaseDatosRed);
        sql.append(" ORDER BY " + nombre);

        return sql.toString();
    }

    private void setAPP(String column, String value) {
        switch (column) {
            case plantillaId:
                setPlantillaIdBilletera(value);
                break;
            case codigo:
                setCodBilletera(value);
                break;
            case nombre:
                if (validarNombre(value)) {
                    String[] data = value.split("#");
                    setNombreBilletera(data[1]);
                } else {
                    setNombreBilletera(value);
                }
                break;
            case activar:
                setActive(value);
                break;
            case logo:
                setLogoBilletera(getLogo(value));
                break;
        }
    }

    private boolean validarNombre(String value) {
        if (value.indexOf("#") != -1) {
            String[] data = value.split("#");
            if (data != null && data.length > 1) {
                if (data[0] != null && !data[0].isEmpty()) {
                    try {
                        int position = Integer.parseInt(data[0]);
                        Logger.info("Posicion: " + position);
                        isLast = false;
                        return true;
                    } catch (NumberFormatException ex) {
                        Logger.exception(clase, ex);
                        Logger.info("Error NumberFormatException " + ex);
                    }
                } else {
                    UIUtils.toast((Activity) mContext, R.drawable.redinfonet, "No se encuentra codigo para la billetera " + data[1], Toast.LENGTH_SHORT);
                    setActive("false");
                }
            }
        }
        isLast = true;
        return false;
    }

    private Drawable getLogo(String logoString) {
        try {
            String base64String = "";
            if (logoString != null) {
                Logger.info("Base64: " + logoString);
                Logger.info("Contains: " + logoString.contains("*nm*"));
                if (logoString.contains("*nm*")) {
                    base64String = logoString.replace("*nm*", "");
                } else {
                    base64String = logoString;
                }
                if (!isBase64(base64String)) {
                    return null;
                }


                byte[] decodeImage = Base64.decode(base64String, Base64.DEFAULT);
                Drawable logoBilletera = new BitmapDrawable(getBitmapFromBytes(decodeImage));
                return logoBilletera;
            }
        } catch (Exception ex) {
            Logger.exception(clase, "Error al obtener el logo de billeteras " + ex);
            return null;
        }
        return null;
    }

    private boolean isBase64(String stringBase64) {
        String regex =
                "([A-Za-z0-9+/]{4})*" +
                        "([A-Za-z0-9+/]{4}|[A-Za-z0-9+/]{3}=|[A-Za-z0-9+/]{2}==)";
        Pattern patron = Pattern.compile(regex);
        return patron.matcher(stringBase64).matches();
    }

}
