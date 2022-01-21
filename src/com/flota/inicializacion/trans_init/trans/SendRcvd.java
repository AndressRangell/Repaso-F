package com.flota.inicializacion.trans_init.trans;

import static com.flota.defines_bancard.DefinesBANCARD.ENTRY_POINT;
import static com.flota.defines_bancard.DefinesBANCARD.PROCESSING;
import static com.flota.defines_bancard.DefinesBANCARD.REVOK;
import static com.flota.defines_bancard.DefinesBANCARD.TERMINAL;
import static com.flota.inicializacion.trans_init.Init.APLICACIONES;
import static com.flota.inicializacion.trans_init.Init.CAPKS;
import static com.flota.inicializacion.trans_init.Init.CARDS;
import static com.flota.inicializacion.trans_init.Init.COMERCIOS;
import static com.flota.inicializacion.trans_init.Init.DEVICE;
import static com.flota.inicializacion.trans_init.Init.EMVAPPS;
import static com.flota.inicializacion.trans_init.Init.HOST;
import static com.flota.inicializacion.trans_init.Init.INIT_PARCIAL;
import static com.flota.inicializacion.trans_init.Init.INIT_TOTAL;
import static com.flota.inicializacion.trans_init.Init.IPS;
import static com.flota.inicializacion.trans_init.Init.PLANES;
import static com.flota.inicializacion.trans_init.Init.RED;
import static com.flota.inicializacion.trans_init.Init.SUCURSAL;
import static com.flota.inicializacion.trans_init.Init.TRANSACCIONES;
import static com.flota.inicializacion.trans_init.Init.tareas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.common.base.Strings;
import com.newpos.libpay.Logger;
import com.newpos.libpay.global.TMConfig;
import com.newpos.libpay.utils.ISOUtil;
import com.wposs.flota.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import cn.desert.newpos.payui.UIUtils;


/**
 * Created by Technology&Solutions on 27/03/2017.
 */

/**
 * Send and wait response from host.
 */
public class SendRcvd extends AsyncTask<Void, Integer, byte[]> {

    public static final String TAG = "SendClass";
    public static final int TIMEOUT_DEFAULT = 1;
    public static final int TIMEOUT_SOCKET = 4;
    public static final int NO_ACCESS_INTERNET = 2;
    public static final int HOST_OFF = 3;
    private static final String TXT_T = ".txtT";
    private static final String P_310100 = "310100";
    private final String ipHost;
    private final int portHost;
    private final int timeout;
    private final Context context;
    Socket clientRequest = null;
    private int resultTx = 0;
    private InputStream in;
    private OutputStream dis;
    private TcpCallback callback;
    private ProgressDialog pd;

    private String pathDefault;
    private String nii;
    private String tid;
    private int tramaQueEnvia;
    private String fileName;
    private String offset;
    private File file;
    private String resultOk;

    private boolean isWithMensaje;

    /**
     * @param ipHost
     * @param portHost
     * @param timeOut
     * @param ctx
     * @param callback
     */
    public SendRcvd(String ipHost, int portHost, int timeOut, Context ctx, final TcpCallback callback) {
        this.callback = callback;
        this.ipHost = ipHost;
        this.portHost = portHost;
        this.timeout = timeOut;
        this.context = ctx;

        pd = new ProgressDialog(ctx);
    }

    public SendRcvd(String ipHost, int portHost, int timeOut, Context ctx) {
        this.ipHost = ipHost;
        this.portHost = portHost;
        this.timeout = timeOut;
        this.context = ctx;
        this.isWithMensaje = true;
    }

    /**
     * Check if to port of server is open
     *
     * @param ip
     * @param port
     * @param timeout
     * @return
     */
    public static boolean isPortOpen(final String ip, final int port, final int timeout) {
        try {
            Socket socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), timeout);
            socket.close();

            Thread.sleep(100);
            return true;
        } catch (Exception e) {
            if (e instanceof InterruptedException) {
                Thread.currentThread().interrupt();
            }
            e.printStackTrace();
            Logger.exception(TAG, e);
            return false;
        }
    }

    public void callbackResponse(final TcpCallback callback) {
        this.callback = callback;
    }

    public String getPathDefault() {
        return pathDefault;
    }

    public void setPathDefault(String pathDefault) {
        this.pathDefault = pathDefault;
    }

    public String getNii() {
        return nii;
    }

    public void setNii(String nii) {
        this.nii = nii;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public int getTramaQueEnvia() {
        return tramaQueEnvia;
    }

    public void setTramaQueEnvia(int tramaQueEnvia) {
        this.tramaQueEnvia = tramaQueEnvia;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public void setWithMensaje(boolean withMensaje) {
        isWithMensaje = withMensaje;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pd = new ProgressDialog(context);
        this.pd.setCancelable(false);
        this.pd.show();
        this.pd.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        this.pd.setContentView(R.layout.progress_inicializacion);
        TextView textView = pd.findViewById(R.id.Texto);
        textView.setText("Inicializando POS");
        mostrarSerialvsVersion(pd);
    }

    private void mostrarSerialvsVersion(ProgressDialog pd) {
        TextView tvVersion = pd.findViewById(R.id.tvVersion);
        TextView tvSerial = pd.findViewById(R.id.tvSerial);
        UIUtils.mostrarSerialvsVersion(tvVersion, tvSerial);
    }

    @Override
    protected byte[] doInBackground(Void... voids) {

        long waitTime;
        byte[] lenIsoRx = new byte[2];
        ByteArrayOutputStream byteOs;
        byteOs = new ByteArrayOutputStream();

        if (!isNetworkAvailable()) {
            resultTx = NO_ACCESS_INTERNET;
            return new byte[0];
        }

        try {

            clientRequest = new Socket();
            clientRequest.setSoTimeout(timeout);

            clientRequest.connect(new InetSocketAddress(ipHost, portHost), 5000);

            in = clientRequest.getInputStream();
            dis = clientRequest.getOutputStream();

            if (clientRequest.isConnected()) {

                offset = "" + calcularOffset(fileName, true);
                while (true) {
                    byte[] txBuf = packIsoInit();

                    Logger.info(TAG, "Sending...");
                    Logger.info(TAG, ISOUtil.hexString(txBuf));
                    dis.write(txBuf);
                    dis.flush();

                    waitTime = System.currentTimeMillis() + this.timeout;

                    do {
                        if (System.currentTimeMillis() >= waitTime) {
                            resultTx = TIMEOUT_DEFAULT;
                            break;
                        }


                        int i;
                        int len;
                        long total = 0;
                        int lenpp = 0;
                        byte[] bb;


                        try {
                            byteOs = new ByteArrayOutputStream();
                            if ((i = in.read(lenIsoRx)) != -1) {
                                len = ISOUtil.bcdToInt(lenIsoRx);
                                bb = new byte[len + 2];

                                lenpp = len;
                                while (len > 0 && (i = in.read(bb)) != -1) {
                                    total += i;
                                    publishProgress((int) ((total * 100) / lenpp));
                                    byteOs.write(bb, 0, i);
                                    len -= i;
                                }
                                break;
                            }
                        } catch (InterruptedIOException e) {
                            // 读取超时处理
                            Logger.info("PAY_SDK", "Recive：3 读取流数据超时异常 1");
                            Logger.exception(TAG, e);
                            return new byte[0];
                        }
                    } while (true);
                    Logger.info(TAG, "Connection closing...");

                    if (resultTx == TIMEOUT_DEFAULT) {
                        break;
                    }


                    try {
                        byte[] rxBuf = byteOs.toByteArray();

                        if (rxBuf == null)
                            break;

                        //Recibe la rx del host
                        Logger.info(TAG, "Receiving...");
                        Logger.info(TAG, ISOUtil.hexString(rxBuf));

                        if (unpackDescarga(new ISO(rxBuf, ISO.LENGHT_NOT_INCLUDE, ISO.TPDU_INCLUDE))) {
                            ISO rspIso = new ISO(rxBuf, ISO.LENGHT_NOT_INCLUDE, ISO.TPDU_INCLUDE);
                            TMConfig.getInstance().incTraceNo().save();

                            Logger.info(TAG, rspIso.getField(ISO.FIELD_60_RESERVED_PRIVATE));

                            if (rspIso.getField(ISO.FIELD_03_PROCESSING_CODE).equals("310101")) {
                                offset = "" + calcularOffset(fileName, false);
                            } else if (rspIso.getField(ISO.FIELD_03_PROCESSING_CODE).equals(P_310100)) {
                                resultOk = "OK"; // Finaliza inicializacion con 310100
                                break;
                            }
                        } else {
                            break;
                        }
                    } catch (Exception e) {
                        Logger.exception(TAG, e);
                        return new byte[0];
                    }
                }
            } else {
                Logger.error("Clt", "Client no connected..");
                resultTx = HOST_OFF;
            }

        } catch (IOException e) {
            Logger.error(TAG, "The port of server is closed...");
            resultTx = mensajeExceptiones(e);
            return new byte[0];

        } finally {

            try {
                if (resultTx != HOST_OFF && resultTx != TIMEOUT_SOCKET) {
                    in.close();
                    dis.close();
                    clientRequest.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Logger.exception(TAG, e);
            }
        }
        return byteOs.toByteArray();
    }

    private int mensajeExceptiones(IOException e) {
        if (e instanceof SocketTimeoutException) {
            return TIMEOUT_SOCKET;
        } else if (e instanceof UnknownHostException) {
            return HOST_OFF;
        } else {
            e.printStackTrace();
            Logger.exception(TAG, e);
            return HOST_OFF;
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(byte[] iso) {
        super.onPostExecute(iso);
        try {

            //Barra indicadora de progreso
            if (pd != null && pd.isShowing())
                pd.dismiss();
            //Check messages of error
            validatedMessageError(resultTx);
            //call for process the buffer
            callback.rspHost(iso, resultOk);

        } catch (Exception e) {
            e.printStackTrace();
            Logger.exception(TAG, e);
        }
    }

    /**
     * Check if device have connectivity to internet
     *
     * @return
     */
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    /**
     * Show messages of error
     *
     * @param msgE
     */
    public void validatedMessageError(int msgE) {
        if (isWithMensaje) {
            switch (msgE) {
                case TIMEOUT_DEFAULT:
                    UIUtils.toast((Activity) context, R.drawable.redinfonet, "ERROR, TIEMPO DE ESPERA AGOTADO", Toast.LENGTH_LONG);
                    break;
                case NO_ACCESS_INTERNET:
                    UIUtils.toast((Activity) context, R.drawable.infonetwhite, "ERROR, NO HAY CONEXIÓN A INTERNET", Toast.LENGTH_LONG);
                    break;
                case HOST_OFF:
                    UIUtils.toast((Activity) context, R.drawable.redinfonet, "ERROR, NO HAY CONEXIÓN CON EL SERVIDOR", Toast.LENGTH_LONG);
                    break;
                case TIMEOUT_SOCKET:
                    UIUtils.toast((Activity) context, R.drawable.redinfonet, "TIEMPO DE ESPERA DE RESPUESTA DEL SERVIDOR AGOTADO", Toast.LENGTH_LONG);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + msgE);
            }
        }
    }

    //===================== proceso init ==================
    private long calcularOffset(String fileName, boolean deleteFile) {
        long len = 0;
        java.io.File dir = new File(pathDefault);
        file = new File(pathDefault + File.separator + fileName + "T");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        if (file.exists()) {
            if (deleteFile)
                file.delete();

            len = file.length();

        } else {
            try {
                if (!file.createNewFile()) {
                    Logger.debug("tag", "create file fail");
                    return -1;
                }
            } catch (IOException e) {
                e.printStackTrace();
                Logger.exception(TAG, e);
                return -1;
            }
        }
        return len;
    }

    private String getHash(String fileNameTable, boolean forcedInit) {
        String ret = "NA";
        FileInputStream fileIn;
        if (!forcedInit) {

            File fileToRead = new File(pathDefault + File.separator + fileNameTable);
            try {
                if (fileToRead.exists()) {
                    fileIn = new FileInputStream(fileToRead);
                    InputStreamReader inputRead = new InputStreamReader(fileIn);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();

                    byte[] inputBuffer = new byte[1024];
                    int charRead;

                    while ((charRead = fileIn.read(inputBuffer)) > 0) {
                        bos.write(inputBuffer, 0, charRead);
                    }
                    inputRead.close();
                    bos.close();

                    ret = Tools.hashSha1(bos.toByteArray());
                }
            } catch (Exception e) {
                e.printStackTrace();
                Logger.exception(TAG, e);
            }
        }
        return ret;
    }

    private byte[] armarTramaDescarga(String aFileName, String aOffset) {
        String outField60 = null;
        String solicitudBytes = "65000";
        byte[] tmp = null;

        ISO iso = new ISO(ISO.LENGHT_INCLUDE, ISO.TPDU_INCLUDE);
        iso.setTPDUId("60");
        nii = ISOUtil.padleft(nii + "", 4, '0');
        iso.setTPDUDestination(nii);
        iso.setTPDUSource("0000");

        iso.setMsgType("0800");
        iso.setField(ISO.FIELD_03_PROCESSING_CODE, P_310100);
        iso.setField(ISO.FIELD_11_SYSTEMS_TRACE_AUDIT_NUMBER, Strings.padStart(String.valueOf(
                TMConfig.getInstance().getTraceNo()), 6, '0'));

        outField60 = aFileName + "," + aOffset + "," + solicitudBytes;
        iso.setField(ISO.FIELD_60_RESERVED_PRIVATE, outField60);
        iso.setField(ISO.FIELD_61_RESERVED_PRIVATE, getTid());
        iso.setField(ISO.FIELD_62_RESERVED_PRIVATE, "NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|NA|");

        tmp = iso.getTxnOutput();

        return tmp;
    }

    private byte[] armarTramaDescargaParcial(String aFileName, String aOffset, boolean forcedInit) {
        String outField61 = null;
        String separator = "|";
        String tidAUX = aFileName.replace(".zip", "");

        String offset = null;
        byte[] tmp = null;

        offset = aOffset;

        ISO iso = new ISO(ISO.LENGHT_INCLUDE, ISO.TPDU_INCLUDE);
        iso.setTPDUId("60");
        nii = ISOUtil.padleft(nii + "", 4, '0');
        iso.setTPDUDestination(nii);
        iso.setTPDUSource("0000");

        iso.setMsgType("0800");
        if (offset.equals("0"))
            iso.setField(ISO.FIELD_03_PROCESSING_CODE, P_310100);
        else
            iso.setField(ISO.FIELD_03_PROCESSING_CODE, "310101");

        iso.setField(ISO.FIELD_11_SYSTEMS_TRACE_AUDIT_NUMBER, Strings.padStart(String.valueOf(TMConfig.getInstance().getTraceNo()), 6, '0'));
        iso.setField(ISO.FIELD_41_CARD_ACCEPTOR_TERMINAL_IDENTIFICATION, "MED40400");

        if (offset.equals("0"))
            iso.setField(ISO.FIELD_60_RESERVED_PRIVATE, "NEWPOS_9220_034.zip,0,20000");
        else
            iso.setField(ISO.FIELD_60_RESERVED_PRIVATE, "NEWPOS_9220_034.zip,20000,20000");

        outField61 = "";

        outField61 += getHash(tidAUX + "_" + RED + TXT_T, forcedInit) + separator +
                getHash(tidAUX + "_" + COMERCIOS + TXT_T, forcedInit) + separator +
                getHash(tidAUX + "_" + SUCURSAL + TXT_T, forcedInit) + separator +
                getHash(tidAUX + "_" + DEVICE + TXT_T, forcedInit) + separator +
                getHash(tidAUX + "_" + CARDS + TXT_T, forcedInit) + separator +
                getHash(tidAUX + "_" + EMVAPPS + TXT_T, forcedInit) + separator +
                getHash(tidAUX + "_" + CAPKS + TXT_T, forcedInit) + separator +
                getHash(tidAUX + "_" + HOST + TXT_T, forcedInit) + separator +
                getHash(tidAUX + "_" + IPS + TXT_T, forcedInit) + separator +
                getHash(tidAUX + "_" + PLANES + TXT_T, forcedInit) + separator +
                getHash(tidAUX + "_" + TRANSACCIONES + TXT_T, forcedInit) + separator +
                getHash(tidAUX + "_" + APLICACIONES + TXT_T, forcedInit) + separator +
                getHash(tidAUX + "_" + tareas + TXT_T, forcedInit) + separator +
                // getHash(tidAUX+"_"+emvappsdebug+TXT_T,forcedInit) + separator +

                //CTL Files
                getHash(tidAUX + "_" + ENTRY_POINT + ".bin", forcedInit) + separator +
                getHash(tidAUX + "_" + PROCESSING + ".bin", forcedInit) + separator +
                getHash(tidAUX + "_" + REVOK + ".bin", forcedInit) + separator +
                getHash(tidAUX + "_" + TERMINAL + ".bin", forcedInit) + separator;

        iso.setField(ISO.FIELD_62_RESERVED_PRIVATE, outField61);

        tmp = iso.getTxnOutput();

        return tmp;
    }

    private byte[] packIsoInit() {
        byte[] data = new byte[0];

        if (tramaQueEnvia == INIT_TOTAL) {
            data = armarTramaDescarga(fileName, offset);
        } else if (tramaQueEnvia == INIT_PARCIAL) {
            data = armarTramaDescargaParcial(fileName, offset, false);
        }

        return data;
    }

    private boolean unpackDescarga(ISO rspTx) {
        String rspCode = rspTx.getField(ISO.FIELD_39_RESPONSE_CODE);
        String procCode = rspTx.getField(ISO.FIELD_03_PROCESSING_CODE);
        byte[] f64;
        String field60;
        String field61;
        String field62;
        String hashSegmento;

        if (procCode.equals("960080")) {
            field62 = rspTx.getField(ISO.FIELD_62_RESERVED_PRIVATE);
            hashSegmento = field62.substring(0, field62.indexOf("|"));
            f64 = rspTx.getFieldB(ISO.FIELD_64_MESSAGE_AUTHENTICATION_CODE);
            FileOutputStream fileOutputStream = null;
            try {
                fileOutputStream = new FileOutputStream(file, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Logger.exception(TAG, e);
            }

            try {
                if (Tools.hashSha1(f64).equals(hashSegmento)) {
                    fileOutputStream.write(f64, 0, rspTx.getSizeField(64));
                }

            } catch (IOException e) {
                e.printStackTrace();
                Logger.exception(TAG, e);
            }
            return true;
        } else {
            switch (rspCode) {
                case "00":
                    field61 = rspTx.getField(ISO.FIELD_62_RESERVED_PRIVATE);
                    hashSegmento = field61.substring(0, field61.indexOf("|"));
                    f64 = rspTx.getFieldB(ISO.FIELD_64_MESSAGE_AUTHENTICATION_CODE);
                    FileOutputStream fileOutputStream = null;
                    try {
                        fileOutputStream = new FileOutputStream(file, true);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                        Logger.exception(TAG, e);
                    }

                    try {
                        if (Tools.hashSha1(f64).equals(hashSegmento)) {
                            fileOutputStream.write(f64, 0, rspTx.getSizeField(64));
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                        Logger.exception(TAG, e);
                    }
                    return true;
                case "05":
                    field60 = "ERROR EN LA DESCARGA \n NO EXISTE TERMINAL";
                    resultOk = field60;
                    return false;
                case "95":
                    field60 = rspTx.getField(ISO.FIELD_60_RESERVED_PRIVATE);
                    resultOk = field60;
                    return false;
                default:
                    field60 = "Code: " + rspCode + " ERROR DESCONOCIDO";
                    resultOk = field60;
                    return false;
            }
        }

    }

    /**
     * In this interface the definition of the onPostExecute
     * method is performed, which receives the
     * response of the request from the WS method.
     */
    public interface TcpCallback {
        void rspHost(byte[] rxBuf, String resultOk);
    }
}


