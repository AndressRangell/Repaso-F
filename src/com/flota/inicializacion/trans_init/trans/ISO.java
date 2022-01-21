package com.flota.inicializacion.trans_init.trans;

import com.newpos.libpay.Logger;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Elkin Beltrán
 */
public class ISO {

    public static final int TYPE_LENGHT_FIX = 0;
    public static final int TYPE_LENGHT_FLLVAR = 1;
    public static final int TYPE_LENGHT_FLLLVAR = 2;
    public static final int TYPE_LENGHT_FHHHVAR = 3;
    public static final int TYPE_CONTENT_ATTN = 0;
    public static final int TYPE_CONTENT_ATTAN = 1;
    public static final int TYPE_CONTENT_ATTANS = 2;
    public static final int LENGHT_NOT_INCLUDE = 0;
    public static final int LENGHT_INCLUDE = 1;
    public static final int TPDUNOT_INCLUDE = 0;
    public static final int TPDU_INCLUDE = 1;
    public static final int START_FIELD = 0;
    public static final int FIELD_01_BITMAP_EXTENDED = 1;
    public static final int FIELD_02_PRIMARY_ACCOUNT_NUMBER = 2;
    public static final int FIELD_03_PROCESSING_CODE = 3;
    public static final int FIELD_04_AMOUNT_TRANSACTION = 4;
    public static final int FIELD_05_AMOUNT_SETTLEMENT = 5;
    public static final int FIELD_06_AMOUNT_CARDHOLDER_BILLING = 6;
    public static final int FIELD_07_TRANSMISSION_DATE_TIME_MMDDHHMMSS = 7;
    public static final int FIELD_08_AMOUNT_CARDHOLDER_BILLING_FEE = 8;
    public static final int FIELD_09_CONVERSION_RATE_SETTLEMENT = 9;
    public static final int FIELD_10_CONVERSION_RATE_CARD_HOLDER_BILLING = 10;
    public static final int FIELD_11_SYSTEMS_TRACE_AUDIT_NUMBER = 11;
    public static final int FIELD_12_TIME_LOCAL_TRANSACTION_HHMMSS = 12;
    public static final int FIELD_13_DATE_LOCAL_TRANSACTION_MMDD = 13;
    public static final int FIELD_14_DATE_EXPIRATION_YYMM = 14;
    public static final int FIELD_15_DATE_SETTLEMENT_MMDD = 15;
    public static final int FIELD_16_DATE_CONVERSION_MMDD = 16;
    public static final int FIELD_17_DATE_CAPTURE = 17;
    public static final int FIELD_18_MERCHANTS_TYPE = 18;
    public static final int FIELD_19_ACQUIRING_INSTITUTION_COUNTRY_CODE = 19;
    public static final int FIELD_20_PRIMARY_ACCOUNT_NUMBER_EXTENDED_COUNTRY_CODE = 20;
    public static final int FIELD_21_FORWARDING_INSTITUTION_COUNTRY_CODE = 21;
    public static final int FIELD_22_POINT_OF_SERVICE_ENTRY_MODE = 22;
    public static final int FIELD_23_CARD_SEQUENCE_NUMBER = 23;
    public static final int FIELD_24_NETWORK_INTERNATIONAL_IDENTIFIER = 24;
    public static final int FIELD_25_POINT_OF_SERVICE_CONDITION_CODE = 25;
    public static final int FIELD_26_POINT_OF_SERVICE_PIN_CAPTURE_CODE = 26;
    public static final int FIELD_27_AUTHORIZATION_IDENTIFICATION_RESPONSE_LENGHT = 27;
    public static final int FIELD_28_AMOUNT_TRANSACTION_FEE_X_N = 28;
    public static final int FIELD_29_AMOUNT_SETTLEMENT_FEE_X_N = 29;
    public static final int FIELD_30_AMOUNT_TRANSACTION_PROCESSING_FEE_X_N = 30;
    public static final int FIELD_31_AMOUNT_SETTLEMENT_PROCESSING_FEE = 31;
    public static final int FIELD_32_ACQUIRING_INSTITUTION_IDENTIFICATION_CODE = 32;
    public static final int FIELD_33_FORWARDING_INSTITUTION_IDENTIFICATION_CODE = 33;
    public static final int FIELD_34_PRIMARY_ACCOUNT_NUMBER_EXTENDED = 34;
    public static final int FIELD_35_TRACK_2_DATA = 35;
    public static final int FIELD_36_TRACK_3_DATA = 36;
    public static final int FIELD_37_RETRIEVAL_REFERENCE_NUMBER = 37;
    public static final int FIELD_38_AUTHORIZATION_IDENTIFICATION_RESPONSE = 38;
    public static final int FIELD_39_RESPONSE_CODE = 39;
    public static final int FIELD_40_SERVICE_RESTRICTION_CODE = 40;
    public static final int FIELD_41_CARD_ACCEPTOR_TERMINAL_IDENTIFICATION = 41;
    public static final int FIELD_42_CARD_ACCEPTOR_IDENTIFICATION_CODE = 42;
    public static final int FIELD_43_CARD_ACCEPTOR_NAME_LOCATION = 43;
    public static final int FIELD_44_ADDITIONAL_RESPONSE_DATA = 44;
    public static final int FIELD_45_TRACK_1_DATA = 45;
    public static final int FIELD_46_ADDITIONAL_DATA_ISO = 46;
    public static final int FIELD_47_ADDITIONAL_DATA_NATIONAL = 47;
    public static final int FIELD_48_ADDITIONAL_DATA_PRIVATE = 48;
    public static final int FIELD_49_CURRENCY_CODE_TRANSACTION = 49;
    public static final int FIELD_50_CURRENCY_CODE_SETTLEMENT = 50;
    public static final int FIELD_51_CURRENCY_CODE_CARD_HOLDER_BILLING = 51;
    public static final int FIELD_52_PERSONAL_IDENTIFICATION_NUMBER_PIN_DATA = 52;
    public static final int FIELD_53_SECURITY_RELATED_CONTROL_INFORMATION = 53;
    public static final int FIELD_54_ADDITIONAL_AMOUNTS = 54;
    public static final int FIELD_55_RESERVED_ISO = 55;
    public static final int FIELD_56_RESERVED_ISO = 56;
    public static final int FIELD_57_RESERVED_NATIONAL = 57;
    public static final int FIELD_58_RESERVED_NATIONAL = 58;
    public static final int FIELD_59_RESERVED_NATIONAL = 59;
    public static final int FIELD_60_RESERVED_PRIVATE = 60;
    public static final int FIELD_61_RESERVED_PRIVATE = 61;
    public static final int FIELD_62_RESERVED_PRIVATE = 62;
    public static final int FIELD_63_RESERVED_PRIVATE = 63;
    public static final int FIELD_64_MESSAGE_AUTHENTICATION_CODE = 64;
    public static final int END_ISO_FIELD = 66;
    public static final int E_1_LENGHT = 66;
    public static final int E_2_ID_TPDU = 67;
    public static final int E_3_DESTINATION = 68;
    public static final int E_4_SOURCE = 69;
    public static final int E_5_MESSAGE_TYPE = 70;
    public static final int E_6_PRIMARY_BITMAP = 71;
    public static final int E_7_SECONDARY_BITMAP = 72;
    public static final int END_FIELD = 73;

    public static final String VENTA_ON_LINE = "00";
    public static final String VENTA_OFF_LINE = "01";
    public static final String TRANS_REVERSO = "02";
    public static final String TRANS_ANULACION = "03";
    public static final String TRANS_BATCH = "05";
    public static final String CONSULTA_VENTAS = "06";
    public static final String PAGOS = "07";
    public static final String CONSULTA_TICKET = "08";
    public static final String LOGIN_TRANS = "09";
    public static final String TRANS_CIERRE = "10";
    public static final String REP_PAR_CAJA = "11";
    public static final String INF_FIN_SORT = "12";
    public static final String REP_CIERRE = "13";
    public static final String LISTA_NUMEROS = "14";
    public static final String TICKET_GANADORES = "15";
    public static final String ID_CONSULTA_SINCRO = "17";
    public static final String ID_REP_OTRO_PRODUCTOS = "27";
    public static final String SEPARADOR_PIPE = "|";

    public static final byte FLLVAR = 0x01;
    public static final byte FLLLVAR = 0x02;
    public static final byte FIX = 0x04;
    public static final byte ATTN = 0x10;
    public static final byte ATTAN = 0x20;
    public static final byte ATTANS = 0x40;

    private static final String TAG = "ISO.java";
    private static final byte TRUE = 0x01;
    private static final byte[] inputBitmap = new byte[8];
    private static final byte[] bitmapOutput = new byte[8];

    boolean isoModificado = true;
    FieldDecodeT[] fieldDecode = new FieldDecodeT[135];
    FieldDefinitionT[] fieldDefinition = new FieldDefinitionT[135];
    private byte[] input;
    private byte includeLenght = TRUE;
    private byte includeTpdu = TRUE;
    private int pointerInput = 0;
    private int pointerOutput = 0;
    private int pointerBitmapOutput = 0;
    private byte[] output = new byte[2048];

    public ISO(byte[] input, int lenght, int tpdu) {
        initIsoDefinition(fieldDefinition);
        initFieldDecode();
        this.input = input;
        this.includeLenght = (byte) lenght;
        this.includeTpdu = (byte) tpdu;
        pointerOutput = 0;
        if (lenght == ISO.LENGHT_INCLUDE)
            pointerOutput += 2;
        decode();
    }

    public ISO(int lenght, int tpdu) {
        initIsoDefinition(fieldDefinition);
        this.includeLenght = (byte) lenght;
        this.includeTpdu = (byte) tpdu;
        pointerOutput = 0;
        if (lenght == ISO.LENGHT_INCLUDE)
            pointerOutput += 2;
    }

    public static final String dateYYYYMMDDStr() {
        Calendar cal = Calendar.getInstance();
        int mm = cal.get(Calendar.MONTH) + 1;
        int dd = cal.get(Calendar.DAY_OF_MONTH);
        int yyyy = cal.get(Calendar.YEAR);
        String mmC;
        String ddC;
        String yyyyC;

        ///Mes
        if (mm < 10) {
            mmC = "0" + mm;
        } else {
            mmC = String.valueOf(mm);
        }

        ///Dia
        if (dd < 10) {
            ddC = "0" + dd;
        } else {
            ddC = String.valueOf(dd);
        }

        //Año
        if (yyyy < 200)
            yyyy += 1900;

        yyyyC = String.valueOf(yyyy);

        return yyyyC + mmC + ddC;
    }

    public static int getStan() {
        return SharedPreferences.getValueIntPreference(Tools.getCurrentContext(), "STAN");
    }

    public static void incStan() {
        int stan;
        stan = SharedPreferences.getValueIntPreference(Tools.getCurrentContext(), "STAN");
        if (stan < 999999)
            stan++;
        else stan = 0;

        SharedPreferences.saveValueIntPreference(Tools.getCurrentContext(), SharedPreferences.KEY_STAN, stan);
    }

    public static void decStan() {
        int stan;
        stan = SharedPreferences.getValueIntPreference(Tools.getCurrentContext(), SharedPreferences.KEY_STAN);
        if (stan > 0)
            stan--;
        else stan = 0;

        SharedPreferences.saveValueIntPreference(Tools.getCurrentContext(), SharedPreferences.KEY_STAN, stan);
    }

    private void copyBytes(byte[] source, byte[] destination, int startSource, int startDestination, int numOfBytes) {
        int i;
        int j = 0;
        for (i = startSource; i < startSource + numOfBytes; i++) {
            destination[startDestination + j] = source[startSource + j];
            j += 1;
        }
    }

    public boolean setTPDUId(String id) {
        try {
            byte[] tmp = null;
            int size = 0;
            HexEncoding strToByte = new HexEncoding();
            tmp = strToByte.getBytes(id);
            size = tmp.length;
            if (size == 1) {
                System.arraycopy(tmp, 0, output, pointerOutput, size);
                pointerOutput += size;
                return true;
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.exception(TAG, e);
            return false;
        }
    }

    public boolean setTPDUId(byte[] id) {
        try {
            int size = 0;
            size = id.length;
            if (size == 1) {
                System.arraycopy(id, 0, output, pointerOutput, size);
                pointerOutput += size;
                return true;
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.exception(TAG, e);
            return false;
        }
    }

    public boolean setTPDUSource(String source) {
        try {
            byte[] tmp = null;
            int size = 0;
            HexEncoding strToByte = new HexEncoding();
            tmp = strToByte.getBytes(source);
            size = tmp.length;
            if (size == 2) {
                System.arraycopy(tmp, 0, output, pointerOutput, size);
                pointerOutput += size;
                return true;
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.exception(TAG, e);
            return false;
        }
    }

    public boolean setTPDUSource(byte[] source) {
        try {
            int size = 0;
            size = source.length;
            if (size == 2) {
                System.arraycopy(source, 0, output, pointerOutput, size);
                pointerOutput += size;
                return true;
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.exception(TAG, e);
            return false;
        }
    }

    public boolean setTPDUDestination(String destination) {
        try {
            byte[] tmp = null;
            int size = 0;
            HexEncoding strToByte = new HexEncoding();
            tmp = strToByte.getBytes(destination);
            size = tmp.length;
            if (size == 2) {
                System.arraycopy(tmp, 0, output, pointerOutput, size);
                pointerOutput += size;
                return true;
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.exception(TAG, e);
            return false;
        }
    }

    public boolean setTPDUDestination(byte[] destination) {
        try {
            int size = 0;
            size = destination.length;
            if (size == 2) {
                System.arraycopy(destination, 0, output, pointerOutput, size);
                pointerOutput += size;
                return true;
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.exception(TAG, e);
            return false;
        }
    }

    public boolean setMsgType(String msgType) {
        try {
            byte[] tmp = new byte[2];
            HexEncoding strToByte = new HexEncoding();
            tmp = strToByte.getBytes(msgType);
            if (msgType.length() == 4) {
                System.arraycopy(tmp, 0, output, pointerOutput, 2);
                pointerOutput += 2;
                pointerBitmapOutput = pointerOutput;
                pointerOutput += 8;
                return true;
            } else {
                pointerOutput += 2;
                pointerBitmapOutput = pointerOutput;
                pointerOutput += 8;
                return false;
            }
        } catch (Exception e) {
            pointerOutput += 2;
            pointerBitmapOutput = pointerOutput;
            pointerOutput += 8;
            Logger.exception(TAG, e);
            return false;
        }
    }

    public boolean setField(int id, byte[] content) {
        try {
            int size = 0;
            int field = 0;
            field = id - 1;
            int flag = 0x80;
            bitmapOutput[field / 8] |= (byte) (flag >> (field % 8));
            if ((fieldDefinition[id].typeLenght == (byte) ISO.TYPE_LENGHT_FIX) &&
                    (fieldDefinition[id].typeContent == (byte) ISO.TYPE_CONTENT_ATTN)) {
                if ((fieldDefinition[id].size % 2) != 0)
                    size = (fieldDefinition[id].size + 1) / 2;
                else
                    size = fieldDefinition[id].size / 2;
                if (content.length == size) {
                    System.arraycopy(content, 0, output, pointerOutput, content.length);
                    pointerOutput += size;
                    return true;
                } else
                    return false;
            } else if ((fieldDefinition[id].typeLenght == (byte) ISO.TYPE_LENGHT_FIX) &&
                    (fieldDefinition[id].typeContent == (byte) ISO.TYPE_CONTENT_ATTAN)) {
                size = fieldDefinition[id].size;
                if (content.length == size) {
                    System.arraycopy(content, 0, output, pointerOutput, content.length);
                    pointerOutput += size;
                    return true;
                } else
                    return false;
            } else if ((fieldDefinition[id].typeLenght == (byte) ISO.TYPE_LENGHT_FLLLVAR) &&
                    (fieldDefinition[id].typeContent == (byte) ISO.TYPE_CONTENT_ATTAN)) {
                if (content.length <= 999) {
                    pointerOutput = setLenght(output, pointerOutput, content.length);
                    System.arraycopy(content, 0, output, pointerOutput, content.length);
                    pointerOutput += content.length;
                    return true;
                } else
                    return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.exception(TAG, e);
            return false;
        }
    }

    public boolean setField(int id, String content) {
        try {
            int size = 0;
            HexEncoding toByte = new HexEncoding();
            //calculamos el bitmap
            int field = 0;
            field = id - 1;
            int flag = 0x80;
            bitmapOutput[field / 8] = (byte) (bitmapOutput[field / 8] | (byte) (flag >> (field % 8)));
            byte[] tmp = null;
            if ((fieldDefinition[id].typeLenght == (byte) ISO.TYPE_LENGHT_FIX) &&
                    (fieldDefinition[id].typeContent == (byte) ISO.TYPE_CONTENT_ATTN)) {
                if ((fieldDefinition[id].size % 2) != 0)
                    size = (fieldDefinition[id].size + 1) / 2;
                else
                    size = fieldDefinition[id].size / 2;
                tmp = toByte.getBytes(content);
                if (tmp.length == size) {
                    System.arraycopy(tmp, 0, output, pointerOutput, tmp.length);
                    pointerOutput += size;
                    return true;
                } else
                    return false;
            } else if ((fieldDefinition[id].typeLenght == (byte) ISO.TYPE_LENGHT_FIX) &&
                    (fieldDefinition[id].typeContent == (byte) ISO.TYPE_CONTENT_ATTAN)) {
                size = fieldDefinition[id].size;
                tmp = content.getBytes();
                if (tmp.length == size) {
                    System.arraycopy(tmp, 0, output, pointerOutput, tmp.length);
                    pointerOutput += size;
                    return true;
                } else
                    return false;
            } else if ((fieldDefinition[id].typeLenght == (byte) ISO.TYPE_LENGHT_FLLLVAR) &&
                    (fieldDefinition[id].typeContent == (byte) ISO.TYPE_CONTENT_ATTAN)) {
                tmp = content.getBytes();
                if (tmp.length <= 999) {
                    pointerOutput = setLenght(output, pointerOutput, tmp.length);
                    System.arraycopy(tmp, 0, output, pointerOutput, tmp.length);
                    pointerOutput += tmp.length;
                    return true;
                } else
                    return false;
            } else if ((fieldDefinition[id].typeLenght == (byte) ISO.TYPE_LENGHT_FHHHVAR) &&
                    (fieldDefinition[id].typeContent == (byte) ISO.TYPE_CONTENT_ATTAN)) {
                tmp = content.getBytes();
                if (tmp.length <= 65000) {
                    pointerOutput = setLenghtBin(output, pointerOutput, tmp.length);
                    System.arraycopy(tmp, 0, output, pointerOutput, tmp.length);
                    pointerOutput += tmp.length;
                    return true;
                } else
                    return false;
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.exception(TAG, e);
            return false;
        }
    }

    public byte[] getTxnOutput() {
        try {
            byte[] tmp = new byte[pointerOutput];
            System.arraycopy(output, 0, tmp, 0, pointerOutput);
            if (includeLenght == TRUE) {
                tmp[0] = (byte) ((pointerOutput - 2) / 256);
                tmp[1] = (byte) ((pointerOutput - 2) % 256);
            }
            //Copiamos el bitmap
            System.arraycopy(bitmapOutput, 0, tmp, pointerBitmapOutput, 8);
            return tmp;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.exception(TAG, e);
            return new byte[0];
        }
    }

    private void decode() {
        int i = 0;
        int mask = 0x80;
        int idFld = 0;
        int idByte = 0;
        for (i = 0; i < END_FIELD; i++) {
            fieldDecode[i].start = 0;
            fieldDecode[i].size = 0;
            fieldDecode[i].exist = false;
        }
        //lenght
        if (includeLenght == TRUE) {
            fieldDecode[E_1_LENGHT].start = 0;
            fieldDecode[E_1_LENGHT].size = 2;
            fieldDecode[E_1_LENGHT].exist = true;
            pointerInput += 2;
        }
        //tpdu
        if (includeTpdu == TRUE) {
            fieldDecode[E_2_ID_TPDU].start = pointerInput;
            fieldDecode[E_2_ID_TPDU].size = 1;
            fieldDecode[E_2_ID_TPDU].exist = true;
            pointerInput += 1;
            fieldDecode[E_3_DESTINATION].start = pointerInput;
            fieldDecode[E_3_DESTINATION].size = 2;
            fieldDecode[E_3_DESTINATION].exist = true;
            pointerInput += 2;
            fieldDecode[E_4_SOURCE].start = pointerInput;
            fieldDecode[E_4_SOURCE].size = 2;
            fieldDecode[E_4_SOURCE].exist = true;
            pointerInput += 2;
        }
        //Message type
        fieldDecode[E_5_MESSAGE_TYPE].start = pointerInput;
        fieldDecode[E_5_MESSAGE_TYPE].size = 2;
        fieldDecode[E_5_MESSAGE_TYPE].exist = true;
        pointerInput += 2;
        //Primary BitMap
        fieldDecode[E_6_PRIMARY_BITMAP].start = pointerInput;
        fieldDecode[E_6_PRIMARY_BITMAP].size = 8;
        fieldDecode[E_6_PRIMARY_BITMAP].exist = true;
        copyBytes(input, inputBitmap, pointerInput, 0, 8);
        pointerInput += 8;
        //Decode all fields
        for (i = FIELD_01_BITMAP_EXTENDED; i < END_ISO_FIELD; i++) {
            if (i == FIELD_01_BITMAP_EXTENDED) {
                idFld += 1;
                continue;
            }
            if ((mask & inputBitmap[idByte]) != 0) {
                pointerInput = decodeField(idFld, pointerInput);
            }
            mask = (byte) (mask >> 1);
            if (mask == 0) {
                mask = 0x80;
                idByte += 1;
            }
            idFld += 1;
        }

    }

    private int decodeField(int field, int pointer) {
        int size = 0;
        if ((fieldDefinition[field].typeLenght == (byte) ISO.TYPE_LENGHT_FIX) &&
                (fieldDefinition[field].typeContent == (byte) ISO.TYPE_CONTENT_ATTN)) {
            if ((fieldDefinition[field].size % 2) != 0)
                size = (fieldDefinition[field].size + 1) / 2;
            else
                size = fieldDefinition[field].size / 2;
        } else if ((fieldDefinition[field].typeLenght == (byte) ISO.TYPE_LENGHT_FIX) &&
                (fieldDefinition[field].typeContent == (byte) ISO.TYPE_CONTENT_ATTAN))
            size = fieldDefinition[field].size;
        else if ((fieldDefinition[field].typeLenght == (byte) ISO.TYPE_LENGHT_FLLLVAR) &&
                (fieldDefinition[field].typeContent == (byte) ISO.TYPE_CONTENT_ATTAN)) {
            size = getLenght(input, pointer, ISO.TYPE_LENGHT_FLLLVAR);
            pointer += 2;
        } else if ((fieldDefinition[field].typeLenght == (byte) ISO.TYPE_LENGHT_FLLVAR) &&
                (fieldDefinition[field].typeContent == (byte) ISO.TYPE_CONTENT_ATTN)) {
            size = getLenght(input, pointer, ISO.TYPE_LENGHT_FLLVAR);
            pointer += 1;
            if ((size % 2) != 0)
                size = (size + 1) / 2;
            else
                size = size / 2;
        } else if ((fieldDefinition[field].typeLenght == (byte) ISO.TYPE_LENGHT_FHHHVAR)) {
            size = getLenght(input, pointer, ISO.TYPE_LENGHT_FHHHVAR);
            pointer += 2;
        } else
            size = fieldDefinition[field].size;
        fieldDecode[field].exist = true;
        fieldDecode[field].start = pointer;
        fieldDecode[field].size = size;
        pointer += size;
        return pointer;
    }

    public int getSizeField(int field) {
        return fieldDecode[field].size;
    }

    public String getField(int id) {
        try {
            String field = "";
            HexEncoding toHex = new HexEncoding();
            if ((id == E_1_LENGHT) ||
                    (id == E_2_ID_TPDU) ||
                    (id == E_3_DESTINATION) ||
                    (id == E_4_SOURCE) ||
                    (id == E_5_MESSAGE_TYPE) ||
                    (id == E_6_PRIMARY_BITMAP)
            ) {
                field = toHex.hexString(input, fieldDecode[id].start, fieldDecode[id].size);
            } else if ((fieldDefinition[id].typeLenght == (byte) ISO.TYPE_LENGHT_FIX) &&
                    (fieldDefinition[id].typeContent == (byte) ISO.TYPE_CONTENT_ATTN)) {
                field = toHex.hexString(input, fieldDecode[id].start, fieldDecode[id].size);
            } else if ((fieldDefinition[id].typeLenght == (byte) ISO.TYPE_LENGHT_FIX) &&
                    (fieldDefinition[id].typeContent == (byte) ISO.TYPE_CONTENT_ATTAN)) {
                field = toHex.getString(input, fieldDecode[id].start, fieldDecode[id].size);

            } else if ((fieldDefinition[id].typeLenght == (byte) ISO.TYPE_LENGHT_FLLLVAR) &&
                    (fieldDefinition[id].typeContent == (byte) ISO.TYPE_CONTENT_ATTAN)) {
                field = toHex.getString(input, fieldDecode[id].start, fieldDecode[id].size);
            } else if ((fieldDefinition[id].typeLenght == (byte) ISO.TYPE_LENGHT_FLLVAR) &&
                    (fieldDefinition[id].typeContent == (byte) ISO.TYPE_CONTENT_ATTN)) {
                field = toHex.hexString(input, fieldDecode[id].start, fieldDecode[id].size);
            } else if ((fieldDefinition[id].typeLenght == (byte) ISO.TYPE_LENGHT_FHHHVAR) &&
                    (fieldDefinition[id].typeContent == (byte) ISO.TYPE_CONTENT_ATTAN)) {
                field = toHex.getString(input, fieldDecode[id].start, fieldDecode[id].size);
            }
            return field;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.exception(TAG, e);
            return null;
        }
    }

    public byte[] getFieldB(int id) {
        try {
            byte[] outputField = null;
            if (fieldDecode[id].exist) {
                outputField = new byte[fieldDecode[id].size];
                System.arraycopy(input, fieldDecode[id].start, outputField, 0, fieldDecode[id].size);
            }
            return outputField;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.exception(TAG, e);
            return new byte[0];
        }
    }

    public int getLenght(byte[] data, int start, int typeLen) {
        int len = 0;
        try {
            if (typeLen == ISO.TYPE_LENGHT_FLLLVAR) {
                len = (((data[start] & 0xF0) >> 4) * 1000) + ((data[start] & 0x0F) * 100) + (((data[start + 1] & 0xF0) >> 4) * 10) + (data[start + 1] & 0x0F);
            } else if (typeLen == ISO.TYPE_LENGHT_FLLVAR) {
                len = (((data[start] & 0xF0) >> 4) * 10) + (data[start] & 0x0F);
            } else if (typeLen == ISO.TYPE_LENGHT_FHHHVAR) {
                int result = 0;
                int lenField;
                int lenField1;

                lenField = data[start];
                lenField1 = data[start + 1];

                if (lenField < 0) {
                    lenField += 256;
                }
                if (lenField1 < 0) {
                    lenField1 += 256;
                }
                result = lenField * 256;
                result += lenField1;
                len = result;
            }
            return len;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.exception(TAG, e);
            return 0;
        }
    }

    public int setLenght(byte[] txn, int pos, int len) {
        txn[pos] = (byte) ((((((len / 10) / 10) / 10) & 0x0F) << 4) | ((((len / 10) / 10) % 10) & 0x0F));
        txn[pos + 1] = (byte) (((((len / 10) % 10) & 0x0F) << 4) | (len % 10));
        pos += 2;
        return pos;
    }

    public int setLenghtBin(byte[] txn, int pos, int len) {
        txn[pos] = (byte) ((((((len / 16) / 16) / 16) & 0x0F) << 4) | ((((len / 16) / 16) % 16) & 0x0F));
        txn[pos + 1] = (byte) (((((len / 16) % 16) & 0x0F) << 4) | (len % 16));
        pos += 2;
        return pos;
    }

    public byte[] getTime() {
        byte[] tmp = null;
        HexEncoding toByte = new HexEncoding();

        try {
            String auxStr = timeStr();
            tmp = toByte.getBytes(auxStr);
            if (tmp.length == 3)
                return tmp;
            else
                return new byte[0];
        } catch (Exception e) {
            e.printStackTrace();
            Logger.exception(TAG, e);
            return new byte[0];
        }
    }

    public byte[] getDate() {
        byte[] tmp = null;
        HexEncoding toByte = new HexEncoding();

        try {
            String auxStr = dateStr();
            tmp = toByte.getBytes(auxStr);
            if (tmp.length == 2)
                return tmp;
            else
                return new byte[0];
        } catch (Exception e) {
            e.printStackTrace();
            Logger.exception(TAG, e);
            return new byte[0];
        }
    }

    public void initFieldDecode() {
        int i;
        for (i = 0; i < END_FIELD; i++) {
            fieldDecode[i] = new FieldDecodeT(0, 0, false);
        }
    }

    public void initIsoDefinition(FieldDefinitionT[] isoField) {
        //processing code
        isoField[FIELD_03_PROCESSING_CODE] = new FieldDefinitionT(ISO.TYPE_LENGHT_FIX,
                ISO.TYPE_CONTENT_ATTN,
                6);
        //amount
        isoField[FIELD_04_AMOUNT_TRANSACTION] = new FieldDefinitionT(ISO.TYPE_LENGHT_FIX,
                ISO.TYPE_CONTENT_ATTN,
                12);
        //TIMEDATE
        isoField[FIELD_07_TRANSMISSION_DATE_TIME_MMDDHHMMSS] = new FieldDefinitionT(ISO.TYPE_LENGHT_FIX,
                ISO.TYPE_CONTENT_ATTN,
                10);
        //stan
        isoField[FIELD_11_SYSTEMS_TRACE_AUDIT_NUMBER] = new FieldDefinitionT(ISO.TYPE_LENGHT_FIX,
                ISO.TYPE_CONTENT_ATTN,
                6);
        //time
        isoField[FIELD_12_TIME_LOCAL_TRANSACTION_HHMMSS] = new FieldDefinitionT(ISO.TYPE_LENGHT_FIX,
                ISO.TYPE_CONTENT_ATTN,
                6);
        //date
        if (isoModificado) {
            isoField[FIELD_13_DATE_LOCAL_TRANSACTION_MMDD] = new FieldDefinitionT(ISO.TYPE_LENGHT_FIX,
                    ISO.TYPE_CONTENT_ATTN,
                    8);
        } else {
            isoField[FIELD_13_DATE_LOCAL_TRANSACTION_MMDD] = new FieldDefinitionT(ISO.TYPE_LENGHT_FIX,
                    ISO.TYPE_CONTENT_ATTN,
                    4);
        }

        //nii
        isoField[FIELD_24_NETWORK_INTERNATIONAL_IDENTIFIER] = new FieldDefinitionT(ISO.TYPE_LENGHT_FIX,
                ISO.TYPE_CONTENT_ATTN,
                4);
        //field35
        isoField[FIELD_35_TRACK_2_DATA] = new FieldDefinitionT(ISO.TYPE_LENGHT_FLLVAR,
                ISO.TYPE_CONTENT_ATTN,
                8);

        //rrn
        isoField[FIELD_37_RETRIEVAL_REFERENCE_NUMBER] = new FieldDefinitionT(ISO.TYPE_LENGHT_FIX,
                ISO.TYPE_CONTENT_ATTAN,
                12);

        //Authorization id
        isoField[FIELD_38_AUTHORIZATION_IDENTIFICATION_RESPONSE] = new FieldDefinitionT(ISO.TYPE_LENGHT_FIX,
                ISO.TYPE_CONTENT_ATTAN,
                8);
        //Response code
        isoField[FIELD_39_RESPONSE_CODE] = new FieldDefinitionT(ISO.TYPE_LENGHT_FIX,
                ISO.TYPE_CONTENT_ATTAN,
                2);
        //terminal id
        isoField[FIELD_41_CARD_ACCEPTOR_TERMINAL_IDENTIFICATION] = new FieldDefinitionT(ISO.TYPE_LENGHT_FIX,
                ISO.TYPE_CONTENT_ATTAN,
                8);
        //acquirer id
        isoField[FIELD_42_CARD_ACCEPTOR_IDENTIFICATION_CODE] = new FieldDefinitionT(ISO.TYPE_LENGHT_FIX,
                ISO.TYPE_CONTENT_ATTAN,
                15);
        //field47  (additional iva...)
        isoField[FIELD_47_ADDITIONAL_DATA_NATIONAL] = new FieldDefinitionT(ISO.TYPE_LENGHT_FLLLVAR,
                ISO.TYPE_CONTENT_ATTAN,
                8);
        //field48
        isoField[FIELD_48_ADDITIONAL_DATA_PRIVATE] = new FieldDefinitionT(ISO.TYPE_LENGHT_FLLLVAR,
                ISO.TYPE_CONTENT_ATTAN,
                8);

        //field54
        isoField[FIELD_54_ADDITIONAL_AMOUNTS] = new FieldDefinitionT(ISO.TYPE_LENGHT_FLLLVAR,
                ISO.TYPE_CONTENT_ATTAN,
                8);


        //field58
        isoField[FIELD_58_RESERVED_NATIONAL] = new FieldDefinitionT(ISO.TYPE_LENGHT_FLLLVAR,
                ISO.TYPE_CONTENT_ATTAN,
                8);

        //PinBlock
        isoField[FIELD_52_PERSONAL_IDENTIFICATION_NUMBER_PIN_DATA] = new FieldDefinitionT(ISO.TYPE_LENGHT_FIX,
                ISO.TYPE_CONTENT_ATTN,
                16);
        //field60
        isoField[FIELD_60_RESERVED_PRIVATE] = new FieldDefinitionT(ISO.TYPE_LENGHT_FLLLVAR,
                ISO.TYPE_CONTENT_ATTAN,
                8);
        //field61
        isoField[FIELD_61_RESERVED_PRIVATE] = new FieldDefinitionT(ISO.TYPE_LENGHT_FLLLVAR,
                ISO.TYPE_CONTENT_ATTAN,
                8);
        //field62
        isoField[FIELD_62_RESERVED_PRIVATE] = new FieldDefinitionT(ISO.TYPE_LENGHT_FHHHVAR,
                ISO.TYPE_CONTENT_ATTAN,
                8);
        //field63
        isoField[FIELD_63_RESERVED_PRIVATE] = new FieldDefinitionT(ISO.TYPE_LENGHT_FLLLVAR,
                ISO.TYPE_CONTENT_ATTAN,
                8);

        //field64
        isoField[FIELD_64_MESSAGE_AUTHENTICATION_CODE] = new FieldDefinitionT(ISO.TYPE_LENGHT_FHHHVAR,
                ISO.TYPE_CONTENT_ATTAN,
                8);
    }

    public String timeStr() {
        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        int hr = cal.get(Calendar.HOUR);
        int mi = cal.get(Calendar.MINUTE);
        int se = cal.get(Calendar.SECOND);
        String hrC;
        String miC;
        String seC;

        ///Hora
        if (cal.get(Calendar.AM_PM) == Calendar.AM) {
            if (hr == 0)
                hr = 12;  ///Noon

            if (hr < 10) {
                hrC = "0" + hr;
            } else {
                hrC = String.valueOf(hr);
            }
        } else {
            hrC = String.valueOf(hr + 12); ///Se agregó para pointpay un + 1
        }

        ///Minuto
        if (mi < 10) {
            miC = "0" + mi;
        } else {
            miC = String.valueOf(mi);
        }

        ///Segundo
        if (se < 10) {
            seC = "0" + se;
        } else {
            seC = String.valueOf(se);
        }

        return hrC + miC + seC;
    }

    public String dateStr() {
        Calendar cal = Calendar.getInstance();
        int mm = cal.get(Calendar.MONTH) + 1;
        int dd = cal.get(Calendar.DAY_OF_MONTH);
        String mmC;
        String ddC;

        ///Mes
        if (mm < 10) {
            mmC = "0" + mm;
        } else {
            mmC = String.valueOf(mm);
        }

        ///Dia
        if (dd < 10) {
            ddC = "0" + dd;
        } else {
            ddC = String.valueOf(dd);
        }
        return mmC + ddC;
    }

    public static class FieldDefinitionT {
        int typeLenght;
        int size;
        int typeContent;

        public FieldDefinitionT(int typeLenght, int typeContent, int size) {
            this.typeLenght = typeLenght;
            this.size = size;
            this.typeContent = typeContent;
        }
    }

    public static class FieldDecodeT {
        private boolean exist;
        private int start;
        private int size;

        FieldDecodeT(int start, int size, boolean exist) {
            this.start = start;
            this.size = size;
            this.exist = exist;
        }
    }


}
