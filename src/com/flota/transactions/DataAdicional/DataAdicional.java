package com.flota.transactions.DataAdicional;

import static com.newpos.libpay.utils.ISOUtil.convertStringToHex;

import android.content.Context;

import com.flota.model.SubField;
import com.flota.model.SubFieldsModel;
import com.flota.tools.UtilNetwork;
import com.google.common.primitives.Ints;
import com.newpos.libpay.Logger;
import com.newpos.libpay.utils.ISOUtil;
import com.pos.device.config.DevConfig;
import com.wposs.flota.BuildConfig;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class DataAdicional {

    public static ArrayList<SubFieldsModel> subFieldsModel = new ArrayList<>();
    static String clase = "DataAdicional.java";
    private static ArrayList<SubField> decoFields = new ArrayList<>();
    private static int indice = 0;
    String msgId;

    /**
     * @param msgId -> Identificador del tipo de mensaje
     */
    public DataAdicional(String msgId) {
        this.msgId = msgId;
        loadSubFieldsModel();
    }

    /**
     * Devuelve un array list de SubFieldsModel
     *
     * @return subFieldsModel -> es el modelo de la transaccion con el (mti, el identificaor de la transaccion(procode) y la fila de campos que va contener la transacción)
     */
    public static ArrayList<SubFieldsModel> getSubTrans() {
        return subFieldsModel;
    }

    /**
     * Se hace el modelo de respuesta del campo63, se mapea de acuerdo al msgId y el proCode
     */
    private static void loadSubFieldsModel() {
        if (subFieldsModel != null) {
            subFieldsModel.clear();
        } else {
            subFieldsModel = new ArrayList<>();
        }
        subFieldsModel.add(new SubFieldsModel("0200", "800000", new int[]{1, 2, 3, 4, 5, 6, 9, 33, 70, 81, 90, 94, 95}));
        subFieldsModel.add(new SubFieldsModel("0200", "810000", new int[]{2, 3, 4, 7, 8, 33, 70, 81, 90, 93, 94, 95}));
        subFieldsModel.add(new SubFieldsModel("0200", "820000", new int[]{1, 33, 70, 81, 90, 94, 95}));
        subFieldsModel.add(new SubFieldsModel("0200", "830000", new int[]{7, 9, 33, 70, 81, 90, 94, 95}));
    }

    /**
     * Se obtiene el valor de un campo en especifico
     *
     * @param idCampo -> el identificador del campo
     * @return res -> Dato del campo
     */
    public static String getField(int idCampo) {
        String res = null;
        for (SubField sub : getSubFieldsList()) {
            try {
                if (Integer.parseInt(sub.getFieldId()) == idCampo) {
                    res = sub.getFieldData();
                }
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return res;
    }

    /**
     * Es un  array de SubField
     *
     * @return subFields -> son los subcampos para el campo 63
     */
    private static ArrayList<SubField> getSubFieldsList() {
        return decoFields;
    }

    /**
     * Se actualiza un campo o lo añade en caso de que no esté
     *
     * @param id   -> el código del campo
     * @param data -> dato del campo
     */
    public static void addOrUpdate(int id, String data) {
        String idCampo = ISOUtil.padleft(String.valueOf(id), 2, '0');
        for (SubField subField: decoFields){
            if (subField.getFieldId().equals(idCampo)) {
                subField.setFieldData(data);
                return;
            }
        }
        decoFields.add(new SubField(idCampo, data));
    }

    /**
     * Se setean los subcampos para el campo 63, a partir de una trama (en hexadecimal)
     *
     * @param field63
     */
    public void setSubCampos(String field63) {
        indice = 0;
        decoFields = new ArrayList<>();
        for (int i = 0; i < field63.length(); i = indice) {
            String fieldLen = field63.substring(indice, indice += 4);
            String fieldId = ISOUtil.hex2AsciiStr(field63.substring(indice, indice + 4));
            String fieldData = obtenerCampo(field63);
            decoFields.add(new SubField(fieldId, fieldData));
            indice += Integer.parseInt(fieldLen) * 2;
        }
    }

    /**
     * Se obtiene la longitud del campo(id + dato), luego de esto se calcula la longitud real del campo 63(longitud +id +dato)
     * este dato se usa para obtener el campo en especifico tomando del campo 63 los caracteres desde el indice+4 hasta la longitud Real
     * ese campo se convierte a Ascii y se almanena en Data
     *
     * @param campo63
     * @return
     */
    private String obtenerCampo(String campo63) {
        if (!campo63.equals("")) {
            try {
                int lenCampo = Integer.parseInt(campo63.substring(indice - 4, indice)) * 2;
                int lenReal = (indice) + lenCampo;
                if (campo63.length() < ((indice) + lenCampo)) {
                    lenReal = campo63.length();
                }
                String data = campo63.substring(indice + 4, lenReal);
                return ISOUtil.hex2AsciiStr(data);
            } catch (NumberFormatException ex) {
                Logger.exception(clase, "Error NumberFormatException");
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Se setean los campos dependiendo del tipo de trnasaccion (msgID) y el codigo de proceso (proCodde)
     *
     * @return Hex.toString()
     */
    public String getSubFields(String proCode) {
        StringBuilder Hex = new StringBuilder();
        for (SubFieldsModel subField : getSubTrans()) {
            if (subField.getMti().equals(msgId)) {
                switch (subField.getMti()) {
                    case "0400":
                    case "0800":
                        Hex.append(getField63(subField.getFields()));
                        break;
                    case "0200":
                        if (subField.getProCode().equals(proCode)) {
                            Hex.append(getField63(subField.getFields()));
                        }
                        break;
                    default:
                        break;
                }
            }
        }
        return Hex.toString();
    }

    /**
     * Se valida la longitud para la respuesta del campo 63 dependiendo de su ID
     *
     * @param subPrint
     * @return res.toString();
     */
    private String getField63(int[] subPrint) {
        StringBuilder res = new StringBuilder();
        if (subPrint.length > 0) {
            ArrayList<SubField> sb = getSubFieldsList();
            Collections.sort(sb, new CustomComparator());
            for (SubField field : getSubFieldsList()) {
                int id = Integer.parseInt(field.getFieldId());
                boolean exist = Ints.contains(subPrint, id);
                Logger.error(clase, "getField63: exists " + exist);
                if (exist) {
                    String data = getFieldData(field.getFieldId(), field.getFieldData());
                    res.append(data);
                }
            }
        } else {
            Logger.error(clase, "getField63: Array Vacio");
            res = new StringBuilder();
        }
        return res.toString();
    }

    /**
     * Se crea el campo 63 (se pasa en formato Hexa)
     *
     * @param fieldId
     * @param fieldData
     * @return res
     */

    private String getFieldData(String fieldId, String fieldData) {
        String res;
        int len = ISOUtil.convertStringToHex(fieldData).length() / 2;
        len = len + 2;
        String leng = ISOUtil.padleft(String.valueOf(len), 4, '0');
        res = leng + convertStringToHex(fieldId) + convertStringToHex(fieldData);
        return res;
    }

    /**
     * Se setean los campos para toda las transacciones
     *
     * @param contextv
     */
    public void commonConfig(Context contextv) {
        String serialTerminal = DevConfig.getSN();
        addOrUpdate(81, serialTerminal);

        StringBuilder bSerial = new StringBuilder(serialTerminal);
        bSerial.insert(5, '-');
        addOrUpdate(70, bSerial.toString());

        setCampo90();
        addOrUpdate(94, UtilNetwork.getIpFull(contextv));
        addOrUpdate(95, UtilNetwork.getImei(contextv));
    }

    /**
     * Se setea el nombre de la versión en el campo 90
     */
    private void setCampo90() {
        int lenIni = 0;
        try {
            String verAxu = BuildConfig.VERSION_NAME;
            if (BuildConfig.VERSION_NAME.length() > 7) { // Tamaño maximo de longitud = 10
                lenIni = BuildConfig.VERSION_NAME.length() - 7;
                verAxu = BuildConfig.VERSION_NAME.substring(lenIni, BuildConfig.VERSION_NAME.length());
            }
            verAxu = ISOUtil.padright(verAxu, 7, ' ');
            Logger.error(clase, "setCampo90: verAux " + verAxu);
            addOrUpdate(90, verAxu);
        } catch (Exception e) {
            Logger.exception(clase, e);
        }
    }

    class CustomComparator implements Comparator<SubField> {
        @Override
        public int compare(SubField sb1, SubField sb2) {
            return sb1.getFieldId().compareTo(sb2.getFieldId());
        }
    }
}
