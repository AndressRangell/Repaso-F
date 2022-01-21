package interactor.casting_application;

import android.annotation.SuppressLint;
import android.content.Context;

import com.flota.inicializacion.configuracioncomercio.Tareas;
import com.flota.inicializacion.trans_init.trans.ISO;
import com.flota.inicializacion.trans_init.trans.Tools;
import com.google.common.base.Strings;
import com.newpos.libpay.Logger;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.utils.ISOUtil;
import com.wposs.flota.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CastingTransPackImpl implements interactor.casting_application.TransPack {

    public static final String TAG = "CastingTransPackImpl.java";

    private Context context;
    private List<Tareas> tasks;

    public CastingTransPackImpl(Context context, List<Tareas> tasks) {
        this.context = context;
        this.tasks = tasks;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getCurrentTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
        return sdf.format(new Date());
    }

    @Override
    public byte[] packIsoInit() {
        String nii = context.getResources().getString(R.string.niiConfig);
        try {
            nii = ISOUtil.padleft(nii, 4, '0');
        } catch (Exception e) {
            e.printStackTrace();
        }

        ISO iso = new ISO(ISO.LENGHT_INCLUDE, ISO.TPDU_INCLUDE);
        iso.setTPDUId("60");
        iso.setTPDUDestination(nii);
        iso.setTPDUSource("0000");
        iso.setMsgType("0800");
        iso.setField(ISO.FIELD_03_PROCESSING_CODE, "510100");
        iso.setField(ISO.FIELD_11_SYSTEMS_TRACE_AUDIT_NUMBER,
                Strings.padStart(String.valueOf(TMConfig.getInstance().getTraceNo()), 6, '0'));
        iso.setField(ISO.FIELD_41_CARD_ACCEPTOR_TERMINAL_IDENTIFICATION, setTID()); // "20123456"
        if (tasks != null && !tasks.isEmpty()) {
            String campo58 = tasks.get(0).getTarea() + "," + getCurrentTimeStamp();
            iso.setField(ISO.FIELD_58_RESERVED_NATIONAL, campo58);
        }
        iso.setField(ISO.FIELD_61_RESERVED_PRIVATE, Tools.getSerial());

        return iso.getTxnOutput();
    }

    public String setTID() {
        try {
            String serial = Tools.getSerial(); // 4 digitos iniciales del serial corresponden al modelo
            return serial.substring(2, serial.length());
        } catch (Exception e) {
            Logger.exception(TAG, e);
        }
        return "";
    }
}
