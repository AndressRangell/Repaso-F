package com.flota.inicializacion.init_emv;

import static com.flota.inicializacion.trans_init.Init.NAME_DB;
import static org.jpos.stis.Util.hex2byte;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.widget.Toast;

import com.flota.inicializacion.trans_init.trans.DbHelper;
import com.newpos.libpay.Logger;
import com.pos.device.emv.EMVHandler;
import com.pos.device.emv.IEMVHandler;
import com.pos.device.emv.TerminalAidInfo;
import com.wposs.flota.R;

import org.jpos.stis.TLV_parsing;

import cn.desert.newpos.payui.UIUtils;


/**
 * @author francisco
 */
public class EmvAppRow {

    public static final String[] fields = new String[]{
            "eAID",
            "eType",
            "eBitField",
            "eRSBThresh",
            "eRSTarget",
            "eRSBMax",
            "eTACDenial",
            "eTACOnline",
            "eTACDefault",
            "eACFG",
            "eACFGPlain"
    };
    private static final String TAG = "EmvAppRow.clase";
    private static EmvAppRow emvappRow;
    String clase = "EMVAPP_ROW.java";
    private String eAID;
    private String eType;
    private String eBitField;
    private String eRSBThresh;
    private String eRSTarget;
    private String eRSBMax;
    private String eTACDenial;
    private String eTACOnline;
    private String eTACDefault;
    private String eACFG;
    private String eACFGPlain;
    private boolean showDebug = true;


    private EmvAppRow() {
    }

    public static EmvAppRow getSingletonInstance() {
        if (emvappRow == null) {
            emvappRow = new EmvAppRow();
        } else {
            Logger.error(TAG, "No se puede crear otro objeto, ya existe");
        }
        return emvappRow;
    }

    public void setEmvAppRow(String column, String value) {
        switch (column) {
            case "eAID":
                seteAID(value);
                break;
            case "eType":
                seteType(value);
                break;
            case "eBitField":
                seteBitField(value);
                break;
            case "eRSBThresh":
                seteRSBThresh(value);
                break;
            case "eRSTarget":
                seteRSTarget(value);
                break;
            case "eRSBMax":
                seteRSBMax(value);
                break;
            case "eTACDenial":
                seteTACDenial(value);
                break;
            case "eTACOnline":
                seteTACOnline(value);
                break;
            case "eTACDefault":
                seteTACDefault(value);
                break;
            case "eACFG":
                seteACFG(value);
                break;
            case "eACFGPlain":
                seteACFGPlain(value);
                break;

            default:
                break;
        }

    }

    public boolean checkSigned() {
        StringBuilder textToVerify = new StringBuilder();
        textToVerify.append(this.eAID);
        textToVerify.append(this.eType);
        textToVerify.append(this.eBitField);
        textToVerify.append(this.eRSBThresh);
        textToVerify.append(this.eRSTarget);
        textToVerify.append(this.eRSBMax);
        textToVerify.append(this.eTACDenial);
        textToVerify.append(this.eTACOnline);
        textToVerify.append(this.eTACDefault);
        textToVerify.append(this.eACFG);
        textToVerify.append(this.eACFGPlain);

        return true;
    }

    public void clearEmvAppRow() {
        for (String s : EmvAppRow.fields) {
            setEmvAppRow(s, "");
        }
    }

    public void selectEmvAppRow(Context context) {
        DbHelper databaseAccess = new DbHelper(context, NAME_DB, null, 1);
        databaseAccess.openDb(NAME_DB);

        String sql = consutlaSQL();
        Cursor cursor = databaseAccess.rawQuery(sql);
        Logger.error(TAG, "********************* EMVAPP SQL ******** " + sql);
        try {
            cursor.moveToFirst();
            int indexColumn;
            IEMVHandler emvHandler = EMVHandler.getInstance();
            while (!cursor.isAfterLast()) {
                clearEmvAppRow();
                indexColumn = 0;
                for (String s : EmvAppRow.fields) {
                    setEmvAppRow(s, cursor.getString(indexColumn++));
                }

                if (showDebug) {
                    Logger.debug("emvinit", "\n" + this.getString());
                    Logger.debug("emvinit", "EMVAPP_ROW checkSigned: " + (this.checkSigned() ? "true" : "false"));
                    Logger.error(TAG, "");
                    Logger.error(TAG, "");
                    Logger.error(TAG, "");
                }

                TLV_parsing tlvParsing = new TLV_parsing(geteACFG());

                if (showDebug) {
                    Logger.debug("emvinit:", "\n 9F06: " + tlvParsing.getValue(0x9F06));
                    Logger.debug("emvinit: ", "\n" + tlvParsing.getAllTags());
                }

                TerminalAidInfo terminalAidInfo = new TerminalAidInfo();
                terminalAidInfo.setAIDdLength(tlvParsing.getValueB(0x9f06).length);
                terminalAidInfo.setAId(tlvParsing.getValueB(0x9f06));


                byte[] tmp = hex2byte(this.geteBitField());
                //enable
                terminalAidInfo.setSupportPartialAIDSelect((tmp[0] &= 0x01) != 0x01);//disable

                terminalAidInfo.setApplicationPriority(0);
                terminalAidInfo.setTargetPercentage(0);
                terminalAidInfo.setMaximumTargetPercentage(0);


                if (!tlvParsing.getValue(0x9f1b).equals("NA")) {
                    terminalAidInfo.setTerminalFloorLimit(Integer.parseInt(tlvParsing.getValue(0x9f1b)));
                }

                terminalAidInfo.setThresholdValue(Integer.parseInt(this.geteRSBMax()));
                terminalAidInfo.setTerminalActionCodeDenial(hex2byte(this.geteTACDenial()));
                terminalAidInfo.setTerminalActionCodeOnline(hex2byte(this.geteTACOnline()));
                terminalAidInfo.setTerminalActionCodeDefault(hex2byte(this.geteTACDefault()));

                if (!tlvParsing.getValue(0x9f01).equals("NA")) {
                    terminalAidInfo.setAcquirerIdentifier(tlvParsing.getValueB(0x9f01));
                }

                byte[] ddol = tlvParsing.getValueB(0x9f49);
                if (ddol != null) {
                    terminalAidInfo.setLenOfDefaultDDOL(ddol.length);
                    terminalAidInfo.setDefaultDDOL(ddol);
                }


                byte[] tdol = tlvParsing.getValueB(0x0097);
                if (tdol != null) {
                    terminalAidInfo.setLenOfDefaultTDOL(tdol.length);
                    terminalAidInfo.setDefaultTDOL(tdol);
                }


                byte[] applicationVersion = tlvParsing.getValueB(0x9F09);
                if (applicationVersion != null) {
                    terminalAidInfo.setApplicationVersion(applicationVersion);
                }

                int rta = emvHandler.addAidInfo(terminalAidInfo);

                if (showDebug)
                    Logger.debug("emvinitapp", "load aid, aid: " + tlvParsing.getValue(0x9f06) + " - Result: " + rta);

                cursor.moveToNext();

            }
            cursor.close();
        } catch (Exception e) {
            Logger.exception(clase, e);
            Logger.error(TAG, e);
            UIUtils.toast((Activity) context, R.drawable.ic_redinfonet, "Error al cargar AID", Toast.LENGTH_SHORT);
            cursor.moveToNext();
        }
        databaseAccess.closeDb();
    }

    private String consutlaSQL() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ");
        int counter = 1;
        for (String s : EmvAppRow.fields) {
            sql.append(s);
            if (counter++ < EmvAppRow.fields.length) {
                sql.append(",");
            }
        }
        sql.append(" from emvapps");
        sql.append(";");

        return sql.toString();
    }

    public String getString() {
        StringBuilder sb = new StringBuilder();
        sb.append("EMVAPP_ROW: \n");

        sb.append("\t");
        sb.append("eAID: ");
        sb.append(this.eAID);
        sb.append("\n");

        sb.append("\t");
        sb.append("eType: ");
        sb.append(this.eType);
        sb.append("\n");

        sb.append("\t");
        sb.append("eBitField: ");
        sb.append(this.eBitField);
        sb.append("\n");

        sb.append("\t");
        sb.append("eRSBThresh: ");
        sb.append(this.eRSBThresh);
        sb.append("\n");

        sb.append("\t");
        sb.append("eRSTarget: ");
        sb.append(this.eRSTarget);
        sb.append("\n");

        sb.append("\t");
        sb.append("eRSBMax: ");
        sb.append(this.eRSBMax);
        sb.append("\n");

        sb.append("\t");
        sb.append("eTACDenial: ");
        sb.append(this.eTACDenial);
        sb.append("\n");

        sb.append("\t");
        sb.append("eTACOnline: ");
        sb.append(this.eTACOnline);
        sb.append("\n");

        sb.append("\t");
        sb.append("eTACDefault: ");
        sb.append(this.eTACDefault);
        sb.append("\n");

        sb.append("\t");
        sb.append("eACFG: ");
        sb.append(this.eACFG);
        sb.append("\n");

        sb.append("\t");
        sb.append("eACFGPlain: ");
        sb.append(this.eACFGPlain);
        sb.append("\n");

        return sb.toString();

    }


    public String geteAID() {
        return eAID;
    }

    public void seteAID(String eAID) {
        this.eAID = eAID;
    }

    public String geteType() {
        return eType;
    }

    public void seteType(String eType) {
        this.eType = eType;
    }

    public String geteBitField() {
        return eBitField;
    }

    public void seteBitField(String eBitField) {
        this.eBitField = eBitField;
    }

    public String geteRSBThresh() {
        return eRSBThresh;
    }

    public void seteRSBThresh(String eRSBThresh) {
        this.eRSBThresh = eRSBThresh;
    }

    public String geteRSTarget() {
        return eRSTarget;
    }

    public void seteRSTarget(String eRSTarget) {
        this.eRSTarget = eRSTarget;
    }

    public String geteRSBMax() {
        return eRSBMax;
    }

    public void seteRSBMax(String eRSBMax) {
        this.eRSBMax = eRSBMax;
    }

    public String geteTACDenial() {
        return eTACDenial;
    }

    public void seteTACDenial(String eTACDenial) {
        this.eTACDenial = eTACDenial;
    }

    public String geteTACOnline() {
        return eTACOnline;
    }

    public void seteTACOnline(String eTACOnline) {
        this.eTACOnline = eTACOnline;
    }

    public String geteTACDefault() {
        return eTACDefault;
    }

    public void seteTACDefault(String eTACDefault) {
        this.eTACDefault = eTACDefault;
    }

    public String geteACFG() {
        return eACFG;
    }

    public void seteACFG(String eACFG) {
        this.eACFG = eACFG;
    }

    public String geteACFGPlain() {
        return eACFGPlain;
    }

    public void seteACFGPlain(String eACFGPlain) {
        this.eACFGPlain = eACFGPlain;
    }
}
